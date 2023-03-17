package com.ivanfranchin.studentservice.mapper;

import com.ivanfranchin.studentservice.model.Student;
import com.ivanfranchin.studentservice.rest.dto.CreateStudentRequest;
import com.ivanfranchin.studentservice.rest.dto.StudentResponse;

public interface StudentMapper {

    Student toStudent(CreateStudentRequest createStudentRequest);

    StudentResponse toStudentResponse(Student student);
}
