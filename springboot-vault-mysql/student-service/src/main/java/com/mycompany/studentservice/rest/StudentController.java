package com.mycompany.studentservice.rest;

import com.mycompany.studentservice.model.Student;
import com.mycompany.studentservice.rest.dto.CreateStudentDto;
import com.mycompany.studentservice.rest.dto.StudentDto;
import com.mycompany.studentservice.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
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

@Slf4j
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    private final MapperFacade mapperFacade;
    private final Environment env;

    public StudentController(StudentService studentService, MapperFacade mapperFacade, Environment env) {
        this.studentService = studentService;
        this.mapperFacade = mapperFacade;
        this.env = env;
    }

    @GetMapping("/dbcredentials")
    public String getDBCredentials() {
        String username = env.getProperty("spring.datasource.username");
        String password = env.getProperty("spring.datasource.password");

        return String.format("%s/%s", username, password);
    }

    @GetMapping
    public List<StudentDto> getAllStudents() {
        log.info("Get all students");

        return studentService.getAllStudents()
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
