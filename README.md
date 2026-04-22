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
