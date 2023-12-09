package org.example.extractFromJson.predtest;

public class Root {

    private static String token;
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Root.token = token;
    }

    public Root(Data data) {
        this.data = data;
    }

    public Root() {
    }
}
