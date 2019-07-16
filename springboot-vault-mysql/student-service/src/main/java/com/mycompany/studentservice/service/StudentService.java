package com.mycompany.studentservice.service;

import com.mycompany.studentservice.model.Student;

import java.util.List;

public interface StudentService {

    List<Student> getAllStudents();

    Student saveStudent(Student student);

}
