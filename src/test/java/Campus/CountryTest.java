package Campus;

import Campus.Model.Country;
import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CountryTest {
    Cookies cookies;
    @BeforeClass
    //@Test
    public void loginCampus(){
        baseURI = "https://test.mersys.io/";

        Map<String, String> credential = new HashMap<>();
        credential.put("username","turkeyts");
        credential.put("password","TechnoStudy123");
        credential.put("rememberMe","true");

        cookies =
        given()
                .contentType(ContentType.JSON)
                .body(credential)

                .when()
                .post("auth/login")

                .then()
                .log().body()
                .statusCode(200)
                .extract().response().getDetailedCookies()

        ;
        //String cookies =
        //System.out.println(cookies);
    }

    Faker faker = new Faker();
    String countryID;
    String countryName;
    String countryShortName;
    String countryCode;

    @Test
    public void createCountry(){

        countryName = faker.country().name()+ " " + (int)(Math.random()*10000);
        countryShortName  = faker.country().countryCode3()+(int)(Math.random()*100);
        countryCode = faker.country().countryCode2()+(int)(Math.random()*10);

        Country country = new Country();
        country.setName(countryName);
        country.setShortName(countryShortName);
        country.setCode(countryCode);

        countryID =
        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(country)

                .when()
                .post("school-service/api/countries")

                .then()
                .log().body()
                .statusCode(201)
                .extract().jsonPath().getString("id")
        ;
        System.out.println("countryID = " + countryID);
    }

    @Test (dependsOnMethods = "createCountry") // dependsonmethods olmadan negative kisim calismiyor.
    public void createCountryNegative(){

        Country country = new Country();
        country.setName(countryName);
        country.setCode(countryCode);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(country)

                .when()
                .post("school-service/api/countries")

                .then()
                //.contentType(ContentType.ANY)
                .log().body()
                .statusCode(400)
                .body("message", equalTo("The Country with Name \""+countryName+"\" already exists."))

        ;
        System.out.println("countryID = " + countryID);
    }
    String updateCountryName;
    String updateCountryShortName;
    String updateCountryCode;


    @Test(dependsOnMethods = "createCountry", priority = 1)
    public void updateCountry(){

        updateCountryName = faker.country().name()+ " " + (int)(Math.random()*10000);
        updateCountryShortName  = faker.country().countryCode3()+(int)(Math.random()*100);
        updateCountryCode = faker.country().countryCode2()+(int)(Math.random()*10);

        Country country = new Country();
        country.setId(countryID);
        country.setName(updateCountryName);
        country.setShortName(updateCountryShortName);
        country.setCode(updateCountryCode);

        countryID =
                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(country)

                        .when()
                        .put("school-service/api/countries")

                        .then()
                        //.contentType(ContentType.ANY)
                        .log().body()
                        .statusCode(200)
                        .extract().jsonPath().getString("id")
        ;
        System.out.println("countryID = " + countryID);
    }

    @Test(dependsOnMethods = "createCountry", priority = 2)
    public void deleteCountryById(){

                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .pathParam("countryID", countryID)

                        .when()
                        .delete("school-service/api/countries/{countryID}")

                        .then()
                        .log().body()
                        .statusCode(400)

        ;
    }
    @Test(dependsOnMethods = "deleteCountryById")
    public void deleteCountryByIdNegative(){

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .pathParam("countryID", countryID)

                .when()
                .delete("school-service/api/countries/{countryID}")

                .then()
                .log().body()
                .statusCode(400)

        ;
    }
    @Test(dependsOnMethods = "deleteCountryById")
    public void updateCountryNegative(){

        updateCountryName = faker.country().name()+ " " + (int)(Math.random()*10000);
        updateCountryShortName  = faker.country().countryCode3()+(int)(Math.random()*100);
        updateCountryCode = faker.country().countryCode2()+(int)(Math.random()*10);

        Country country = new Country();
        country.setId(countryID);
        country.setName(updateCountryName);
        country.setShortName(updateCountryShortName);
        country.setCode(updateCountryCode);

        countryID =
                given()
                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(country)

                        .when()
                        .put("school-service/api/countries")

                        .then()
                        //.contentType(ContentType.ANY)
                        .log().body()
                        .statusCode(400)
                        .extract().jsonPath().getString("id")
        ;
        System.out.println("countryID = " + countryID);
    }

}
