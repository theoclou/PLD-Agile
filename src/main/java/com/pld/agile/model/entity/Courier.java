package com.pld.agile.model.entity;

public class Courier {
    @SuppressWarnings("FieldMayBeFinal")
    private  Integer id;

    public Courier(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }
}
