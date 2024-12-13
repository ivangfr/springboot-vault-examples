package com.ivanfranchin.studentservice.rest;

import com.ivanfranchin.studentservice.mapper.StudentMapper;
import com.ivanfranchin.studentservice.model.Student;
import com.ivanfranchin.studentservice.rest.dto.CreateStudentRequest;
import com.ivanfranchin.studentservice.rest.dto.StudentResponse;
import com.ivanfranchin.studentservice.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    private final Environment environment;
    private final StudentMapper studentMapper;

    public StudentController(StudentService studentService, Environment environment, StudentMapper studentMapper) {
        this.studentService = studentService;
        this.environment = environment;
        this.studentMapper = studentMapper;
    }

    @GetMapping("/dbcredentials")
    public String getDBCredentials() {
        return String.format("%s/%s",
                environment.getProperty("spring.datasource.username"),
                environment.getProperty("spring.datasource.password"));
    }

    @GetMapping("/secretMessage")
    public String getSecretMessage() {
        return environment.getProperty("message");
    }

    @GetMapping
    public List<StudentResponse> getStudents() {
        return studentService.getStudents()
                .stream()
                .map(studentMapper::toStudentResponse)
                .toList();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public StudentResponse createStudent(@Valid @RequestBody CreateStudentRequest createStudentRequest) {
        Student student = studentMapper.toStudent(createStudentRequest);
        student = studentService.saveStudent(student);
        return studentMapper.toStudentResponse(student);
    }
}
