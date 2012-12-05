#!/bin/sh

CP=nekodtd.jar:lib/xml-apis.jar:lib/xercesImpl.jar:lib/xercesSamples.jar 

echo "class path is $CP"

java -cp $CP xni.Writer -p org.cyberneko.dtd.DTDConfiguration $1 > temp.dtdx

java -cp $CP org.apache.xalan.xslt.Process -IN temp.dtdx -XSL data/dtd/dtdx2flat.xsl > temp.flat

java -cp $CP org.apache.xalan.xslt.Process -IN temp.flat -XSL data/dtd/flat2xsd.xsl




