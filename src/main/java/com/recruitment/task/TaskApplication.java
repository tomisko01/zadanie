package com.recruitment.task;

import com.recruitment.task.api.Contact;
import com.recruitment.task.api.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
                "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255), city VARCHAR(30), age VARCHAR(3))");

        jdbcTemplate.execute("DROP TABLE contacts IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE contacts(" +
                "id SERIAL, customerId VARCHAR (18), contactDetails VARCHAR(255), type VARCHAR(1))");

        // Split up the array of whole names into an array of first/last names
        List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        // Use a Java 8 stream to print out each tuple of the list
        splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));

        // Uses JdbcTemplate's batchUpdate operation to bulk load data
        jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name, city, age) " +
                "VALUES (?,?,'lublin',13)", splitUpNames);

        List<Object[]> contacts = Collections.singletonList("123 asd 1").stream()
        .map(a-> a.split(" "))
        .collect(Collectors.toList());

        jdbcTemplate.batchUpdate("INSERT INTO contacts(customerId, contactDetails, type) " +
                "VALUES (?,?,?)", contacts);


        log.info("Querying for customer records where first_name = 'Josh':");
        jdbcTemplate.query(
                "SELECT id, first_name, last_name, age, city FROM customers WHERE first_name = ?", new Object[]{"Josh"},
                (rs, rowNum) -> Customer.builder()
                                        .id(rs.getLong("id"))
                                        .age(rs.getInt("age"))
                                        .city(rs.getString("city"))
                .lastName(rs.getString("last_name"))
                .firstName(rs.getString("first_name"))
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
