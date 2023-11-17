import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.TestsData.DataForChangeUserName;
import org.example.TestsData.DataForLoginTest;
import org.example.extractToJson.Data;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.example.extractToJson.Root;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TestClass {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-mesto.praktikum-services.ru/";
    }

    @Test
    @Step
    @DisplayName("Тест №1: Авторизация + изъятие приходящего токена в переменную")
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
        assertNotNull(root); // Проверяю что Объект не пустой в итоге
        assertNotNull(root.getToken()); // Проверяю что конкретно поле token у данного класса не пустое
        System.out.println(root.getToken()); // Вывожу токен, чтобы проверить
    }

    @Step
    @DisplayName("Тест №2: Изменение имени пользователя")
    @Description("Тест на эндпоинт 'api/users/me', проверка статус кода и изменился ли никнейм приходящий в json")
    public void changeUserName() {
        DataForChangeUserName forChangeUserName = new DataForChangeUserName("Денис","QA Auto");
        Response response;
        response = given()
                .header("Content-type", "application/json")
                .auth().oauth2() // ПРОДУМАТЬ ЧТОБЫ ТОКЕН БЫЛ СОХРАНЕН НА УРОВНЕ КЛАССА, ДУМАЮ СТАТИК МОЖНО ИСПОЛЬЗОВАТЬ
                .and()
                .body(forChangeUserName)
                .when()
                .put("api/users/me");
                response.then().assertThat().statusCode(200)
                        .and().assertThat().body("data.name", equalTo("Денис"));
    }
}


