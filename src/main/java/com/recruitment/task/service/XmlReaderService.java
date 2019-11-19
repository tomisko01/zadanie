package com.recruitment.task.service;

import com.recruitment.task.XmlHandler;
import com.recruitment.task.api.CustomerFromFile;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class XmlReaderService {

    public List<CustomerFromFile> readCustomersFromFile(String fileName) {
        List<CustomerFromFile> customers = new ArrayList<>();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            XmlHandler handler = new XmlHandler();

            File file = new File(fileName);
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");

            saxParser.parse(is, handler);
            customers = handler.getCustomers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }
}
