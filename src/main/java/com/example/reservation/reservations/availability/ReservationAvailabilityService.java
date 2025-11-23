package com.example.reservation.reservations.availability;

import com.example.reservation.reservations.ReservationRepository;
import com.example.reservation.reservations.ReservationService;
import com.example.reservation.reservations.ReservationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationAvailabilityService {
    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository repository;

    public ReservationAvailabilityService(ReservationRepository repository) {
        this.repository = repository;
    }

    public boolean isReservationAvailable(Long roomId, LocalDate startDate, LocalDate endDate) {
        List<Long> reservationIds = repository.findIdsBetweenStartAndEndDatesByRoomId(roomId, startDate, endDate, ReservationStatus.APPROVED);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Incorrect dates");
        }


        if (reservationIds.isEmpty()) {
            return true;
        }

        log.warn("Conflict with ids : " + reservationIds);
        return false;

    }

}
