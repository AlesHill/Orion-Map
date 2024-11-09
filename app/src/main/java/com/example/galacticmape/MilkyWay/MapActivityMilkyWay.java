package com.example.galacticmape.MilkyWay;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.example.galacticmape.Corporation.CorporationActivity;
import com.example.galacticmape.Corporation.CorporationPlanetsActivity;
import com.example.galacticmape.Corporation.CorporationShipsActivity;
import com.example.galacticmape.JsonFileUpdater;
import com.example.galacticmape.MainActivity;
import com.example.galacticmape.Owner.OwnerActivity;
import com.example.galacticmape.Owner.OwnerPlanetsActivity;
import com.example.galacticmape.Owner.OwnerShipsActivity;
import com.example.galacticmape.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.graphics.PointF;
import android.widget.Button;
import android.widget.LinearLayout;

public class MapActivityMilkyWay extends AppCompatActivity {

    private CustomMapViewMilkyWay customMapView;
    private JSONObject magiestralsData;
    private JSONObject markersData;
    private ArrayList<String> markerTitles;

    private LinearLayout sidePanel;
    private View panelHandle;
    private Button buttonOwner, buttonCorporation, buttonBack;
    private LinearLayout ownerList, corporationList;
    private Button buttonOwnerPlanets, buttonOwnerShips, buttonOwnerStates;
    private Button buttonCorporationPlanets, buttonCorporationShips, buttonCorporationStates;
    private boolean isPanelOpen = false;
    private int maxPanelWidth = 880;

    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Создание экземпляра JsonFileUpdater и вызов метода обновления
        JsonFileUpdater jsonUpdater = new JsonFileUpdater(this);
        jsonUpdater.updateJsonFiles();

        // Загружаем JSON с маркерами и магистралями
        try {
            markersData = loadJSONFromAssetMarkersMilkWay();
            magiestralsData = loadJSONFromAssetMagiestrals();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        // Инициализация кастомной карты
        customMapView = findViewById(R.id.imageView);

        // Устанавливаем максимальное увеличение
        customMapView.setMaxScale(10f);  // Увеличьте значение 10f до желаемого предельного увеличения

        // Передаем данные маркеров и магистралей в кастомный вид карты
        customMapView.setMagiestralsData(magiestralsData);
        customMapView.setMarkersData(markersData);

        // Поисковая строка
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);

