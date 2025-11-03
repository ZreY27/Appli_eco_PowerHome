package com.example.projetmobile.entities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.projetmobile.R;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ReservedSlotAdapter extends ArrayAdapter<TimeSlot> {
    private Context context;
    private List<TimeSlot> reservedSlots;

    public ReservedSlotAdapter(Context context, List<TimeSlot> reservedSlots) {
        super(context, 0, reservedSlots);
        this.context = context;
        this.reservedSlots = reservedSlots;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_creneau, parent, false);
        }

        TimeSlot timeSlot = reservedSlots.get(position);

        TextView beginText = convertView.findViewById(R.id.debut);
        TextView endText = convertView.findViewById(R.id.fin);
        TextView maxWattageText = convertView.findViewById(R.id.wattage);

        beginText.setText(context.getString(R.string.debut) + timeSlot.getFormattedBegin());
        endText.setText(context.getString(R.string.fin) + timeSlot.getFormattedEnd());
        maxWattageText.setText(context.getString(R.string.wattage) + timeSlot.getMax_wattage() + " W");

        Date endDate = timeSlot.getEnd();
        Date currentDate = new Date();

        //la date du créneau est passé
        if (endDate != null && endDate.before(currentDate)) {
            convertView.setAlpha(0.5f);
        } else { //la date du créneau n'est pas encore passé
            convertView.setAlpha(1.0f);
        }

        triSlot();

        return convertView;
    }

    private void triSlot() {
        reservedSlots.sort(new Comparator<TimeSlot>() {
            @Override
            public int compare(TimeSlot t1, TimeSlot t2) {
                Date date1 = t1.getBegin();
                Date date2 = t2.getBegin();

                if (date1 != null && date2 != null) {
                    return date2.compareTo(date1);
                }
                return 0;
            }
        });
        notifyDataSetChanged();
    }
}


