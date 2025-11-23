package com.example.reservation.reservations;

import jakarta.validation.constraints.NotNull;

public record ReservationSearchByFilter(
        @NotNull
        Long roomId,
        @NotNull
        Long userId,
        Integer pageSize,
        Integer pageNumber

) {
}
