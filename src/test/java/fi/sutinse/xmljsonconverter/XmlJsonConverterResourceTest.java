package fi.sutinse.xmljsonconverter;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class XmlJsonConverterResourceTest {

    @Test
    public void testJsonEndpointWithMatchingData() {
        String requestBody = "{\n" +
                "  \"xmlContent\": \"<person><name>John</name><age>30</age></person>\",\n" +
                "  \"jsonContent\": \"{\\\"name\\\":\\\"John\\\",\\\"age\\\":\\\"30\\\"}\"\n" +
                "}";

        given()
                .when()
                .contentType("application/json")
                .body(requestBody)
                .post("/convert/json")
                .then()
                .statusCode(200)
                .body(containsString("MATCH"));
    }

    @Test
    public void testJsonEndpointWithDifferentData() {
        String requestBody = "{\n" +
                "  \"xmlContent\": \"<person><name>John</name><age>30</age></person>\",\n" +
                "  \"jsonContent\": \"{\\\"name\\\":\\\"Jane\\\",\\\"age\\\":\\\"25\\\"}\"\n" +
                "}";

        given()
                .when()
                .contentType("application/json")
                .body(requestBody)
                .post("/convert/json")
                .then()
                .statusCode(200)
                .body(containsString("DIFFERENCES FOUND"));
    }

    @Test
    public void testJsonEndpointWithMissingXml() {
        String requestBody = "{\n" +
                "  \"jsonContent\": \"{\\\"name\\\":\\\"John\\\",\\\"age\\\":\\\"30\\\"}\"\n" +
                "}";

        given()
                .when()
                .contentType("application/json")
                .body(requestBody)
                .post("/convert/json")
                .then()
                .statusCode(500); // This will be an error because xmlContent is null
    }

    @Test
    public void testJsonEndpointWithComplexXml() {
        String requestBody = "{\n" +
                "  \"xmlContent\": \"<company><employees><employee><name>John</name><department>IT</department></employee><employee><name>Jane</name><department>HR</department></employee></employees></company>\",\n" +
                "  \"jsonContent\": \"{\\\"employees\\\":{\\\"employee\\\":[{\\\"name\\\":\\\"John\\\",\\\"department\\\":\\\"IT\\\"},{\\\"name\\\":\\\"Jane\\\",\\\"department\\\":\\\"HR\\\"}]}}\"\n" +
                "}";

        given()
                .when()
                .contentType("application/json")
                .body(requestBody)
                .post("/convert/json")
                .then()
                .statusCode(200)
                .body(containsString("XML to JSON Conversion and Comparison Report"));
    }
}