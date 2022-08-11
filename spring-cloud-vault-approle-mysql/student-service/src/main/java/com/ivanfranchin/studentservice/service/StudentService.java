package com.ivanfranchin.studentservice.service;

import com.ivanfranchin.studentservice.model.Student;

import java.util.List;

public interface StudentService {

    List<Student> getStudents();

    Student saveStudent(Student student);
}
