package com.hotel.model;

import java.util.Arrays;
import java.util.List;

public class DeluxeRoom extends Room {

    public DeluxeRoom(String roomNumber, double baseRate, int capacity) {
        super(roomNumber, baseRate, capacity);
    }

    @Override
    public double calculatePrice(int nights) {
        if (nights <= 0) return 0;
        // Deluxe rooms charge 1.5x of the base rate
        return getBaseRate() * 1.5 * nights;
    }

    @Override
    public List<String> getAmenities() {
        return Arrays.asList("High-speed Wi-Fi", "AC", "Smart TV", "Mini-bar", "Safety Deposit Box", "Coffee Maker");
    }

    @Override
    public RoomType getRoomType() {
        return RoomType.DELUXE;
    }
}
