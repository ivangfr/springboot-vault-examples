package com.ivanfranchin.studentservice.rest;

import com.ivanfranchin.studentservice.model.Student;
import com.ivanfranchin.studentservice.repository.StudentRepository;
import com.ivanfranchin.studentservice.rest.dto.CreateStudentRequest;
import com.ivanfranchin.studentservice.rest.dto.StudentResponse;
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

    private final StudentRepository studentRepository;
    private final Environment environment;

    public StudentController(StudentRepository studentRepository, Environment environment) {
        this.studentRepository = studentRepository;
        this.environment = environment;
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
        return studentRepository.findAll()
                .stream()
                .map(this::toStudentResponse)
                .toList();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public StudentResponse createStudent(@Valid @RequestBody CreateStudentRequest createStudentRequest) {
        Student student = studentRepository.save(toStudent(createStudentRequest));
        return toStudentResponse(student);
    }

    private Student toStudent(CreateStudentRequest createStudentRequest) {
        return new Student(createStudentRequest.firstName(), createStudentRequest.lastName(), createStudentRequest.email());
    }

    private StudentResponse toStudentResponse(Student student) {
        return new StudentResponse(student.getId(), student.getFirstName(), student.getLastName(), student.getEmail());
    }
}
