/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.dao;

/**
 *
 * @author thivi
 */
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class MockDatabase {
    public static final List<Room> ROOMS = new ArrayList<>();
    public static final List<Sensor> SENSORS = new ArrayList<>();
    public static final Map<String, List<SensorReading>> READINGS = new HashMap<>();
    
    // Our counter for auto-generating reading IDs (Reading-1, Reading-2...)
    public static int readingCounter = 1;
    
    // Counter for auto-generating Room IDs
    public static int roomCounter = 3;
    
    static {
        // Initialise dummy rooms
        Room library = new Room("Room-1", "Library Quiet Study", 50);
        ROOMS.add(library);
        ROOMS.add(new Room("Room-2", "Main Lecture Theatre", 200));
        

        // Initialise dummy sensor 
        Sensor tempSensor = new Sensor("Sensor-1", "Temperature", "ACTIVE", 22.5, "Room-1");
        SENSORS.add(tempSensor);
        library.getSensorIds().add(tempSensor.getId());

        // Initialise dummy reading with friendly ID, and link it to "Sensor-1"
        List<SensorReading> dummyReadings = new ArrayList<>();
        dummyReadings.add(new SensorReading("Reading-0", System.currentTimeMillis(), 22.5));
        READINGS.put("Sensor-1", dummyReadings);
    }
}