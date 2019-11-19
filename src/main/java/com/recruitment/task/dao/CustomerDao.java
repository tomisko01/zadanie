package com.recruitment.task.dao;

import com.recruitment.task.api.CustomerFromFile;

import java.util.List;

public interface CustomerDao {

    /**
     * saves all customers with associated contacts
     *
     * @param customersList
     */

    void saveAll(List<CustomerFromFile> customersList);
}
