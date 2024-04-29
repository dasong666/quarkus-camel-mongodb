package org.myeii.labs;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class MyMongoDBAppTest {

    @Test
    public void testPutJsonEndpoint() {
        String requestBody = """
            [
                {"key1": "value1", "key2": "value2"}
            ]
            """;

        given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .put("/putajson?dbname=testDB&collectionName=testCollection")
            .then()
            .statusCode(200)
            .body("message", equalTo("Data inserted successfully into testDB.testCollection"),
                  "insertedCount", equalTo(1),
                  "insertedIds.size()", equalTo(1));  // You can also check other properties if needed
    }
}
