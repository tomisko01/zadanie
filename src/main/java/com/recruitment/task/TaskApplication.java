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
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        List<CustomerFromFile> customersList = new ArrayList<>();
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            XmlHandler handler = new XmlHandler();

            File directory = new File("./");
            System.out.println(directory.getAbsolutePath());
            File file = new File("./src/main/resources/dane-osoby.xml");
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");

            saxParser.parse(is, handler);
            customersList = handler.getCustomers();

            for (CustomerFromFile customer : customersList) {
                System.out.println(customer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!customersList.isEmpty()) {
            String queryInsertCustomer = "INSERT INTO customers(name, surname, city, age) VALUES (?, ?, ?, ?)";
            String queryInsertContact = "INSERT INTO contacts(customerId, contactDetails, type) VALUES (?, ?, ?)";

            try (Connection connection = DataSourceUtils.getConnection(Objects.requireNonNull(jdbcTemplate.getDataSource()))) {
                connection.setAutoCommit(false);

                customersList.forEach(customer -> {
                    jdbcTemplate.update(
                            new PreparedStatementCreator() {
                                @Override
                                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                                    PreparedStatement ps = connection.prepareStatement(queryInsertCustomer, Statement.RETURN_GENERATED_KEYS);
                                    ps.setString(1, customer.getName());
                                    ps.setString(2, customer.getSurname());
                                    ps.setString(3, customer.getCity());
                                    ps.setInt(4, customer.getAge());
                                    return ps;
                                }
                            }, keyHolder);

                    int customerId = (int) keyHolder.getKey();

                    customer.getContactFromFile().forEach(contact -> {
                        jdbcTemplate.update(queryInsertContact, customerId, contact.getContactDetails(), contact.getIntType());
                    });
                });
                connection.commit();
            }
        }

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
