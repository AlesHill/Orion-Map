package com.example.galacticmape.Owner;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.galacticmape.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Iterator;

public class OwnerShipDetailActivity extends AppCompatActivity {

    private LinearLayout shipsContainer;
    private JSONObject varieblData;
    private JSONObject shipsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_ship);

        // Устанавливаем действие для кнопки "Назад"
        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        // Получаем идентификатор государства
        String stateId = getIntent().getStringExtra("stateId");
        Log.d("OwnerShipDetailActivity", "stateId: " + stateId);

        if (stateId == null) {
            Log.e("OwnerShipDetailActivity", "stateId is null. Проверьте, передается ли параметр правильно.");
            return;
        }

        shipsContainer = findViewById(R.id.ships_container);

        // Загрузка JSON данных
        loadJSONData();

        // Загрузка и отображение списка кораблей для выбранного государства
        loadShipsForState(stateId);
    }

    private void loadJSONData() {
        try {
            varieblData = new JSONObject(loadJSONFromAsset("variebl.json"));
            shipsList = new JSONObject(loadJSONFromAsset("shipsList.json"));
        } catch (Exception e) {
            Log.e("OwnerShipDetailActivity", "Ошибка загрузки JSON данных", e);
        }
    }

    private void loadShipsForState(String stateId) {
        try {
            // Загружаем данные о кораблях для выбранного государства из statesList.json
            JSONObject statesData = new JSONObject(loadJSONFromAsset("statesList.json"));
            JSONObject stateData = statesData.getJSONObject(stateId);
            JSONObject shipsForState = stateData.getJSONObject("ships_list");

            if (shipsForState.length() == 0) {
                Log.e("OwnerShipDetailActivity", "Нет данных о кораблях для государства: " + stateId);
            } else {
                Log.d("OwnerShipDetailActivity", "Найдено " + shipsForState.length() + " кораблей для государства " + stateId);
            }

            Iterator<String> keys = shipsForState.keys();
            while (keys.hasNext()) {
                String shipId = keys.next();
                int quantity = shipsForState.optInt(shipId, 0); // Количество кораблей
                displayShipDetails(shipId, quantity);
            }
        } catch (Exception e) {
            Log.e("OwnerShipDetailActivity", "Ошибка при загрузке кораблей для государства", e);
        }
    }

    private JSONObject findShipData(String shipId) {
        try {
            // Проходим по всем разделам в shipsList
            for (Iterator<String> keys = shipsList.keys(); keys.hasNext(); ) {
                String seriesKey = keys.next();
                JSONObject seriesData = shipsList.getJSONObject(seriesKey);

                // Проходим по всем кораблям в каждой серии
                for (Iterator<String> shipKeys = seriesData.keys(); shipKeys.hasNext(); ) {
                    String shipName = shipKeys.next();
                    JSONObject shipData = seriesData.getJSONObject(shipName);

                    // Сравниваем id корабля с заданным shipId
                    if (shipData.optString("id").equals(shipId)) {
                        return shipData; // Возвращаем данные о корабле, если id совпадает
                    }
                }
            }
        } catch (Exception e) {
            Log.e("OwnerShipDetailActivity", "Ошибка при поиске данных корабля: " + shipId, e);
        }
        return null; // Возвращаем null, если корабль с заданным id не найден
    }

    private void displayShipDetails(String shipId, int quantity) {
        try {
            JSONObject shipData = findShipData(shipId);
            if (shipData == null) return;

            String name = shipData.optString("name", "Неизвестно");
            String className = shipData.optString("class_ship", "Неизвестно");
            String subclass = shipData.optString("subclass_ship", "Неизвестно");
            String role = shipData.optString("role", "Неизвестно");
            String type = shipData.optString("type_ship", "Неизвестно");
            String generation = shipData.optString("version", "Неизвестно");

            // Получаем ключи для иконок класса и роли
            String classIconKey = getIconKeyForClass(className);
            String roleIconKey = getIconKeyForRole(role);

            // Передаем className и role в addShipButton
            addShipButton(name, classIconKey, subclass, roleIconKey, className, role, type, generation, quantity);

        } catch (Exception e) {
            Log.e("OwnerShipDetailActivity", "Ошибка отображения деталей корабля", e);
        }
    }

    private String getIconKeyForClass(String className) {
        try {
            JSONObject classMap = varieblData.getJSONObject("class");
            for (Iterator<String> it = classMap.keys(); it.hasNext(); ) {
                String key = it.next();
                if (classMap.getString(key).equals(className)) {
                    return key;  // Возвращает ключ, например, "battleship"
                }
            }
        } catch (JSONException e) {
            Log.e("OwnerShipDetailActivity", "Ошибка получения ключа иконки для класса", e);
        }
        return null;
    }

    private String getIconKeyForRole(String roleName) {
        try {
            JSONObject roleMap = varieblData.getJSONObject("role");
            for (Iterator<String> it = roleMap.keys(); it.hasNext(); ) {
                String key = it.next();
                if (roleMap.getString(key).equals(roleName)) {
                    return key;  // Возвращает ключ, например, "cruiser"
                }
            }
        } catch (JSONException e) {
            Log.e("OwnerShipDetailActivity", "Ошибка получения ключа иконки для роли", e);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    private void addShipButton(String name, String classIconKey, String subclass, String roleIconKey,
                               String className, String role, String type, String generation, int quantity) {
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, 0, 0, 16);

        int classDrawableId = getResources().getIdentifier("class_" + classIconKey, "drawable", getPackageName());
        int roleDrawableId = getResources().getIdentifier("role_" + roleIconKey, "drawable", getPackageName());

        Drawable classDrawable = classDrawableId != 0 ? ContextCompat.getDrawable(this, classDrawableId) : null;
        Drawable roleDrawable = roleDrawableId != 0 ? ContextCompat.getDrawable(this, roleDrawableId) : null;

        int color = getColorBySubclass(subclass);

        ImageView classIcon = new ImageView(this);
        classIcon.setImageDrawable(classDrawable);
        if (classDrawable != null) {
            classIcon.setColorFilter(color);
        }
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(80, 80);
        iconParams.setMargins(8, 0, 8, 0);
        classIcon.setLayoutParams(iconParams);

        ImageView roleIcon = new ImageView(this);
        roleIcon.setImageDrawable(roleDrawable);
        if (roleDrawable != null) {
            roleIcon.setColorFilter(color);
        }
        roleIcon.setLayoutParams(iconParams);

        Button shipButton = new Button(this);
        shipButton.setText(name);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.setMargins(8, 0, 0, 0);
        shipButton.setLayoutParams(buttonParams);

        // Установка информации о корабле в TextView
        TextView shipInfoView = new TextView(this);
        shipInfoView.setText(
                "Класс: " + className +
                        "\nПодкласс: " + subclass +
                        "\nРоль: " + role +
                        "\nТип: " + type +
                        "\nПоколение: " + generation +
                        "\nКоличество: " + quantity
        );
        shipInfoView.setVisibility(View.GONE);

        shipInfoView.setBackgroundColor(Color.parseColor("#444444"));
        shipInfoView.setPadding(32, 16, 32, 16);
        shipInfoView.setTextColor(Color.WHITE);
        shipInfoView.setTextSize(14);

        shipButton.setOnClickListener(v -> {
            if (shipInfoView.getVisibility() == View.GONE) {
                shipInfoView.setVisibility(View.VISIBLE);
            } else {
                shipInfoView.setVisibility(View.GONE);
            }
        });

        buttonLayout.addView(classIcon);
        buttonLayout.addView(roleIcon);
        buttonLayout.addView(shipButton);

        LinearLayout shipLayout = new LinearLayout(this);
        shipLayout.setOrientation(LinearLayout.VERTICAL);
        shipLayout.addView(buttonLayout);
        shipLayout.addView(shipInfoView);

        shipsContainer.addView(shipLayout);
    }

    private int getColorBySubclass(String subclassName) {
        try {
            JSONObject subclassMap = varieblData.getJSONObject("subclass");
            String subclassKey = null;

            for (Iterator<String> it = subclassMap.keys(); it.hasNext(); ) {
                String key = it.next();
                if (subclassMap.getString(key).equals(subclassName)) {
                    subclassKey = key;
                    break;
                }
            }

            if (subclassKey != null) {
                switch (subclassKey) {
                    case "light":
                        return Color.GREEN;
                    case "heavy":
                        return Color.rgb(242,89,13);
                    case "superheavy":
                        return Color.RED;
                    default:
                        return Color.YELLOW;
                }
            }
        } catch (JSONException e) {
            Log.e("OwnerShipDetailActivity", "Ошибка получения цвета для подкласса", e);
        }
        return Color.YELLOW; // Возвращаем желтый, если подкласс не найден
    }

    private String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return json;
    }
}