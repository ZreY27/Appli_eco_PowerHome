package com.example.projetmobile.entities;

import java.util.ArrayList;
import java.util.List;

public class Appliance {

    private int id;
    private String name;
    private String reference;
    private int wattage;

    private int habitat_id;

    public Appliance() {
    }

    public Appliance(int id, String name, String reference, int wattage) {
        this.id = id;
        this.name = name;
        this.reference = reference;
        this.wattage = wattage;
    }

    public String getName(){
        return name;
    }

    public String getReference() {
        return reference;
    }

    public int getWattage() {
        return wattage;
    }

    public int getId() {
        return id;
    }
}
