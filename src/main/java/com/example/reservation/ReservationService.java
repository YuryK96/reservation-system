package com.example.reservation;


import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservationService {

    private final Map<Long, Reservation> reservationMap;
    private final AtomicLong idCounter;

    public ReservationService() {
        reservationMap = new HashMap<>();
        idCounter = new AtomicLong();
    }

    public Reservation getReservationById(Long id) {

        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found reservation by id = " + id);
        }

        return reservationMap.get(id);
    }

    public Reservation createReservation(Reservation createReservation) {
        if (createReservation.id() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }

        if (createReservation.status() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }


        Reservation newReservation = new Reservation(
                idCounter.incrementAndGet(),
                createReservation.userId(),
                createReservation.roomId(),
                createReservation.startDate(),
                createReservation.endDate(),
                ReservationStatus.PENDING
        );

        reservationMap.put(newReservation.id(), newReservation);
        return newReservation;
    }

    public Reservation updateReservation(Long id, Reservation updateReservation) {
        if (id == null) {
            throw new IllegalArgumentException("Id required");
        }

        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found reservation by id = " + id);
        }

        Reservation foundReservation = reservationMap.get(id);

        if (foundReservation.status() == ReservationStatus.APPROVED || foundReservation.status() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("can't change reservation status");
        }

        Reservation updatedReservation = new Reservation(
                foundReservation.id(),
                updateReservation.userId(),
                updateReservation.roomId(),
                updateReservation.startDate(),
                updateReservation.endDate(),
                ReservationStatus.PENDING
        );

        reservationMap.put(id, updatedReservation);
        return foundReservation;


    }

    public Reservation approveReservation(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id required");
        }

        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found reservation by id = " + id);
        }

        Reservation foundReservation = reservationMap.get(id);

        if (foundReservation.status() == ReservationStatus.APPROVED || foundReservation.status() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("can't change reservation status");
        }

        if (isReservationConflict(foundReservation)) {
            throw new IllegalStateException("conflict room");

        }

        Reservation updatedReservation = new Reservation(
                foundReservation.id(),
                foundReservation.userId(),
                foundReservation.roomId(),
                foundReservation.startDate(),
                foundReservation.endDate(),
                ReservationStatus.APPROVED
        );

        reservationMap.put(id, updatedReservation);
        return foundReservation;
    }

    public void deleteReservation(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id required");
        }

        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found reservation by id = " + id);
        }

        reservationMap.remove(id);


    }

    public List<Reservation> getAllReservations() {
        return reservationMap.values().stream().toList();
    }

    private boolean isReservationConflict(Reservation reservation) {
        return reservationMap
                .values()
                .stream()
                .anyMatch(r -> r.roomId() != reservation.roomId()
                        && r.id() == reservation.id()
                        && !(r.startDate().isBefore(reservation.endDate()) && reservation.startDate().isBefore(r.endDate()))
                );

    }
}
