package com.pld.agile.model.entity;

public class Courier {
    @SuppressWarnings("FieldMayBeFinal")
    private  Integer id;
    private String name;
    private final Integer speed=1500;
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
    public Integer getSpeed()
    {
        return this.speed;
    }
}
