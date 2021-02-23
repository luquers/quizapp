package com.example.proyectoandroid;

import java.util.ArrayList;
import java.util.UUID;

public class Quiz {

    private String id;
    private String name;

    public Quiz(){

    }
    public Quiz(String id, String name) {
        this.id = id;

        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
