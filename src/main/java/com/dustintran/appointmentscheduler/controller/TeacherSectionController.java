package com.dustintran.appointmentscheduler.controller;

import com.dustintran.appointmentscheduler.repository.StudentGroupRepository;
import com.dustintran.appointmentscheduler.service.CourseSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class TeacherSectionController {

    private final CourseSectionService sectionService;
    private final StudentGroupRepository groupRepository;

    @GetMapping("/teacher/sections")
    public String page(Model model, Authentication auth) {
        model.addAttribute("sections", sectionService.findAll());
        model.addAttribute("groups", groupRepository.findAll());
        model.addAttribute("displayName", auth.getName());

        return "teacher-sections";
    }

    @PostMapping("/teacher/sections")
    public String createSection(@RequestParam String name, Model model) {
        try {
            sectionService.create(name);
            return "redirect:/teacher/sections";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("sections", sectionService.findAll());
            model.addAttribute("groups", groupRepository.findAll());
            model.addAttribute("error", ex.getMessage());
            return "teacher-sections";
        }
    }

    @PostMapping("/teacher/groups")
    public String createGroup(@RequestParam Long sectionId,
                              @RequestParam String name,
                              Model model) {
        try {
            var section = sectionService.findById(sectionId);

            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Group name is required");
            }

            groupRepository.save(
                com.dustintran.appointmentscheduler.model.StudentGroup.builder()
                    .name(name.trim())
                    .section(section)
                    .build()
            );

            return "redirect:/teacher/sections";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("sections", sectionService.findAll());
            model.addAttribute("groups", groupRepository.findAll());
            model.addAttribute("error", ex.getMessage());
            return "teacher-sections";
        }
    }
}