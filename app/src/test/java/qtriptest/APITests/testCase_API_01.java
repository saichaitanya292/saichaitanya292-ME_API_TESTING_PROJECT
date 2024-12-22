package qtriptest.APITests;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ResponseBody;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.RestAssured;
import java.util.UUID;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class testCase_API_01 {
    static {
        RestAssured.baseURI = "https://content-qtripdynamic-qa-backend.azurewebsites.net/api/v1"; // Replace with your actual base URL
    }

    // Test Case to register a user and then login
    @Test(groups = {"API Tests"})
    public void testUserRegistrationAndLogin() {
        String uniqueId = UUID.randomUUID().toString();

        // Create the email address with a unique UUID
        String email = "testuser" + uniqueId + "@gmail.com";
        String requestBody = "{ \"email\": \"" + email + "\", \"password\": \"s@gmail.com\", \"confirmpassword\": \"s@gmail.com\" }";

        // 1. Register a new user/home/crio-user/workspace/saichaitanya292-ME_API_TESTING_PROJECT/testng.xml
        Response registerResponse = given()
            .contentType(ContentType.JSON)
            .body(requestBody)  // Replace with actual request body
            .when()
            .post("/register")  // Replace with the actual register API endpoint
            .then()
            .statusCode(201)  // Check for status code 201
            .extract().response();

    

       String loginrequestBody = "{ \"email\": \"" + email + "\", \"password\": \"s@gmail.com\"}";
        Response loginResponse = given()
            .contentType(ContentType.JSON)
            .body(loginrequestBody)  
            .when()
            .post("/login")  // Replace with the actual login API endpoint
            .then()
            .statusCode(201)  // Check for status code 201 (successful login)
            .body("success", equalTo(true))  // Check if the success flag is true
            .body("token", not(emptyString()))  // Check if the token is returned
            .extract().response();

        // Optionally, you can print the login response for debugging
        System.out.println("Login Response: " + loginResponse.asString());
    }
    
   
}
