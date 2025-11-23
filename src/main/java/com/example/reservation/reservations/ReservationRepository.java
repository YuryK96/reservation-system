package com.example.reservation.reservations;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

//    List<ReservationEntity> findAllByStatusIs(ReservationStatus status);


    @Query("SELECT r FROM ReservationEntity AS r WHERE r.status = :status")
    List<ReservationEntity> findAllByStatusIs(

            @Param("status") ReservationStatus status);


    @Query("""
            SELECT r.id FROM ReservationEntity AS r 
            WHERE r.roomId = :roomId 
            AND r.status = :status
            AND r.startDate >= :startDate 
            AND r.endDate <= :endDate
            """)
    List<Long> findIdsBetweenStartAndEndDatesByRoomId(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") ReservationStatus status);


    @Query("""
            SELECT r FROM ReservationEntity AS r 
            WHERE (:roomId IS NULL OR r.roomId = :roomId)  
            AND (:userId IS NULL OR r.userId = :userId)    
            """)
    List<ReservationEntity> searchAllByFilter(
            @Param("userId") Long userId,
            @Param("roomId") Long roomId,
            @PageableDefault(page = 0, size = 10) Pageable pageable);

    @Modifying
    @Query("""
            UPDATE ReservationEntity r 
            SET r.status = :status 
            WHERE r.id = :id
            """)
    void setStatus(
            @Param("id") Long id,
            @Param("status") ReservationStatus reservationStatus
    );

}
