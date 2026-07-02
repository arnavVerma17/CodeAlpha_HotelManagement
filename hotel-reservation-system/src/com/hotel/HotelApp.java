package com.hotel;

import com.hotel.model.*;
import com.hotel.service.HotelService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class HotelApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final HotelService hotelService = new HotelService();

    // ANSI Colors for high-quality console UI
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String PURPLE = "\u001B[35m";

    public static void main(String[] args) {
        System.out.println(CYAN + BOLD + "==================================================" + RESET);
        System.out.println(CYAN + BOLD + "          Welcome to Luxe Haven Resorts           " + RESET);
        System.out.println(CYAN + BOLD + "==================================================" + RESET);

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = promptString("Select an option (1-6): ").trim();
            switch (choice) {
                case "1":
                    handleSearchRooms();
                    break;
                case "2":
                    handleBookRoom();
                    break;
                case "3":
                    handleViewBooking();
                    break;
                case "4":
                    handleCancelBooking();
                    break;
                case "5":
                    handleListReservations();
                    break;
                case "6":
                    System.out.println(YELLOW + "\nThank you for choosing Luxe Haven Resorts. Goodbye!" + RESET);
                    running = false;
                    break;
                default:
                    System.out.println(RED + "Invalid option. Please choose a number between 1 and 6." + RESET);
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n" + CYAN + BOLD + "--- MAIN MENU ---" + RESET);
        System.out.println(BLUE + "1. " + RESET + "🔍 Search Available Rooms");
        System.out.println(BLUE + "2. " + RESET + "🛏️  Book a Room");
        System.out.println(BLUE + "3. " + RESET + "📄 View Booking Details");
        System.out.println(BLUE + "4. " + RESET + "❌ Cancel a Reservation");
        System.out.println(BLUE + "5. " + RESET + "💼 View All Reservations (Admin)");
        System.out.println(BLUE + "6. " + RESET + "🚪 Exit");
        System.out.println(CYAN + "-----------------" + RESET);
    }

    private static void handleSearchRooms() {
        System.out.println("\n" + BOLD + "--- SEARCH ROOMS ---" + RESET);
        LocalDate checkIn = promptDate("Enter Check-in Date (YYYY-MM-DD): ", LocalDate.now());
        LocalDate checkOut = promptDate("Enter Check-out Date (YYYY-MM-DD): ", checkIn.plusDays(1));

        RoomType type = promptRoomType();
        int capacity = promptInt("Enter Minimum Guest Capacity (0 for any): ", 0);

        List<Room> availableRooms = hotelService.searchAvailableRooms(checkIn, checkOut, type, capacity == 0 ? null : capacity);
        displayRoomResults(availableRooms, (int) java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut));
    }

    private static void handleBookRoom() {
        System.out.println("\n" + BOLD + "--- BOOK A ROOM ---" + RESET);
        LocalDate checkIn = promptDate("Enter Check-in Date (YYYY-MM-DD): ", LocalDate.now());
        LocalDate checkOut = promptDate("Enter Check-out Date (YYYY-MM-DD): ", checkIn.plusDays(1));

        List<Room> availableRooms = hotelService.searchAvailableRooms(checkIn, checkOut, null, null);
        if (availableRooms.isEmpty()) {
            System.out.println(RED + "No rooms are available for the selected dates." + RESET);
            return;
        }

        int nights = (int) java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        displayRoomResults(availableRooms, nights);

        String roomNum = promptString("Enter Room Number to Book (or 'cancel' to return): ").trim();
        if (roomNum.equalsIgnoreCase("cancel")) return;

        Room selectedRoom = availableRooms.stream()
                .filter(r -> r.getRoomNumber().equalsIgnoreCase(roomNum))
                .findFirst()
                .orElse(null);

        if (selectedRoom == null) {
            System.out.println(RED + "Invalid room selection or the room is not available." + RESET);
            return;
        }

        // Guest info
        System.out.println("\n" + BOLD + "Enter Guest Information:" + RESET);
        String name = promptString("Full Name: ").trim();
        while (name.isEmpty()) {
            name = promptString(RED + "Name cannot be empty. Full Name: " + RESET).trim();
        }

        String email = promptString("Email Address: ").trim();
        while (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            email = promptString(RED + "Invalid email format. Email Address: " + RESET).trim();
        }

        String phone = promptString("Phone Number: ").trim();
        while (phone.isEmpty()) {
            phone = promptString(RED + "Phone number cannot be empty. Phone Number: " + RESET).trim();
        }

        Guest guest = new Guest(name, email, phone);
        double totalPrice = selectedRoom.calculatePrice(nights);

        // Display Summary
        System.out.println("\n" + PURPLE + BOLD + "==================================================" + RESET);
        System.out.println(PURPLE + BOLD + "                 BOOKING SUMMARY                  " + RESET);
        System.out.println(PURPLE + BOLD + "==================================================" + RESET);
        System.out.printf("Room Number : %s (%s)%n", selectedRoom.getRoomNumber(), selectedRoom.getRoomType());
        System.out.printf("Capacity    : %d guests%n", selectedRoom.getCapacity());
        System.out.printf("Stay Dates  : %s to %s (%d nights)%n", checkIn, checkOut, nights);
        System.out.printf("Rate/Night  : $%.2f%n", selectedRoom.calculatePrice(1));
        System.out.printf("Total Cost  : $%.2f%n", totalPrice);
        System.out.println(PURPLE + "==================================================" + RESET);

        boolean confirm = promptConfirm("Proceed to payment booking? (Y/N): ");
        if (!confirm) {
            System.out.println(YELLOW + "Booking aborted." + RESET);
            return;
        }

        // Simulated payment flow
        System.out.println("\n" + YELLOW + "--- PAYMENT GATEWAY (SIMULATED) ---" + RESET);
        System.out.println("1. Credit/Debit Card");
        System.out.println("2. Cash/Pay at Counter (Pending Payment)");
        String payOption = promptString("Choose option (1-2): ").trim();

        Reservation reservation;
        try {
            reservation = hotelService.bookRoom(selectedRoom.getRoomNumber(), guest, checkIn, checkOut);
        } catch (IllegalArgumentException e) {
            System.out.println(RED + "Booking failed: " + e.getMessage() + RESET);
            return;
        }

        if (payOption.equals("1")) {
            String cardNumber = promptString("Enter 16-Digit Card Number: ").replaceAll("\\s+", "");
            while (!cardNumber.matches("\\d{16}")) {
                cardNumber = promptString(RED + "Invalid. Enter exactly 16 digits: " + RESET).replaceAll("\\s+", "");
            }
            String cardHolder = promptString("Cardholder Name: ").trim();
            while (cardHolder.isEmpty()) {
                cardHolder = promptString(RED + "Cannot be empty. Cardholder Name: " + RESET).trim();
            }
            String cvv = promptString("Enter 3 or 4-Digit CVV: ").trim();
            while (!cvv.matches("\\d{3,4}")) {
                cvv = promptString(RED + "Invalid CVV. Enter 3 or 4 digits: " + RESET).trim();
            }

            System.out.print("\n" + YELLOW + "Processing payment... " + RESET);
            simulateNetworkDelay(1200);
            boolean success = hotelService.processPayment(reservation, cardNumber, cardHolder, cvv);
            if (success) {
                System.out.println(GREEN + "Approved! Payment charged successfully." + RESET);
            } else {
                System.out.println(RED + "Payment Rejected. Booking created with PENDING payment." + RESET);
            }
        } else {
            System.out.println(YELLOW + "Booking set as PENDING. Please pay at check-in counter." + RESET);
        }

        // Display final receipt
        displayReceipt(reservation);
    }

    private static void handleViewBooking() {
        System.out.println("\n" + BOLD + "--- VIEW BOOKING DETAILS ---" + RESET);
        String resId = promptString("Enter Reservation ID: ").trim();
        Reservation reservation = hotelService.getReservation(resId);
        if (reservation == null) {
            System.out.println(RED + "No reservation found with ID: " + resId + RESET);
            return;
        }
        displayReceipt(reservation);
    }

    private static void handleCancelBooking() {
        System.out.println("\n" + BOLD + "--- CANCEL RESERVATION ---" + RESET);
        String resId = promptString("Enter Reservation ID: ").trim();
        Reservation reservation = hotelService.getReservation(resId);
        if (reservation == null) {
            System.out.println(RED + "No reservation found with ID: " + resId + RESET);
            return;
        }

        displayReceipt(reservation);
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            System.out.println(RED + "This reservation is already cancelled." + RESET);
            return;
        }

        boolean confirm = promptConfirm("Are you sure you want to CANCEL this booking? (Y/N): ");
        if (!confirm) {
            System.out.println(YELLOW + "Cancellation aborted." + RESET);
            return;
        }

        try {
            boolean success = hotelService.cancelReservation(resId);
            if (success) {
                System.out.println(GREEN + "Reservation cancelled successfully." + RESET);
                if (reservation.getPaymentStatus() == PaymentStatus.REFUNDED) {
                    System.out.println(GREEN + "Refund of $" + String.format("%.2f", reservation.getTotalPrice()) + " processed to original payment method." + RESET);
                }
            } else {
                System.out.println(RED + "Failed to cancel reservation." + RESET);
            }
        } catch (IllegalStateException e) {
            System.out.println(RED + e.getMessage() + RESET);
        }
    }

    private static void handleListReservations() {
        System.out.println("\n" + BOLD + "--- ADMIN: ALL RESERVATIONS ---" + RESET);
        List<Reservation> all = hotelService.getAllReservations();
        if (all.isEmpty()) {
            System.out.println(YELLOW + "No reservations found in the database." + RESET);
            return;
        }

        System.out.println(BLUE + "+------------+--------+----------------------+------------+------------+-----------+----------+-----------+" + RESET);
        System.out.println(BLUE + "| Booking ID | Room # | Guest Name           | Check-In   | Check-Out  | Total ($) | Status   | Payment   |" + RESET);
        System.out.println(BLUE + "+------------+--------+----------------------+------------+------------+-----------+----------+-----------+" + RESET);
        for (Reservation r : all) {
            String name = r.getGuest().getName();
            if (name.length() > 20) name = name.substring(0, 17) + "...";
            
            String statusColor = r.getStatus() == ReservationStatus.CONFIRMED ? GREEN : RED;
            String payColor = r.getPaymentStatus() == PaymentStatus.PAID ? GREEN : 
                             (r.getPaymentStatus() == PaymentStatus.REFUNDED ? YELLOW : RED);

            System.out.printf("| %-10s | %-6s | %-20s | %s | %s | %9.2f | " + statusColor + "%-8s" + RESET + " | " + payColor + "%-9s" + RESET + " |%n",
                    r.getReservationId(),
                    r.getRoomNumber(),
                    name,
                    r.getCheckInDate(),
                    r.getCheckOutDate(),
                    r.getTotalPrice(),
                    r.getStatus().name(),
                    r.getPaymentStatus().name());
        }
        System.out.println(BLUE + "+------------+--------+----------------------+------------+------------+-----------+----------+-----------+" + RESET);
    }

    private static void displayRoomResults(List<Room> roomsList, int nights) {
        if (roomsList.isEmpty()) {
            System.out.println(YELLOW + "No matching rooms available." + RESET);
            return;
        }
        System.out.println(GREEN + "+--------+----------+----------+-----------------+------------------------------------------------------------------+" + RESET);
        System.out.println(GREEN + "| Room # | Type     | Max Occ. | Cost (" + nights + " nights) | Amenities                                                        |" + RESET);
        System.out.println(GREEN + "+--------+----------+----------+-----------------+------------------------------------------------------------------+" + RESET);
        for (Room r : roomsList) {
            String amenities = String.join(", ", r.getAmenities());
            if (amenities.length() > 62) {
                amenities = amenities.substring(0, 59) + "...";
            }
            System.out.printf("| %-6s | %-8s | %-8d | $%-13.2f | %-64s |%n",
                    r.getRoomNumber(),
                    r.getRoomType().name(),
                    r.getCapacity(),
                    r.calculatePrice(nights),
                    amenities);
        }
        System.out.println(GREEN + "+--------+----------+----------+-----------------+------------------------------------------------------------------+" + RESET);
    }

    private static void displayReceipt(Reservation r) {
        System.out.println("\n" + GREEN + BOLD + "==================================================" + RESET);
        System.out.println(GREEN + BOLD + "              OFFICIAL BOOKING RECEIPT            " + RESET);
        System.out.println(GREEN + BOLD + "==================================================" + RESET);
        System.out.printf("Reservation ID : " + BOLD + "%s" + RESET + "%n", r.getReservationId());
        System.out.printf("Booking Status : %s%s%s%n", 
                (r.getStatus() == ReservationStatus.CONFIRMED ? GREEN : RED), r.getStatus(), RESET);
        System.out.println("--------------------------------------------------");
        System.out.printf("Room Number    : Room %s%n", r.getRoomNumber());
        System.out.printf("Check-In Date  : %s%n", r.getCheckInDate());
        System.out.printf("Check-Out Date : %s (%d nights)%n", r.getCheckOutDate(), r.getNights());
        System.out.println("--------------------------------------------------");
        System.out.printf("Guest Name     : %s%n", r.getGuest().getName());
        System.out.printf("Guest Email    : %s%n", r.getGuest().getEmail());
        System.out.printf("Guest Phone    : %s%n", r.getGuest().getPhoneNumber());
        System.out.println("--------------------------------------------------");
        System.out.printf("Payment Status : %s%s%s%n", 
                (r.getPaymentStatus() == PaymentStatus.PAID ? GREEN : 
                 (r.getPaymentStatus() == PaymentStatus.REFUNDED ? YELLOW : RED)), 
                r.getPaymentStatus(), RESET);
        System.out.printf("Total Charge   : " + BOLD + "$%.2f" + RESET + "%n", r.getTotalPrice());
        System.out.println(GREEN + BOLD + "==================================================" + RESET);
    }

    // Input Helpers
    private static String promptString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static int promptInt(String prompt, int defaultValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return defaultValue;
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid number format. Try again." + RESET);
            }
        }
    }

    private static boolean promptConfirm(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            System.out.println(RED + "Please answer Y or N." + RESET);
        }
    }

    private static LocalDate promptDate(String prompt, LocalDate minimumDate) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println(YELLOW + "Defaulting to: " + minimumDate + RESET);
                return minimumDate;
            }
            try {
                LocalDate date = LocalDate.parse(input);
                if (date.isBefore(minimumDate)) {
                    System.out.println(RED + "Date must be on or after: " + minimumDate + RESET);
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                System.out.println(RED + "Invalid date format. Please use YYYY-MM-DD format (e.g., 2026-07-10)." + RESET);
            }
        }
    }

    private static RoomType promptRoomType() {
        while (true) {
            System.out.print("Select Room Category (S = Standard, D = Deluxe, U = Suite, A = All): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.isEmpty() || input.equals("A")) return null;
            switch (input) {
                case "S": return RoomType.STANDARD;
                case "D": return RoomType.DELUXE;
                case "U": return RoomType.SUITE;
                default: System.out.println(RED + "Invalid option." + RESET);
            }
        }
    }

    private static void simulateNetworkDelay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
