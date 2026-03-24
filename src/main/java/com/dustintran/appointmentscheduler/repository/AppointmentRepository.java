package com.dustintran.appointmentscheduler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dustintran.appointmentscheduler.model.Appointment;
import com.dustintran.appointmentscheduler.model.StudentGroup;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    long countBySlot_Id(Long slotId);
    boolean  existsBySlot_IdAndStudent_Username(Long slotId, String username);
    List<Appointment> findByStudent_UsernameOrderBySlot_StartTimeAsc(String username);    
    boolean existsBySlot_IdAndStudent_Group(Long slotId, StudentGroup group);

    @Query("""
        select a.slot.id, count(a)
        from Appointment a
        where a.slot.id in :slotIds
        group by a.slot.id
    """)
    List<Object[]>  countBookingsForSlotIds(List<Long> slotIds);
}
