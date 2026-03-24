package com.dustintran.appointmentscheduler.repository;

import com.dustintran.appointmentscheduler.model.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
    Optional<CourseSection> findByName(String name);
}