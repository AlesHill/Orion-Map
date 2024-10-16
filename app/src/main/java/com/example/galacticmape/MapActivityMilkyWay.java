package com.example.galacticmape;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.davemorrissey.labs.subscaleview.ImageSource;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.graphics.PointF;

public class MapActivityMilkyWay extends AppCompatActivity {

    private CustomMapView customMapView;
    private JSONObject magiestralsData;
    private JSONObject markersData;
    private ArrayList<String> markerTitles;

    @SuppressLint("ClickableViewAccessibility")
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