package com.example.projetmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private String nomUser;
    private String prenomUser;
    private String mailUser;
    private String mdpUser;
    private String confirmUser;
    private EditText nom,prenom,mail,mdp,confirmemdp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSavedLanguage();
        setContentView(R.layout.activity_register);

        Button retour = findViewById(R.id.retour);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

         nom = findViewById(R.id.nom);
         prenom = findViewById(R.id.prenom);
         mail = findViewById(R.id.mail);
         mdp = findViewById(R.id.mdp);
         confirmemdp = findViewById(R.id.confirmemdp);

        Button validbtn = findViewById(R.id.validbtn);
        validbtn.setOnClickListener(v -> mailEstDansBD());
    }

    private void mailEstDansBD() {
        nomUser = nom.getText().toString().trim();
        prenomUser = prenom.getText().toString().trim();
        mailUser = mail.getText().toString().trim();
        mdpUser = mdp.getText().toString().trim();
        confirmUser = confirmemdp.getText().toString().trim();

        //si champs sont vides
        if (nomUser.isEmpty() || prenomUser.isEmpty() || mailUser.isEmpty() || mdpUser.isEmpty() || confirmUser.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        //mdp sont identiques ?
        if (!mdpUser.equals(confirmUser)) {
            Toast.makeText(this, "Les mots de passe ne sont pas cohérents", Toast.LENGTH_SHORT).show();
            return;
        }

        //url verifmail.php
        String url = "http://10.0.2.2/powerhome/verifmail.php?email=" + mailUser;

        Ion.with(this)
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Toast.makeText(RegisterActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                boolean emailExists = jsonObject.getBoolean("emailExists");

                                if (!emailExists) {
                                    enregistrerUser();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Email déjà existant !", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                Toast.makeText(RegisterActivity.this, "Erreur JSON", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void enregistrerUser() {
        String url = "http://10.0.2.2/powerhome/enregistrer.php?firstname=" + nomUser + "&lastname=" + prenomUser
                + "&email=" + mailUser
                + "&password=" + mdpUser;

        Ion.with(this)
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            boolean estEnregistrer = jsonObject.getBoolean("success");

                            if (estEnregistrer) {
                                //redirection vers login
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String message = jsonObject.getString("message");
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException ex) {
                            Toast.makeText(RegisterActivity.this, "Erreur JSON", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void loadSavedLanguage() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String savedLanguage = sharedPreferences.getString("My_Lang", "fr");

        Locale locale = new Locale(savedLanguage);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

}