package com.hotel.repository;

import com.hotel.model.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FilePersistence {
    private static final String DATA_DIR = "data";
    private static final String ROOMS_FILE = DATA_DIR + "/rooms.csv";
    private static final String RESERVATIONS_FILE = DATA_DIR + "/reservations.csv";

    public FilePersistence() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Error creating data directory: " + e.getMessage());
        }
    }

    // Load rooms from file, or initialize with defaults if not exists
    public List<Room> loadRooms() {
        List<Room> rooms = new ArrayList<>();
        File file = new File(ROOMS_FILE);

        if (!file.exists()) {
            rooms = initializeDefaultRooms();
            saveRooms(rooms);
            return rooms;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            // Skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String roomNumber = parts[0];
                    RoomType type = RoomType.valueOf(parts[1]);
                    double baseRate = Double.parseDouble(parts[2]);
                    int capacity = Integer.parseInt(parts[3]);

                    // Polymorphic instantiation based on RoomType
                    Room room;
                    switch (type) {
                        case STANDARD:
                            room = new StandardRoom(roomNumber, baseRate, capacity);
                            break;
                        case DELUXE:
                            room = new DeluxeRoom(roomNumber, baseRate, capacity);
                            break;
                        case SUITE:
                            room = new SuiteRoom(roomNumber, baseRate, capacity);
                            break;
                        default:
                            continue;
                    }
                    rooms.add(room);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading rooms, falling back to defaults: " + e.getMessage());
            rooms = initializeDefaultRooms();
        }
        return rooms;
    }

    // Save rooms to file
    public void saveRooms(List<Room> rooms) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ROOMS_FILE))) {
            pw.println("roomNumber,roomType,baseRate,capacity");
            for (Room room : rooms) {
                pw.printf("%s,%s,%.2f,%d%n",
                        room.getRoomNumber(),
                        room.getRoomType().name(),
                        room.getBaseRate(),
                        room.getCapacity());
            }
        } catch (IOException e) {
            System.err.println("Error saving rooms: " + e.getMessage());
        }
    }

    // Load reservations from file
    public List<Reservation> loadReservations() {
        List<Reservation> reservations = new ArrayList<>();
        File file = new File(RESERVATIONS_FILE);

        if (!file.exists()) {
            return reservations; // Return empty list
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            // Skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 10) {
                    String reservationId = parts[0];
                    String roomNumber = parts[1];
                    String guestName = parts[2];
                    String guestEmail = parts[3];
                    String guestPhone = parts[4];
                    LocalDate checkInDate = LocalDate.parse(parts[5]);
                    LocalDate checkOutDate = LocalDate.parse(parts[6]);
                    double totalPrice = Double.parseDouble(parts[7]);
                    ReservationStatus status = ReservationStatus.valueOf(parts[8]);
                    PaymentStatus paymentStatus = PaymentStatus.valueOf(parts[9]);

                    Guest guest = new Guest(guestName, guestEmail, guestPhone);
                    Reservation reservation = new Reservation(reservationId, roomNumber, guest, 
                                                              checkInDate, checkOutDate, totalPrice, 
                                                              status, paymentStatus);
                    reservations.add(reservation);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading reservations: " + e.getMessage());
        }
        return reservations;
    }

    // Save reservations to file
    public void saveReservations(List<Reservation> reservations) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(RESERVATIONS_FILE))) {
            pw.println("reservationId,roomNumber,guestName,guestEmail,guestPhone,checkInDate,checkOutDate,totalPrice,status,paymentStatus");
            for (Reservation r : reservations) {
                Guest g = r.getGuest();
                pw.printf("%s,%s,%s,%s,%s,%s,%s,%.2f,%s,%s%n",
                        r.getReservationId(),
                        r.getRoomNumber(),
                        escapeCsv(g.getName()),
                        escapeCsv(g.getEmail()),
                        escapeCsv(g.getPhoneNumber()),
                        r.getCheckInDate().toString(),
                        r.getCheckOutDate().toString(),
                        r.getTotalPrice(),
                        r.getStatus().name(),
                        r.getPaymentStatus().name());
            }
        } catch (IOException e) {
            System.err.println("Error saving reservations: " + e.getMessage());
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace(",", " "); // Simple replacement to maintain clean CSV split
    }

    // Default Rooms inventory
    private List<Room> initializeDefaultRooms() {
        List<Room> rooms = new ArrayList<>();
        // Standard rooms (Base Rate: $80.00, Capacity: 2)
        rooms.add(new StandardRoom("101", 80.00, 2));
        rooms.add(new StandardRoom("102", 80.00, 2));
        rooms.add(new StandardRoom("103", 80.00, 2));
        rooms.add(new StandardRoom("104", 80.00, 2));

        // Deluxe rooms (Base Rate: $130.00, Capacity: 3)
        rooms.add(new DeluxeRoom("201", 130.00, 3));
        rooms.add(new DeluxeRoom("202", 130.00, 3));
        rooms.add(new DeluxeRoom("203", 130.00, 3));

        // Suite rooms (Base Rate: $220.00, Capacity: 4)
        rooms.add(new SuiteRoom("301", 220.00, 4));
        rooms.add(new SuiteRoom("302", 220.00, 4));
        return rooms;
    }
}
