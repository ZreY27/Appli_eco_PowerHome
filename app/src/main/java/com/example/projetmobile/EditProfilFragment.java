package com.example.projetmobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class EditProfilFragment extends Fragment {
    private EditText firstNameEditText, lastNameEditText, emailEditText;
    private TextView ecocoinTextView;
    private CheckBox machineALaver, ferARepasser, climatiseur, aspirateur;
    private Button enregistre, modification;
    private boolean isEditing = false;
    private int userId = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profil, container, false);

        //nom, prenom, mail
        firstNameEditText = view.findViewById(R.id.first_name);
        lastNameEditText = view.findViewById(R.id.last_name);
        emailEditText = view.findViewById(R.id.email);

        //Eco-coins
        ecocoinTextView = view.findViewById(R.id.ecoin);

        //checkbox
        machineALaver = view.findViewById(R.id.machine);
        ferARepasser = view.findViewById(R.id.fer);
        climatiseur = view.findViewById(R.id.clim);
        aspirateur = view.findViewById(R.id.aspi);

        //btn modif, enregistrer
        enregistre = view.findViewById(R.id.enregbtn);
        modification = view.findViewById(R.id.modifbtn);

        //mail de la personne qui s'est connecté
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", null);

        if (userEmail != null) {
            loadUserProfile(userEmail);
        } else {
            Toast.makeText(getContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
        }

        //modif impossible
        setEditMode(isEditing);

        enregistre.setEnabled(false);

        //modif possible
        modification.setOnClickListener(v -> {
            //annuler les modifs
            if (modification.getText().toString().equals(getString(R.string.annuler))) {
                loadUserProfile(userEmail);
            }
            isEditing = !isEditing;
            setEditMode(isEditing);
        });

        enregistre.setOnClickListener(v -> {
            saveUserProfile();
            setEditMode(false);
        });

        return view;
    }


    private void loadUserProfile(String mail) {
        String url = "http://10.0.2.2/powerhome/getProfil.php?email=" + mail;
        Ion.with(requireContext())
                .load(url)
                .asString()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Erreur de récupération", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        JSONObject response = new JSONObject(result);

                        firstNameEditText.setText(response.getString("first_name"));
                        lastNameEditText.setText(response.getString("last_name"));
                        emailEditText.setText(response.getString("email"));
                        ecocoinTextView.setText(Integer.toString(response.getInt("eco_coins")));
                        JSONArray appliancesArray = response.optJSONArray("appliances");
                        if (appliancesArray != null) {
                            List<String> appliancesList = new ArrayList<>();

                            for (int i = 0; i < appliancesArray.length(); i++) {
                                appliancesList.add(appliancesArray.getString(i));
                            }

                            machineALaver.setChecked(appliancesList.contains("Machine a laver"));
                            ferARepasser.setChecked(appliancesList.contains("Fer a repasser"));
                            climatiseur.setChecked(appliancesList.contains("Climatiseur"));
                            aspirateur.setChecked(appliancesList.contains("Aspirateur"));
                        }

                    } catch (JSONException jsonException) {
                        Toast.makeText(getContext(), "Erreur de parsing des données", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserProfile() {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();

        int aMachine = machineALaver.isChecked() ? 1 : 0;
        int aFer = ferARepasser.isChecked() ? 1 : 0;
        int aClim = climatiseur.isChecked() ? 1 : 0;
        int aAspi = aspirateur.isChecked() ? 1 : 0;

        JSONArray appliancesArray = new JSONArray();

        //ajout si coché
        if (aAspi == 1) {
            try {
                appliancesArray.put(new JSONObject()
                        .put("name", "Aspirateur")
                        .put("reference", "AS1001")
                        .put("wattage", 700));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (aMachine == 1) {
            try {
                appliancesArray.put(new JSONObject()
                        .put("name", "Machine a laver")
                        .put("reference", "ML4002")
                        .put("wattage", 1500));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (aClim == 1) {
            try {
                appliancesArray.put(new JSONObject()
                        .put("name", "Climatiseur")
                        .put("reference", "CL3001")
                        .put("wattage", 2000));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (aFer == 1) {
            try {
                appliancesArray.put(new JSONObject()
                        .put("name", "Fer a repasser")
                        .put("reference", "FR2001")
                        .put("wattage", 1200));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            String encodedAppliancesJson = URLEncoder.encode(appliancesArray.toString(), "UTF-8");

            String url = "http://10.0.2.2/powerhome/updateProfil.php?" +
                    "first_name=" + firstName +
                    "&last_name=" + lastName +
                    "&email=" + email +
                    "&appliances=" + encodedAppliancesJson;

            Ion.with(requireContext())
                    .load(url)
                    .asString()
                    .setCallback((e, result) -> {
                        if (e != null) {
                            Toast.makeText(getContext(), "Erreur de mise à jour", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            JSONObject response = new JSONObject(result);
                            Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void setEditMode(boolean enabled) {
        firstNameEditText.setEnabled(enabled);
        lastNameEditText.setEnabled(enabled);
        emailEditText.setEnabled(enabled);
        aspirateur.setEnabled(enabled);
        climatiseur.setEnabled(enabled);
        ferARepasser.setEnabled(enabled);
        machineALaver.setEnabled(enabled);

        if (enabled){
            modification.setText(R.string.annuler);
            enregistre.setEnabled(true);
        }
        else {
            modification.setText(R.string.modifier);
            enregistre.setEnabled(false);
        }
    }
}