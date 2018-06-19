package com.example.mingle.roleplapp;

import java.io.Serializable;

public class CharacterClass implements Serializable {
    private int id;
    private String name;
    private int userId;
    private String avatar;
    private int universeId;
    private String biography;
    private String indexCard;

    public CharacterClass() {
    }

    public CharacterClass(int id, String name, String avatar, String biography, String indexCard, int userId, int universeId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.avatar = avatar;
        this.universeId = universeId;
        this.biography = biography;
        this.indexCard = indexCard;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getUniverseId() {
        return universeId;
    }

    public void setUniverseId(int universeId) {
        this.universeId = universeId;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getIndexCard() {
        return indexCard;
    }

    public void setIndexCard(String indexCard) {
        this.indexCard = indexCard;
    }

    @Override
    public String toString() {
        return "CharacterClass{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", avatar='" + avatar + '\'' +
                ", universeId=" + universeId +
                ", biography='" + biography + '\'' +
                ", indexCard='" + indexCard + '\'' +
                '}';
    }
}
