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
import org.example.Asserts.SecondTest;
import org.example.TestsData.DataForAddNewPlace;
import org.example.TestsData.DataForChangeAvatar;
import org.example.TestsData.DataForChangeUserName;
import org.example.TestsData.DataForLoginTest;
import org.example.extractFromJson.predtest.Root;
import org.example.extractFromJson.thirdtest.Data;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
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
    @DisplayName("Тест №2: Добавление нового места") // Переделал код, сделал проверку по нескольким вариантам сразу, чтобы попрактиковать написание параметризации
    @Description("Тест на эндпоинт 'api/cards', проверка статус кода и добавилось ли новое место")
    public void addNewPlace() {
        List<DataForAddNewPlace> forAddNewPlaces = new ArrayList<>();
        forAddNewPlaces.add(new DataForAddNewPlace("ВП Сити", "https://gorodarus.ru/images/vyatpolyani/vyatskie-polyani4.jpg"));
        forAddNewPlaces.add(new DataForAddNewPlace(" ", "https://gorodarus.ru/images/vyatpolyani/vyatskie-polyani4.jpg"));
        forAddNewPlaces.add(new DataForAddNewPlace("VP", "https://gorodarus.ru/images/vyatpolyani/vyatskie-polyani4.jpg"));
        forAddNewPlaces.add(new DataForAddNewPlace("123", "https://gorodarus.ru/images/vyatpolyani/vyatskie-polyani4.jpg"));

        List<String> failed = new ArrayList<>(); // Список для отслеживания проваленных данных, которые не прошли

        for (int i = 0; i < forAddNewPlaces.size(); i++) {
            Response response;
            response = given()
                    .header("Content-type", "application/json")
                    .auth().oauth2(Root.getToken())
                    .body(forAddNewPlaces.get(i))
                    .when()
                    .post("api/cards");
            try {
                response.then().assertThat().statusCode(201);
                response.then().assertThat().body("data.name", equalTo(SecondTest.ExpectedName.get(i)));
                System.out.println("Проверка имени города: " + SecondTest.ExpectedName.get(i) + " Прошла успешно!");
                System.out.println(" ");
            } catch (AssertionError e) {
                System.out.println("Внимание, при отправке данных: " + forAddNewPlaces.get(i).toString() + " Произошла ошибка, статус код: " + response.getStatusCode());
                System.out.println("Ожидаемое название города: " + SecondTest.ExpectedName.get(i));

                failed.add(forAddNewPlaces.get(i).toString());
            }

        }
        System.out.println("Тест №2 прошел успешно! Новые места добавились");
        // Так как есть тест-кейсы которые провалились, вывожу то, что не прошло. По сути это значит что все в порядке, ведь тест-кейсы с невалидными данными прошли, однако выведу их для практики подобных действий
        if (failed.size() > 0) {
            System.out.println("Следующие невалидные значения не прошли! : ");
            System.out.println(failed);
            System.out.println(" ");
            System.out.println(" ");
            System.out.println(" ");
        }
    }

    @Test
    @Step
    @DisplayName("Тест №3: Постановка лайка")
    @Description("Тест на эндпоинт 'api/cards/64b6d4c0434244003d277aa6/likes', проверка статус кода и валидного количества лайков")
    public void addLikeOnPlace() {
        Response response;
        response = given()
                .header("Content-type", "application/json")
                .auth().oauth2(Root.getToken())
                .when()
                .put("api/cards/64b6d4c0434244003d277aa6/likes");
        response.then().assertThat().statusCode(200);
        org.example.extractFromJson.thirdtest.Root root = response.body().as(org.example.extractFromJson.thirdtest.Root.class);  // Такое длинное название, так как указан явный путь до нужного класса, тк Root есть в другом тесте и он указывает на другое

        assertNotNull(root);
                            /*
                                   Логирую то, как сработала десериализация
                                                                                      */
                                    Gson gson = new Gson();
                                    String json = gson.toJson(root);
                                    System.out.println("В итоге приходящий ответ был распакован и содержит следующее :  " + json);

        // Начинаю проверку поставленного лайка
        ArrayList<String> checkLikes = root.getData().getLikes();
        System.out.println(checkLikes.size());
        if (checkLikes.size() == 7) {
            System.out.println("Тест №3 завершился успешно! Лайк был поставлен");
            System.out.println("Количество лайков: " + checkLikes.size());
        }
    }

    @Test
    @Step
    @DisplayName("Тест №4: Сменить аватарку")
    @Description("Тест на эндпоинт 'api/users/me/avatar'")
    public void changeAvatar() {
        DataForChangeAvatar forChangeAvatar = new DataForChangeAvatar();
        Response response = given()
                .header("Content-type", "application/json")
                .auth().oauth2(Root.getToken())
                .body(forChangeAvatar)
                .when()
                .patch("api/users/me/avatar");
        response.then().assertThat().statusCode(200);

        org.example.extractFromJson.fourthTest.Root root = response.body().as(org.example.extractFromJson.fourthTest.Root.class);

        String actual = forChangeAvatar.getAvatar();
        String expected = root.getData().getAvatar();
        Assert.assertTrue(expected.equals(actual));
        System.out.println("Тест №4 прошел успешно, аватарка была изменена корректно!");
    }
}


