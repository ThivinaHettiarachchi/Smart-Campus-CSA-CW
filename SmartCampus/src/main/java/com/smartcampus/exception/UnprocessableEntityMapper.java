/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exception;

/**
 *
 * @author thivi
 */
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnprocessableEntityMapper implements ExceptionMapper<UnprocessableEntityException> {
    @Override
    public Response toResponse(UnprocessableEntityException exception) {
        return Response.status(422) // 422 Unprocessable Entity
                .entity("{\"error\":\"" + exception.getMessage() + "\"}")
                .type("application/json")
                .build();
    }
}