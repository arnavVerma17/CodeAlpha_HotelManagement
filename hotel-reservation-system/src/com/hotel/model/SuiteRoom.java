package com.hotel.model;

import java.util.Arrays;
import java.util.List;

public class SuiteRoom extends Room {

    public SuiteRoom(String roomNumber, double baseRate, int capacity) {
        super(roomNumber, baseRate, capacity);
    }

    @Override
    public double calculatePrice(int nights) {
        if (nights <= 0) return 0;
        // Suite rooms charge 2.5x of the base rate
        return getBaseRate() * 2.5 * nights;
    }

    @Override
    public List<String> getAmenities() {
        return Arrays.asList("Ultra High-speed Wi-Fi", "AC", "75\" Smart TV", "Premium Mini-bar", 
                             "Safety Deposit Box", "Luxury Bathtub", "Separate Living Room", 
                             "Complimentary Breakfast", "24/7 Butler Service");
    }

    @Override
    public RoomType getRoomType() {
        return RoomType.SUITE;
    }
}
