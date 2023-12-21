package org.example.TestsData;

        /*
             Для теста №1 по авторизации
                                             */
public class DataForLoginTest {

    private String email = "jensenmart@yandex.ru";
    private String password = "123456";

            public DataForLoginTest(String email, String password) {
                this.email = email;
                this.password = password;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }
        }
