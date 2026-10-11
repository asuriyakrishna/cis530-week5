package edu.bellevue.cis530.week5.service.impl;

import edu.bellevue.cis530.week5.controller.StudentResponse;
import edu.bellevue.cis530.week5.entity.Student;
import edu.bellevue.cis530.week5.entity.StudentProfile;
import edu.bellevue.cis530.week5.exception.DuplicateStudentEmailException;
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
    public StudentResponse createStudent(Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateStudentEmailException(student.getEmail());
        }
        return toResponse(studentRepository.save(student));
    }

    @Transactional(readOnly = true)
    @Override
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(StudentServiceImpl::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public StudentResponse getStudentById(Long id) {
        return toResponse(findStudent(id));
    }

    @Override
    public StudentResponse updateStudent(Long id, Student updatedStudent) {
        Student student = findStudent(id);
        if (studentRepository.existsByEmailAndIdNot(updatedStudent.getEmail(), id)) {
            throw new DuplicateStudentEmailException(updatedStudent.getEmail());
        }
        student.setFirstName(updatedStudent.getFirstName());
        student.setLastName(updatedStudent.getLastName());
        student.setEmail(updatedStudent.getEmail());
        student.setMajor(updatedStudent.getMajor());
        student.setGpa(updatedStudent.getGpa());
        student.setEnrollmentYear(updatedStudent.getEnrollmentYear());

        StudentProfile updatedProfile = updatedStudent.getProfile();
        StudentProfile profile = student.getProfile();
        if (profile == null) {
            student.setProfile(updatedProfile);
        } else {
            profile.setPhone(updatedProfile.getPhone());
            profile.setAddress(updatedProfile.getAddress());
            profile.setCity(updatedProfile.getCity());
            profile.setState(updatedProfile.getState());
            profile.setZip(updatedProfile.getZip());
            profile.setEmergencyContactName(updatedProfile.getEmergencyContactName());
            profile.setEmergencyContactPhone(updatedProfile.getEmergencyContactPhone());
        }
        return toResponse(studentRepository.save(student));
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.delete(findStudent(id));
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse.Profile getProfileByStudentId(Long id) {
        return toResponse(findStudent(id)).profile();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsByMajor(String major) {
        return studentRepository.findByMajor(major).stream()
                .map(StudentServiceImpl::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsByEnrollmentYear(Integer year) {
        return studentRepository.findByEnrollmentYear(year).stream()
                .map(StudentServiceImpl::toResponse)
                .toList();
    }

    private Student findStudent(Long id) {
        return studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    private static StudentResponse toResponse(Student student) {
        StudentProfile profile = student.getProfile();
        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getMajor(),
                student.getGpa(),
                student.getEnrollmentYear(),
                new StudentResponse.Profile(
                        profile.getId(),
                        profile.getPhone(),
                        profile.getAddress(),
                        profile.getCity(),
                        profile.getState(),
                        profile.getZip(),
                        profile.getEmergencyContactName(),
                        profile.getEmergencyContactPhone()));
    }
}
