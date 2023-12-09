package org.example.extractFromJson.thirdtest;

import io.qameta.allure.internal.shadowed.jackson.annotation.JsonFormat;

import java.util.ArrayList;
import java.util.Date;

public class Data {

    private ArrayList<String> likes;
    private String _id;
    private String name;
    private String link;
    private String owner;
    private String createdAt;

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Data(ArrayList<String> likes, String _id, String name, String link, String owner, String createdAt) {
        this.likes = likes;
        this._id = _id;
        this.name = name;
        this.link = link;
        this.owner = owner;
        this.createdAt = createdAt;
    }

    public Data() {

    }

}
