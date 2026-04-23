# Smart Campus REST API

## Overview of API Design
This project is a RESTful API built to manage a "Smart Campus" environment, specifically handling physical Rooms, IoT Sensors, and historical Sensor Readings. 

The application is built using Java and the **JAX-RS (Jersey)** framework. It follows a clean, modular architecture:
* **Data Access Object (DAO) Pattern:** The API uses a `GenericDAO` to cleanly separate the business logic from data retrieval.
* **In-Memory Storage:** Data is stored in a Singleton `MockDatabase`, allowing for rapid testing and simulation of API state without requiring external database configuration.
* **Sub-Resource Routing:** It features advanced URL navigation (e.g., routing from a Sensor directly to its nested Readings).
* **Global Exception Handling:** A `GlobalExceptionMapper` intercepts runtime errors to prevent stack trace leaks, ensuring security while returning user-friendly JSON error messages (e.g., 404, 409, 422, 403, and 500 status codes).

## Build and Launch Instructions
To run this project locally, follow these steps:

1. **Clone the repository:**
   `git clone https://github.com/ThivinaHettiarachchi/Smart-Campus-CSA-CW.git`
2. **Open the project:**
   Open Apache NetBeans IDE, go to `File > Open Project`, and select the cloned `SmartCampus` folder.
3. **Configure the Server:**
   Ensure you have a local web server configured in NetBeans (such as Apache Tomcat or GlassFish). Right-click the project, select `Properties > Run`, and verify your server is selected.
4. **Build and Run:**
   Right-click the project in the Projects window, select **Clean and Build**. Once complete, click **Run**. The server will launch and host the API at `http://localhost:8080/SmartCampus/api/v1/`.

## Sample cURL Commands
Here are twelve commands to test the API directly from your terminal:

**1. Create a New Room (POST)**
curl -X POST http://localhost:8080/SmartCampus/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"name": "Media Room","capacity": 20}'

**2. Retrieve All Rooms (GET)**
curl -X GET http://localhost:8080/SmartCampus/api/v1/rooms

**3. Delete a Room (DELETE)**
curl -X DELETE http://localhost:8080/SmartCampus/api/v1/rooms/Room-3 

**4. Create a New Sensor linked to a Room (POST)**
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id": "Sensor-2","type": "CO2","status": "MAINTENANCE","roomId": "Room-2"}'

**5. Retrieve All Sensors (GET)**
curl -X GET http://localhost:8080/SmartCampus/api/v1/sensors

**6. Filter Sensors by Type (GET)**
curl -X GET "http://localhost:8080/SmartCampus/api/v1/sensors?type=Temperature"

**7. Add a nested Sensor Reading (POST)**
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors/Sensor-1/readings \
-H "Content-Type: application/json" \
-d '{"value": 24.5}'

**8. Adding new readings to a sensor (POST)**
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors/Sensor-1/readings \
-H "Content-Type: application/json" \
-d '{"value": 32}'

**9. Attemping to delete a room which has Sensors (DELETE)**
curl -X DELETE http://localhost:8080/SmartCampus/api/v1/rooms/Room-1

**10. Post a sensor with a roomId that does not exist (POST)**
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id": "Sensor-3","type": "Occupancy Trackers","status": "ACTIVE","roomId": "Room-4"}'

**11. Attemping give a new reading for a sensor which in Maintenance (POST)**
curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors/Sensor-2/readings \
-H "Content-Type: application/json" \
-d '{"value": 40}'

**12. Attemping to delete all Rooms (DELETE)**
curl -X DELETE http://localhost:8080/SmartCampus/api/v1/rooms


## Conceptual Report

### Part 1: Service Architecture & Setup

**1.1 Project & Application Configuration**
In Smart Campus API, JAX-RS treats Resource classes (`RoomResource` and `SensorResource`) as per-request. This means the server instantiates a brand new `RoomResource` object for every incoming HTTP request and destroys it immediately after responding. Because of this lifecycle, cannot store campus data in standard instance variables inside the resource classes, as the data would wipe on every request. To solve this and prevent data loss, delegated data storage to a Singleton `MockDatabase` class accessed via DAOs. This architectural decision ensures all incoming requests interact with the exact same instance in memory. To prevent race conditions when multiple clients add or modify sensors simultaneously, internal data structures (like maps or lists in the database) must utilize thread-safe collections or synchronized blocks.

**1.2 The "Discovery" Endpoint**
HATEOAS (Hypermedia As The Engine Of Application State) is a hallmark of REST because it decouples the client from specific URL structure. By providing navigation links (such as `_links` pointing to `/api/v1/rooms` or `/api/v1/sensors`) directly within JSON responses, API becomes self-discoverable. 
This benefits developers building the Smart Campus frontend dashboard because they do not need to hardcode exact URIs or constantly read static documentation. If eventually update routing, their client applications will not break because they dynamically follow the URLs provided in the server's responses.

