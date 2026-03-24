package com.dustintran.appointmentscheduler.service;

import com.dustintran.appointmentscheduler.model.CourseSection;
import com.dustintran.appointmentscheduler.repository.CourseSectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseSectionService {
    private final CourseSectionRepository sections;
    public List<CourseSection> findAll() {
        return sections.findAll();
    }

    public CourseSection create(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Section name is required");
        }

        if (sections.findByName(name.trim()).isPresent()) {
            throw new IllegalArgumentException("Section name already exists");
        }

        return sections.save(CourseSection.builder()
                        .name(name.trim())
                        .build());
    }

    public CourseSection findById(Long id) {
    return sections.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Section not found"));
}
}
