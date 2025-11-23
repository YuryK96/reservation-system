package com.example.reservation.reservations.availability;

import com.example.reservation.reservations.ReservationController;
import com.example.reservation.reservations.ReservationStatus;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservation/availability")
public class ReservationAvailabilityController {

    private final ReservationAvailabilityService service;
    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);


    public ReservationAvailabilityController(ReservationAvailabilityService service) {
        this.service = service;
    }

    @PostMapping("/check")
    public ResponseEntity<CheckAvailabilityResponse> checkAvailability(
            @Valid @RequestBody CheckAvailabilityRequest request
    ) {
        log.info("Check availability request {}", request);

        boolean isAvailable = service.isReservationAvailable(
                request.roomId(),
                request.startDate(),
                request.endDate()
        );

        log.warn("Check availability request {} is {}", request, isAvailable);

        String message = isAvailable ? "Room available" : "Room unavailable";

        AvailabilityStatus status = isAvailable ? AvailabilityStatus.AVAILABLE : AvailabilityStatus.RESERVED;

        return ResponseEntity.status(HttpStatus.OK).body(new CheckAvailabilityResponse(message, status));

    }


}
