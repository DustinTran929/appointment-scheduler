package com.dustintran.appointmentscheduler.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.dustintran.appointmentscheduler.model.Appointment;
import com.dustintran.appointmentscheduler.model.AppointmentSlot;
import com.dustintran.appointmentscheduler.model.User;
import com.dustintran.appointmentscheduler.repository.AppointmentRepository;
import com.dustintran.appointmentscheduler.repository.AppointmentSlotRepository;
import com.dustintran.appointmentscheduler.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    
    private final AppointmentRepository appointments;
    private final AppointmentSlotRepository slots;
    private final UserRepository users;

    public Appointment bookSlot(Long slotId, String username) {
        AppointmentSlot slot = slots.findById(slotId)
            .orElseThrow(() -> new IllegalArgumentException("Slot not found"));

        if (slot.getStatus() != AppointmentSlot.Status.ACTIVE) {
            throw new IllegalArgumentException("This slot is not active");
        }

        if (!slot.getStartTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("You can only book future slots");
        }

        User student = users.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        if (!"STUDENT".equals(student.getRole())) {
            throw new IllegalArgumentException("Only students can book appointments");
        }

        validateEligibility(slot, student);

        if (slot.getType() == AppointmentSlot.Type.INDIVIDUAL) {
            boolean alreadyBooked = appointments.existsBySlot_IdAndStudent_Username(slotId, username);
            if (alreadyBooked) {
                throw new IllegalArgumentException("You already booked this slot");
            }
        } else {
            if (student.getGroup() == null) {
                throw new IllegalArgumentException("You are not assigned to a group");
            }

            boolean groupAlreadyBooked = appointments.existsBySlot_IdAndStudent_Group(slotId, student.getGroup());
            if (groupAlreadyBooked) {
                throw new IllegalArgumentException("Your group already booked this slot");
            }
        }

        long bookedCount = appointments.countBySlot_Id(slotId);
        if (bookedCount >= slot.getCapacity()) {
            throw new IllegalArgumentException("This slot is already full");
        }

        Appointment appointment = Appointment.builder()
                .slot(slot)
                .student(student)
                .createdAt(LocalDateTime.now())
                .build();

        return appointments.save(appointment);
    }

    private void validateEligibility(AppointmentSlot slot, User student) {
        if (student.getSection() == null) {
            throw new IllegalArgumentException("You are not assgined to a section");
        }

        if (!student.getSection().getId().equals(slot.getSection().getId())) {
            throw new IllegalArgumentException("You are not allowed to book this slot");
        }

        if (slot.getTargetScope() == AppointmentSlot.TargetScope.SPECIFIC_GROUP) {
            if (student.getGroup() == null) {
                throw new IllegalArgumentException("You are not assigned to a group");
            }

            if (slot.getGroup() == null || !student.getGroup().getId().equals(slot.getGroup().getId())) {
                throw new IllegalArgumentException("This slot is reserved for another group");
            }
        }
    }

    public List<Appointment> myAppointments(String username) {
        return appointments.findByStudent_UsernameOrderBySlot_StartTimeAsc(username);
    }

    public Map<Long, Long> countBookingsForSlots(List<Long> slotIds) {
        Map<Long, Long> result = new HashMap<>();
        if (slotIds == null || slotIds.isEmpty()) {
            return result;
        }
        for (Object[] row : appointments.countBookingsForSlotIds(slotIds)) {
            result.put((Long) row[0], (Long) row[1]);
        }
        return result;
    }
}