package com.codealpha.hotel.model;

/**
 * Room — Represents a hotel room entity.
 */
public class Room {

    public enum RoomCategory { STANDARD, DELUXE, SUITE }

    private int          id;
    private String       roomNumber;
    private RoomCategory category;
    private double       pricePerNight;
    private int          capacity;
    private String       description;
    private boolean      isAvailable;

    // ─── Constructors ──────────────────────────────────────────────────────────
    public Room() {}

    public Room(String roomNumber, RoomCategory category, double pricePerNight,
                int capacity, String description) {
        this.roomNumber    = roomNumber;
        this.category      = category;
        this.pricePerNight = pricePerNight;
        this.capacity      = capacity;
        this.description   = description;
        this.isAvailable   = true;
    }

    // ─── Getters & Setters ─────────────────────────────────────────────────────
    public int          getId()            { return id; }
    public void         setId(int id)      { this.id = id; }

    public String       getRoomNumber()                   { return roomNumber; }
    public void         setRoomNumber(String roomNumber)  { this.roomNumber = roomNumber; }

    public RoomCategory getCategory()                     { return category; }
    public void         setCategory(RoomCategory cat)     { this.category = cat; }

    public double       getPricePerNight()                       { return pricePerNight; }
    public void         setPricePerNight(double pricePerNight)   { this.pricePerNight = pricePerNight; }

    public int          getCapacity()              { return capacity; }
    public void         setCapacity(int capacity)  { this.capacity = capacity; }

    public String       getDescription()                    { return description; }
    public void         setDescription(String description)  { this.description = description; }

    public boolean      isAvailable()                     { return isAvailable; }
    public void         setAvailable(boolean available)   { this.isAvailable = available; }

    @Override
    public String toString() {
        return String.format("Room[%s | %s | PKR %.0f/night | Capacity: %d | %s]",
                roomNumber, category, pricePerNight, capacity,
                isAvailable ? "Available" : "Booked");
    }
}
