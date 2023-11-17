package org.example.extractToJson;

public class Root {

    private Data data;
    private String token;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Root(Data data, String token) {
        this.data = data;
        this.token = token;
    }

    public Root() {

    }
}
