package edu.bellevue.cis530.week5.service.impl;

import edu.bellevue.cis530.week5.controller.StudentProfileRequest;
import edu.bellevue.cis530.week5.controller.StudentRequest;
import edu.bellevue.cis530.week5.controller.StudentResponse;
import edu.bellevue.cis530.week5.entity.Student;
import edu.bellevue.cis530.week5.entity.StudentProfile;
import edu.bellevue.cis530.week5.exception.ResourceNotFoundException;
import edu.bellevue.cis530.week5.repository.StudentRepository;
import edu.bellevue.cis530.week5.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public StudentResponse create(StudentRequest request) {
        Student student = new Student();
        applyRequest(student, request);
        return toResponse(studentRepository.save(student));
    }

    @Transactional(readOnly = true)
    @Override
    public List<StudentResponse> findAll() {
        return studentRepository.findAll().stream()
                .map(StudentServiceImpl::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public StudentResponse findById(Long id) {
        return toResponse(findStudent(id));
    }

    @Override
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = findStudent(id);
        applyRequest(student, request);
        return toResponse(studentRepository.save(student));
    }

    @Override
    public void delete(Long id) {
        studentRepository.delete(findStudent(id));
    }

    private Student findStudent(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    private static void applyRequest(Student student, StudentRequest request) {
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());

        StudentProfile profile = student.getProfile();
        if (profile == null) {
            profile = new StudentProfile();
            student.setProfile(profile);
        }
        applyProfileRequest(profile, request.getProfile());
    }

    private static void applyProfileRequest(StudentProfile profile, StudentProfileRequest request) {
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setAddress(request.getAddress());
        profile.setMajor(request.getMajor());
        profile.setDateOfBirth(request.getDateOfBirth());
    }

    private static StudentResponse toResponse(Student student) {
        StudentProfile profile = student.getProfile();
        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                new StudentResponse.Profile(
                        profile.getId(),
                        profile.getPhoneNumber(),
                        profile.getAddress(),
                        profile.getMajor(),
                        profile.getDateOfBirth()));
    }
}
