package com.recruitment.task;

import com.recruitment.task.api.CustomerFromFile;
import com.recruitment.task.dao.CustomerDao;
import com.recruitment.task.service.XmlReaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class TaskApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(TaskApplication.class);

    private static final String XML = "xml";
    private static final String DEFAULT_XML_FILE_PATH = "./src/main/resources/dane-osoby.xml";

    public static void main(String[] args) {
        SpringApplication.run(TaskApplication.class, args);
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CustomerDao customerDao;

    @Autowired
    XmlReaderService xmlReader;

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
        String fileExtension = null;
        String fileArgument = null;

        if (args.length > 0) {
            fileArgument = args[0];
            int dotIndex = fileArgument.lastIndexOf(".");
            fileExtension = fileArgument.substring(dotIndex);
        }

        if (args.length == 0 || fileExtension.equalsIgnoreCase(XML)) {
            customersList = xmlReader.readCustomersFromFile(args.length == 0 ? DEFAULT_XML_FILE_PATH : fileArgument);
        }

        if (!customersList.isEmpty()) {
            customerDao.saveAll(customersList);
        }
    }
}
