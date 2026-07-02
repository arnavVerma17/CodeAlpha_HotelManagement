package com.hotel.model;

import java.util.Arrays;
import java.util.List;

public class StandardRoom extends Room {

    public StandardRoom(String roomNumber, double baseRate, int capacity) {
        super(roomNumber, baseRate, capacity);
    }

    @Override
    public double calculatePrice(int nights) {
        if (nights <= 0) return 0;
        return getBaseRate() * nights;
    }

    @Override
    public List<String> getAmenities() {
        return Arrays.asList("Wi-Fi", "TV", "Desk", "Private Bathroom");
    }

    @Override
    public RoomType getRoomType() {
        return RoomType.STANDARD;
    }
}
