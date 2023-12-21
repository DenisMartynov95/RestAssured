package org.example.TestsData;

public class DataForChangeUserName {


    private String name;
    private String about;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }


    public DataForChangeUserName(String name, String about) {
        this.name = name;
        this.about = about;
    }

}
