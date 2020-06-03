package com.mycompany.studentservice.rest;

import com.mycompany.studentservice.mapper.StudentMapper;
import com.mycompany.studentservice.model.Student;
import com.mycompany.studentservice.rest.dto.CreateStudentDto;
import com.mycompany.studentservice.rest.dto.StudentDto;
import com.mycompany.studentservice.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    private final Environment environment;
    private final StudentMapper studentMapper;

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
    public List<StudentDto> getStudents() {
        return studentService.getStudents()
                .stream()
                .map(studentMapper::toStudentDto)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public StudentDto createStudent(@Valid @RequestBody CreateStudentDto createStudentDto) {
        Student student = studentMapper.toStudent(createStudentDto);
        student = studentService.saveStudent(student);
        return studentMapper.toStudentDto(student);
    }

}
