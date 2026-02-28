package com.example.demo.entitys;


public class Categoria {

    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Categoria(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Categoria() {
    }
    
}