---

### Part 2: Room Management

**2.1 Room Resource Implementation**
In `RoomResource.getAllRooms()` method, we return the full list of `Room` objects (including name and capacity). Returning only IDs would save initial network bandwidth, but it introduces the "N+1 query problem." The client dashboard would have to make dozens of additional `GET /rooms/{id}` requests just to display the room names, significantly increasing server load and latency. Returning the full objects in one payload consumes slightly more bandwidth initially, but drastically improves client-side processing by giving the UI everything it needs to render the campus map in a single HTTP round-trip.

**2.2 Room Deletion & Safety Logic**
Yes, `DELETE` operation in `RoomResource` is strictly idempotent. Idempotency guarantees that issuing the exact same request multiple times leaves the system in the same state. 
If a client sends `DELETE /api/v1/rooms/Room-1`, `GenericDAO` removes the room, and we return a `200 OK` or `204 No Content`. If the client mistakenly clicks delete again and sends the exact same request, the DAO will not find the room, and method returns a `404 Not Found`. Despite the different HTTP status code, the underlying server state remains completely unchanged (Room-1 is still deleted), making the method safely idempotent.

---

### Part 3: Sensor Operations & Linking

**3.1 Sensor Resource & Integrity**
The `@Consumes(MediaType.APPLICATION_JSON)` annotation which attached to `POST` method for creating sensors. If a client attempts to send sensor data formatted as `text/plain` or `application/xml`, the JAX-RS framework intercepts the request before it even triggers our Java method. The framework will automatically reject the request and return a `415 Unsupported Media Type` error. This guarantees that method logic only processes valid JSON, saves from writing tedious manual format-checking code.

**3.2 Filtered Retrieval & Search**
In `SensorResource`, implemented filtering using `@QueryParam("type")` to handle requests like `/sensors?type=CO2`. This is architecturally superior to hardcoding the type into the URL path (e.g., `/sensors/type/CO2`). URL paths should strictly identify resources and hierarchy, whereas query parameters act as optional modifiers. Using `@QueryParam` allows for clean composability; if decided later to filter by type and status, can easily accept `/sensors?type=CO2&status=ACTIVE`. Trying to shove multiple optional filters into a rigid URL path creates deeply nested, brittle routes that violate REST principles.

---

### Part 4: Deep Nesting with Sub-Resources

**4.1 The Sub-Resource Locator Pattern**
To handle nested paths like `/sensors/{id}/readings`, used a Sub-Resource Locator in `SensorResource`. Instead of attaching a `@GET` or `@POST` to that path, the method simply returns a new instance of `ReadingResource`. This pattern prevents `SensorResource` class from becoming a massive, unreadable monolith. By delegating the nested logic, we successfully separated our concerns: `SensorResource` handles sensor management, while `ReadingResource` exclusively handles the logic for data readings. This keeps classes highly cohesive, easier to test, and significantly easier to maintain.

---

### Part 5: Advanced Error Handling, Exception Mapping & Logging

**5.2 Dependency Validation (422 Unprocessable Entity)**
When validating a new sensor payload, if the client provides a `roomId` that does not exist in  `MockDatabase`, we return a `422 Unprocessable Entity` instead of a `404 Not Found`. A `404` status specifically implies that the target URI (`/api/v1/sensors`) does not exist. The `422` status is semantically accurate here: it informs the client that the URI was correct and the JSON syntax was perfectly valid, but the business logic contained inside (the foreign key reference to the room) was impossible to process.

**5.4 The Global Safety Net (500)**
`GlobalExceptionMapper` has implemented to catch unexpected `Throwable` crashes. Without this safety net, the Jersey/Tomcat runtime would return default HTML error pages containing  internal Java stack traces. Exposing stack traces is a severe cybersecurity vulnerability. It leaks our underlying framework names, exact version numbers, and internal class structures (like `com.smartcampus...`). An attacker could weaponize this footprinting data to cross-reference known software vulnerabilities (CVEs), drastically lowering the effort required to execute a targeted exploit against our API.

**5.5 API Request & Response Logging Filters**
By utilizing JAX-RS logging filters (like `ContainerRequestFilter`), we intercept incoming and outgoing traffic globally at the framework level. If didn't use filters, we would have to manually type `Logger.info()` inside every single `@GET`, `@POST`, and `@DELETE` method across all our resource classes. This manual approach clutters the core business logic, violates the DRY (Don't Repeat Yourself) principle, and introduces human error (as a developer might easily forget to log a newly added method). Filters keep our code perfectly clean while guaranteeing 100% logging coverage.
