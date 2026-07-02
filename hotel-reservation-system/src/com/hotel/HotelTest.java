package com.hotel;

import com.hotel.model.*;
import com.hotel.service.HotelService;
import com.hotel.repository.FilePersistence;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class HotelTest {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("         Running Automated Verification           ");
        System.out.println("==================================================");

        try {
            testPolymorphicPricing();
            testOverlapConflictResolution();
            testFilePersistenceLifecycle();
            
            System.out.println("\n\u001B[32m[SUCCESS] All verification tests passed successfully!\u001B[0m");
        } catch (Throwable e) {
            System.err.println("\n\u001B[31m[FAILED] Test verification failed: " + e.getMessage() + "\u001B[0m");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void testPolymorphicPricing() {
        System.out.print("Testing Polymorphic Rate Calculations... ");
        
        Room standard = new StandardRoom("101", 100.00, 2);
        Room deluxe = new DeluxeRoom("201", 100.00, 3);
        Room suite = new SuiteRoom("301", 100.00, 4);

        // Standard: 100 * 1 = 100
        assertEqual(100.00, standard.calculatePrice(1), "Standard rate 1 night");
        assertEqual(300.00, standard.calculatePrice(3), "Standard rate 3 nights");

        // Deluxe: 100 * 1.5 * 1 = 150
        assertEqual(150.00, deluxe.calculatePrice(1), "Deluxe rate 1 night");
        assertEqual(450.00, deluxe.calculatePrice(3), "Deluxe rate 3 nights");

        // Suite: 100 * 2.5 * 1 = 250
        assertEqual(250.00, suite.calculatePrice(1), "Suite rate 1 night");
        assertEqual(750.00, suite.calculatePrice(3), "Suite rate 3 nights");

        System.out.println("Passed!");
    }

    private static void testOverlapConflictResolution() {
        System.out.print("Testing Date Overlap Detection & Prevention... ");
        
        // Clean/temporary setup for testing
        // Delete reservations file if exists to avoid collision from previous runs
        File file = new File("data/reservations.csv");
        if (file.exists()) {
            file.delete();
        }

        HotelService service = new HotelService();
        Guest guest = new Guest("Test User", "test@test.com", "12345");

        LocalDate d1 = LocalDate.now().plusDays(2);
        LocalDate d2 = LocalDate.now().plusDays(5);
        LocalDate d3 = LocalDate.now().plusDays(4);
        LocalDate d4 = LocalDate.now().plusDays(8);

        // Book 101 for d1 -> d2
        Reservation r1 = service.bookRoom("101", guest, d1, d2);
        assertEqual("101", r1.getRoomNumber(), "Room number booked");

        // Test check: room 101 should NOT be available during overlap period (d3 to d4 overlaps because d3(plus4) < d2(plus5) and d1(plus2) < d4(plus8))
        boolean isAvailableOverlap = service.isRoomAvailable("101", d3, d4);
        assertEqual(false, isAvailableOverlap, "Room availability check under overlap");

        // Test book overlap: should throw exception
        boolean exceptionThrown = false;
        try {
            service.bookRoom("101", guest, d3, d4);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertEqual(true, exceptionThrown, "Overlap exception should be thrown");

        // Room 101 should be available for completely separate date range
        LocalDate d5 = LocalDate.now().plusDays(10);
        LocalDate d6 = LocalDate.now().plusDays(12);
        boolean isAvailableSeparate = service.isRoomAvailable("101", d5, d6);
        assertEqual(true, isAvailableSeparate, "Room availability check outside overlap");

        System.out.println("Passed!");
    }

    private static void testFilePersistenceLifecycle() {
        System.out.print("Testing File Storage Persistence & Reloading... ");

        HotelService serviceBefore = new HotelService();
        Guest guest = new Guest("Persisted Guest", "persisted@email.com", "999-999");
        LocalDate checkIn = LocalDate.now().plusDays(20);
        LocalDate checkOut = LocalDate.now().plusDays(22);

        // Make reservation to trigger write to CSV
        Reservation originalRes = serviceBefore.bookRoom("102", guest, checkIn, checkOut);
        String savedId = originalRes.getReservationId();

        // Instantiate new service which triggers load from CSV file
        HotelService serviceAfter = new HotelService();
        Reservation loadedRes = serviceAfter.getReservation(savedId);

        assertEqual(true, loadedRes != null, "Reservation loaded from file");
        assertEqual("102", loadedRes.getRoomNumber(), "Correct room number loaded");
        assertEqual("Persisted Guest", loadedRes.getGuest().getName(), "Correct guest name loaded");
        assertEqual(checkIn, loadedRes.getCheckInDate(), "Correct check-in date loaded");
        assertEqual(checkOut, loadedRes.getCheckOutDate(), "Correct check-out date loaded");

        System.out.println("Passed!");
    }

    private static void assertEqual(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }
}
