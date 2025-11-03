package com.example.projetmobile.entities;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.projetmobile.AppData;
import com.example.projetmobile.CalendrierFragment;
import com.example.projetmobile.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class TimeSlotAdapter extends ArrayAdapter<TimeSlot> {
    private List<TimeSlot> items;
    private boolean[] expandedStates;
    private Map<Integer, Integer> wattageSums;
    private CalendrierFragment calendrierFragment;
    private Date date;

    public TimeSlotAdapter(@NonNull Context context, int time_slot_list, @NonNull List<TimeSlot> items, List<Booking> bookings, CalendrierFragment calendrierFragment) {
        super(context, time_slot_list, items);
        this.items = items;
        this.expandedStates = new boolean[items.size()];
        this.wattageSums = calculateWattageSums(bookings);
        this.calendrierFragment = calendrierFragment;
    }

    private Map<Integer, Integer> calculateWattageSums(List<Booking> bookings) {
        Map<Integer, Integer> wattageMap = new HashMap<>();
        for (Booking booking : bookings) {
            int timeSlotId = booking.getTimeSlot().getId();
            int wattage = booking.getAppliance().getWattage();
            wattageMap.put(timeSlotId, wattageMap.getOrDefault(timeSlotId, 0) + wattage);
        }
        return wattageMap;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_time_slot, parent, false);
            holder = new ViewHolder();
            holder.begin = convertView.findViewById(R.id.timeslot_begin_text);
            holder.end = convertView.findViewById(R.id.timeslot_end_text);
            holder.timeslotContainer = convertView.findViewById(R.id.timeslot_container);
            holder.reserveButton = convertView.findViewById(R.id.reserver_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TimeSlot slot = getItem(position);
        if (slot != null) {
            date = slot.getBegin();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            holder.begin.setText(sdf.format(slot.getBegin()));
            holder.end.setText(sdf.format(slot.getEnd()));

            int totalWattage = wattageSums.getOrDefault(slot.getId(), 0);
            int maxWattage = slot.getMax_wattage();
            float percentage = (totalWattage / (float) maxWattage) * 100;

            if (percentage < 30) {
                holder.timeslotContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
            } else if (percentage < 70) {
                holder.timeslotContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orange_palette));
            } else {
                holder.timeslotContainer.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
            }

            boolean isExpanded = expandedStates[position];
            holder.reserveButton.setTranslationX(isExpanded ? 0 : 600);
            holder.reserveButton.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            holder.timeslotContainer.setOnClickListener(v -> {
                if (expandedStates[position]) {
                    hideButton(holder.reserveButton);
                } else {
                    showButton(holder.reserveButton);
                }
                expandedStates[position] = !expandedStates[position];
            });

            holder.reserveButton.setOnClickListener(v -> {
                List<Appliance> appliances = AppData.getInstance().getAppliances();

                if (appliances.isEmpty()) {
                    Toast.makeText(getContext(), "Aucun appareil disponible.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] applianceNames = new String[appliances.size()];
                for (int i = 0; i < appliances.size(); i++) {
                    applianceNames[i] = appliances.get(i).getName();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choisissez un appareil");
                builder.setItems(applianceNames, (dialog, which) -> {
                    Appliance selectedAppliance = appliances.get(which);
                    reserverTimeSlot(slot.getId(), selectedAppliance.getId());
                });

                builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());
                builder.show();
            });

        }

        return convertView;
    }

    private void showButton(Button button) {
        button.setVisibility(View.VISIBLE);
        button.animate().translationX(0).setDuration(300);
    }

    private void hideButton(Button button) {
        button.animate().translationX(600).setDuration(300).withEndAction(() -> button.setVisibility(View.GONE));
    }

    public void updateData(List<TimeSlot> newItems, List<Booking> bookings) {
        this.items = newItems;
        this.expandedStates = new boolean[newItems.size()];
        this.wattageSums = calculateWattageSums(bookings);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView begin;
        TextView end;
        RelativeLayout timeslotContainer;
        Button reserveButton;
    }

    private void reserverTimeSlot(int timeSlotId, int applianceId) {
        String url = "http://10.0.2.2/powerhome/addReservation.php";

        // Affichage d'un dialogue de chargement
        ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Réservation en cours...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        // Envoi des paramètres via POST
        Ion.with(getContext())
                .load("POST", url)
                .setBodyParameter("time_slot_id", String.valueOf(timeSlotId))
                .setBodyParameter("appliance_id", String.valueOf(applianceId))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        pDialog.dismiss();

                        if (e != null) {
                            Toast.makeText(getContext(), "Erreur réseau : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            JSONObject jsonResponse = new JSONObject(result);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                // La réservation a réussi, maintenant on met à jour les eco_coins
                                //updateEcoCoins(timeSlotId, applianceId);

                                calendrierFragment.refreshTimeSlots();
                                int order = jsonResponse.getInt("order");
                                Toast.makeText(getContext(), "Réservation réussie ! Numéro d'ordre : " + order, Toast.LENGTH_LONG).show();
                            } else {
                                String message = jsonResponse.getString("message");
                                Toast.makeText(getContext(), "Échec de la réservation : " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException ex) {
                        }
                    }
                });
    }
    /**
    private void updateEcoCoins(int timeSlotId, int applianceId) {
        // URL du script PHP qui met à jour les eco_coins
        String ecoCoinUrl = "http://10.0.2.2/powerhome/eco_coin.php";

        ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Chargement en cours...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();
        // Envoi des paramètres pour la mise à jour des eco_coins
        Ion.with(getContext())
                .load("POST", ecoCoinUrl)
                .setBodyParameter("time_slot_id", String.valueOf(timeSlotId))
                .setBodyParameter("appliance_id", String.valueOf(applianceId))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        pDialog.dismiss(); // Ferme le dialogue de chargement

                        if (e != null) {
                            Log.e("updateEcoCoins", "Erreur : ", e);
                            Toast.makeText(getContext(), "Erreur lors de la mise à jour des eco_coins : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            JSONObject jsonResponse = new JSONObject(result);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                // Si la mise à jour des eco_coins a réussi
                                int newEcoCoins = jsonResponse.getInt("eco_coins");
                                Toast.makeText(getContext(), "Eco coins mis à jour. Nouveau solde : " + newEcoCoins, Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonResponse.getString("message");
                                Toast.makeText(getContext(), "Échec de la mise à jour des eco_coins : " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            Toast.makeText(getContext(), "Erreur lors de la mise à jour des eco_coins", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
**/
}
