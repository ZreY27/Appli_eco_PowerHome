package com.example.projetmobile.entities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeSlot {
    private int id;
    private Date begin;
    private Date end;
    private int max_wattage;

    public TimeSlot(int id, String begin, String end, int max_wattage) {
        this.id = id;
        this.max_wattage = max_wattage;
        this.begin = convertStringToDate(begin);
        this.end = convertStringToDate(end);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getBegin() {
        return begin;
    }

    public Date getEnd() {
        return end;
    }

    public int getMax_wattage() {
        return max_wattage;
    }

    public static List<TimeSlot> getListFromJson(String json){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        Type type = new TypeToken<List<TimeSlot>>(){}.getType();
        List<TimeSlot> list = gson.fromJson(json, type);
        return list;
    }

    private Date convertStringToDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getFormattedBegin() {
        return formatDate(begin);
    }

    public String getFormattedEnd() {
        return formatDate(end);
    }

    private String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMM yyyy HH:mm", Locale.FRANCE);
        return sdf.format(date);
    }
}
