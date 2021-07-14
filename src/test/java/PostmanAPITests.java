import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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

    @Test
    public void postmanGetSingleCollection() {

        baseURI = collectionsBaseURI;
        String collectionUid = "16604891-86ad9b7b-1b71-4930-a600-0ab43ac3927f";

        given()
            .header( "X-Api-Key", apiKey )
            .get( "/" + collectionUid )
        .then()
            .statusCode( 200 )
            .body( "collection.info.name", equalTo( "Postman API" ) )
            .log().all();
    }

    @Test
    public void postmanCreateCollection() {

        baseURI = collectionsBaseURI;
        String workspaceId = "d13fbb01-ecea-49ce-86dd-19ecbf5ec1d5"; // Postman API Tests workspace.

        JSONParser parser = new JSONParser();
        JSONObject request = new JSONObject();

        try {
            request = (JSONObject) parser.parse("""
                    {
                        "collection": {
                             "info": {
                                "name": "Reqres Testing API",
                                "description": "Reqres is a test resource for REST APIs.",
                                "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
                             },
                             "item": [
                                {
                                    "name": "List Users",
                                    "request": {
                                        "url": "https://reqres.in/api/users?page=1",
                                        "method": "GET",
                                        "description": "Lists 6 users per page",
                                        "header": [],
                                        "body": {
                                            "mode": "raw",
                                            "raw": ""
                                        }
                                    },
                                    "response": []
                                },
                                {
                                    "name": "Single User",
                                    "request": {
                                        "url": "https://reqres.in/api/users/1",
                                        "method": "GET",
                                        "description": "Gets single user given ID",
                                        "header": [],
                                        "body": {
                                            "mode": "raw",
                                            "raw": ""
                                        }
                                    },
                                    "response": []
                                },
                                {
                                    "name": "Create User",
                                    "request": {
                                        "url": "https://reqres.in/api/users",
                                        "method": "POST",
                                        "description": "Creates a new user",
                                        "header": [
                                            {
                                                 "key": "Content-Type",
                                                 "value": "application/json",
                                                 "description": ""
                                            }
                                        ],
                                        "body": {
                                            "mode": "raw",
                                            "raw": "{\\"name\\": \\"ado387\\", \\"job\\": \\"developer\\"}"
                                        }
                                    },
                                    "response": []
                                }
                             ]
                        }
                    }
                    """
            );
        } catch ( ParseException e ) {
            System.out.println( e );
        }

        given()
            .header( "X-Api-Key", apiKey )
            .queryParam( "workspace", workspaceId )
            .body( request.toJSONString() )
        .when()
            .post()
        .then()
            .statusCode( 200 )
            .body( "collection", hasKey( "id") )
            .log().all();

    }
}
