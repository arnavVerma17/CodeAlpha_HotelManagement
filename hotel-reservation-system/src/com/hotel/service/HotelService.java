package com.hotel.service;

import com.hotel.model.*;
import com.hotel.repository.FilePersistence;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class HotelService {
    private final FilePersistence persistence;
    private final List<Room> rooms;
    private final List<Reservation> reservations;

    public HotelService() {
        this.persistence = new FilePersistence();
        this.rooms = persistence.loadRooms();
        this.reservations = persistence.loadReservations();
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    // Check if a room is available for a given range
    public boolean isRoomAvailable(String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        for (Reservation res : reservations) {
            if (res.getRoomNumber().equals(roomNumber) && res.getStatus() == ReservationStatus.CONFIRMED) {
                // Check if dates overlap: (start1 < end2) && (start2 < end1)
                if (checkIn.isBefore(res.getCheckOutDate()) && res.getCheckInDate().isBefore(checkOut)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Search available rooms with filters
    public List<Room> searchAvailableRooms(LocalDate checkIn, LocalDate checkOut, RoomType type, Integer minCapacity) {
        return rooms.stream()
                .filter(room -> type == null || room.getRoomType() == type)
                .filter(room -> minCapacity == null || room.getCapacity() >= minCapacity)
                .filter(room -> isRoomAvailable(room.getRoomNumber(), checkIn, checkOut))
                .collect(Collectors.toList());
    }

    // Make a reservation
    public Reservation bookRoom(String roomNumber, Guest guest, LocalDate checkIn, LocalDate checkOut) throws IllegalArgumentException {
        // Date validations
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be at least one day after check-in date.");
        }

        // Room validation
        Room room = rooms.stream()
                .filter(r -> r.getRoomNumber().equals(roomNumber))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Room number " + roomNumber + " does not exist."));

        // Availability check
        if (!isRoomAvailable(roomNumber, checkIn, checkOut)) {
            throw new IllegalArgumentException("Room number " + roomNumber + " is not available for the selected dates.");
        }

        // Price calculation
        int nights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalPrice = room.calculatePrice(nights);

        // Generate unique reservation ID (shorter readable version)
        String reservationId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Create reservation
        Reservation reservation = new Reservation(
                reservationId,
                roomNumber,
                guest,
                checkIn,
                checkOut,
                totalPrice,
                ReservationStatus.CONFIRMED,
                PaymentStatus.PENDING
        );

        reservations.add(reservation);
        persistence.saveReservations(reservations);
        return reservation;
    }

    // Process simulated payment
    public boolean processPayment(Reservation reservation, String cardNumber, String cardHolder, String cvv) {
        // Validate card format
        if (cardNumber == null || !cardNumber.matches("\\d{16}")) {
            return false;
        }
        if (cardHolder == null || cardHolder.trim().isEmpty()) {
            return false;
        }
        if (cvv == null || !cvv.matches("\\d{3,4}")) {
            return false;
        }

        // Update payment status
        reservation.setPaymentStatus(PaymentStatus.PAID);
        persistence.saveReservations(reservations);
        return true;
    }

    // Cancel reservation
    public boolean cancelReservation(String reservationId) {
        for (Reservation res : reservations) {
            if (res.getReservationId().equalsIgnoreCase(reservationId)) {
                if (res.getStatus() == ReservationStatus.CANCELLED) {
                    throw new IllegalStateException("Reservation is already cancelled.");
                }
                res.setStatus(ReservationStatus.CANCELLED);
                if (res.getPaymentStatus() == PaymentStatus.PAID) {
                    res.setPaymentStatus(PaymentStatus.REFUNDED);
                }
                persistence.saveReservations(reservations);
                return true;
            }
        }
        return false;
    }

    // View specific reservation
    public Reservation getReservation(String reservationId) {
        return reservations.stream()
                .filter(res -> res.getReservationId().equalsIgnoreCase(reservationId))
                .findFirst()
                .orElse(null);
    }
}
