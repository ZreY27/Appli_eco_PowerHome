package com.example.projetmobile.entities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

public class Booking {

    private int id;
    private int order;
    private Date booked_at;
    private Appliance appliance;
    private TimeSlot timeSlot;

    public Booking() {
    }

    public Booking(int id, int order, Date booked_at) {
        this.id = id;
        this.order = order;
        this.booked_at = booked_at;
    }

    public static List<Booking> getListFromJson(String json){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        Type type = new TypeToken<List<Booking>>(){}.getType();
        List<Booking> list = gson.fromJson(json, type);
        return list;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public Appliance getAppliance() {
        return appliance;
    }
}
