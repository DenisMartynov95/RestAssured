import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.qameta.allure.internal.shadowed.jackson.databind.SerializationFeature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.TestsData.DataForAddNewPlace;
import org.example.TestsData.DataForChangeUserName;
import org.example.TestsData.DataForLoginTest;
import org.example.extractFromJson.predtest.Root;
import org.example.extractFromJson.thirdtest.Data;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;


public class PositiveTests {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-mesto.praktikum-services.ru/";
    }

    @Before
    // Перед каждым тестом необходимо авторизироваться на тестовый аккаунт и получать токен. Я конечно мог поступить проще, и просто внедрять готовый токен в каждый тест, но я хочу специально в этот раз так усложнить
    @Test
    @Step
    @DisplayName("Предтест: Авторизация + изъятие приходящего токена в переменную")
    @Description("Тест на эндпоинт 'api/signin' для авторизации и проверка статус кода. А так же извлекаю приходящий Jwt Token в класс в пакете Tests Data")
    public void loginOnMestoPageAndSaveJwtToken() {
        DataForLoginTest forLogin = new DataForLoginTest("jensenmart@yandex.ru", "123456");
        Response response;
        response = given()
                .header("Content-type", "application/json")
                .and()
                .body(forLogin)
                .when()
                .post("api/signin");
                response.then().assertThat().statusCode(200); // Тест на авторизацию и проверку статус кода

        /*
            Далее заканчиваю тест извлечением приходящего jwt token в ответе на авторизацию
                                                                                                   */
        Root root = response.then().extract().body().as(Root.class); // Извлекаю пришедший ответ в Pojo класс. Для этого создаю экземпляр класса и помещаю в него ответ

        String acceptToken = response.then().extract().path("token"); // Так же ОТДЕЛЬНО в ОТДЕЛЬНУЮ СТАТИЧНУЮ переменную извлекаю пришедший токен
        Root.setToken(acceptToken); // В созданную для этого статичную переменную в классе Root, путем сеттера

        assertNotNull(root.getData()); // Проверяю что Объект не пустой в итоге
        assertNotNull(acceptToken); // Проверяю что конкретно поле token у данного класса не пустое
//        System.out.println(acceptToken); // Вывожу токен, чтобы проверить

    }

    @Test
    @Step
    @DisplayName("Тест №1: Изменение имени пользователя")
    @Description("Тест на эндпоинт 'api/users/me', проверка статус кода и изменился ли никнейм приходящий в json")
    public void changeUserName() {
        DataForChangeUserName forChangeUserName = new DataForChangeUserName("Денис","QA Auto");
        Response response;
        response = given()
                .header("Content-type", "application/json")
                .auth().oauth2(Root.getToken())
                .and()
                .body(forChangeUserName)
                .when()
                .patch("api/users/me");
                response.then().assertThat().statusCode(200)
                        .and().assertThat().body("data.name", equalTo("Денис"));
                System.out.println("Тест №1 прошел успешно! Имя изменилось");
    }

    @Test
    @Step
    @DisplayName("Тест №2: Добавление нового места")
    @Description("Тест на эндпоинт 'api/cards', проверка статус кода и добавилось ли новое место")
    public void addNewPlace() {
        DataForAddNewPlace forAddNewPlace = new DataForAddNewPlace("ВП Сити", "https://gorodarus.ru/images/vyatpolyani/vyatskie-polyani4.jpg");
        Response response;
        response = given()
                .header("Content-type", "application/json")
                .auth().oauth2(Root.getToken())
                .body(forAddNewPlace)
                .when()
                .post("api/cards");
                response.then().assertThat().statusCode(201)
                        .and().assertThat().body("data.name", equalTo("ВП Сити"));
        System.out.println("Тест №2 прошел успешно! Новое место добавилось");
    }

    @Test
    @Step
    @DisplayName("Тест №3: Постановка лайка")
    @Description("Тест на эндпоинт 'api/cards/64b6a97a453cdc0042ff3d44/likes', проверка статус кода и валидного количества лайков")
    public void addLikeOnPlace() {
        Response response;
        response = given()
                .header("Content-type", "application/json")
                .auth().oauth2(Root.getToken())
                .when()
                .put("api/cards/64b6a97a453cdc0042ff3d44/likes");
        response.then().assertThat().statusCode(200);

        Root root = response.then().extract().body().as(Root.class); // Сначала заполню класс Root приходящим JSON
        assertNotNull(root); // Проверяю что в итоге данный класс заполнен пришедшим JSON

        System.out.println(root.getData().toString()); // ПОКА НЕ РАБОТАЕТ!, НУЖНО ПРИДУМАТЬ КАК ПРОВЕРИТЬ, ЧТО ПЕРЕМЕННЫЕ В DATA ЗАПОЛНЕНЫ И РАБОТАТЬ С getLikes() у Data, сравнивая с _id в ROot

//        // Начинаю проверку только определенного индекса, который записался в лист likes
//        ArrayList<String> checkLikes = data.getLikes();
//        System.out.println(checkLikes);

//                .and().assertThat().body("data.")
    }
}


