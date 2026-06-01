package qtriptest.APITests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;
import io.restassured.RestAssured;
import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class testCase_API_01 {
    
    // Constants
    private static final String BASE_URI = "https://content-qtripdynamic-qa-backend.azurewebsites.net/api/v1";
    private static final String REGISTER_ENDPOINT = "/register";
    private static final String LOGIN_ENDPOINT = "/login";
    private static final int EXPECTED_STATUS_CODE = 201;
    private static final String TEST_PASSWORD = "s@gmail.com";
    
    private String testEmail;
    
    static {
        RestAssured.baseURI = BASE_URI;
    }

    @BeforeMethod
    public void setUp() {
        // Generate unique email for each test
        String uniqueId = UUID.randomUUID().toString();
        testEmail = "testuser" + uniqueId + "@gmail.com";
    }

    @Test(groups = {"API Tests"}, description = "Register a new user with valid credentials")
    public void testUserRegistration() {
        String requestBody = createRegistrationRequest(testEmail, TEST_PASSWORD, TEST_PASSWORD);

        Response response = given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post(REGISTER_ENDPOINT)
            .then()
            .statusCode(EXPECTED_STATUS_CODE)
            .body("success", equalTo(true))
            .extract()
            .response();

        Assert.assertNotNull(response.jsonPath().getString("data.userId"), 
            "User ID should be returned in registration response");
    }

    @Test(groups = {"API Tests"}, description = "Login with registered user credentials")
    public void testUserLogin() {
        // First register the user
        String registrationBody = createRegistrationRequest(testEmail, TEST_PASSWORD, TEST_PASSWORD);
        given()
            .contentType(ContentType.JSON)
            .body(registrationBody)
            .when()
            .post(REGISTER_ENDPOINT)
            .then()
            .statusCode(EXPECTED_STATUS_CODE);

        // Then login
        String loginBody = createLoginRequest(testEmail, TEST_PASSWORD);
        Response loginResponse = given()
            .contentType(ContentType.JSON)
            .body(loginBody)
            .when()
            .post(LOGIN_ENDPOINT)
            .then()
            .statusCode(EXPECTED_STATUS_CODE)
            .body("success", equalTo(true))
            .body("token", not(emptyString()))
            .extract()
            .response();

        String token = loginResponse.jsonPath().getString("token");
        Assert.assertNotNull(token, "Authentication token should be returned");
        System.out.println("Login successful. Token: " + token);
    }

    @Test(groups = {"API Tests"}, description = "Verify login fails with incorrect password")
    public void testLoginWithInvalidPassword() {
        // Register with correct password
        String registrationBody = createRegistrationRequest(testEmail, TEST_PASSWORD, TEST_PASSWORD);
        given()
            .contentType(ContentType.JSON)
            .body(registrationBody)
            .when()
            .post(REGISTER_ENDPOINT)
            .then()
            .statusCode(EXPECTED_STATUS_CODE);

        // Attempt login with wrong password
        String loginBody = createLoginRequest(testEmail, "wrongpassword");
        given()
            .contentType(ContentType.JSON)
            .body(loginBody)
            .when()
            .post(LOGIN_ENDPOINT)
            .then()
            .statusCode(401); // Unauthorized
    }

    // Helper methods
    private String createRegistrationRequest(String email, String password, String confirmPassword) {
        return "{ \"email\": \"" + email + "\", \"password\": \"" + password 
            + "\", \"confirmpassword\": \"" + confirmPassword + "\" }";
    }

    private String createLoginRequest(String email, String password) {
        return "{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }";
    }
}
