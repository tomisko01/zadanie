package com.recruitment.task.dao.impl;

import com.recruitment.task.api.CustomerFromFile;
import com.recruitment.task.dao.CustomerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Component
public class CustomerDaoImpl implements CustomerDao {
    final private String queryInsertCustomer = "INSERT INTO customers(name, surname, city, age) VALUES (?, ?, ?, ?)";
    final private String queryInsertContact = "INSERT INTO contacts(customerId, contactDetails, type) VALUES (?, ?, ?)";

    private KeyHolder keyHolder = new GeneratedKeyHolder();

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void saveAll(List<CustomerFromFile> customersList) {

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
