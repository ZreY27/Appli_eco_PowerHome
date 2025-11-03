package com.example.projetmobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetmobile.entities.Appliance;
import com.example.projetmobile.entities.ApplianceAdapter;
import com.example.projetmobile.entities.Habitat;
import com.example.projetmobile.entities.ReservedSlotAdapter;
import com.example.projetmobile.entities.TimeSlot;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonHabitatFragment extends Fragment {

    private TextView floorText, areaText, residentText, wattageText;
    private ApplianceAdapter applianceAdapter;
    private List<Appliance> appliances;
    private ProgressDialog pDialog;
    private ListView listView;
    private List<TimeSlot> reservedTimeSlots;
    private ReservedSlotAdapter adapter;

    public MonHabitatFragment() {
        reservedTimeSlots = new ArrayList<>();
        appliances = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mon_habitat, container, false);

        // Initialiser les vues
        floorText = view.findViewById(R.id.text_floor);
        areaText = view.findViewById(R.id.text_area);
        residentText = view.findViewById(R.id.text_resident);
        wattageText = view.findViewById(R.id.text_watt);

        //equipement
        ListView applianceList = view.findViewById(R.id.appliances_list);
        applianceAdapter = new ApplianceAdapter(requireContext(), R.layout.item_appliance, appliances);
        applianceList.setAdapter(applianceAdapter);

        //creneau
        listView = view.findViewById(R.id.reservedSlot);
        adapter = new ReservedSlotAdapter(requireContext(), reservedTimeSlots);
        listView.setAdapter(adapter);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String mail = sharedPreferences.getString("userEmail", null);

        // Lancer la récupération
        if (mail != null) {
            getMyHabitat(mail);
            getConso(mail);
            getReservedTimeSlots(mail);
        }
        else
            Toast.makeText(getContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show();



        return view;
    }

    private void getMyHabitat(String mail) {
        String url = "http://10.0.2.2/powerhome/getMyHabitat.php?email=" + mail;

        dismissProgressDialog();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.msghabitat));
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        Ion.with(requireContext())
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        dismissProgressDialog();

                        if (e != null) {
                            Toast.makeText(getContext(), "Erreur réseau", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (result.trim().contains("error")) {
                            Toast.makeText(getContext(), "Erreur : " + result, Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (result.trim().startsWith("{")) {
                            Habitat habitat = Habitat.getFromJson(result);

                            floorText.setText(getString(R.string.etage) + " " + habitat.getFloor());
                            areaText.setText(getString(R.string.surface) + " " + habitat.getArea() + " " + getString(R.string.m2));
                            residentText.setText(habitat.getName());

                            appliances.clear();
                            appliances.addAll(habitat.getAppliances());
                            AppData.getInstance().setAppliances(appliances);
                            applianceAdapter.notifyDataSetChanged();

                        } else {
                            Log.e("Habitat", "Erreur serveur : " + result + "'");
                            Toast.makeText(getContext(), "Erreur : " + result, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void getConso(String mail) {
        String url = "http://10.0.2.2/powerhome/getConso.php?email=" + mail;

        Ion.with(requireContext())
                .load(url)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        if (e != null) {
                            Toast.makeText(getContext(), "Erreur réseau : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            JSONObject jsonResponse = new JSONObject(result.getResult());

                            if (jsonResponse.getBoolean("success")) {
                                int totalWattage = jsonResponse.getInt("total_wattage");

                                wattageText.setText(getString(R.string.wattage) + totalWattage + " W");
                            }
                        } catch (JSONException ex) {
                            Toast.makeText(getContext(), "Erreur de parsing JSON : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void getReservedTimeSlots(String mail) {
        //url getReservedSlot.php
        String url = "http://10.0.2.2/powerhome/getReservedSlot.php?email=" + mail;

        dismissProgressDialog();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        Ion.with(requireContext())
                .load(url)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        dismissProgressDialog();
                        if (e != null) {
                            Toast.makeText(getContext(), "Erreur réseau : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (response != null) {
                            reservedTimeSlots.clear();
                            reservedTimeSlots.addAll(TimeSlot.getListFromJson(response.getResult()));
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Aucun créneau réservé.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

}