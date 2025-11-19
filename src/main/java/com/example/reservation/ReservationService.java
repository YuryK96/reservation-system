package com.example.reservation;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservationService {
    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);
    private ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public Reservation getReservationById(Long id) throws EntityNotFoundException {


        ReservationEntity reservationEntity = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));

        return toDomainReservation(reservationEntity);
    }

    @Transactional
    public void cancelReservation(Long id){

        if (!repository.existsById(id)){
            throw new EntityNotFoundException("Not found reservation by id = " + id);
        }

        repository.setStatus(id, ReservationStatus.CANCELLED);
        log.info("Successfully canceled Reservation");
    }

    public Reservation createReservation(Reservation createReservation) {
        if (createReservation.id() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }

        if (createReservation.status() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }


        ReservationEntity entityToSave = new ReservationEntity(
                null,
                createReservation.userId(),
                createReservation.roomId(),
                createReservation.startDate(),
                createReservation.endDate(),
                ReservationStatus.PENDING
        );

        ReservationEntity savedEntity = repository.save(entityToSave);

        return toDomainReservation(savedEntity);
    }

    public Reservation updateReservation(Long id, Reservation updateReservation) {
        if (id == null) {
            throw new IllegalArgumentException("Id required");
        }

        if (!repository.existsById(id)){
            throw new EntityNotFoundException("Not found reservation by id = " + id);
        }

        ReservationEntity reservationEntity = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));


        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("can't change reservation status");
        }

        ReservationEntity reservationForUpdate = new ReservationEntity(
                reservationEntity.getId(),
                updateReservation.userId(),
                updateReservation.roomId(),
                updateReservation.startDate(),
                updateReservation.endDate(),
                ReservationStatus.PENDING
        );

        ReservationEntity updatedReservation = repository.save(reservationForUpdate);
        return toDomainReservation(updatedReservation);


    }

    public Reservation approveReservation(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id required");
        }

        ReservationEntity foundReservation = repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Not found reservation by id = " + id)
        );

        if (foundReservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("can't change reservation status");
        }

        if (isReservationConflict(foundReservation)) {
            throw new IllegalStateException("conflict room");

        }


        foundReservation.setStatus(ReservationStatus.APPROVED);

         repository.save(foundReservation);

        return toDomainReservation(foundReservation);
    }

    public void deleteReservation(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id required");
        }

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Not found reservation by id = " + id);
        }

        repository.deleteById(id);


    }

    public List<Reservation> getAllReservations() {
        List<ReservationEntity> allEntities = repository.findAll();

        List<Reservation> reservationList = allEntities.stream()
                .map(this::toDomainReservation)
                .toList();
        return reservationList;
    }

    private boolean isReservationConflict(ReservationEntity reservation) {

        return repository.findAll()
                .stream()
                .anyMatch(r -> r.getRoomId() != reservation.getRoomId()
                        && r.getId() == reservation.getId()
                        && !(r.getStartDate().isBefore(reservation.getEndDate()) && reservation.getStartDate().isBefore(r.getEndDate()))
                );

    }

    private Reservation toDomainReservation(ReservationEntity reservationEntity) {
        return new Reservation(
                reservationEntity.getId(),
                reservationEntity.getUserId(),
                reservationEntity.getRoomId(),
                reservationEntity.getStartDate(),
                reservationEntity.getEndDate(),
                reservationEntity.getStatus()
        );
    }
}
