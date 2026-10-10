package edu.bellevue.cis530.week5.repository;

import edu.bellevue.cis530.week5.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
