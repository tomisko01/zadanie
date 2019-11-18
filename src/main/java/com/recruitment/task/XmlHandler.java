package com.recruitment.task;

import com.recruitment.task.api.CustomerFromFile;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class XmlHandler extends DefaultHandler {
    private List<CustomerFromFile> customers = null;
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

        System.out.println("Start Element :" + qName);

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


        if (qName.equalsIgnoreCase("email")) {
            bEmail = true;
        }

        if (qName.equalsIgnoreCase("phone")) {
            bPhone = true;
        }

        if (qName.equalsIgnoreCase("jabber")) {
            bJabber = true;
        }

        if (this.bContacts && !qName.equalsIgnoreCase("email") && !qName.equalsIgnoreCase("phone") && !qName.equalsIgnoreCase("jabber")) {
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
        System.out.println("End Element :" + qName);
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
            // add Employee object to list
            customers.add(customer);
        }
/*
        if (qName.equalsIgnoreCase("contacts")) {
            bContacts = false;
        }*/
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        stringBuilder.append(new String(ch, start, length));

/*        if (Stream.of(customer).allMatch(Objects::nonNull)) {
            int id = jdbcTemplate.update("INSERT INTO customers(name, surname, city, age) " +
                    "VALUES (?,?,?,?)", customer.getName(), customer.getSurname(), customer.getCity(), customer.getAge());
            log.info(String.valueOf(id));
        }*/

   /*                 if (bUnknown) {
                        System.out.println("contact unknown : " + new String(ch, start, length));
                        contacts.add(Contact.builder()
                                .intType(ContactTypeEnum.UNKNOWN.getShortForm())
                                .contactDetails(new String(ch, start, length))
                                .build());
                        bUnknown = false;
                    }

                    if (bPhone) {
                        System.out.println("Phone : " + new String(ch, start, length));
                        contacts.add(Contact.builder()
                                .intType(ContactTypeEnum.PHONE.getShortForm())
                                .contactDetails(new String(ch, start, length))
                                .build());
                        bPhone = false;
                    }

                    if (bEmail) {
                        System.out.println("Email : " + new String(ch, start, length));
                        contacts.add(Contact.builder()
                                .intType(ContactTypeEnum.EMAIL.getShortForm())
                                .contactDetails(new String(ch, start, length))
                                .build());
                        bEmail = false;
                    }

                    if (bJabber) {
                        System.out.println("Jabber : " + new String(ch, start, length));
                        contacts.add(Contact.builder()
                                .intType(ContactTypeEnum.JABBER.getShortForm())
                                .contactDetails(new String(ch, start, length))
                                .build());
                        bJabber = false;
                    }*/
    }
}
