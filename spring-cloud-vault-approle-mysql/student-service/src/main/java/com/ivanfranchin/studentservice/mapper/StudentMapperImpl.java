package com.ivanfranchin.studentservice.mapper;

import com.ivanfranchin.studentservice.model.Student;
import com.ivanfranchin.studentservice.rest.dto.CreateStudentRequest;
import com.ivanfranchin.studentservice.rest.dto.StudentResponse;
import org.springframework.stereotype.Service;

@Service
public class StudentMapperImpl implements StudentMapper {

    @Override
    public Student toStudent(CreateStudentRequest createStudentRequest) {
        if (createStudentRequest == null) {
            return null;
        }
        Student student = new Student();
        student.setFirstName(createStudentRequest.getFirstName());
        student.setLastName(createStudentRequest.getLastName());
        student.setEmail(createStudentRequest.getEmail());
        return student;
    }

    @Override
    public StudentResponse toStudentResponse(Student student) {
        if (student == null) {
            return null;
        }
        return new StudentResponse(student.getId(), student.getFirstName(), student.getLastName(), student.getEmail());
    }
}
