package com.pld.agile.model;

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
