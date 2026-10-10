package edu.bellevue.cis530.week5.repository;

import edu.bellevue.cis530.week5.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
}
