package edu.bellevue.cis530.week5.service;

import edu.bellevue.cis530.week5.entity.Student;
import edu.bellevue.cis530.week5.controller.StudentResponse;

import java.util.List;

public interface StudentService {

    StudentResponse createStudent(Student student);

    List<StudentResponse> getAllStudents();

    StudentResponse getStudentById(Long id);

    StudentResponse updateStudent(Long id, Student student);

    void deleteStudent(Long id);

    StudentResponse.Profile getProfileByStudentId(Long id);

    List<StudentResponse> getStudentsByMajor(String major);

    List<StudentResponse> getStudentsByEnrollmentYear(Integer year);
}
