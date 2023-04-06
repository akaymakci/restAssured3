import POJO.Location;
import POJO.ToDo;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class Tasks {

    /** Task 1
     * create a request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * Converting Into POJO
     */
    @Test
    public void extractingJsonPOJO() {

    ToDo todo=
        given()

                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")
                // linki ve metodu veriyoruz

                .then()
                .extract().as(ToDo.class);
//                .log().body() //.log().all()
//                .statusCode(200)
        ;

        //System.out.println("todo = " + todo);
        System.out.println("todo.getId() = " + todo.getTitle());
        
    }

    /**
     * Task 2
     * create a request to https://httpstat.us/203
     * expect status 203
     * expect content type TEXT
     */

    @Test
    public  void task2()
    {
        given()
                .when()
                .get("https://httpstat.us/203")

                .then()
                .log().body()
                .statusCode(203)
                .contentType(ContentType.TEXT)
        ;
    }

    /**
     * Task 3
     * create a request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * expect content type JSON
     * expect title in response body to be "quis ut nam facilis et officia qui"
     */

    @Test
    public void task3()
    {
        given()
                .when()
                .get("https://jsonplaceholder.typicode.com/todos/2")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("title", equalTo("quis ut nam facilis et officia qui"))
        ;
    }

    /** Task 4
     * create a request to https://jsonplaceholder.typicode.com/todos
     * expect status 200
     * expect content type JSON
     * expect third item have:
     *      title = "fugiat veniam minus"
     *      userId = 1
     */

    @Test
    public void task4() {
        //List<String> list =

            given()
                    .when()
                    .get("https://jsonplaceholder.typicode.com/todos")
                    .then()
                    .log().body()
                    .body("[2].title", equalTo("fugiat veniam minus"))
                    .body("[2].userId", equalTo(1))
                    .statusCode(200)
                    .contentType(ContentType.JSON)

                    //.extract().path("userId")
            ;

//        System.out.println("list = " + list);
//        System.out.println("list.get(2) = " + list.get(20));
        //System.out.println("str = " + str);
    }

    
}
