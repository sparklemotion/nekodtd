<!-- $Id$ -->
<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'
                xmlns='http://relaxng.org/ns/structure/1.0'>

 <xsl:param name='type_prefix'>T_</xsl:param>

 <xsl:include href='funcs.xsl'/>

 <xsl:output method='xml'/>

 <!-- MAIN TEMPLATES -->

 <xsl:template match='/'>
  <xsl:if test='dtd/externalSubset|//parameterEntity|//conditional'>
   <xsl:message terminate='yes'>
ERROR: The DTDx file MUST be converted to a flattened form before 
       generating the XML Schema file. Please use the dtd2flat.xsl
       stylesheet first and then process that output with this
       stylesheet.</xsl:message>
  </xsl:if>
  <xsl:if test='dtd/@sysid'>
   <xsl:comment>Generated from <xsl:value-of select='dtd/@sysid'/></xsl:comment>
  </xsl:if>
  <grammar datatypeLibrary='http://www.w3.org/2001/XMLSchema-datatypes'>
   <start>
    <choice>
     <xsl:for-each select='//elementDecl'>
      <xsl:variable name='ename'><xsl:value-of select='@ename'/></xsl:variable>
      <xsl:if test='not(preceding::elementDecl[@ename=$ename])'>
       <xsl:apply-templates select='.' mode='start'/>
      </xsl:if>
     </xsl:for-each>
    </choice>
   </start>
   <xsl:for-each select='//contentModel'>
    <xsl:variable name='ename'><xsl:value-of select='@ename'/></xsl:variable>
    <xsl:if test='not(preceding::contentModel[@ename=$ename])'>
     <xsl:apply-templates select='.' mode='define'/>
    </xsl:if>
   </xsl:for-each>
   <xsl:if test='//elementDecl/@model="ANY"'>
    <xsl:comment> any type declaration </xsl:comment>
    <define name="__any__">
     <element>
      <anyName/>
      <zeroOrMore>
       <choice>
        <attribute><anyName/></attribute>
        <text/>
        <ref name="__any__"/>
       </choice>
      </zeroOrMore>
     </element>
    </define>
   </xsl:if>
  </grammar>
 </xsl:template>

 <xsl:template match='elementDecl' mode='start'>
  <element name='{@ename}'>
   <ref name='{$type_prefix}{@ename}'/>
  </element>
 </xsl:template>

 <xsl:template match='contentModel' mode='define'>
  <xsl:call-template name='comment_contentModel'/>
  <define name='{$type_prefix}{@ename}'>
   <xsl:variable name='ename'><xsl:value-of select='@ename'/></xsl:variable>
   <xsl:choose>
    <xsl:when test='empty and not(//attributeDecl[@ename=$ename])'>
     <empty/>
    </xsl:when>
    <xsl:when test='any'>
     <zeroOrMore>
      <choice>
       <attribute><anyName/></attribute>
       <text/>
       <ref name="__any__"/>
      </choice>
     </zeroOrMore>
    </xsl:when>
    <xsl:when test='group/pcdata and not(group/element)'>
     <text/>
    </xsl:when>
    <xsl:when test='group/pcdata and group/element'>
     <zeroOrMore>
      <choice>
       <text/>
       <xsl:for-each select='group/element'>
        <element name='mumble'>
         <ref name='{$type_prefix}{@name}'/>
        </element>
       </xsl:for-each>
      </choice>
     </zeroOrMore>
    </xsl:when>
    <xsl:otherwise>
     <xsl:apply-templates select='group' mode='children'/>
    </xsl:otherwise>
   </xsl:choose>
   <xsl:for-each select='//attributeDecl[@ename=$ename]'>
    <xsl:variable name='aname'><xsl:value-of select='@aname'/></xsl:variable>
    <xsl:if test='not(preceding::attributeDecl[@ename=$ename][@aname=$aname])'>
     <xsl:apply-templates select='.'/>
    </xsl:if>
   </xsl:for-each>
  </define>
 </xsl:template>

 <xsl:template match='group' mode='children'>
  <xsl:variable name='name'><xsl:value-of select='name(following-sibling::*[1])'/></xsl:variable>
  <xsl:variable name='type'><xsl:value-of select='following-sibling::*[1]/@type'/></xsl:variable>
  <xsl:choose>
   <xsl:when test='$name="occurrence" and $type="?"'>
    <optional>
     <xsl:call-template name='group'/>
    </optional>
   </xsl:when>
   <xsl:when test='$name="occurrence" and $type="*"'>
    <zeroOrMore>
     <xsl:call-template name='group'/>
    </zeroOrMore>
   </xsl:when>
   <xsl:when test='$name="occurrence" and $type="+"'>
    <oneOrMore>
     <xsl:call-template name='group'/>
    </oneOrMore>
   </xsl:when>
   <xsl:otherwise>
    <xsl:call-template name='group'/>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

 <xsl:template name='group'>
  <xsl:choose>
   <xsl:when test='separator[@type="|"]'>
    <choice>
     <xsl:apply-templates mode='children'/>
    </choice>
   </xsl:when>
   <xsl:when test='separator[@type=","]'>
    <group>
     <xsl:apply-templates mode='children'/>
    </group>
   </xsl:when>
   <xsl:otherwise>
    <xsl:apply-templates mode='children'/>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

 <xsl:template match='element' mode='children'>
  <xsl:variable name='name'><xsl:value-of select='name(following-sibling::*[1])'/></xsl:variable>
  <xsl:variable name='type'><xsl:value-of select='following-sibling::*[1]/@type'/></xsl:variable>
  <xsl:choose>
   <xsl:when test='$name="occurrence" and $type="?"'>
    <optional>
     <xsl:call-template name='element'/>
    </optional>
   </xsl:when>
   <xsl:when test='$name="occurrence" and $type="*"'>
    <zeroOrMore>
     <xsl:call-template name='element'/>
    </zeroOrMore>
   </xsl:when>
   <xsl:when test='$name="occurrence" and $type="+"'>
    <oneOrMore>
     <xsl:call-template name='element'/>
    </oneOrMore>
   </xsl:when>
   <xsl:otherwise>
    <xsl:call-template name='element'/>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

 <xsl:template name='element'>
  <element name='{@name}'>
   <ref name='{$type_prefix}{@name}'/>
  </element>
 </xsl:template>

 <xsl:template match='attributeDecl'>
  <xsl:call-template name='comment_attributeDecl'/>
  <xsl:choose>
   <xsl:when test='@required="true"'>
    <xsl:call-template name='attribute'/>
   </xsl:when>
   <xsl:otherwise>
    <optional>
     <xsl:call-template name='attribute'/>
    </optional>
   </xsl:otherwise>
  </xsl:choose>
 </xsl:template>

 <xsl:template name='attribute'>
  <attribute name='{@aname}'>
   <xsl:choose>
    <xsl:when test='@fixed="true"'>
     <value><xsl:value-of select='@default'/></value>
    </xsl:when>
    <xsl:when test='starts-with(@atype,"(")'>
     <choice>
      <xsl:for-each select='enumeration'>
       <value><xsl:value-of select='@value'/></value>
      </xsl:for-each>
     </choice>
    </xsl:when>
    <xsl:when test='@atype="CDATA"'>
     <text/>
    </xsl:when>
    <xsl:otherwise>
     <data type='{@atype}'/>
    </xsl:otherwise>
   </xsl:choose>
  </attribute>
 </xsl:template>

</xsl:stylesheet>