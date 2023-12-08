import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.TestsData.DataForAddNewPlace;
import org.example.TestsData.DataForChangeUserName;
import org.example.TestsData.DataForLoginTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.example.extractFromJson.Root;
import org.junit.Before;
import org.junit.Test;


public class PositiveTests {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-mesto.praktikum-services.ru/";
    }

    @Before
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

        String acceptToken = response.then().extract().path("token"); // Так же ОТДЕЛЬНО в ОТДЕЛЬНУЮ переменную извлекаю пришедший токен
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
}


