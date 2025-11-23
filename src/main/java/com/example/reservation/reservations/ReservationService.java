package com.example.reservation.reservations;


import com.example.reservation.reservations.availability.ReservationAvailabilityService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ReservationService {
    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);
    private final ReservationRepository repository;
    private final ReservationMapper mapper;
    private final ReservationAvailabilityService availabilityService;

    public ReservationService(ReservationRepository repository, ReservationMapper mapper, ReservationAvailabilityService availabilityService) {
        this.repository = repository;
        this.mapper = mapper;
        this.availabilityService = availabilityService;
    }

    public Reservation getReservationById(Long id) throws EntityNotFoundException {

        if (id.equals(10L)) {
            throw new RuntimeException("Test");
        }


        ReservationEntity reservationEntity = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));

        return mapper.toDomain(reservationEntity);
    }

    @Transactional
    public void cancelReservation(Long id) {

        ReservationEntity reservationEntity = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));


        if (reservationEntity.getStatus().equals(ReservationStatus.APPROVED)) {
            throw new IllegalArgumentException("Reservation already is approved");

        }


        if (reservationEntity.getStatus().equals(ReservationStatus.CANCELLED)) {
            throw new IllegalArgumentException("Reservation already is cancelled");

        }

        repository.setStatus(id, ReservationStatus.CANCELLED);
        log.info("Successfully canceled Reservation");
    }

    public Reservation createReservation(Reservation createReservation) {

        if (createReservation.status() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }
        if (createReservation.startDate().plusDays(1).isAfter(createReservation.endDate())) {
            throw new IllegalArgumentException("Incorrect dates");
        }


        ReservationEntity entityToSave = mapper.toEntity(createReservation);
        entityToSave.setId(null);
        entityToSave.setStatus(ReservationStatus.PENDING);

        ReservationEntity savedEntity = repository.save(entityToSave);

        return mapper.toDomain(savedEntity);
    }

    public Reservation updateReservation(Long id, Reservation updateReservation) {
        if (id == null) {
            throw new IllegalArgumentException("Id required");
        }

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Not found reservation by id = " + id);
        }

        ReservationEntity reservationEntity = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));


        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Can't change reservation status");
        }

        if (updateReservation.startDate().plusDays(1).isAfter(updateReservation.endDate())) {
            throw new IllegalArgumentException("Incorrect dates");
        }


        ReservationEntity reservationForUpdate = mapper.toEntity(updateReservation);

        reservationForUpdate.setId(reservationEntity.getId());
        reservationForUpdate.setStatus(ReservationStatus.PENDING);

        ReservationEntity updatedReservation = repository.save(reservationForUpdate);
        return mapper.toDomain(updatedReservation);


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

        boolean isAvailableToApprove = availabilityService.isReservationAvailable(foundReservation.getRoomId(), foundReservation.getStartDate(), foundReservation.getEndDate());

        if (!isAvailableToApprove) {
            throw new IllegalStateException("conflict room");

        }


        foundReservation.setStatus(ReservationStatus.APPROVED);

        repository.save(foundReservation);

        return mapper.toDomain(foundReservation);
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

    public List<Reservation> searchAllByFilter(ReservationSearchByFilter filter) {
        int pageSize = filter.pageSize() != null ? filter.pageSize() : 10;
        int pageNumber = filter.pageNumber() != null ? filter.pageNumber() : 0;

        Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        List<ReservationEntity> filteredEntities = repository.searchAllByFilter(filter.userId(), filter.roomId(), pageable);


        List<Reservation> reservationList = filteredEntities.stream()
                .map(mapper::toDomain)
                .toList();
        return reservationList;
    }


}
