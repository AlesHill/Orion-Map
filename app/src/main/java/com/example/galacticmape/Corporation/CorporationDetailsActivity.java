package com.example.galacticmape.Corporation;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.galacticmape.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

public class CorporationDetailsActivity extends AppCompatActivity {

    private ImageView imageViewFlagCorporation;
    private TextView textViewFullNameCorporation, textViewCorporationType, textViewCapitalCorporation, textViewBelonging;
    private TextView textViewIndustries, textViewProducts;
    private TextView textViewPrimaryPlanetsMilkyWay, textViewSecondaryPlanetsMilkyWay, textViewTertiaryPlanetsMilkyWay, textViewShipsMilkyWay;
    private TextView textViewPrimaryPlanetsLMC, textViewSecondaryPlanetsLMC, textViewTertiaryPlanetsLMC, textViewShipsLMC;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceCorporation) {
        super.onCreate(savedInstanceCorporation);
        setContentView(R.layout.activity_corporation_detail);

        // Связываем элементы с их идентификаторами
        imageViewFlagCorporation = findViewById(R.id.imageViewFlagCorporation);
        textViewFullNameCorporation = findViewById(R.id.textViewFullNameCorporation);
        textViewCorporationType = findViewById(R.id.textViewCorporationType);
        textViewCapitalCorporation = findViewById(R.id.textViewCapitalCorporation);
        textViewBelonging = findViewById(R.id.textViewBelonging);
        textViewIndustries = findViewById(R.id.textViewIndustries);
        textViewProducts = findViewById(R.id.textViewProducts);
        textViewPrimaryPlanetsMilkyWay = findViewById(R.id.textViewPrimaryPlanetsMilkyWay);
        textViewSecondaryPlanetsMilkyWay = findViewById(R.id.textViewSecondaryPlanetsMilkyWay);
        textViewTertiaryPlanetsMilkyWay = findViewById(R.id.textViewTertiaryPlanetsMilkyWay);
        textViewShipsMilkyWay = findViewById(R.id.textViewShipsMilkyWay);
        textViewPrimaryPlanetsLMC = findViewById(R.id.textViewPrimaryPlanetsLMC);
        textViewSecondaryPlanetsLMC = findViewById(R.id.textViewSecondaryPlanetsLMC);
        textViewTertiaryPlanetsLMC = findViewById(R.id.textViewTertiaryPlanetsLMC);
        textViewShipsLMC = findViewById(R.id.textViewShipsLMC);
        buttonBack = findViewById(R.id.buttonBack);

        // Получаем переданный идентификатор корпорации
        String corporationId = getIntent().getStringExtra("corporationId");

        // Загружаем данные по корпорации
        loadCorporationDetails(corporationId);

        // Обработчик кнопки "Назад"
        buttonBack.setOnClickListener(v -> finish());
    }

    private String loadJsonFromInternalStorage(String fileName) throws IOException {
        File file = new File(getFilesDir(), "assets/" + fileName);
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader reader = new BufferedReader(isr);

        StringBuilder jsonContent = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonContent.append(line);
        }

        reader.close();
        isr.close();
        fis.close();

        return jsonContent.toString();
    }

    private void loadCorporationDetails(String corporationId) {
        try {
            // Загружаем JSON данные из внутреннего хранилища
            String json = loadJsonFromInternalStorage("corporationList.json");
            JSONObject corporationsObject = new JSONObject(json);
            JSONObject corporation = corporationsObject.getJSONObject(corporationId);

            // Заполняем данные с проверкой на пустоту
            String fullName = getValidString(corporation, "title_name", "Неизвестно");
            String photoName = getValidString(corporation, "photo_name", "default_image");
            String corporationType = getValidString(corporation, "corporation_type", "Нет данных");
            String capital = getValidString(corporation, "capital", "Неизвестно");
            String ownerName = getValidString(corporation, "owner_names", "Неизвестно");

            // Загрузка изображения флага
            int flagId = getResources().getIdentifier(photoName, "drawable", getPackageName());
            imageViewFlagCorporation.setImageResource(flagId);

            // Заполняем текстовые поля
            textViewFullNameCorporation.setText(fullName);
            textViewCorporationType.setText("Тип Корпорации: " + corporationType);
            textViewCapitalCorporation.setText("Столица: " + capital);
            textViewBelonging.setText("Принадлежность: " + getStateFullName(ownerName));
            textViewIndustries.setText("Отрасли: " + getValidString(corporation, "industry", "Нет данных"));
            textViewProducts.setText("Продукция: " + getValidString(corporation, "products", "Нет данных"));

            // Загрузить данные о планетах и кораблях
            loadPlanetsAndShipsDetails(corporationId);

        } catch (Exception e) {
            Log.e("CorporationDetails", "Ошибка при загрузке данных о корпорации: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Метод для получения валидной строки или значения по умолчанию
    private String getValidString(JSONObject jsonObject, String key, String defaultValue) {
        try {
            String value = jsonObject.optString(key, defaultValue);
            // Проверяем, что строка не пустая и не содержит только пробелы
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        } catch (Exception e) {
            Log.e("CorporationDetails", "Ошибка при получении значения для ключа " + key + ": " + e.getMessage());
        }
        return defaultValue;
    }

    private String getStateFullName(String ownerName) {
        // Загружаем данные о государствах для получения полного названия по ownerName
        try {
            InputStream inputStream = getAssets().open("statesList.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");
            JSONObject statesObject = new JSONObject(json);

            if (statesObject.has(ownerName)) {
                return statesObject.getJSONObject(ownerName).getString("title_name");
            }
        } catch (Exception e) {
            Log.e("CorporationDetails", "Ошибка при загрузке данных о государствах: " + e.getMessage());
            e.printStackTrace();
        }

        return "Неизвестно";
    }

    private void loadPlanetsAndShipsDetails(String corporationId) {
        try {
            // Читаем JSON с маркерами для Млечного Пути и Большого Магелланового Облака
            InputStream inputStreamMilkyWay = getAssets().open("markersMilkyWay.json");
            InputStream inputStreamLMC = getAssets().open("markersLargeMagellanicCloud.json");

            byte[] bufferMilkyWay = new byte[inputStreamMilkyWay.available()];
            byte[] bufferLMC = new byte[inputStreamLMC.available()];

            inputStreamMilkyWay.read(bufferMilkyWay);
            inputStreamLMC.read(bufferLMC);

            inputStreamMilkyWay.close();
            inputStreamLMC.close();

            String jsonMilkyWay = new String(bufferMilkyWay, "UTF-8");
            String jsonLMC = new String(bufferLMC, "UTF-8");

            JSONObject markersMilkyWay = new JSONObject(jsonMilkyWay);
            JSONObject markersLMC = new JSONObject(jsonLMC);

            // Обработка данных для Млечного Пути и Большого Магелланового Облака
            calculatePlanetsAndShips(markersMilkyWay, corporationId, "MilkyWay");
            calculatePlanetsAndShips(markersLMC, corporationId, "LMC");

        } catch (Exception e) {
            Log.e("CorporationDetails", "Ошибка при загрузке данных о планетах и кораблях: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void calculatePlanetsAndShips(JSONObject markers, String corporationId, String galaxy) {
        try {
            int primaryCount = 0;
            int secondaryCount = 0;
            int tertiaryCount = 0;
            int maxShipCount = 0;

            Iterator<String> keys = markers.keys();
            while (keys.hasNext()) {
                String markerKey = keys.next();
                JSONObject marker = markers.getJSONObject(markerKey);

                // Проверяем, принадлежит ли маркер данной корпорации по полю corporation_influence
                if (marker.optString("corporation_influence", "").equals(corporationId)) {
                    String markerType = marker.optString("marker_type", "Неизвестно");
                    String economy = marker.optString("economy", "Отсутствует");
                    boolean isCapital = marker.optBoolean("is_capital", false);

                    // Увеличиваем счётчики меток по типам
                    switch (markerType) {
                        case "Первичная":
                            primaryCount++;
                            break;
                        case "Вторичная":
                            secondaryCount++;
                            break;
                        case "Третичная":
                            tertiaryCount++;
                            break;
                    }

                    // Определяем количество кораблей на основе экономики
                    int economyShips = 0;
                    switch (economy) {
                        case "Экуменополис":
                            economyShips = 1000;
                            break;
                        case "Завод":
                            economyShips = 1500;
                            break;
                        case "Ресурсная":
                            economyShips = 500;
                            break;
                        case "Пищевая":
                            economyShips = 250;
                            break;
                        case "Отсутствует":
                        default:
                            economyShips = 0;
                            break;
                    }

                    // Множитель для типа метки (учитываем только первичные)
                    double markerTypeMultiplier = markerType.equals("Первичная") ? 1.0 : 0;

                    // Увеличиваем количество кораблей втрое, если это столица
                    if (isCapital) {
                        economyShips *= 1.5;
                    }

                    // Рассчитываем общее количество кораблей для этой метки
                    int totalShips = (int) (economyShips * markerTypeMultiplier);
                    maxShipCount += totalShips;
                }
            }

            // Заполняем данные в зависимости от галактики
            if (galaxy.equals("MilkyWay")) {
                textViewPrimaryPlanetsMilkyWay.setText("Первичные: " + primaryCount);
                textViewSecondaryPlanetsMilkyWay.setText("Вторичные: " + secondaryCount);
                textViewTertiaryPlanetsMilkyWay.setText("Третичные: " + tertiaryCount);
                textViewShipsMilkyWay.setText("Количество кораблей: " + maxShipCount);
            } else if (galaxy.equals("LMC")) {
                textViewPrimaryPlanetsLMC.setText("Первичные: " + primaryCount);
                textViewSecondaryPlanetsLMC.setText("Вторичные: " + secondaryCount);
                textViewTertiaryPlanetsLMC.setText("Третичные: " + tertiaryCount);
                textViewShipsLMC.setText("Количество кораблей: " + maxShipCount);
            }

        } catch (Exception e) {
            Log.e("CorporationDetails", "Ошибка в вычислении планет и кораблей: " + e.getMessage());
            e.printStackTrace();
        }
    }
}