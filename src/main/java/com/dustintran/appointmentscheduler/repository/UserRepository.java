package com.dustintran.appointmentscheduler.repository;

import com.dustintran.appointmentscheduler.model.CourseSection;
import com.dustintran.appointmentscheduler.model.StudentGroup;
import com.dustintran.appointmentscheduler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByRoleOrderByUsernameAsc(String role);

    List<User> findByRoleAndSectionOrderByUsernameAsc(String role, CourseSection section);

    List<User> findByRoleAndGroupOrderByUsernameAsc(String role, StudentGroup group);

    long countByGroup(StudentGroup group);
}