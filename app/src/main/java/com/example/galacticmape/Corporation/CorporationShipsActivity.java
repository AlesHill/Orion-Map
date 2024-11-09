package com.example.galacticmape.Corporation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import com.example.galacticmape.R;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.Iterator;

public class CorporationShipsActivity extends AppCompatActivity {

    private LinearLayout linearLayoutCorporation;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceCorporation) {
        super.onCreate(savedInstanceCorporation);
        setContentView(R.layout.activity_corporation);

        linearLayoutCorporation = findViewById(R.id.linearLayoutCorporation);
        buttonBack = findViewById(R.id.buttonBack);

        loadCorporations();

        buttonBack.setOnClickListener(v -> {
            finish(); // Возвращаемся на карту
        });
    }

    private void loadCorporations() {
        try {
            // Читаем JSON файл с государствами
            InputStream inputStream = getAssets().open("corporationList.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);

            // Итерация по ключам JSON объекта для добавления кнопок государств
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String corporationKey = keys.next();
                JSONObject corporationObject = jsonObject.getJSONObject(corporationKey);
                String corporationTitle = corporationObject.getString("title_name");

                Button corporationButton = new Button(this);
                corporationButton.setText(corporationTitle);
                corporationButton.setBackground(getResources().getDrawable(R.drawable.button_background));
                corporationButton.setTextColor(getResources().getColor(android.R.color.white));
                corporationButton.setOnClickListener(v -> {
                    Intent intent = new Intent(CorporationShipsActivity.this, CorporationShipDetailsActivity.class);
                    intent.putExtra("corporationId", corporationKey); // Передаем идентификатор государства
                    startActivity(intent); // Запускаем новую Activity
                });

                linearLayoutCorporation.addView(corporationButton);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}