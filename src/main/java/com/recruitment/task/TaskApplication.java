package com.recruitment.task;

import com.recruitment.task.api.Contact;
import com.recruitment.task.api.Customer;
import com.recruitment.task.api.CustomerFromFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Stream;

@SpringBootApplication
public class TaskApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(TaskApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(TaskApplication.class, args);
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("Creating tables");

        jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE customers(" +
                "id SERIAL, name VARCHAR(255), surname VARCHAR(255), city VARCHAR(30), age VARCHAR(3))");

        jdbcTemplate.execute("DROP TABLE contacts IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE contacts(" +
                "id SERIAL, customerId VARCHAR (18), contactDetails VARCHAR(255), type VARCHAR(1))");

        // Split up the array of whole names into an array of first/last names
        /*List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        // Use a Java 8 stream to print out each tuple of the list
        splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));*/

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {
                boolean bname = false,
                        bsurname = false,
                        bage = false,
                        bcity = false,
                        bcontacts = false,
                        bunknown = false,
                        bemail = false,
                        bphone = false,
                        bjabber = false;


                public void startElement(String uri, String localName, String qName,
                                         Attributes attributes) throws SAXException {

                    System.out.println("Start Element :" + qName);

                    if (qName.equalsIgnoreCase("name")) {
                        bname = true;
                    }

                    if (qName.equalsIgnoreCase("surname")) {
                        bsurname = true;
                    }

                    if (qName.equalsIgnoreCase("age")) {
                        bage = true;
                    }

                    if (qName.equalsIgnoreCase("city")) {
                        bcity = true;
                    }

                    if (qName.equalsIgnoreCase("email")) {
                        bemail = true;
                    }

                    if (qName.equalsIgnoreCase("phone")) {
                        bphone = true;
                    }

                    if (qName.equalsIgnoreCase("jabber")) {
                        bjabber = true;
                    }

                    if (this.bcontacts && !qName.equalsIgnoreCase("email") && !qName.equalsIgnoreCase("phone") && !qName.equalsIgnoreCase("jabber")) {
                        bunknown = true;
                    }

                    if (qName.equalsIgnoreCase("contacts")) {
                        bcontacts = true;
                    }
                }

                public void endElement(String uri, String localName,
                                       String qName) throws SAXException {
                    System.out.println("End Element :" + qName);

                    if (qName.equalsIgnoreCase("contacts")) {
                        bcontacts = false;
                    }
                }

                public void characters(char ch[], int start, int length) throws SAXException {

                    CustomerFromFile customer = new CustomerFromFile();
                    if (bname) {
                        System.out.println("Name : " + new String(ch, start, length));
                        customer.setName(new String(ch, start, length));
                        bname = false;
                    }

                    if (bsurname) {
                        System.out.println("Surname : " + new String(ch, start, length));
                        customer.setSurname(new String(ch, start, length));
                        bsurname = false;
                    }

                    if (bage) {
                        System.out.println("Age : " + new String(ch, start, length));
                        customer.setAge(Integer.parseInt(new String(ch, start, length)));
                        bage = false;
                    }

                    if (bcity) {
                        System.out.println("City : " + new String(ch, start, length));
                        customer.setCity(new String(ch, start, length));
                        bcity = false;
                    }

                    if (Stream.of(customer).allMatch(Objects::nonNull)) {
                        int id = jdbcTemplate.update("INSERT INTO customers(name, surname, city, age) " +
                                "VALUES (?,?,?,?)", customer.getName(), customer.getSurname(), customer.getCity(), customer.getAge());
                        log.info(String.valueOf(id));
                    }

   /*                 if (bunknown) {
                        System.out.println("contact unknown : " + new String(ch, start, length));
                        contacts.add(Contact.builder()
                                .intType(ContactTypeEnum.UNKNOWN.getShortForm())
                                .contactDetails(new String(ch, start, length))
                                .build());
                        bunknown = false;
                    }

                    if (bphone) {
                        System.out.println("Phone : " + new String(ch, start, length));
                        contacts.add(Contact.builder()
                                .intType(ContactTypeEnum.PHONE.getShortForm())
                                .contactDetails(new String(ch, start, length))
                                .build());
                        bphone = false;
                    }

                    if (bemail) {
                        System.out.println("Email : " + new String(ch, start, length));
                        contacts.add(Contact.builder()
                                .intType(ContactTypeEnum.EMAIL.getShortForm())
                                .contactDetails(new String(ch, start, length))
                                .build());
                        bemail = false;
                    }

                    if (bjabber) {
                        System.out.println("Jabber : " + new String(ch, start, length));
                        contacts.add(Contact.builder()
                                .intType(ContactTypeEnum.JABBER.getShortForm())
                                .contactDetails(new String(ch, start, length))
                                .build());
                        bjabber = false;
                    }*/
                }
            };

            File directory = new File("./");
            System.out.println(directory.getAbsolutePath());
            File file = new File("./src/main/resources/dane-osoby.xml");
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");

            saxParser.parse(is, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        // Uses JdbcTemplate's batchUpdate operation to bulk load data
        jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name, city, age) " +
                "VALUES (?,?,'lublin',13)", splitUpNames);

        List<Object[]> contacts = Collections.singletonList("123 asd 1").stream()
                .map(a -> a.split(" "))
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate("INSERT INTO contacts(customerId, contactDetails, type) " +
                "VALUES (?,?,?)", contacts);


        log.info("Querying for customer records where first_name = 'Josh':");*/
        jdbcTemplate.query(
                "SELECT id, name, surname, age, city FROM customers",
                (rs, rowNum) -> Customer.builder()
                        .id(rs.getLong("id"))
                        .age(rs.getInt("age"))
                        .city(rs.getString("city"))
                        .surname(rs.getString("surname"))
                        .name(rs.getString("name"))
                        .build()
        ).forEach(customer -> log.info(customer.toString()));

        jdbcTemplate.query(
                "SELECT id,customerId, contactDetails, type FROM contacts ",
                (rs, rowNum) -> Contact.builder()
                        .id(rs.getLong("id"))
                        .customerId(rs.getInt("customerId"))
                        .contactDetails(rs.getString("contactDetails"))
                        .build()
        ).forEach(contact -> log.info(contact.toString()));
    }
}
