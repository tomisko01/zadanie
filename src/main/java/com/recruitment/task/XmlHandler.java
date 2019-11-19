package com.recruitment.task;

import com.recruitment.task.api.ContactFromFile;
import com.recruitment.task.api.ContactTypeEnum;
import com.recruitment.task.api.CustomerFromFile;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class XmlHandler extends DefaultHandler {
    private List<CustomerFromFile> customers = null;
    private List<ContactFromFile> contacts = null;
    private CustomerFromFile customer = null;
    private StringBuilder stringBuilder = null;

    private boolean bName = false,
            bSurname = false,
            bAge = false,
            bCity = false,
            bContacts = false,
            bUnknown = false,
            bEmail = false,
            bPhone = false,
            bJabber = false;

    public XmlHandler() {
        super();
    }

    public List<CustomerFromFile> getCustomers() {
        return customers;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        if (qName.equalsIgnoreCase("person")) {
            customer = new CustomerFromFile();
            if (customers == null)
                customers = new ArrayList<>();
        } else if (qName.equalsIgnoreCase("name")) {
            bName = true;
        } else if (qName.equalsIgnoreCase("surname")) {
            bSurname = true;
        } else if (qName.equalsIgnoreCase("age")) {
            bAge = true;
        } else if (qName.equalsIgnoreCase("city")) {
            bCity = true;
        }

        if (qName.equalsIgnoreCase("contacts")) {
            if (contacts == null)
                contacts = new ArrayList<>();
        } else if (qName.equalsIgnoreCase("email")) {
            bEmail = true;
        } else if (qName.equalsIgnoreCase("phone")) {
            bPhone = true;
        } if (qName.equalsIgnoreCase("jabber")) {
            bJabber = true;
        } if (this.bContacts && !qName.equalsIgnoreCase("email") && !qName.equalsIgnoreCase("phone") && !qName.equalsIgnoreCase("jabber")) {
            bUnknown = true;
        }

        if (qName.equalsIgnoreCase("contacts")) {
            bContacts = true;
        }

        stringBuilder = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName,
                           String qName) throws SAXException {
        if (bName) {
            customer.setName(stringBuilder.toString());
            bName = false;
        }

        if (bSurname) {
            customer.setSurname(stringBuilder.toString());
            bSurname = false;
        }

        if (bAge) {
            customer.setAge(Integer.parseInt(stringBuilder.toString()));
            bAge = false;
        }

        if (bCity) {
            customer.setCity(stringBuilder.toString());
            bCity = false;
        }

        if (qName.equalsIgnoreCase("person")) {
            bContacts = false;
            customer.setContactFromFile(contacts);
            customers.add(customer);
            contacts = null;
        }

        if (bUnknown) {
            assert contacts != null;
            contacts.add(ContactFromFile.builder()
                    .intType(ContactTypeEnum.UNKNOWN.getShortForm())
                    .contactDetails(stringBuilder.toString())
                    .build());
            bUnknown = false;
        }

        if (bPhone) {
            assert contacts != null;
            contacts.add(ContactFromFile.builder()
                    .intType(ContactTypeEnum.PHONE.getShortForm())
                    .contactDetails(stringBuilder.toString())
                    .build());
            bPhone = false;
        }

        if (bEmail) {
            contacts.add(ContactFromFile.builder()
                    .intType(ContactTypeEnum.EMAIL.getShortForm())
                    .contactDetails(stringBuilder.toString())
                    .build());
            bEmail = false;
        }

        if (bJabber) {
            contacts.add(ContactFromFile.builder()
                    .intType(ContactTypeEnum.JABBER.getShortForm())
                    .contactDetails(stringBuilder.toString())
                    .build());
            bJabber = false;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        stringBuilder.append(new String(ch, start, length));
    }
}
