package org.example.TestsData;

public class DataForAddNewPlace {

    private String name;
    private String link;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public DataForAddNewPlace(String name, String link) {
        this.name = name;
        this.link = link;
    }

    @Override
    public String toString() {
        return "DataForAddNewPlace{" +
                "name='" + name + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
