package com.example.mingle.roleplapp;

import java.io.Serializable;

public class Post implements Serializable {
    private int id;
    private String creationDate;
    private String content;
    private int idRol;
    private int idChar;

    public Post() {
    }

    public Post(int id, String creationDate, String content, int idRol, int idChar) {
        this.id = id;
        this.creationDate = creationDate;
        this.content = content;
        this.idRol = idRol;
        this.idChar = idChar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public int getIdChar() {
        return idChar;
    }

    public void setIdChar(int idChar) {
        this.idChar = idChar;
    }
}