        // Добавляем слушатель для обработки нажатий на иконку крестика
        autoCompleteTextView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (autoCompleteTextView.getRight() - autoCompleteTextView.getCompoundDrawables()[2].getBounds().width())) {
                    autoCompleteTextView.setText(""); // Очищаем поле поиска
                    return true;
                }
            }
            return false;
        });

        // Получаем список названий меток
        markerTitles = getMarkerTitles();

        // Создаем адаптер для автозаполнения
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, markerTitles);
        autoCompleteTextView.setAdapter(adapter);

        // Обработка выбора из списка
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedMarker = (String) parent.getItemAtPosition(position);
            moveToMarker(selectedMarker);  // Перемещаемся к выбранной метке
        });

        // Устанавливаем изображение карты
        customMapView.setImage(ImageSource.resource(R.drawable.milky_way_map));

        // Принудительная перерисовка карты
        customMapView.invalidate();

        sidePanel = findViewById(R.id.side_panel);
        panelHandle = findViewById(R.id.panel_handle);

        buttonOwner = findViewById(R.id.button_owner);
        buttonCorporation = findViewById(R.id.button_corporation);
        buttonBack = findViewById(R.id.button_back);

        ownerList = findViewById(R.id.owner_list);
        corporationList = findViewById(R.id.corporation_list);

        buttonOwnerPlanets = findViewById(R.id.button_owner_planets);
        buttonOwnerShips = findViewById(R.id.button_owner_ships);
        buttonOwnerStates = findViewById(R.id.button_owner_states);
        buttonCorporationPlanets = findViewById(R.id.button_corporation_planets);
        buttonCorporationShips = findViewById(R.id.button_corporation_ships);
        buttonCorporationStates = findViewById(R.id.button_corporation_states);

        sidePanel.setOnTouchListener((v, event) -> {
            // Если панель открыта, блокируем касания
            if (isPanelOpen) {
                return true; // Возвращаем true, чтобы блокировать передачу касаний
            }
            return false; // Если панель закрыта, передаем касания дальше
        });

        // Логика открытия и закрытия боковой панели
        panelHandle.setOnClickListener(v -> {
            if (isPanelOpen) {
                collapsePanel();
            } else {
                expandPanel();
            }
        });

        // Логика для раскрытия списка владельцев ("Планеты" и "Корабли")
        buttonOwner.setOnClickListener(v -> {
            if (ownerList.getVisibility() == View.GONE) {
                ownerList.setVisibility(View.VISIBLE); // Показать подсписок владельцев
            } else {
                ownerList.setVisibility(View.GONE); // Скрыть подсписок владельцев
            }
        });

        // Логика для раскрытия списка корпораций ("Планеты" и "Корабли")
        buttonCorporation.setOnClickListener(v -> {
            if (corporationList.getVisibility() == View.GONE) {
                corporationList.setVisibility(View.VISIBLE); // Показать подсписок корпораций
            } else {
                corporationList.setVisibility(View.GONE); // Скрыть подсписок корпораций
            }
        });

        // Переход на страницу "Планеты владельцев"
        buttonOwnerPlanets.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivityMilkyWay.this, OwnerPlanetsActivity.class);
            startActivity(intent);
        });

        // Переход на страницу "Корабли владельцев"
        buttonOwnerShips.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivityMilkyWay.this, OwnerShipsActivity.class);
            startActivity(intent);
        });

        // Переход на страницу "Планеты владельцев"
        buttonOwnerStates.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivityMilkyWay.this, OwnerActivity.class);
            startActivity(intent);
        });

        // Переход на страницу "Планеты корпораций"
        buttonCorporationPlanets.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivityMilkyWay.this, CorporationPlanetsActivity.class);
            startActivity(intent);
        });

        // Переход на страницу "Корабли корпораций"
        buttonCorporationShips.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivityMilkyWay.this, CorporationShipsActivity.class);
            startActivity(intent);
        });

        // Переход на страницу "Планеты корпораций"
        buttonCorporationStates.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivityMilkyWay.this, CorporationActivity.class);
            startActivity(intent);
        });

        // Переход на главную страницу по кнопке "Назад"
        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivityMilkyWay.this, MainActivity.class);
            startActivity(intent);
            finish(); // Завершаем текущую активность, чтобы не возвращаться сюда при нажатии назад
        });
    }

    private void expandPanel() {
        ValueAnimator animator = ValueAnimator.ofInt(sidePanel.getLayoutParams().width, maxPanelWidth);
        animator.setDuration(300);
        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            // Обновляем ширину панели
            ViewGroup.LayoutParams params = sidePanel.getLayoutParams();
            params.width = animatedValue;
            sidePanel.setLayoutParams(params);

            // Синхронизируем положение ручки с правым краем панели
            panelHandle.setTranslationX(animatedValue);
        });
        animator.start();
        isPanelOpen = true;
    }

    private void collapsePanel() {
        ValueAnimator animator = ValueAnimator.ofInt(sidePanel.getLayoutParams().width, 0);
        animator.setDuration(300);
        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            // Обновляем ширину панели
            ViewGroup.LayoutParams params = sidePanel.getLayoutParams();
            params.width = animatedValue;
            sidePanel.setLayoutParams(params);

            // Синхронизируем положение ручки
            panelHandle.setTranslationX(animatedValue);
        });
        animator.start();
        isPanelOpen = false;
    }

    // Метод для получения списка названий меток
    private ArrayList<String> getMarkerTitles() {
        ArrayList<String> titles = new ArrayList<>();
        try {
            Iterator<String> keys = markersData.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject marker = markersData.getJSONObject(key);
                String title = marker.getString("title");
                titles.add(title);  // Добавляем название метки в список
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return titles;
    }

    // Метод для перемещения к метке по названию
    private void moveToMarker(String query) {
        try {
            boolean markerFound = false;  // Флаг, чтобы узнать, найдена ли метка
            Iterator<String> keys = markersData.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject marker = markersData.getJSONObject(key);
                String title = marker.getString("title");

                // Сравнение названия метки с запросом
                if (title.equalsIgnoreCase(query)) {
                    // Логирование найденной метки
                    Log.d("SearchMarker", "Найдена метка: " + title);

                    // Получаем координаты метки
                    String[] coords = key.replace("(", "").replace(")", "").split(", ");
                    float x = Float.parseFloat(coords[0]);
                    float y = Float.parseFloat(coords[1]);

                    // Преобразуем координаты на карте
                    PointF markerPosition = new PointF(x, y);  // Используем координаты напрямую

                    if (markerPosition != null) {
                        // Перемещаем камеру на метку
                        customMapView.setScaleAndCenter(customMapView.getMaxScale(), markerPosition);
                        customMapView.invalidate();
                        markerFound = true;
                    }
                    break;  // Метка найдена, завершаем цикл
                }
            }

            // Если метка не найдена, выводим сообщение в лог
            if (!markerFound) {
                Log.d("SearchMarker", "Метка не найдена: " + query);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject loadJSONFromAssetMagiestrals() throws JSONException, IOException {
        String json;
        InputStream is = getAssets().open("magiestralsMilkyWay.json");  // Имя файла с магистралями
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        json = new String(buffer, "UTF-8");
        return new JSONObject(json);
    }

    // Метод для загрузки JSON из assets
    public JSONObject loadJSONFromAssetMarkersMilkWay() throws JSONException, IOException {
        String json;
        InputStream is = getAssets().open("markersMilkyWay.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        json = new String(buffer, "UTF-8");

        return new JSONObject(json);
    }
}