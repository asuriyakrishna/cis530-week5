package edu.bellevue.cis530.week5.service;

import edu.bellevue.cis530.week5.controller.StudentRequest;
import edu.bellevue.cis530.week5.controller.StudentResponse;

import java.util.List;

public interface StudentService {

    StudentResponse create(StudentRequest request);

    List<StudentResponse> findAll();

    StudentResponse findById(Long id);

    StudentResponse update(Long id, StudentRequest request);

    void delete(Long id);
}
