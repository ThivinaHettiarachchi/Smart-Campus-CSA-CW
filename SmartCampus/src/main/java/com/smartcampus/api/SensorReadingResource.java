/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api;

/**
 *
 * @author thivi
 */

import com.smartcampus.dao.MockDatabase;
import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SensorReadingResource {

    private final Sensor parentSensor; // Store the whole sensor object now

    public SensorReadingResource(Sensor parentSensor) {
        this.parentSensor = parentSensor;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {
        List<SensorReading> readings = MockDatabase.READINGS.getOrDefault(parentSensor.getId(), new ArrayList<>());
        return Response.ok(readings).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading newReading) {
        // TASK 3 REQUIREMENT: Block if status is MAINTENANCE
        if ("MAINTENANCE".equalsIgnoreCase(parentSensor.getStatus())) {
            throw new SensorUnavailableException("Sensor is physically disconnected for maintenance.");
        }

        if (newReading.getId() == null || newReading.getId().trim().isEmpty()) {
            // Use friendly sequential IDs like "reading-1", "reading-2", etc.
            newReading.setId("reading-" + MockDatabase.readingCounter);
            MockDatabase.readingCounter++; // Increase the counter for the next one
        }
        if (newReading.getTimestamp() == 0) {
            newReading.setTimestamp(System.currentTimeMillis());
        }

        MockDatabase.READINGS.computeIfAbsent(parentSensor.getId(), k -> new ArrayList<>()).add(newReading);
        return Response.status(Response.Status.CREATED).entity(newReading).build();
    }
}