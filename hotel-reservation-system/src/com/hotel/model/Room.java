package com.hotel.model;

import java.util.List;

public abstract class Room {
    private String roomNumber;
    private double baseRate;
    private int capacity;

    public Room(String roomNumber, double baseRate, int capacity) {
        this.roomNumber = roomNumber;
        this.baseRate = baseRate;
        this.capacity = capacity;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public double getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    // Polymorphic methods
    public abstract double calculatePrice(int nights);
    public abstract List<String> getAmenities();
    public abstract RoomType getRoomType();

    @Override
    public String toString() {
        return "Room " + roomNumber + " [" + getRoomType() + "] - Capacity: " + capacity + 
               " - Rate: $" + String.format("%.2f", calculatePrice(1)) + "/night";
    }
}
