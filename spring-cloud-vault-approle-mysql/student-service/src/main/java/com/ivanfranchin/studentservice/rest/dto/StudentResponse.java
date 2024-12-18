package com.ivanfranchin.studentservice.rest.dto;

import com.ivanfranchin.studentservice.model.Student;

public record StudentResponse(Long id, String firstName, String lastName, String email) {

    public static StudentResponse from(Student student) {
        return new StudentResponse(student.getId(), student.getFirstName(), student.getLastName(), student.getEmail());
    }
}
