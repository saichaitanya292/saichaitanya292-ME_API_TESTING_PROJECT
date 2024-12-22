package qtriptest.APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class testCase_API_04 {

    @Test(groups = {"API Tests"})
    public void testDuplicateUserRegistration() {
        // Step 1: Register a new user with a unique email
        String uniqueEmail = "testuser" + System.currentTimeMillis() + "@gmail.com";  // Generate a unique email
        String registrationPayload = "{"
            + "\"email\": \"" + uniqueEmail + "\","
            + "\"password\": \"password123\","
            + "\"confirmpassword\": \"password123\""
            + "}";

        // Register the first user
        Response firstRegistrationResponse = given()
            .contentType(ContentType.JSON)
            .body(registrationPayload)
            .when()
            .post("https://content-qtripdynamic-qa-backend.azurewebsites.net/api/v1/register")  // Replace with actual registration endpoint
            .then()
            .statusCode(201)  // Expected status code for successful registration
            .extract().response();

        System.out.println("First Registration Response: " + firstRegistrationResponse.asString());

        // Step 2: Try to register the same email again (Duplicate registration attempt)
        String secondRegistrationPayload = "{"
            + "\"email\": \"" + uniqueEmail + "\","
            + "\"password\": \"password123\","
            + "\"confirmpassword\": \"password123\""
            + "}";

        // Try to register again with the same email
        Response secondRegistrationResponse = given()
            .contentType(ContentType.JSON)
            .body(secondRegistrationPayload)
            .when()
            .post("https://content-qtripdynamic-qa-backend.azurewebsites.net/api/v1/register")  // Replace with actual registration endpoint
            .then()
            .statusCode(400)  // Expected status code for duplicate registration
            .body("message", equalTo("Email already exists"))  // Ensure the appropriate error message is returned
            .extract().response();

        System.out.println("Second Registration (Duplicate) Response: " + secondRegistrationResponse.asString());
    }
    }

  

