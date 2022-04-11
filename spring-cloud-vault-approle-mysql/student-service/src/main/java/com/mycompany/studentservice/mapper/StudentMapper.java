package com.mycompany.studentservice.mapper;

import com.mycompany.studentservice.model.Student;
import com.mycompany.studentservice.rest.dto.CreateStudentRequest;
import com.mycompany.studentservice.rest.dto.StudentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "id", ignore = true)
    Student toStudent(CreateStudentRequest createStudentRequest);

    StudentResponse toStudentResponse(Student student);
}
