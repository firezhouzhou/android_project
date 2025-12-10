package com.baidu.application.logger.formatter;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * XML 格式化器
 * 
 * @author Android架构师
 * @since 1.0.0
 */
public class XmlFormatter implements ILogFormatter {
    
    @Override
    public String format(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        String xmlStr = obj.toString();
        
        try {
            Source xmlInput = new StreamSource(new StringReader(xmlStr));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(xmlInput, xmlOutput);
            
            return xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
        } catch (Exception e) {
            return "Invalid XML: " + xmlStr;
        }
    }
}
