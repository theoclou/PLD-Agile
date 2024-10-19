package com.pld.agile.model;

public class Courier {
    @SuppressWarnings("FieldMayBeFinal")
    private  Integer id;
    private String name;

    public Courier(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
