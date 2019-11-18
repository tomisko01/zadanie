package com.recruitment.task.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    private long id;
    private long customerId;
    private String contactDetails;
    private int intType;
}
