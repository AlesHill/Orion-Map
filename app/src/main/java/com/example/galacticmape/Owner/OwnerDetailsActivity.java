package com.example.galacticmape.Owner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.galacticmape.R;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.Iterator;

public class OwnerDetailsActivity extends AppCompatActivity {

    private ImageView imageViewFlag;
    private TextView textViewFullName, textViewIdeology, textViewGovernment, textViewFormState, textViewCorporation, textViewCapital;
    private TextView textViewPrimaryPlanetsMilkyWay, textViewSecondaryPlanetsMilkyWay, textViewTertiaryPlanetsMilkyWay, textViewShipsMilkyWay;
    private TextView textViewPrimaryPlanetsLMC, textViewSecondaryPlanetsLMC, textViewTertiaryPlanetsLMC, textViewShipsLMC;
    private Button buttonBack;
    private android.util.Log Log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_detail);

        // Связываем элементы с их идентификаторами
        imageViewFlag = findViewById(R.id.imageViewFlag);
        textViewFullName = findViewById(R.id.textViewFullName);
        textViewIdeology = findViewById(R.id.textViewIdeology);
        textViewGovernment = findViewById(R.id.textViewGovernment);
        textViewFormState = findViewById(R.id.textViewFormState);
        textViewCorporation = findViewById(R.id.textViewCorporation);
        textViewCapital = findViewById(R.id.textViewCapital);
        textViewPrimaryPlanetsMilkyWay = findViewById(R.id.textViewPrimaryPlanetsMilkyWay);
        textViewSecondaryPlanetsMilkyWay = findViewById(R.id.textViewSecondaryPlanetsMilkyWay);
        textViewTertiaryPlanetsMilkyWay = findViewById(R.id.textViewTertiaryPlanetsMilkyWay);
        textViewShipsMilkyWay = findViewById(R.id.textViewShipsMilkyWay);
        textViewPrimaryPlanetsLMC = findViewById(R.id.textViewPrimaryPlanetsLMC);
        textViewSecondaryPlanetsLMC = findViewById(R.id.textViewSecondaryPlanetsLMC);
        textViewTertiaryPlanetsLMC = findViewById(R.id.textViewTertiaryPlanetsLMC);
        textViewShipsLMC = findViewById(R.id.textViewShipsLMC);
        buttonBack = findViewById(R.id.buttonBack);

        // Получаем переданный идентификатор государства
        String stateId = getIntent().getStringExtra("stateId");

        // Загружаем данные по государству
        loadStateDetails(stateId);

        // Обработчик кнопки "Назад"
        buttonBack.setOnClickListener(v -> {
            finish(); // Возвращаемся на предыдущую страницу
        });
    }

    private void loadStateDetails(String stateId) {
        try {
            // Читаем JSON с государствами
            InputStream inputStream = getAssets().open("statesList.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");
            JSONObject statesObject = new JSONObject(json);
            JSONObject state = statesObject.getJSONObject(stateId);

            // Заполняем данные
            String fullName = state.getString("title_name");
            String photoName = state.getString("photo_name");
            String ideology = state.getString("ideology");
            String government = state.getString("government");
            String formState = state.getString("form_state");
            String corporationShortName = state.getString("сorporation_names");
            String capital = state.getString("capital");

            textViewFullName.setText(fullName);
            textViewIdeology.setText("Идеология: " + ideology);
            textViewGovernment.setText("Форма правления: " + government);
            textViewFormState.setText("Форма государственного устройства: " + formState);
            textViewCapital.setText("Столица: " + capital);

            // Загрузка изображения флага
            int flagId = getResources().getIdentifier(photoName, "drawable", getPackageName());
            imageViewFlag.setImageResource(flagId);

            loadCorporationDetails(corporationShortName);

            // Загружаем данные о планетах и кораблях
            loadPlanetsAndShipsDetails(stateId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCorporationDetails(String corporationShortName) {
        try {
            // Читаем JSON с корпорациями
            InputStream inputStream = getAssets().open("corporationList.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");
            JSONObject corporationObject = new JSONObject(json);
            JSONObject corporation = corporationObject.getJSONObject(corporationShortName);
            String fullName = corporation.getString("title_name");

            textViewCorporation.setText("Корпорация: " + fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPlanetsAndShipsDetails(String stateId) {
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
            calculatePlanetsAndShips(markersMilkyWay, stateId, "MilkyWay");
            calculatePlanetsAndShips(markersLMC, stateId, "LMC");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculatePlanetsAndShips(JSONObject markers, String stateId, String galaxy) {
        try {
            int primaryCount = 0;
            int secondaryCount = 0;
            int tertiaryCount = 0;
            int maxShipCount = 0;

            // Перебираем все маркеры
            Iterator<String> keys = markers.keys();
            while (keys.hasNext()) {
                String markerKey = keys.next();
                JSONObject marker = markers.getJSONObject(markerKey);

                // Проверка, принадлежит ли маркер данному государству
                if (marker.getString("owner").equals(stateId)) {
                    String markerType = marker.getString("marker_type");
                    String economy = marker.getString("economy");
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
                            economyShips = 2000;
                            break;
                        case "Завод":
                            economyShips = 3000;
                            break;
                        case "Ресурсная":
                            economyShips = 1000;
                            break;
                        case "Пищевая":
                            economyShips = 500;
                            break;
                        case "Отсутствует":
                        default:
                            economyShips = 0;
                            break;
                    }

                    // Множитель для типа метки (учитываем только первичные)
                    double markerTypeMultiplier = 1.0;
                    if (markerType.equals("Первичная")) {
                        markerTypeMultiplier = 2.0;
                    } else {
                        markerTypeMultiplier = 0; // Игнорируем вторичные и третичные метки для подсчёта кораблей
                    }

                    // Увеличиваем количество кораблей втрое, если это столица
                    if (isCapital) {
                        economyShips *= 3;
                    }

                    // Рассчитываем общее количество кораблей для этой метки
                    int totalShips = (int) (economyShips * markerTypeMultiplier);

                    // Добавляем к общему максимальному количеству кораблей
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

            // Логирование данных для отладки
            Log.d("StateDetails", "Галактика: " + galaxy);
            Log.d("StateDetails", "Первичные: " + primaryCount + ", Вторичные: " + secondaryCount + ", Третичные: " + tertiaryCount);
            Log.d("StateDetails", "Максимальное количество кораблей: " + maxShipCount);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("StateDetails", "Ошибка в вычислении планет и кораблей: " + e.getMessage());
        }
    }
}