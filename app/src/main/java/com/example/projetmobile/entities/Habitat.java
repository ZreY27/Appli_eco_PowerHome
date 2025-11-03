package com.example.projetmobile.entities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Habitat {

    private int id;
    private int floor;
    private double area;
    private User user;
    private List<Appliance> appliances;

    public Habitat() {
        appliances = new ArrayList<>();
    }

    public Habitat(int id, int floor, double area) {
        this.id = id;
        this.floor = floor;
        this.area = area;
        appliances = new ArrayList<>();
    }

    public static Habitat getFromJson(String json){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        Habitat obj = gson.fromJson(json, Habitat.class);
        return obj;
    }

    public static List<Habitat> getListFromJson(String json){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        Type type = new TypeToken<List<Habitat>>(){}.getType();
        List<Habitat> list = gson.fromJson(json, type);
        return list;
    }

    public String getName() {
        return user.getFirstName();
    }

    public int getFloor() {
        return floor;
    }

    public int nbAppliance() {
        return appliances.size();
    }

    public List<Appliance> getAppliances() {
        return appliances;
    }

    public double getArea() {
        return area;
    }

    public User getUser() {
        return user;
    }

    public Appliance getAppliance(int index){
        return appliances.get(index);
    }
}
