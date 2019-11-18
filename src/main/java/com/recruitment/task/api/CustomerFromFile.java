
package com.recruitment.task.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomerFromFile implements Serializable {
    private int age;
    private String name, surname, city;
    private List<ContactFromFile> contactFromFile;
}
