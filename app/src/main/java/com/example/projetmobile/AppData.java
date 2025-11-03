package com.example.projetmobile;

import com.example.projetmobile.entities.Appliance;

import java.util.ArrayList;
import java.util.List;

public class AppData {
    private static AppData instance;
    private List<Appliance> appliances;

    private AppData() {
        appliances = new ArrayList<>();
    }

    public static synchronized AppData getInstance() {
        if (instance == null) {
            instance = new AppData();
        }
        return instance;
    }

    public List<Appliance> getAppliances() {
        return appliances;
    }

    public void setAppliances(List<Appliance> appliances) {
        this.appliances = appliances;
    }
}
