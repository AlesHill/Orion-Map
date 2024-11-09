package com.example.galacticmape.Corporation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import com.example.galacticmape.R;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class CorporationPlanetsActivity extends AppCompatActivity {

    private LinearLayout linearLayoutCorporation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corporation); // Используем макет из OwnerActivity

        linearLayoutCorporation = findViewById(R.id.linearLayoutCorporation); // Используем идентификатор контейнера из activity_owner
        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());

        loadStates();
    }

    private void loadStates() {
        try {
            // Чтение файла statesList.json из папки assets
            InputStream inputStream = getAssets().open("corporationList.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String jsonData = new String(buffer, StandardCharsets.UTF_8);

            JSONObject jsonObject = new JSONObject(jsonData);

            // Перебор всех государств в statesList.json
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject state = jsonObject.getJSONObject(key);
                String corpName = state.getString("title_name");
                String shortCorpName = state.getString("corporation_names");

                // Создание кнопки для каждого государства
                Button stateButton = new Button(this);
                stateButton.setText(corpName);
                stateButton.setBackgroundResource(R.drawable.button_background);
                stateButton.setTextColor(getResources().getColor(android.R.color.white));

                // Переход к списку планет государства при нажатии на кнопку
                stateButton.setOnClickListener(v -> {
                    Intent intent = new Intent(CorporationPlanetsActivity.this, CorporationPlanetDetailsActivity.class);
                    intent.putExtra("corpName", corpName);
                    intent.putExtra("shortCorpName", shortCorpName);
                    startActivity(intent);
                });

                // Добавляем кнопку в макет
                linearLayoutCorporation.addView(stateButton);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}