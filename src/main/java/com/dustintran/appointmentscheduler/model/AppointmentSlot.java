package com.dustintran.appointmentscheduler.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name="appointment_slots",
    indexes = {
        @Index(name = "idx_slot_status_start", columnList = "status,startTime")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentSlot {
    public enum Type { INDIVIDUAL, GROUP}
    public enum Status { ACTIVE, CANCELLED}
    public enum TargetScope { ENTIRE_SECTION, SPECIFIC_GROUP }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Type type;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(nullable=false)
    private LocalDateTime startTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(nullable=false)
    private LocalDateTime endTime;

    @Column(nullable=false)
    private String location;

    @Column(nullable=false)
    private int capacity;   // 1 for INDIVIDUAL, >1 for GROUP

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private CourseSection section;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetScope targetScope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private StudentGroup group;
}
