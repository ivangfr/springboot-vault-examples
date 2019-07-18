package com.mycompany.studentservice.rest;

import com.mycompany.studentservice.model.Student;
import com.mycompany.studentservice.rest.dto.CreateStudentDto;
import com.mycompany.studentservice.rest.dto.StudentDto;
import com.mycompany.studentservice.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Value;
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

@Slf4j
@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private final StudentService studentService;
    private final MapperFacade mapperFacade;

    public StudentController(StudentService studentService, MapperFacade mapperFacade) {
        this.studentService = studentService;
        this.mapperFacade = mapperFacade;
    }

    @GetMapping("/dbcredentials")
    public String getDBCredentials() {
        log.info("Get database credentials");

        return String.format("%s/%s", username, password);
    }

    @GetMapping
    public List<StudentDto> getStudents() {
        log.info("Get students");

        return studentService.getStudents()
                .stream()
                .map(student -> mapperFacade.map(student, StudentDto.class))
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public StudentDto createStudent(@Valid @RequestBody CreateStudentDto createStudentDto) {
        log.info("Post request to create student: {}", createStudentDto);

        Student student = mapperFacade.map(createStudentDto, Student.class);
        student = studentService.saveStudent(student);

        return mapperFacade.map(student, StudentDto.class);
    }

}
