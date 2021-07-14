import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class PostmanAPITests {

    static String collectionsBaseURI = "https://api.getpostman.com/collections";
    static String apiKey = "PMAK-60ecb6041c424a005ca4a1ef-5b8ff2f955e654b7ad5914fe6db815e72d";

    @Test
    public void postmanGetAllCollections() {

        baseURI = collectionsBaseURI;

        String[] collections = {
                "1021f472-9cbd-40df-91e3-b61b53777295",
                "86ad9b7b-1b71-4930-a600-0ab43ac3927f",
                "e8b4cb39-75c2-458a-822f-abcaae897570"
        };

        given()
            .header( "X-Api-Key", apiKey )
            .get()
        .then()
            .statusCode( 200 )
            .body( "collections.id", hasItems( collections ) )
            .log().all();
    }
}
