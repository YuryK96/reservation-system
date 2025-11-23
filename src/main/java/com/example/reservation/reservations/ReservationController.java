package com.example.reservation.reservations;


import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    public ResponseEntity<List<Reservation>> getAllReservations(
            @RequestParam(name = "roomId", required = false) Long roomId,
            @RequestParam(name= "userId", required = false) Long userId,
            @RequestParam(name="pageSize", required = false) Integer pageSize,
            @RequestParam(name="pageNumber", required = false) Integer pageNumber
    ) {

        ReservationSearchByFilter filter = new ReservationSearchByFilter(roomId, userId, pageSize, pageNumber);

        return ResponseEntity.ok(reservationService.searchAllByFilter(filter));
    }

    @PostMapping()
    public ResponseEntity<Reservation> createReservation(@RequestBody @Valid Reservation reservationCreate) {
        log.info("createReservation");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("test", "123")
                .body(reservationService.createReservation(reservationCreate));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation(@PathVariable("id") Long id) {
        log.info("approveReservation");
        return ResponseEntity
                .status(HttpStatus.OK).body(reservationService.approveReservation(id));


    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable("id") Long id, @RequestBody @Valid Reservation reservationUpdate) {
        log.info("updateReservation");
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("test", "123")
                .body(reservationService.updateReservation(id, reservationUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") Long id) {
        log.info("deleteReservation");


        reservationService.deleteReservation(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT).build();

    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable("id") Long id) {
        log.info("cancelReservation");


        reservationService.cancelReservation(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT).build();

    }
}
