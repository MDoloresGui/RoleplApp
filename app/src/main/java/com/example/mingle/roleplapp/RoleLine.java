package com.example.mingle.roleplapp;

public class RoleLine {
    private int id;
    private String title;
    private String creationDate;
    private String lastUpdateDate;
    private int masterId;
    private int idUniverse;
    private String description;

    public RoleLine(int id, String title, String creationDate, String lastUpdateDate, int masterId, int idUniverse, String description) {
        this.id = id;
        this.title = title;
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
        this.masterId = masterId;
        this.idUniverse = idUniverse;
        this.description = description;
    }

    public RoleLine() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public int getIdUniverse() {
        return idUniverse;
    }

    public void setIdUniverse(int idUniverse) {
        this.idUniverse = idUniverse;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
