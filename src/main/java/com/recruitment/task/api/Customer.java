
package com.recruitment.task.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.omg.CORBA.RepositoryIdHelper;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Customer {
    private long id;
    private int age;
    private String name, surname, city;
    private List<Contact> contacts;
}
