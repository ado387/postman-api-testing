import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class PostmanAPITests {

    static String collectionsBaseURI = "https://api.getpostman.com/collections";
    static String apiKey = "";

    @Test
    public void postmanGetAllCollections() {

        baseURI = collectionsBaseURI;

        String[] collections = {
                "84c08e9e-dc49-4855-93fe-f97d4cc3a0f5",
                "f31f74c7-e793-4a88-b113-74a017e13128",
                "8a6c602c-bdaa-4ad7-9f76-b39556f4b925"
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
                                "name": "Example Collection",
                                "description": "Example collection for testing postman API.",
                                "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
                             },
                             "item": [
                                {
                                    "name": "All resources",
                                    "request": {
                                        "url": "https://example.com/api/resources",
                                        "method": "GET",
                                        "description": "Lists all resources",
                                        "header": [],
                                        "body": {
                                            "mode": "raw",
                                            "raw": ""
                                        }
                                    },
                                    "response": []
                                },
                                {
                                    "name": "Single resource",
                                    "request": {
                                        "url": "https://example.com/api/resources/1",
                                        "method": "GET",
                                        "description": "Gets single resource given ID",
                                        "header": [],
                                        "body": {
                                            "mode": "raw",
                                            "raw": ""
                                        }
                                    },
                                    "response": []
                                },
                                {
                                    "name": "Create resource",
                                    "request": {
                                        "url": "https://example.com/api/resources",
                                        "method": "POST",
                                        "description": "Creates a new resource",
                                        "header": [
                                            {
                                                 "key": "Content-Type",
                                                 "value": "application/json",
                                                 "description": ""
                                            }
                                        ],
                                        "body": {
                                            "mode": "raw",
                                            "raw": "{resource: [] }"
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

    @Test
    public void postmanMissingNameParamTest() {

        baseURI = collectionsBaseURI;
        String workspaceId = "d13fbb01-ecea-49ce-86dd-19ecbf5ec1d5"; // Postman API Tests workspace.

        JSONParser parser = new JSONParser();
        JSONObject request = new JSONObject();

        try {
            request = (JSONObject) parser.parse("""
                    {
                        "collection": {
                             "info": {
                                "description": "Reqres is a test resource for REST APIs.",
                                "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
                             }
                             "item": []
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
            .statusCode( 400 )
            .body( "error.details[0]", equalTo( "info: must have required property 'name'" ) )
            .log().all();
    }

    @Test
    public void postmanMissingItemParamTest() {

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
                             }
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
            .statusCode( 400 )
            .body( "error.details[0]", equalTo( ": must have required property 'item'" ) )
            .log().all();
    }

    @Test
    public void postmanNonExistentWorkspaceTest() {

        baseURI = collectionsBaseURI;
        String workspaceId = "d13fbb01-ecea-49ce-86dd"; // Non-existing workspace ID.

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
                             "item": []
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
            .statusCode( 400 )
            .body( "error.name", equalTo( "instanceNotFoundError" ) )
            .log().all();
    }

    @Test
    public void postmanCreateUniqueCollections() {
        baseURI = collectionsBaseURI;
        String workspaceId = "d13fbb01-ecea-49ce-86dd-19ecbf5ec1d5"; // Postman API Tests workspace.

        JSONParser parser = new JSONParser();
        JSONObject request = new JSONObject();

        // Create: Reqres Testing API collection.
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
            .body( "collection.name", equalTo( "Reqres Testing API") )
            .log().all();



        // Create: OpenWeather API collection.
        try {
            request = (JSONObject) parser.parse("""
                    {
                    	"collection": {
                    		 "info": {
                    			"name": "OpenWeather API",
                    			"description": "Get current data given city name and api key.",
                    			"schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
                    		 },
                    		 "item": [
                    			{
                    				"name": "Current Weather Sarajevo",
                    				"request": {
                    					"url": "api.openweathermap.org/data/2.5/weather?q=Sarajevo&appid=918896278e907ea175546cee3415f5be",
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
                    				"name": "Current Weather Tuzla",
                    				"request": {
                    					"url": "api.openweathermap.org/data/2.5/weather?q=Tuzla&appid=918896278e907ea175546cee3415f5be",
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
                    				"name": "Current Weather Zenica",
                    				"request": {
                    					"url": "api.openweathermap.org/data/2.5/weather?q=Zenica&appid=918896278e907ea175546cee3415f5be",
                    					"method": "GET",
                    					"description": "Lists 6 users per page",
                    					"header": [],
                    					"body": {
                    						"mode": "raw",
                    						"raw": ""
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
            .body( "collection.name", equalTo( "OpenWeather API") )
            .log().all();



        // Create: Postman API collection.
        try {
            request = (JSONObject) parser.parse("""
                    {
                     	"collection": {
                     		 "info": {
                     			"name": "Postman API",
                     			"description": "Get and create Postman collections.",
                     			"schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
                     		 },
                     		 "item": [
                     			{
                     				"name": "All Collections",
                     				"request": {
                     					"url": "https://api.getpostman.com/collections",
                     					"method": "GET",
                     					"description": "Lists all collections.",
                     					"header": [],
                     					"body": {
                     						"mode": "raw",
                     						"raw": ""
                     					}
                     				},
                     				"response": []
                     			},
                     			{
                     				"name": "Single Collection",
                     				"request": {
                     					"url": "https://api.getpostman.com/collections/16604891-8a6c602c-bdaa-4ad7-9f76-b39556f4b925",
                     					"method": "GET",
                     					"description": "Fetches single collection given its uid.",
                     					"header": [],
                     					"body": {
                     						"mode": "raw",
                     						"raw": ""
                     					}
                     				},
                     				"response": []
                     			},
                     			{
                     				"name": "Create Collection",
                     				"request": {
                     					"url": "https://api.getpostman.com/collections?workspace=d13fbb01-ecea-49ce-86dd",
                     					"method": "POST",
                     					"description": "Create a collection. Body leftout for simplicity.",
                     					"header": [],
                     					"body": {
                     						"mode": "raw",
                     						"raw": ""
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
            .body( "collection.name", equalTo( "Postman API") )
            .log().all();
    }
}
