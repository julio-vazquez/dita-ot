package org.dita.dost.reader;

import java.io.File;

import org.dita.dost.log.DITAOTJavaLogger;
import org.dita.dost.module.Content;
import org.dita.dost.module.ContentImpl;
import org.dita.dost.util.Constants;
import org.dita.dost.util.MergeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * MergeMapParser reads the ditamap file after preprocessing and merges
 * different files into one intermediate result. It calls MergeTopicParser
 * to process the topic file.
 * 
 * @author Zhang, Yuan Peng
 */
public class MergeMapParser extends AbstractXMLReader {
	private XMLReader reader = null;
	private StringBuffer mapInfo = null;
	private MergeTopicParser topicParser = null;
	private DITAOTJavaLogger logger = null;
	private MergeUtils util;
	private ContentImpl content;
	private String dirPath = null;

	public MergeMapParser() {
		
		try{
			if (System.getProperty(Constants.SAX_DRIVER_PROPERTY) == null){
				//The default sax driver is set to xerces's sax driver
				System.setProperty(Constants.SAX_DRIVER_PROPERTY, Constants.SAX_DRIVER_DEFAULT_CLASS);
			}
			if(reader == null){
				reader = XMLReaderFactory.createXMLReader();
				reader.setContentHandler(this);
				reader.setProperty(Constants.LEXICAL_HANDLER_PROPERTY,this);
				reader.setFeature(Constants.FEATURE_NAMESPACE_PREFIX, true);
				reader.setFeature(Constants.FEATURE_VALIDATION, true); 
				reader.setFeature(Constants.FEATURE_VALIDATION_SCHEMA, true);
			}
			if(mapInfo == null){
				mapInfo = new StringBuffer(Constants.INT_1024);
			}
			
			topicParser = new MergeTopicParser();
			content = new ContentImpl();
			util = MergeUtils.getInstance();
		}catch (Exception e){
			logger.logException(e);
		}
	}

	public Content getContent() {
		content.setValue(mapInfo.append((StringBuffer)topicParser.getContent().getValue()));
		return content;
	}

	public void read(String filename) {
		try{
			File input = new File(filename);
			dirPath = input.getParent();
			reader.parse(filename);
		}catch(Exception e){
			logger.logException(e);
		}
	}

	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		mapInfo.append(Constants.LESS_THAN)
		.append(Constants.SLASH)
		.append(qName)
		.append(Constants.GREATER_THAN);
	}

	
	public void characters(char[] ch, int start, int length) throws SAXException {
		mapInfo.append(ch, start, length);
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		
		String scopeValue = null;
		String formatValue = null;
		String fileId = null;
		int attsLen = atts.getLength();
		
		mapInfo.append(Constants.LESS_THAN).append(qName);
		
		for (int i = 0; i < attsLen; i++) {
            String attQName = atts.getQName(i);
            String attValue = atts.getValue(i);
            if(Constants.ATTRIBUTE_NAME_HREF.equals(attQName) 
            		&& attValue != null
            		&& !Constants.STRING_EMPTY.equalsIgnoreCase(attValue)){
            	scopeValue = atts.getValue(Constants.ATTRIBUTE_NAME_SCOPE);
        		formatValue = atts.getValue(Constants.ATTRIBUTE_NAME_FORMAT);
        		
//        		if (attValue.indexOf(Constants.SHARP) != -1){
//        			attValue = attValue.substring(0, attValue.indexOf(Constants.SHARP));
//        		}
        		
        		if((scopeValue == null 
    					|| Constants.ATTR_SCOPE_VALUE_LOCAL.equalsIgnoreCase(scopeValue))
    					&& (formatValue == null 
    							|| Constants.ATTR_FORMAT_VALUE_DITA.equalsIgnoreCase(formatValue))){
    				if (util.isVisited(attValue)){
    					mapInfo.append(Constants.STRING_BLANK)
            			.append("ohref").append(Constants.EQUAL).append(Constants.QUOTATION)
            			.append(attValue).append(Constants.QUOTATION);
    					
//    					random = RandomUtils.getRandomNum();
//    					filename = attValue + "(" + Long.toString(random) + ")";
    					attValue = Constants.SHARP + util.getIdValue(attValue);
    					//parse the file but give it another file name
//    					topicParser.read(filename);
    				}else{
    					mapInfo.append(Constants.STRING_BLANK)
            			.append("ohref").append(Constants.EQUAL).append(Constants.QUOTATION)
            			.append(attValue).append(Constants.QUOTATION);
    					    					
    					//parse the topic
    					fileId = topicParser.parse(attValue,dirPath);
    					util.visit(attValue);
    					attValue = Constants.SHARP + fileId;
    				}
    			}
        		
            }
            
            //output all attributes
            mapInfo.append(Constants.STRING_BLANK)
            		.append(attQName).append(Constants.EQUAL).append(Constants.QUOTATION)
            		.append(attValue).append(Constants.QUOTATION);
        }
		mapInfo.append(Constants.GREATER_THAN);
		
	}
	
	

}