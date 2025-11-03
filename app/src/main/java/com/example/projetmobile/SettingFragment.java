package com.example.projetmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Locale;

public class SettingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        RadioGroup radioGroup = view.findViewById(R.id.langue);
        RadioButton radioFr = view.findViewById(R.id.fr);
        RadioButton radioEn = view.findViewById(R.id.en);
        RadioButton radioEs = view.findViewById(R.id.es);

        // Vérifier la langue enregistrée et la mettre en surbrillance
        String savedLanguage = getSavedLanguage(requireContext());
        switch (savedLanguage) {
            case "fr":
                radioFr.setChecked(true);
                break;
            case "en":
                radioEn.setChecked(true);
                break;
            case "es":
                radioEs.setChecked(true);
                break;
        }

        // Lorsque l'utilisateur change la langue
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.fr) {
                setLocale("fr");
            } else if (checkedId == R.id.en) {
                setLocale("en");
            } else if (checkedId == R.id.es) {
                setLocale("es");
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    //change la langue
    private void setLocale(String languageCode) {
        saveLanguage(requireContext(), languageCode);

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        requireContext().getResources().updateConfiguration(config, requireContext().getResources().getDisplayMetrics());

        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    //sauvegarder la langue
    private void saveLanguage(Context context, String languageCode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("My_Lang", languageCode);
        editor.apply();
    }

    //récupérer la langue enregistrée
    private String getSavedLanguage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        return sharedPreferences.getString("My_Lang", "fr");
    }
}
