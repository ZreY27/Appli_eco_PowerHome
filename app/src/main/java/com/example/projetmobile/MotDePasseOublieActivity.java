package com.example.projetmobile;

import android.content.Intent;
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

public class MotDePasseOublieActivity extends AppCompatActivity {
    private EditText nouveaumdp, email, confirmdp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mot_de_passe_oublie);

        nouveaumdp = findViewById(R.id.nouveaumdp);
        confirmdp = findViewById(R.id.confirmemdp);
        email = findViewById(R.id.email);

        AppCompatButton btnmdp = findViewById(R.id.btnmdp);
        btnmdp.setOnClickListener(v -> mailEstDansBD());

        AppCompatButton btnretour = findViewById(R.id.retour);
        btnretour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MotDePasseOublieActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void mailEstDansBD() {
        String mail = email.getText().toString().trim();
        String mdp = nouveaumdp.getText().toString().trim();
        String confirmation = confirmdp.getText().toString().trim();

        //si champs sont vides
        if (mail.isEmpty() || mdp.isEmpty() || confirmation.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        //mdp sont identiques ?
        if (!mdp.equals(confirmation)) {
            Toast.makeText(this, "Les mots de passe ne sont pas cohérents", Toast.LENGTH_SHORT).show();
            return;
        }

        //url verifmail.php
        String url = "http://10.0.2.2/powerhome/verifmail.php?email=" + mail;

        Ion.with(this)
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Toast.makeText(MotDePasseOublieActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                boolean emailExists = jsonObject.getBoolean("emailExists");

                                if (emailExists) {
                                    modifMotDePasse();
                                } else {
                                    Toast.makeText(MotDePasseOublieActivity.this, "Email non trouvé!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                Toast.makeText(MotDePasseOublieActivity.this, "Erreur JSON", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void modifMotDePasse() {
        String mail = email.getText().toString().trim();
        String mdp = nouveaumdp.getText().toString().trim();

        //url changermdp.php
        String url = "http://10.0.2.2/powerhome/changermdp.php?email=" + mail + "&newPassword=" + mdp;

        Ion.with(this)
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Toast.makeText(MotDePasseOublieActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                boolean passwordUpdated = jsonObject.getBoolean("passwordUpdated");

                                if (passwordUpdated) {
                                    Toast.makeText(MotDePasseOublieActivity.this, "Mot de passe changé avec succès!", Toast.LENGTH_SHORT).show();

                                    //redirection login
                                    Intent intent = new Intent(MotDePasseOublieActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(MotDePasseOublieActivity.this, "Échec de la mise à jour du mot de passe", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException ex) {
                                Toast.makeText(MotDePasseOublieActivity.this, "Erreur JSON", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
}
