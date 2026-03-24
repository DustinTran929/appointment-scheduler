package com.dustintran.appointmentscheduler.repository;

import com.dustintran.appointmentscheduler.model.CourseSection;
import com.dustintran.appointmentscheduler.model.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {
    List<StudentGroup> findBySectionOrderByNameAsc(CourseSection section);

    Optional<StudentGroup> findBySectionAndName(CourseSection section, String name);
}