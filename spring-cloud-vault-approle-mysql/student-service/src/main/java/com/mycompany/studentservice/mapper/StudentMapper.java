package com.mycompany.studentservice.mapper;

import com.mycompany.studentservice.model.Student;
import com.mycompany.studentservice.rest.dto.CreateStudentDto;
import com.mycompany.studentservice.rest.dto.StudentDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    Student toStudent(CreateStudentDto createStudentDto);

    StudentDto toStudentDto(Student student);

}
