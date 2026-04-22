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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/rooms") // This makes the URL: /api/v1/rooms
public class RoomResource {

    // Connect to our in-memory database using the DAO pattern from Tutorial 8
    private final GenericDAO<Room> roomDao = new GenericDAO<>(MockDatabase.ROOMS);

    /**
     * GET /api/v1/rooms
     * Returns a list of all rooms.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        List<Room> rooms = roomDao.getAll();
        return Response.ok(rooms).build();
    }

    /**
     * GET /api/v1/rooms/{roomId}
     * Returns a single room by its ID.
     */
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = roomDao.getById(roomId);
        
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found\"}")
                    .build();
        }
        
        return Response.ok(room).build();
    }

    /**
     * POST /api/v1/rooms
     * Creates a new room.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room newRoom) {
        
        // Check if the user forgot to send an ID from Postman
        if (newRoom.getId() == null || newRoom.getId().trim().isEmpty()) {
            // Auto-generate a friendly ID like "Room-3"
            newRoom.setId("Room-" + com.smartcampus.dao.MockDatabase.roomCounter);
            com.smartcampus.dao.MockDatabase.roomCounter++; 
        }

        roomDao.add(newRoom);
        return Response.status(Response.Status.CREATED).entity(newRoom).build();
    }

    /**
     * DELETE /api/v1/rooms/{roomId}
     * Deletes a room ONLY if it has no sensors.
     */
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = roomDao.getById(roomId);
        
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found\"}").build();
        }

        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new com.smartcampus.exception.RoomNotEmptyException(
                "Cannot delete room. It contains active sensors."
            );
        }

        roomDao.delete(roomId);
        return Response.noContent().build();
    }
}