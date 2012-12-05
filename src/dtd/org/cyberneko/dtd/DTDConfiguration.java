/*
 * (C) Copyright 2002-2003, Andy Clark.  All rights reserved.
 *
 * This file is distributed under an Apache style license. Please
 * refer to the LICENSE file for specific details.
 */

package org.cyberneko.dtd;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Locale;

import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.XMLDTDScannerImpl;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.xs.util.SimpleLocator;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.util.ParserConfigurationSettings;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDTDContentModelSource;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;

/**
 * An XNI-based parser configuration that can be used to parse DTD
 * documents to generate an XML representation of the DTD. This
 * configuration can be used directly in order to parse DTD documents
 * or can be used in conjunction with any XNI based tools, such as
 * the Xerces2 implementation.
 * <p>
 * For complete usage information, refer to the documentation.
 *
 * @author Andy Clark
 *
 * @version $Id$
 */
public class DTDConfiguration
    extends ParserConfigurationSettings
    implements XMLParserConfiguration,
               XMLDTDHandler, XMLDTDContentModelHandler {

    //
    // Constants
    //

    // properties

    /** Entity resolver ("http://apache.org/xml/properties/internal/entity-resolver"). */
    protected static final String ENTITY_RESOLVER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_RESOLVER_PROPERTY;

    /** Locale property identifier ("http://apache.org/xml/properties/locale"). */
    protected static final String LOCALE =
        Constants.XERCES_PROPERTY_PREFIX + "locale";

    // element names

    public static final QName E_DTD = new QName(null, "dtd", "dtd", null);
    public static final QName E_EXTERNAL_SUBSET = new QName(null, "externalSubset", "externalSubset", null);
    public static final QName E_PARAMETER_ENTITY = new QName(null, "parameterEntity", "parameterEntity", null);
    public static final QName E_TEXT_DECL = new QName(null, "textDecl", "textDecl", null);
    public static final QName E_ELEMENT_DECL = new QName(null, "elementDecl", "elementDecl", null);
    public static final QName E_ATTLIST = new QName(null, "attlist", "attlist", null);
    public static final QName E_ATTRIBUTE_DECL = new QName(null, "attributeDecl", "attributeDecl", null);
    public static final QName E_ENUMERATION = new QName(null, "enumeration", "enumeration", null);
    public static final QName E_INTERNAL_ENTITY_DECL = new QName(null, "internalEntityDecl", "internalEntityDecl", null);
    public static final QName E_EXTERNAL_ENTITY_DECL = new QName(null, "externalEntityDecl", "externalEntityDecl", null);
    public static final QName E_UNPARSED_ENTITY_DECL = new QName(null, "unparsedEntityDecl", "unparsedEntityDecl", null);
    public static final QName E_NOTATION_DECL = new QName(null, "notationDecl", "notationDecl", null);
    public static final QName E_CONDITIONAL = new QName(null, "conditional", "conditional", null);
    public static final QName E_CONTENT_MODEL = new QName(null, "contentModel", "contentModel", null);
    public static final QName E_ANY = new QName(null, "any", "any", null);
    public static final QName E_EMPTY = new QName(null, "empty", "empty", null);
    public static final QName E_GROUP = new QName(null, "group", "group", null);
    public static final QName E_PCDATA = new QName(null, "pcdata", "pcdata", null);
    public static final QName E_ELEMENT = new QName(null, "element", "element", null);
    public static final QName E_SEPARATOR = new QName(null, "separator", "separator", null);
    public static final QName E_OCCURRENCE = new QName(null, "occurrence", "occurrence", null);
    public static final QName E_COMMENT = new QName(null, "comment", "comment", null);
    public static final QName E_PROCESSING_INSTRUCTION = new QName(null, "processingInstruction", "processingInstruction", null);
    public static final QName E_IGNORED_CHARACTERS = new QName(null, "ignoredCharacters", "ignoredCharacters", null);

    // attribute names

    public static final QName A_SYSID = new QName(null, "sysid", "sysid", null);
    public static final QName A_VERSION = new QName(null, "version", "version", null);
    public static final QName A_ENCODING = new QName(null, "encoding", "encoding", null);
    public static final QName A_ENAME = new QName(null, "ename", "ename", null);
    public static final QName A_MODEL = new QName(null, "model", "model", null);
    public static final QName A_ANAME = new QName(null, "aname", "aname", null);
    public static final QName A_ATYPE = new QName(null, "atype", "atype", null);
    public static final QName A_VALUE = new QName(null, "value", "value", null);
    public static final QName A_REQUIRED = new QName(null, "required", "required", null);
    public static final QName A_FIXED = new QName(null, "fixed", "fixed", null);
    public static final QName A_DEFAULT = new QName(null, "default", "default", null);
    public static final QName A_NAME = new QName(null, "name", "name", null);
    public static final QName A_PUBID = new QName(null, "pubid", "pubid", null);
    public static final QName A_NOTATION = new QName(null, "notation", "notation", null);
    public static final QName A_TYPE = new QName(null, "type", "type", null);
    public static final QName A_DATA = new QName(null, "data", "data", null);
    public static final QName A_TARGET = new QName(null, "target", "target", null);

    // types

    public static final String T_BOOLEAN = "(true|false)";
    public static final String T_CONDITIONAL = "(INCLUDE|IGNORE)";

    // HACK: workarounds Xerces 2.0.x problems

    /** Parser version is Xerces 2.0.0. */
    protected static boolean XERCES_2_0_0 = false;

    /** Parser version is Xerces 2.0.1. */
    protected static boolean XERCES_2_0_1 = false;

    /** Parser version is XML4J 4.0.x. */
    protected static boolean XML4J_4_0_x = false;

    //
    // Static initializer
    //

    static {
        try {
            String VERSION = "org.apache.xerces.impl.Version";
            Object version = ObjectFactory.createObject(VERSION, VERSION);
            java.lang.reflect.Field field = version.getClass().getField("fVersion");
            String versionStr = String.valueOf(field.get(version));
            XERCES_2_0_0 = versionStr.equals("Xerces-J 2.0.0");
            XERCES_2_0_1 = versionStr.equals("Xerces-J 2.0.1");
            XML4J_4_0_x = versionStr.startsWith("XML4J 4.0.");
        }
        catch (Throwable e) {
            // ignore
        }
    } // <clinit>()

    //
    // Data
    //

    // settings

    /** Registered document handler. */
    protected XMLDocumentHandler fDocumentHandler;

    /** Registered error handler. */
    protected XMLErrorHandler fErrorHandler;

    /** Registered entity resolver. */
    protected XMLEntityResolver fEntityResolver;

    // DTD sources

    /** DTD source. */
    protected XMLDTDSource fDTDSource;

    /** DTD content model source. */
    protected XMLDTDContentModelSource fDTDContentModelSource;

    // components


    /** Symbol table. */
    protected SymbolTable fSymbolTable = new SymbolTable();

    /** DTD scanner. */
    protected XMLDTDScannerImpl fScanner = new XMLDTDScannerImpl();

    /** Entity manager. */
    protected XMLEntityManager fEntityManager = new XMLEntityManager();

    /** Error reporter. */
    protected XMLErrorReporter fErrorReporter = new XMLErrorReporter();

    // temp vars

    /** Attributes. */
    private final XMLAttributes fAttributes = new XMLAttributesImpl();

    //
    // Constructors
    //

    /** Default constructor. */
    public DTDConfiguration() {

        // add default features
        String[] featureNames = {
            "http://xml.org/sax/features/validation",
        };
        Boolean[] featureValues = {
            Boolean.FALSE,
        };
        addRecognizedFeatures(featureNames);
        for (int i = 0; i < featureNames.length; i++) {
            Boolean featureValue = featureValues[i];
            if (featureValue != null) {
                String featureName = featureNames[i];
                setFeature(featureName, featureValue.booleanValue());
            }
        }

        // add default properties
        String[] propertyNames = {
            "http://apache.org/xml/properties/internal/symbol-table",
            "http://apache.org/xml/properties/internal/entity-manager",
            "http://apache.org/xml/properties/internal/entity-resolver",
            "http://apache.org/xml/properties/internal/error-reporter",
            "http://apache.org/xml/properties/internal/error-handler",
            LOCALE,
        };
        Object[] propertyValues = {
            fSymbolTable,
            fEntityManager,
            null,
            fErrorReporter,
            null,
            Locale.getDefault(),
        };
        addRecognizedProperties(propertyNames);
        for (int i = 0; i < propertyNames.length; i++) {
            Object propertyValue = propertyValues[i];
            if (propertyValue != null) {
                String propertyName = propertyNames[i];
                setProperty(propertyName, propertyValue);
            }
        }

        // set handlers
        fScanner.setDTDHandler(this);
        fScanner.setDTDContentModelHandler(this);

        // HACK: Xerces 2.0.0
        if (XERCES_2_0_0) {
            // NOTE: These features should not be required but it causes a
            //       problem if they're not there. This will be fixed in
            //       subsequent releases of Xerces.
            String[] recognizedFeatures = new String[] {
                "http://apache.org/xml/features/scanner/notify-builtin-refs",
            };
            addRecognizedFeatures(recognizedFeatures);
            // NOTE: This is a hack to get around a problem in the Xerces 2.0.0
            //       AbstractSAXParser. If it uses a parser configuration that
            //       does not have a SymbolTable, then it will remove *all*
            //       attributes. This will be fixed in subsequent releases of
            //       Xerces.
            String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
            String[] recognizedProperties = new String[] {
                SYMBOL_TABLE,
            };
            addRecognizedProperties(recognizedProperties);
            Object symbolTable = ObjectFactory.createObject("org.apache.xerces.util.SymbolTable",
                                                            "org.apache.xerces.util.SymbolTable");
            setProperty(SYMBOL_TABLE, symbolTable);
        }

        // HACK: Xerces 2.0.1
        if (XERCES_2_0_0 || XERCES_2_0_1 || XML4J_4_0_x) {
            // NOTE: These features should not be required but it causes a
            //       problem if they're not there. This should be fixed in
            //       subsequent releases of Xerces.
            String[] recognizedFeatures = new String[] {
                "http://apache.org/xml/features/validation/schema/normalized-value",
                "http://apache.org/xml/features/scanner/notify-char-refs",
            };
            addRecognizedFeatures(recognizedFeatures);
        }

    } // <init>()

    //
    // XMLParserConfiguration methods
    //

    /** Sets the error handler. */
    @Override
    public void setErrorHandler(XMLErrorHandler handler) {
        fErrorHandler = handler;
    } // setErrorHandler(XMLErrorHandler)

    /** Returns the error handler. */
    @Override
    public XMLErrorHandler getErrorHandler() {
        return fErrorHandler;
    } // getErrorHandler():XMLErrorHandler

    /** Sets the entity resolver. */
    @Override
    public void setEntityResolver(XMLEntityResolver resolver) {
        setProperty(ENTITY_RESOLVER, fEntityResolver = resolver);
    } // setEntityResolver(XMLEntityResolver)

    /** Returns the entity resolver. */
    @Override
    public XMLEntityResolver getEntityResolver() {
        return fEntityResolver;
    } // getEntityResolver():XMLEntityResolver

    /** Sets the document handler. */
    @Override
    public void setDocumentHandler(XMLDocumentHandler handler) {
        fDocumentHandler = handler;
    } // setDocumentHandler(XMLDocumentHandler)

    /** Returns the document handler. */
    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return fDocumentHandler;
    } // getDocumentHandler():XMLDocumentHandler

    /** Sets the DTD handler. */
    @Override
    public void setDTDHandler(XMLDTDHandler handler) {}

    /** Returns the DTD handler. */
    @Override
    public XMLDTDHandler getDTDHandler() { return null; }

    /** Sets the DTD content model handler. */
    @Override
    public void setDTDContentModelHandler(XMLDTDContentModelHandler handler) {}

    /** Returns the DTD content model handler. */
    @Override
    public XMLDTDContentModelHandler getDTDContentModelHandler() { return null; }

    /** Sets the locale. */
    @Override
    public void setLocale(Locale locale) {
        try {
            setProperty(LOCALE, locale);
        }
        catch (Exception e) {
            // ignore
        }
    } // setLocale(Locale)

    /** Returns the locale. */
    @Override
    public Locale getLocale() {
        Locale locale = null;
        try {
            locale = (Locale)getProperty(LOCALE);
            fErrorReporter.setLocale(locale);
        }
        catch (Exception e) {
            // ignore
        }
        return locale;
    } // getLocale():Locale

    /** Parses the DTD file specified by the given input source. */
    @Override
    public void parse(XMLInputSource source) throws XNIException, IOException {
        fScanner.reset(this);
        fEntityManager.reset(this);
        fErrorReporter.reset(this);
        fScanner.setInputSource(source);
        try {
            fScanner.scanDTDExternalSubset(true);
        }
        catch (EOFException e) {
            // ignore
            // NOTE: This is to work around a problem in the Xerces
            //       DTD scanner implementation when used standalone. -Ac
        }
    } // parse(XMLInputSource)

    //
    // XMLDTDHandler methods
    //

    /** Sets the DTD source. */
    @Override
    public void setDTDSource(XMLDTDSource source) {
        fDTDSource = source;
    } // setDTDSource(XMLDTDSource)

    /** Returns the DTD source. */
    @Override
    public XMLDTDSource getDTDSource() {
        return fDTDSource;
    } // getDTDSource():XMLDTDSource

    /** Start DTD. */
    @Override
    public void startDTD(XMLLocator locator, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            String encoding = "UTF-8";
            NamespaceContext nscontext = new NamespaceSupport();
            try {
                // NOTE: Hack to allow the default filter to work with
                //       old and new versions of the XNI document handler
                //       interface. -Ac
                Class cls = fDocumentHandler.getClass();
                Class[] types = {
                    XMLLocator.class, String.class,
                    NamespaceContext.class, Augmentations.class
                };
                Method method = cls.getMethod("startDocument", types);
                Object[] params = {
                    locator, encoding,
                    nscontext, augs
                };
                method.invoke(fDocumentHandler, params);
            }
            catch (XNIException e) {
                throw e;
            }
            catch (Exception e) {
                try {
                    // NOTE: Hack to allow the default filter to work with
                    //       old and new versions of the XNI document handler
                    //       interface. -Ac
                    Class cls = fDocumentHandler.getClass();
                    Class[] types = {
                        XMLLocator.class, String.class, Augmentations.class
                    };
                    Method method = cls.getMethod("startDocument", types);
                    Object[] params = {
                        locator, encoding, augs
                    };
                    method.invoke(fDocumentHandler, params);
                }
                catch (XNIException ex) {
                    throw ex;
                }
                catch (Exception ex) {
                    // NOTE: Should never reach here!
                    throw new XNIException(ex);
                }
            }
            XMLString comment1 = new XMLStringBuffer(
                " Generated by NekoDTD at "+new java.util.Date(System.currentTimeMillis())+' ');
            XMLString comment2 = new XMLStringBuffer(
                " You can download the latest NekoXNI tools from"+
                " http://www.apache.org/~andyc/neko/index.html ");
            fDocumentHandler.comment(comment1, augs);
            fDocumentHandler.comment(comment2, augs);
            fDocumentHandler.doctypeDecl(E_DTD.rawname, null, "dtdx.dtd", augs);
            fAttributes.removeAllAttributes();
            if (locator != null) {
                String systemId = locator.getLiteralSystemId();
                if (systemId != null) {
                    fAttributes.addAttribute(A_SYSID, "CDATA", systemId);
                    fAttributes.setSpecified(0, true);
                }
            }
            fDocumentHandler.startElement(E_DTD, fAttributes, augs);
        }
    } // startDTD(XMLLocator,Augmentations)

    /**
     * Start external subset.
     * <p>
     * <strong>Note:</strong>
     * This method is added for API compatibility with Xerces 2.0.0 and 2.0.1.
     */
    public void startExternalSubset(Augmentations augs) throws XNIException {
        startExternalSubset(null, augs);
    } // startExternalSubset(Augmentations)

    /**
     * Start external subset.
     * <p>
     * <strong>Note:</strong>
     * This method is added or API compatibility with Xerces versions higher
     * than 2.0.1
     */
    @Override
    public void startExternalSubset(XMLResourceIdentifier id,
                                    Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            if (id != null) {
                String pubid = id.getPublicId();
                if (pubid != null) {
                    fAttributes.addAttribute(A_PUBID, "CDATA", pubid);
                    fAttributes.setSpecified(0, true);
                }
                String sysid = id.getLiteralSystemId();
                if (sysid != null) {
                    fAttributes.addAttribute(A_SYSID, "CDATA", sysid);
                    fAttributes.setSpecified(pubid!=null?1:0, true);
                }
            }
            fDocumentHandler.startElement(E_EXTERNAL_SUBSET, fAttributes, augs);
        }
    } // startExternalSubset(XMLResourceIdentifier,Augmentations)

    /** End external subset. */
    @Override
    public void endExternalSubset(Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.endElement(E_EXTERNAL_SUBSET, augs);
        }
    } // endExternalSubset(Augmentations)

    /** End DTD. */
    @Override
    public void endDTD(Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.endElement(E_DTD, augs);
            fDocumentHandler.endDocument(augs);
        }
    } // endDTD(Augmentations)

    /** Comment. */
    @Override
    public void comment(XMLString text, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            if (text.length > 0) {
                fDocumentHandler.startElement(E_COMMENT, fAttributes, augs);
                fDocumentHandler.characters(text, augs);
                fDocumentHandler.endElement(E_COMMENT, augs);
            }
            else {
                fDocumentHandler.emptyElement(E_COMMENT, fAttributes, augs);
            }
        }
    } // comment(XMLString,Augmentations)

    /** Processing instruction. */
    @Override
    public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            fAttributes.addAttribute(A_TARGET, "NMTOKEN", target);
            fAttributes.setSpecified(0, true);
            fAttributes.addAttribute(A_DATA, "CDATA", data.toString());
            fAttributes.setSpecified(1, true);
            fDocumentHandler.emptyElement(E_PROCESSING_INSTRUCTION, fAttributes, augs);
        }
    } // processingInstruction(String,XMLString,Augmentations)

    /** Start parameter entity. */
    @Override
    public void startParameterEntity(String name, XMLResourceIdentifier id, String encoding, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            fAttributes.addAttribute(A_NAME, "CDATA", name);
            fAttributes.setSpecified(0, true);
            String publicId = id.getPublicId();
            if (publicId != null) {
                fAttributes.addAttribute(A_PUBID, "CDATA", publicId);
                fAttributes.setSpecified(1, true);
            }
            String systemId = id.getLiteralSystemId();
            if (systemId != null) {
                fAttributes.addAttribute(A_SYSID, "CDATA", systemId);
                fAttributes.setSpecified(publicId!=null?2:1, true);
            }
            fDocumentHandler.startElement(E_PARAMETER_ENTITY, fAttributes, augs);
        }
    } // startParameterEntity(String,XMLResourceIdentifier,String,Augmentations)

    /** Text declaration. */
    @Override
    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            if (version != null) {
                fAttributes.addAttribute(A_VERSION, "CDATA", version);
                fAttributes.setSpecified(0, true);
            }
            fAttributes.addAttribute(A_ENCODING, "CDATA", encoding);
            fAttributes.setSpecified(version!=null?1:0, true);
            fDocumentHandler.emptyElement(E_TEXT_DECL, fAttributes, augs);
        }
    } // textDecl(String,String,Augmentations)

    /** End parameter entity. */
    @Override
    public void endParameterEntity(String name, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.endElement(E_PARAMETER_ENTITY, augs);
        }
    } // endParameterEntity(String,Augmentations)

    /** Element declaration. */
    @Override
    public void elementDecl(String ename, String model, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            fAttributes.addAttribute(A_ENAME, "NMTOKEN", ename);
            fAttributes.setSpecified(0, true);
            fAttributes.addAttribute(A_MODEL, "CDATA", model);
            fAttributes.setSpecified(1, true);
            fDocumentHandler.emptyElement(E_ELEMENT_DECL, fAttributes, augs);
        }
    } // elementDecl(String,String,Augmentations)

    /** Start attribute list. */
    @Override
    public void startAttlist(String ename, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            fAttributes.addAttribute(A_ENAME, "NMTOKEN", ename);
            fAttributes.setSpecified(0, true);
            fDocumentHandler.startElement(E_ATTLIST, fAttributes, augs);
        }
    } // startAttlist(String,Augmentations)

    /** Attribute declaration. */
    @Override
    public void attributeDecl(String ename, String aname, String atype, String[] _enum, String dtype, XMLString dvalue, XMLString nondvalue, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            fAttributes.addAttribute(A_ENAME, "NMTOKEN", ename);
            fAttributes.setSpecified(0, true);
            fAttributes.addAttribute(A_ANAME, "NMTOKEN", aname);
            fAttributes.setSpecified(1, true);
            if (atype.equals("ENUMERATION")) {
                StringBuffer str = new StringBuffer();
                str.append('(');
                for (int i = 0; i < _enum.length; i++) {
                    if (i > 0) {
                        str.append('|');
                    }
                    str.append(_enum[i]);
                }
                str.append(')');
                atype = str.toString();
            }
            fAttributes.addAttribute(A_ATYPE, "CDATA", atype);
            fAttributes.setSpecified(2, true);
            if (dtype != null) {
                if (dtype.equals("#REQUIRED")) {
                    fAttributes.addAttribute(A_REQUIRED, T_BOOLEAN, "true");
                    fAttributes.setSpecified(fAttributes.getLength()-1, true);
                }
                else if (dtype.equals("#FIXED")) {
                    fAttributes.addAttribute(A_FIXED, T_BOOLEAN, "true");
                    fAttributes.setSpecified(fAttributes.getLength()-1, true);
                }
            }
            if (dvalue != null) {
                fAttributes.addAttribute(A_DEFAULT, "CDATA", dvalue.toString());
                fAttributes.setSpecified(fAttributes.getLength()-1, true);
            }
            if (_enum == null || _enum.length == 0) {
                fDocumentHandler.emptyElement(E_ATTRIBUTE_DECL, fAttributes, augs);
            }
            else {
                fDocumentHandler.startElement(E_ATTRIBUTE_DECL, fAttributes, augs);
                for (int i = 0; i < _enum.length; i++) {
                    fAttributes.removeAllAttributes();
                    fAttributes.addAttribute(A_VALUE, "CDATA", _enum[i]);
                    fAttributes.setSpecified(0, true);
                    fDocumentHandler.emptyElement(E_ENUMERATION, fAttributes, augs);
                }
                fDocumentHandler.endElement(E_ATTRIBUTE_DECL, augs);
            }
        }
    } // attributeDecl(String,String,Augmentations)

    /** End attribute list. */
    @Override
    public void endAttlist(Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.endElement(E_ATTLIST, augs);
        }
    } // endAttlist(Augmentations)

    /** Internal entity declaration. */
    @Override
    public void internalEntityDecl(String name, XMLString value, XMLString nonvalue, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            fAttributes.addAttribute(A_NAME, "NMTOKEN", name);
            fAttributes.setSpecified(0, true);
            fAttributes.addAttribute(A_VALUE, "CDATA", value.toString());
            fAttributes.setSpecified(1, true);
            fDocumentHandler.emptyElement(E_INTERNAL_ENTITY_DECL, fAttributes, augs);
        }
    } // internalEntityDecl(String,XMLString,XMLString,Augmentations)

    /** External entity declaration. */
    @Override
    public void externalEntityDecl(String name, XMLResourceIdentifier id, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            fAttributes.addAttribute(A_NAME, "NMTOKEN", name);
            fAttributes.setSpecified(0, true);
            String publicId = id.getPublicId();
            if (publicId != null) {
                fAttributes.addAttribute(A_PUBID, "CDATA", publicId);
                fAttributes.setSpecified(1, true);
            }
            String systemId = id.getLiteralSystemId();
            fAttributes.addAttribute(A_SYSID, "CDATA", systemId);
            fAttributes.setSpecified(publicId!=null?2:1, true);
            fDocumentHandler.emptyElement(E_EXTERNAL_ENTITY_DECL, fAttributes, augs);
        }
    } // externalEntityDecl(String,XMLResourceIdentifier,Augmentations)

    /** Unparsed entity declaration. */
    @Override
    public void unparsedEntityDecl(String name, XMLResourceIdentifier id, String notation, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            fAttributes.addAttribute(A_NAME, "NMTOKEN", name);
            fAttributes.setSpecified(0, true);
            String publicId = id.getPublicId();
            if (publicId != null) {
                fAttributes.addAttribute(A_PUBID, "CDATA", publicId);
                fAttributes.setSpecified(1, true);
            }
            String systemId = id.getLiteralSystemId();
            if (systemId != null) {
                fAttributes.addAttribute(A_SYSID, "CDATA", systemId);
                fAttributes.setSpecified(publicId!=null?2:1, true);
            }
            fAttributes.addAttribute(A_NOTATION, "NMTOKEN", notation);
            fAttributes.setSpecified(fAttributes.getLength()-1, true);
            fDocumentHandler.emptyElement(E_UNPARSED_ENTITY_DECL, fAttributes, augs);
        }
    } // externalEntityDecl(String,XMLResourceIdentifier,String,Augmentations)

    /** Notation declaration. */
    @Override
    public void notationDecl(String name, XMLResourceIdentifier id, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            fAttributes.addAttribute(A_NAME, "NMTOKEN", name);
            fAttributes.setSpecified(0, true);
            String publicId = id.getPublicId();
            if (publicId != null) {
                fAttributes.addAttribute(A_PUBID, "CDATA", publicId);
                fAttributes.setSpecified(1, true);
            }
            String systemId = id.getLiteralSystemId();
            if (systemId != null) {
                fAttributes.addAttribute(A_SYSID, "CDATA", systemId);
                fAttributes.setSpecified(publicId!=null?2:1, true);
            }
            fDocumentHandler.emptyElement(E_NOTATION_DECL, fAttributes, augs);
        }
    } // notationDecl(String,XMLResourceIdentifier,Augmentations)

    /** Start conditional section. */
    @Override
    public void startConditional(short type, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            String conditional = type == CONDITIONAL_IGNORE ? "IGNORE" : "INCLUDE";
            fAttributes.removeAllAttributes();
            fAttributes.addAttribute(A_TYPE, T_CONDITIONAL, conditional);
            fAttributes.setSpecified(0, true);
            fDocumentHandler.startElement(E_CONDITIONAL, fAttributes, augs);
        }
    } // startConditional(short,Augmentations)

    /** Ignored characters. */
    @Override
    public void ignoredCharacters(XMLString text, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            if (text.length > 0) {
                fDocumentHandler.startElement(E_IGNORED_CHARACTERS, fAttributes, augs);
                fDocumentHandler.characters(text, augs);
                fDocumentHandler.endElement(E_IGNORED_CHARACTERS, augs);
            }
            else {
                fDocumentHandler.emptyElement(E_IGNORED_CHARACTERS, fAttributes, augs);
            }
        }
    } // ignoredCharacters(XMLString,Augmentations)

    /** End conditional section. */
    @Override
    public void endConditional(Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.endElement(E_CONDITIONAL, augs);
        }
    } // endConditional(Augmentations)

    //
    // XMLDTDContentModelHandler methods
    //

    /** Sets the DTD content model source. */
    @Override
    public void setDTDContentModelSource(XMLDTDContentModelSource source) {
        fDTDContentModelSource = source;
    } // setDTDContentModelSource(XMLDTDContentModelSource)

    /** Returns the DTD content model source. */
    @Override
    public XMLDTDContentModelSource getDTDContentModelSource() {
        return fDTDContentModelSource;
    } // getDTDContentModelSource():XMLDTDContentModelSource

    /** Start content model. */
    @Override
    public void startContentModel(String ename, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            fAttributes.addAttribute(A_ENAME, "NMTOKEN", ename);
            fAttributes.setSpecified(0, true);
            fDocumentHandler.startElement(E_CONTENT_MODEL, fAttributes, augs);
        }
    } // startContentModel(String,Augmentations)

    /** End content model. */
    @Override
    public void endContentModel(Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.endElement(E_CONTENT_MODEL, augs);
        }
    } // endContentModel(Augmentations)

    /** Any content model. */
    @Override
    public void any(Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            fDocumentHandler.emptyElement(E_ANY, fAttributes, augs);
        }
    } // any(Augmentations)

    /** Empty content model. */
    @Override
    public void empty(Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            fDocumentHandler.emptyElement(E_EMPTY, fAttributes, augs);
        }
    } // empty(Augmentations)

    /** Start mixed or children content model group. */
    @Override
    public void startGroup(Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            fDocumentHandler.startElement(E_GROUP, fAttributes, augs);
        }
    } // startGroup(Augmentations)

    /** Parsed character data for mixed content model. */
    @Override
    public void pcdata(Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            fDocumentHandler.emptyElement(E_PCDATA, fAttributes, augs);
        }
    } // pcdata(Augmentations)

    /** Element reference in mixed or children content model. */
    @Override
    public void element(String name, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fAttributes.removeAllAttributes();
            fAttributes.addAttribute(A_NAME, "NMTOKEN", name);
            fAttributes.setSpecified(0, true);
            fDocumentHandler.emptyElement(E_ELEMENT, fAttributes, augs);
        }
    } // element(String,Augmentations)

    /** Separator in mixed or children content model. */
    @Override
    public void separator(short type, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            String groupType = type == SEPARATOR_CHOICE ? "|" : ",";
            fAttributes.removeAllAttributes();
            fAttributes.addAttribute(A_TYPE, "CDATA", groupType);
            fAttributes.setSpecified(0, true);
            fDocumentHandler.emptyElement(E_SEPARATOR, fAttributes, augs);
        }
    } // separator(short,Augmentations)

    /** Occurrence count in mixed or children content model. */
    @Override
    public void occurrence(short type, Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            String occurs = type == OCCURS_ONE_OR_MORE ? "+"
                          : type == OCCURS_ZERO_OR_MORE ? "*"
                          : type == OCCURS_ZERO_OR_ONE ? "?" : null;
            fAttributes.removeAllAttributes();
            fAttributes.addAttribute(A_TYPE, "CDATA", occurs);
            fAttributes.setSpecified(0, true);
            fDocumentHandler.emptyElement(E_OCCURRENCE, fAttributes, augs);
        }
    } // occurrence(short,Augmentations)

    /** End mixed or children content model group. */
    @Override
    public void endGroup(Augmentations augs) throws XNIException {
        if (fDocumentHandler != null) {
            fDocumentHandler.endElement(E_GROUP, augs);
        }
    } // endGroup(Augmentations)

} // class DTDConfiguration
