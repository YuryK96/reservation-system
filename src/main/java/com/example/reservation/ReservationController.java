package com.example.reservation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("reservation")
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;


    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable("id") Long id) {
        log.info("Called getReservationById: id= " + id);
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping()
    public ResponseEntity<List<Reservation>> getAllReservations() {

        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @PostMapping()
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservationCreate) {
        log.info("createReservation");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("test", "123")
                .body(reservationService.createReservation(reservationCreate));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(@PathVariable("id") Long id) {
        log.info("approveReservation");

        try {
            return ResponseEntity
                    .status(HttpStatus.OK).body(reservationService.approveReservation(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable("id") Long id, @RequestBody Reservation reservationUpdate) {
        log.info("updateReservation");
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("test", "123")
                .body(reservationService.updateReservation(id, reservationUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
        log.info("deleteReservation");

        try {
            reservationService.deleteReservation(id);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT).build();
        } catch (NoSuchFieldError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }


    }
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable("id") Long id) {
        log.info("cancelReservation");

        try {
            reservationService.cancelReservation(id);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT).build();
        } catch (NoSuchFieldError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }


    }
}
