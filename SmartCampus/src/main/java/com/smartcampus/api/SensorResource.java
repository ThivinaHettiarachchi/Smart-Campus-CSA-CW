/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api;

/**
 *
 * @author thivi
 */

import com.smartcampus.dao.GenericDAO;
import com.smartcampus.dao.MockDatabase;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/sensors")

public class SensorResource {
   // We need BOTH DAOs because creating a sensor modifies a room!
    private final GenericDAO<Sensor> sensorDao = new GenericDAO<>(MockDatabase.SENSORS);
    private final GenericDAO<Room> roomDao = new GenericDAO<>(MockDatabase.ROOMS);

    /**
     * GET /api/v1/sensors
     * Supports optional query parameter: /sensors?type=Temperature
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = sensorDao.getAll();

        // If no type is requested in the URL, return all sensors
        if (type == null || type.trim().isEmpty()) {
            return Response.ok(allSensors).build();
        }

        // Filter the list based on the requested type (case-insensitive)
        List<Sensor> filteredSensors = allSensors.stream()
                .filter(s -> s.getType() != null && s.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());

        return Response.ok(filteredSensors).build();
    }
    
    /**
     * GET /api/v1/sensors/{id}
     * Returns a single sensor by its ID.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorById(@PathParam("id") String id) {
        com.smartcampus.model.Sensor sensor = sensorDao.getById(id);
        
        if (sensor == null) {
            // Return a proper 404 response if the sensor ID doesn't exist
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found\"}")
                    .build();
        }
        
        return Response.ok(sensor).build();
    }

    /**
     * POST /api/v1/sensors
     * Creates a sensor, validates the room, and links them together.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor newSensor) {
        
        if (newSensor.getRoomId() == null || newSensor.getRoomId().trim().isEmpty()) {
            throw new com.smartcampus.exception.UnprocessableEntityException(
                "Unprocessable Entity: roomId is strictly required."
            );
        }

        Room parentRoom = roomDao.getById(newSensor.getRoomId());
        
        if (parentRoom == null) {
            throw new com.smartcampus.exception.LinkedResourceNotFoundException(
                "LinkedResourceNotFound: Room ID " + newSensor.getRoomId() + " does not exist."
            );
        }

        sensorDao.add(newSensor);
        parentRoom.getSensorIds().add(newSensor.getId());

        return Response.status(Response.Status.CREATED).entity(newSensor).build();
    }
    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        
        // 1. Check if the sensor actually exists first!
        Sensor sensor = sensorDao.getById(sensorId);
        if (sensor == null) {
            // If the sensor doesn't exist, immediately throw a 404 Not Found
            throw new WebApplicationException(
                Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Sensor not found\"}")
                        .build()
            );
        }

        // 2. If it exists, hand control over to the Sub-Resource!
        return new SensorReadingResource(sensor);
    }
}