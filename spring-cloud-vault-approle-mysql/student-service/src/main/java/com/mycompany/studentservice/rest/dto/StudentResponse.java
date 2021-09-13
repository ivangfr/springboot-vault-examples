package com.mycompany.studentservice.rest.dto;

import lombok.Value;

@Value
public class StudentResponse {

    Long id;
    String firstName;
    String lastName;
    String email;
}
