package qtriptest.APITests;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
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

import java.io.File;
import java.util.LinkedHashMap;
import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
public class testCase_API_02 {


    
        // Set the base URI for the API
        static {
            RestAssured.baseURI = "https://content-qtripdynamic-qa-backend.azurewebsites.net/api/v1";  // Replace with your actual base URL
        }
    
        @Test(groups = {"API Tests"})
    public void testSearchCityReturnsCorrectNumberOfResults() {
        // 1. Search for "beng" using the cities search API
        String response = given()
            .contentType(ContentType.JSON)
            .param("q", "beng")  // Search term "beng"
            .when()
            .get("/cities")  // Replace with the actual endpoint for searching cities
            .then()
            .statusCode(200)  // Assert status code 200 (OK)
            .extract().asString();  // Extract the response body as a string

        // Print the actual response for inspection
        System.out.println("Search Response: " + response);

        // 2. Now check the response size or specific conditions based on the actual data returned
        // For example, if you want to check if there is a city with a description containing "100+ Places":
        given()
        .contentType(ContentType.JSON) // Specify the content type as JSON
        .param("q", "beng") // Add the query parameter
        .when()
        .get("/cities") // Specify the endpoint
        .then()
        .statusCode(200) // Verify the status code is 200
        .body("size()", equalTo(1)) // Verify the response contains exactly one result
        .body("[0].description", containsString("100+ Places")); 
    }
    }
    
