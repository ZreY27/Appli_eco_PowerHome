package com.example.projetmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView nav;
    private ActionBarDrawerToggle toggle;
    private FragmentManager fm;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSavedLanguage();
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //centre le menu burger avec le titre
        toolbar.post(() -> {
            for (int i = 0; i < toolbar.getChildCount(); i++) {
                View view = toolbar.getChildAt(i);
                //la view est notre menu burger
                if (view instanceof ImageButton) {
                    //centre verticalement le menu burger
                    Toolbar.LayoutParams params = (Toolbar.LayoutParams) view.getLayoutParams();
                    params.gravity = Gravity.CENTER_VERTICAL;
                    view.setLayoutParams(params);
                    break;
                }
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.monhabitat);

        drawer = findViewById(R.id.drawer);

        nav = findViewById(R.id.nav_view);
        fm = getSupportFragmentManager();

        //menu burger
        toggle = new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close);

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.beige_palette));

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //bouton retour dans la barre d’action
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ImageButton p = findViewById(R.id.profil);
        p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fm.beginTransaction().replace(R.id.contentFL, new EditProfilFragment()).commit();
                toolbar.setTitle(R.string.profil);
            }
        });

        //charge le fragment initial
        if (savedInstanceState == null) {
            fm.beginTransaction().replace(R.id.contentFL, new MonHabitatFragment()).commit();
            nav.setCheckedItem(R.id.nav);
        }

        nav.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String title = getString(R.string.monhabitat); //titre par défaut

        if (item.getItemId() == R.id.nav) {
            fm.beginTransaction().replace(R.id.contentFL, new MonHabitatFragment()).commit();
            title = getString(R.string.monhabitat);
        } else if (item.getItemId() == R.id.nav2) {
            fm.beginTransaction().replace(R.id.contentFL, new HabitatFragment()).commit();
            title = getString(R.string.listhabitat);
        } else if (item.getItemId() == R.id.nav3) {
            fm.beginTransaction().replace(R.id.contentFL, new CalendrierFragment()).commit();
            title = getString(R.string.requests);
        } else if (item.getItemId() == R.id.nav4) {
            fm.beginTransaction().replace(R.id.contentFL, new SettingFragment()).commit();
            title = getString(R.string.settings);
        } else if (item.getItemId() == R.id.nav5) {
            logoutUser();
        }

        //maj du titre de la Toolbar
        toolbar.setTitle(title);

        //ferme le menu latéral après sélection
        drawer.postDelayed(() -> drawer.closeDrawer(GravityCompat.START), 150);

        return true;
    }

    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token");
        editor.apply();

        //redirection page de connexion
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
