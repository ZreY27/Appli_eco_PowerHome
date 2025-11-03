package com.example.projetmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class LoginActivity extends AppCompatActivity {

    private EditText mail, mdp;
    private Button loginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSavedLanguage();
        setContentView(R.layout.activity_login);

        mail = findViewById(R.id.email);
        mdp = findViewById(R.id.mdp);
        loginbtn = findViewById(R.id.loginbtn);

        TextView creerT = findViewById(R.id.creercompte);
        creerT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginbtn.setOnClickListener(v -> loginUser());

        TextView mdp = findViewById(R.id.textView_mdp_oublie);
        mdp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MotDePasseOublieActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = mail.getText().toString().trim();
        String password = mdp.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        //url login.php
        String url = "http://10.0.2.2/powerhome/login.php?email=" + email + "&password=" + password;

        Ion.with(this)
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Toast.makeText(LoginActivity.this, "Erreur de connexion", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(result);

                            if (jsonObject.has("token")) {
                                String token = jsonObject.getString("token");
                                String expiration = jsonObject.getString("expired_at");

                                Toast.makeText(LoginActivity.this, "Connexion réussie!", Toast.LENGTH_SHORT).show();

                                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("token", token); // Stocke le token reçu du serveur
                                editor.putString("userEmail", email); // email est récupéré lors de la connexion
                                editor.apply();


                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Erreur: " + result, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException ex) {
                            Toast.makeText(LoginActivity.this, "Mot de passe ou mail incorrect", Toast.LENGTH_LONG).show();
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
