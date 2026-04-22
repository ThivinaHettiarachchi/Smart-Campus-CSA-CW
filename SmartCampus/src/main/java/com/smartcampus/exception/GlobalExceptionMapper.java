/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;

/**
 *
 * @author thivi
 */
//safety net
//prevents Java stack traces from leaking if code crashes

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        // Log the actual error to your server console for debugging
        exception.printStackTrace(); 
        
        // Return a clean, generic message to the client (No stack traces!)
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR) // 500
                .entity("{\"error\":\"An unexpected internal server error occurred.\"}")
                .type("application/json")
                .build();
    }
}
