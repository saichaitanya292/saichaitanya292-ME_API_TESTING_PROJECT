package qtriptest.APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.openqa.selenium.json.Json;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class testCase_API_03 {

    static {
        RestAssured.baseURI = "https://content-qtripdynamic-qa-backend.azurewebsites.net/api/v1";  // Replace with the actual base URL of QTrip API
    }

    @Test(groups = {"API Tests"})
    public void testReservationProcess() {
        // Step 1: Create a new user using the registration API
        String uniqueId = UUID.randomUUID().toString();

        // Create the email address with a unique UUID
        String email = "testuser" + uniqueId + "@gmail.com";
        String requestBody = "{ \"email\": \"" + email + "\", \"password\": \"s@gmail.com\", \"confirmpassword\": \"s@gmail.com\" }";

        Response registerResponse = given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/register")  // Replace with the actual registration endpoint
            .then()
            .statusCode(201)  // Expected status code for successful registration
            .extract().response();

       
        String loginrequestBody = "{ \"email\": \"" + email + "\", \"password\": \"s@gmail.com\"}";
        // Step 2: Perform login with the newly created user
        Response loginResponse = given()
            .contentType(ContentType.JSON)
            .body(loginrequestBody)
            .when()
            .post("/login")  // Replace with the actual login endpoint
            .then()
            .statusCode(201)  // Expected status code for successful login
            .extract().response();

        // Extract the token from the login response
        System.out.println("login Response: " + loginResponse.asString());
        String token = loginResponse.jsonPath().getString("data.token");
        String userId = loginResponse.jsonPath().getString("data.id");
        System.out.println("userId" + userId);
        System.out.println("Extracted Token: " + token);

        // Step 3: Perform a booking (POST call) using the user ID and token
        String bookingPayload = "{"
            + "\"userId\": \"" + userId + "\","
            + "\"name\": \"testuser\","
            + "\"date\": \"2025-09-09\","
            + "\"person\": \"1\","
            + "\"adventure\": \"2447910730\""
            + "}";

        Response bookingResponse = given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .body(bookingPayload)
            .when()
            .post("/reservations/new")  
            .then()
            .statusCode(200)  // Expected status code for successful booking
            .extract().response();

        // Step 4: Perform a GET request to retrieve the user's reservations and ensure the booking appears
        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + token)
            .param("id", userId)
            .when()
            .get("/reservations")  // Replace with the actual endpoint for retrieving reservations
            .then()
            .statusCode(200)  // Ensure the request returns status code 200
            .body("size()", greaterThan(0))  // Ensure that there is at least one reservation in the list
            .body("userId", hasItem(userId))  // Ensure that the user ID is associated with the booking
            .body("adventure", hasItem("2447910730"));  // Ensure that the adventure ID matches the booking
    }
}
