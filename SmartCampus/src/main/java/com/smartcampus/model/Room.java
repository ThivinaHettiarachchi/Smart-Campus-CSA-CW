/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.model;

/**
 *
 * @author thivi
 */
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a physical room in the Smart Campus.
 */
public class Room implements BaseModel {
    private String id; // Unique identifier, e.g., "LIB-301"
    private String name; // Human-readable name
    private int capacity; // Maximum occupancy
    
    // We add this to easily track sensors for the Part 2.2 Delete validation!
    private List<String> sensorIds = new ArrayList<>();

    public Room() {}

    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    @Override
    public String getId() { return id; }
    @Override
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public List<String> getSensorIds() { return sensorIds; }
    public void setSensorIds(List<String> sensorIds) { this.sensorIds = sensorIds; }
}