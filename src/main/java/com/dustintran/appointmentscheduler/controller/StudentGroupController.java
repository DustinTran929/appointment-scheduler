package com.dustintran.appointmentscheduler.controller;

import com.dustintran.appointmentscheduler.model.CourseSection;
import com.dustintran.appointmentscheduler.model.StudentGroup;
import com.dustintran.appointmentscheduler.model.User;
import com.dustintran.appointmentscheduler.repository.CourseSectionRepository;
import com.dustintran.appointmentscheduler.repository.StudentGroupRepository;
import com.dustintran.appointmentscheduler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class StudentGroupController {

    private final UserRepository userRepository;
    private final CourseSectionRepository sectionRepository;
    private final StudentGroupRepository groupRepository;

    @GetMapping("/student/groups")
    public String page(Model model, Authentication auth) {
        User student = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        List<CourseSection> sections = sectionRepository.findAll();

        Map<CourseSection, List<StudentGroup>> groupsBySection = new LinkedHashMap<>();
        Map<Long, Long> groupCounts = new LinkedHashMap<>();

        for (CourseSection section : sections) {
            List<StudentGroup> groups = groupRepository.findBySectionOrderByNameAsc(section);
            groupsBySection.put(section, groups);

            for (StudentGroup group : groups) {
                groupCounts.put(group.getId(), userRepository.countByGroup(group));
            }
        }

        model.addAttribute("student", student);
        model.addAttribute("groupsBySection", groupsBySection);
        model.addAttribute("groupCounts", groupCounts);

        return "student-groups";
    }

    @PostMapping("/student/groups/join")
    public String joinGroup(@RequestParam Long groupId, Authentication auth) {
        User student = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        student.setSection(group.getSection());
        student.setGroup(group);

        userRepository.save(student);
        return "redirect:/student/groups";
    }

    @PostMapping("/student/groups/leave")
    public String leaveGroup(Authentication auth) {
        User student = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        student.setGroup(null);
        userRepository.save(student);

        return "redirect:/student/groups";
    }
}