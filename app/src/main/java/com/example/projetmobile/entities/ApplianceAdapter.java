package com.example.projetmobile.entities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.projetmobile.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplianceAdapter extends ArrayAdapter<Appliance> {
    public ApplianceAdapter(@NonNull Context context, int fragment_mon_habitat, List<Appliance> items) {
        super(context, fragment_mon_habitat, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View layout = convertView;
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            layout = inflater.inflate(R.layout.item_appliance, parent, false);
        }

        Appliance appliance = getItem(position);

        if (appliance != null) {
            ImageView applianceIcon = layout.findViewById(R.id.appliance_img);

            Map<String, Integer> icons = new HashMap<>();
            icons.put("machine a laver", R.drawable.machine);
            icons.put("aspirateur", R.drawable.aspirateur);
            icons.put("climatiseur", R.drawable.climatiseur);
            icons.put("fer a repasser", R.drawable.iron);

            applianceIcon.setImageResource(icons.get(appliance.getName().toLowerCase()));
            applianceIcon.setVisibility(View.VISIBLE);

            TextView applianceRef = layout.findViewById(R.id.appliance_ref);
            applianceRef.setText(appliance.getReference());

            TextView applianceWatt = layout.findViewById(R.id.appliance_watt);
            applianceWatt.setText(appliance.getWattage() + " W");
        }

        return layout;
    }
}
