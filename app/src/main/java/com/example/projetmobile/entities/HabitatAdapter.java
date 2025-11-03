package com.example.projetmobile.entities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.projetmobile.R;

public class HabitatAdapter extends ArrayAdapter<Habitat> {

    private final Context context;
    private final List<Habitat> habitatList;
    public HabitatAdapter(@NonNull Context context,List<Habitat> habitat) {
        super(context, 0, habitat);
        this.context=context;
        this.habitatList=habitat;
    }

    @NonNull
    @Override
    public View getView(int pos, View convertView, @NonNull ViewGroup parent){
        View layout = convertView;
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            layout = inflater.inflate(R.layout.item_habitat, parent, false);
        }

        Habitat habitat = habitatList.get(pos);

        TextView name =  layout.findViewById(R.id.nameHabitat);
        TextView description =  layout.findViewById(R.id.nbequipement);
        TextView floor =  layout.findViewById(R.id.floor);

        LinearLayout l = layout.findViewById(R.id.image);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(50,40);

        name.setText(habitat.getName());
        floor.setText(String.valueOf(habitat.getFloor()));
        description.setText(String.valueOf(habitat.nbAppliance()));

        l.removeAllViews();
        List<Appliance> list = habitat.getAppliances();
        for (Appliance a : list){
            ImageView v = getImageView(a);

            l.addView(v,lp);
        }

        return layout;
    }

    @NonNull
    private ImageView getImageView(Appliance a) {
        ImageView v = new ImageView(this.getContext());

        if (a.getName().equals("Aspirateur"))
            v.setImageResource(R.drawable.aspirateur);

        if (a.getName().equals("Machine a laver"))
            v.setImageResource(R.drawable.machine);

        if (a.getName().equals("Fer a repasser"))
            v.setImageResource(R.drawable.iron);

        if (a.getName().equals("Climatiseur"))
            v.setImageResource(R.drawable.climatiseur);

        return v;
    }

}
