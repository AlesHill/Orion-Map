package com.example.galacticmape.Corporation;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.galacticmape.R;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

public class CorporationPlanetDetailsActivity extends AppCompatActivity {

    private LinearLayout primaryLayout, secondaryLayout, tertiaryLayout;
    private String shortCorpName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corporation_planet);

        primaryLayout = findViewById(R.id.primaryLayout);
        secondaryLayout = findViewById(R.id.secondaryLayout);
        tertiaryLayout = findViewById(R.id.tertiaryLayout);

        // Устанавливаем действие для кнопки "Назад"
        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        // Получаем короткое имя из Intent
        shortCorpName = getIntent().getStringExtra("shortCorpName");

        // Загружаем и отображаем только планеты, принадлежащие этому государству
        loadAndDisplayPlanets(shortCorpName);
    }

    private void loadAndDisplayPlanets(String shortCorpName) {
        ArrayList<String> primaryPlanets = new ArrayList<>();
        ArrayList<String> secondaryPlanets = new ArrayList<>();
        ArrayList<String> tertiaryPlanets = new ArrayList<>();

        try {
            InputStream inputStream = getAssets().open("markersMilkyWay.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            JSONObject markersObject = new JSONObject(new String(buffer, StandardCharsets.UTF_8));
            Iterator<String> keys = markersObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject marker = markersObject.getJSONObject(key);
                String corporation = marker.getString("corporation_influence");
                String markerType = marker.getString("marker_type");

                // Отображаем только планеты, принадлежащие данному государству
                if (corporation.equals(shortCorpName)) {
                    String planetName = marker.getString("title");
                    switch (markerType) {
                        case "Первичная":
                            primaryPlanets.add(planetName);
                            break;
                        case "Вторичная":
                            secondaryPlanets.add(planetName);
                            break;
                        case "Третичная":
                            tertiaryPlanets.add(planetName);
                            break;
                    }
                }
            }

            displayPlanets(primaryPlanets, primaryLayout);
            displayPlanets(secondaryPlanets, secondaryLayout);
            displayPlanets(tertiaryPlanets, tertiaryLayout);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayPlanets(ArrayList<String> planets, LinearLayout layout) {
        layout.removeAllViews();
        for (String planet : planets) {
            TextView planetTextView = new TextView(this);
            planetTextView.setText(planet);
            planetTextView.setTextSize(18);
            planetTextView.setPadding(8, 8, 8, 8);
            planetTextView.setTextColor(getResources().getColor(android.R.color.white));
            layout.addView(planetTextView);
        }
    }
}