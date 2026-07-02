package com.hotel.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation {
    private String reservationId;
    private String roomNumber;
    private Guest guest;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalPrice;
    private ReservationStatus status;
    private PaymentStatus paymentStatus;

    public Reservation(String reservationId, String roomNumber, Guest guest, 
                       LocalDate checkInDate, LocalDate checkOutDate, 
                       double totalPrice, ReservationStatus status, PaymentStatus paymentStatus) {
        this.reservationId = reservationId;
        this.roomNumber = roomNumber;
        this.guest = guest;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public long getNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId + " | Room: " + roomNumber +
               " | Guest: " + guest.getName() + " | Dates: " + checkInDate + " to " + checkOutDate +
               " (" + getNights() + " nights) | Total: $" + String.format("%.2f", totalPrice) +
               " | Status: " + status + " | Payment: " + paymentStatus;
    }
}
