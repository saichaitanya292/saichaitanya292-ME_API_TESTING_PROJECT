package qtriptest.APITests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import qtriptest.config.APIConfig;
import org.testng.Assert;
import org.testng.annotations.*;
import io.restassured.RestAssured;
import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class testCase_API_01 {
    
    private APIConfig apiConfig;
    private String testEmail;
    
    static {
        // Initialize RestAssured with base URI from config
        RestAssured.baseURI = APIConfig.getInstance().getBaseURI();
    }

    @BeforeMethod
    public void setUp() {
        // Get configuration instance
        apiConfig = APIConfig.getInstance();
        
        // Generate unique email for each test
        String uniqueId = UUID.randomUUID().toString();
        testEmail = apiConfig.generateTestEmail(uniqueId);
        
        if (apiConfig.isDebugMode()) {
            System.out.println("Test Email: " + testEmail);
            System.out.println("Configuration: " + apiConfig);
        }
    }

    @Test(groups = {"API Tests"}, description = "Register a new user with valid credentials")
    public void testUserRegistration() {
        String requestBody = createRegistrationRequest(testEmail, apiConfig.getTestPassword(), apiConfig.getTestPassword());

        Response response = given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post(apiConfig.getRegisterEndpoint())
            .then()
            .statusCode(apiConfig.getSuccessStatusCode())
            .body("success", equalTo(true))
            .extract()
            .response();

        Assert.assertNotNull(response.jsonPath().getString("data.userId"), 
            "User ID should be returned in registration response");
        
        if (apiConfig.isDebugMode()) {
            System.out.println("Registration Response: " + response.asString());
        }
    }

    @Test(groups = {"API Tests"}, description = "Login with registered user credentials")
    public void testUserLogin() {
        // First register the user
        String registrationBody = createRegistrationRequest(testEmail, apiConfig.getTestPassword(), apiConfig.getTestPassword());
        given()
            .contentType(ContentType.JSON)
            .body(registrationBody)
            .when()
            .post(apiConfig.getRegisterEndpoint())
            .then()
            .statusCode(apiConfig.getSuccessStatusCode());

        // Then login
        String loginBody = createLoginRequest(testEmail, apiConfig.getTestPassword());
        Response loginResponse = given()
            .contentType(ContentType.JSON)
            .body(loginBody)
            .when()
            .post(apiConfig.getLoginEndpoint())
            .then()
            .statusCode(apiConfig.getSuccessStatusCode())
            .body("success", equalTo(true))
            .body("token", not(emptyString()))
            .extract()
            .response();

        String token = loginResponse.jsonPath().getString("token");
        Assert.assertNotNull(token, "Authentication token should be returned");
        
        if (apiConfig.isDebugMode()) {
            System.out.println("Login Response: " + loginResponse.asString());
        }
    }

    @Test(groups = {"API Tests"}, description = "Verify login fails with incorrect password")
    public void testLoginWithInvalidPassword() {
        // Register with correct password
        String registrationBody = createRegistrationRequest(testEmail, apiConfig.getTestPassword(), apiConfig.getTestPassword());
        given()
            .contentType(ContentType.JSON)
            .body(registrationBody)
            .when()
            .post(apiConfig.getRegisterEndpoint())
            .then()
            .statusCode(apiConfig.getSuccessStatusCode());

        // Attempt login with wrong password
        String loginBody = createLoginRequest(testEmail, "wrongpassword");
        given()
            .contentType(ContentType.JSON)
            .body(loginBody)
            .when()
            .post(apiConfig.getLoginEndpoint())
            .then()
            .statusCode(apiConfig.getUnauthorizedStatusCode());
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
