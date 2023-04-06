package GoRest;

import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GoRestUsersTests {

    @BeforeClass
    void Setup() {

        // RestAssured kendi statik değişkeni tanımlı değer atanıyor.
        baseURI = "https://gorest.co.in/public/v2/";
    }

    Faker faker = new Faker();
    String name = faker.name().firstName();
    FakeValuesService fakeValuesService = new FakeValuesService(
            new Locale("tr-TR"), new RandomService());
    String email = fakeValuesService.bothify("????##@gmail.com");
    public String getRandomEmail(){
        return RandomStringUtils.randomAlphabetic(8)+"@gmail.com";
    }
    public String getRandomName(){
        return RandomStringUtils.randomAlphabetic(8);
    }

    public static String getRandomStatus(){
        int randomInt =(int)(Math.random()*2)+1;
        if (randomInt==1)
            return "active";
        else
            return "inactive";
    }

    public static String getRandomGender(){
        int randomInt =(int)(Math.random()*2)+1;
        if (randomInt==1)
            return "male";
        else
            return "female";
    }

    String status = getRandomStatus();
    String gender = getRandomGender();
    User newUser;
    int userID = 0;
    @Test
    public void createUserObject()
    {
        newUser = new User();
        newUser.setName(name);
        newUser.setGender(gender);
        newUser.setEmail(email);
        newUser.setStatus(status);

        userID=
                given()
                        .header("Authorization","Bearer 0abf64eeeda1c2049dd648df4603d4be8f4eb7ce20a07c91c8779a617c63bdb8")
                        .contentType(ContentType.JSON)
                        .body(newUser)
                        .log().body()
                        .when()
                        .post("users")

                        .then()
                        .log().body() // log().body() cikan sonucun tamamini ekrana yazdirir.
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        //.extract().path("id")
                        .extract().jsonPath().getInt("id")

                ;

        // path : class veya tip dönüşümüne imkan veremeyen direk veriyi verir. List<String> gibi
        // jsonPath : class dönüşümüne ve tip dönüşümüne izin vererek , veriyi istediğimiz formatta verir.

        System.out.println("userID = " + userID);

    }
    String updateName = faker.name().firstName();
    @Test(dependsOnMethods = "createUserObject", priority = 1)
    public void updateUserObject()
    {
        newUser.setName(updateName);


                given()
                        .header("Authorization","Bearer 0abf64eeeda1c2049dd648df4603d4be8f4eb7ce20a07c91c8779a617c63bdb8")
                        .contentType(ContentType.JSON)
                        .body(newUser)
                        .log().body()
                        .pathParam("userID", userID)

                        .when()
                        .put("users/{userID}")

                        .then()
                        .log().body() // log().body() cikan sonucun tamamini ekrana yazdirir.
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .body("name", equalTo(updateName))


                ;

        System.out.println("userID = " + userID);

    }

    @Test(dependsOnMethods = "createUserObject", priority = 2)
    public void getUserByID()
    {

        given()
                .header("Authorization","Bearer 0abf64eeeda1c2049dd648df4603d4be8f4eb7ce20a07c91c8779a617c63bdb8")
                .contentType(ContentType.JSON)
                .log().body()
                .pathParam("userID", userID)

                .when()
                .get("users/{userID}")

                .then()
                .log().body() // log().body() cikan sonucun tamamini ekrana yazdirir.
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(userID))

        ;


    }

    @Test(dependsOnMethods = "createUserObject", priority = 3)
    public void deleteUserById()
    {

        given()
                .header("Authorization","Bearer 0abf64eeeda1c2049dd648df4603d4be8f4eb7ce20a07c91c8779a617c63bdb8")
                .contentType(ContentType.JSON)

                .log().body()
                .pathParam("userID", userID)

                .when()
                .delete("users/{userID}")

                .then()
                .log().body()
                .statusCode(204)


        ;



    }
    @Test(dependsOnMethods = "deleteUserById")
    public void deleteUserByIdNegative() // Genelden calistirmak zorundayiz, ozelden
    // alistirdigimizda, sadece bir kademe geriye giderek calisiyor
    {


        given()
                .header("Authorization","Bearer 0abf64eeeda1c2049dd648df4603d4be8f4eb7ce20a07c91c8779a617c63bdb8")
                .contentType(ContentType.JSON)

                .log().body()
                .pathParam("userID", userID)

                .when()
                .delete("users/{userID}")

                .then()
                .log().body()
                .statusCode(404)


        ;

    }

    @Test()
    public void getUsers()
    {
        Response response=
        given()
                .header("Authorization","Bearer 0abf64eeeda1c2049dd648df4603d4be8f4eb7ce20a07c91c8779a617c63bdb8")

                .when()
                .get("users")

                .then()

                .statusCode(200)
                .extract().response()

        ;

        // TODO : 3 usersın id sini alınız (path ve jsonPath ile ayrı ayrı yapınız)
        int idUser3path= response.path("[3].id");
        int idUser3JsonPath = response.jsonPath().getInt("[3].id");
        System.out.println("idUser3path = " + idUser3path);
        System.out.println("idUser3JsonPath = " + idUser3JsonPath);


        // TODO : Tüm gelen veriyi bir nesneye atınız (google araştırması)
        User[] usersPath=response.as(User[].class);
        System.out.println("Arrays.toString(usersPath) = " + Arrays.toString(usersPath));

        List<User> usersJsonPath=response.jsonPath().getList("", User.class);
        System.out.println("usersJsonPath = " + usersJsonPath);





    }
    // TODO : GetUserById testinde donen useri bir nesneye atin.
    @Test()
    public void getUserByIDExtract()
    {
        User user =
        given()
                .header("Authorization","Bearer 0abf64eeeda1c2049dd648df4603d4be8f4eb7ce20a07c91c8779a617c63bdb8")
                .contentType(ContentType.JSON)
                //.log().body()
                .pathParam("userID", 778012)

                .when()
                .get("users/{userID}")

                .then()
                .log().body()
                .statusCode(200)
                //.extract().as(User.class)
                .extract().jsonPath().getObject("",User.class)
        ;
        System.out.println("user = " + user);


    }
    @Test
    public void getUsersV1() {
        Response response =
                given()
                        .header("Authorization", "Bearer 0abf64eeeda1c2049dd648df4603d4be8f4eb7ce20a07c91c8779a617c63bdb8")

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()

                        .statusCode(200)
                        .extract().response();


//              response.as(); //tüm gelen responsea uygun nesnelerin yapilmasi gerekiyor.
                List<User> dataUsers = response.jsonPath().getList("data", User.class); // JSONPATH bir response içindeki bir parçayı
        // nesneye ödnüştürebiliriz.
        System.out.println(dataUsers);

        // Daha önceki örneklerde (as) Clas dönüşümleri için tüm yapıya karşılık gelen
        // gereken tüm classları yazarak dönüştürüp istediğimiz elemanlara ulaşıyorduk.
        // Burada ise(JsonPath) aradaki bir veriyi clasa dönüştürerek bir list olarak almamıza
        // imkan veren JSONPATH i kullandık.Böylece tek class ise veri alınmış oldu
        // diğer class lara gerek kalmadan

        // path : class veya tip dönüşümüne imkan veremeyen direk veriyi verir. List<String> gibi
        // jsonPath : class dönüşümüne ve tip dönüşümüne izin vererek , veriyi istediğimiz formatta verir.

    }











    @Test (enabled = false)
    public void createUser()
    {
        int userID=
                given()
                        .header("Authorization","Bearer 0abf64eeeda1c2049dd648df4603d4be8f4eb7ce20a07c91c8779a617c63bdb8")
                        .contentType(ContentType.JSON)
                        .body("{\"name\":\""+name+"\", \"gender\":\"male\", \"email\":\""+email+"\", \"status\":\"active\"}")

                        .when()
                        .post("users")

                        .then()
                        .log().body() // log().body() cikan sonucun tamamini ekrana yazdirir.
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id")

                ;
        System.out.println("userID = " + userID);

    }
    @Test (enabled = false)
    public void createUser2()
    {
        Map<String, String > newUser = new HashMap<>();

        newUser.put("name", name);
        newUser.put("gender", gender);
        newUser.put("email", email);
        newUser.put("status", status);


        int userID=
                given()
                        .header("Authorization","Bearer 0abf64eeeda1c2049dd648df4603d4be8f4eb7ce20a07c91c8779a617c63bdb8")
                        .contentType(ContentType.JSON)
                        .body(newUser)
                        .log().body()
                        .when()
                        .post("users")

                        .then()
                        .log().body() // log().body() cikan sonucun tamamini ekrana yazdirir.
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id")

                ;
        System.out.println("userID = " + userID);

    }

}

class User{
    private int id;
    private String name;
    private String gender;
    private String email;
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
