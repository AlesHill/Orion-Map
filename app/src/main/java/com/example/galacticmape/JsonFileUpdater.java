package com.example.galacticmape;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.galacticmape.MilkyWay.CustomMapViewMilkyWay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JsonFileUpdater {

    private static final String BASE_URL = "https://raw.githubusercontent.com/AlesHill/Orion-Map/refs/heads/main/app/src/main/assets/";

    // URL для файлов, требующих перерисовки карты
    private static final String[] MAP_UPDATE_FILES = {
            "magiestralsMilkyWay.json",
            "markersMilkyWay.json"
    };

    // URL для дополнительных файлов, не требующих перерисовки карты
    private static final String[] EXTRA_FILES = {
            "corporationList.json",
            "shipsFlagmanList.json",
            "shipsList.json",
            "statesList.json",
            "timeShipsBuildMod.json",
            "variebl.json"
    };

    private Context context;

    public JsonFileUpdater(Context context) {
        this.context = context;
    }

    // Метод для загрузки и обновления всех JSON файлов
    public void updateJsonFiles() {
        // Сначала обновляем файлы для карты
        for (String fileName : MAP_UPDATE_FILES) {
            new DownloadJsonTask(fileName, true).execute(BASE_URL + fileName);
        }

        // Затем обновляем дополнительные файлы
        for (String fileName : EXTRA_FILES) {
            new DownloadJsonTask(fileName, false).execute(BASE_URL + fileName);
        }
    }

    private class DownloadJsonTask extends AsyncTask<String, Void, Void> {
        private String fileName;
        private boolean requiresMapReload;

        public DownloadJsonTask(String fileName, boolean requiresMapReload) {
            this.fileName = fileName;
            this.requiresMapReload = requiresMapReload;
        }

        @Override
        protected Void doInBackground(String... urls) {
            try {
                String jsonContent = downloadJsonFile(urls[0]);
                if (jsonContent != null) {
                    saveToFile(fileName, jsonContent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (requiresMapReload) {
                reloadMap();
            }
        }

        private String downloadJsonFile(String url) throws IOException {
            Request request = new Request.Builder().url(url).build();
            try (Response response = new OkHttpClient().newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Ошибка при запросе: " + response);
                return response.body().string();
            }
        }

        private void saveToFile(String fileName, String jsonContent) {
            try {
                // Создаем директорию "MilkyWay" во внутреннем хранилище
                File directory = new File(context.getFilesDir(), "MilkyWay");
                if (!directory.exists()) {
                    directory.mkdirs(); // Создаем директорию, если она не существует
                }

                // Сохраняем файл
                File file = new File(directory, fileName);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(jsonContent.getBytes());
                    Log.d("JsonFileUpdater", "Файл сохранён: " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                Log.e("JsonFileUpdater", "Ошибка при сохранении файла: " + fileName, e);
            }
        }
    }

    // Метод для полной перезагрузки карты (только для файлов, связанных с картой)
    private void reloadMap() {
        String magiestralsJson = loadJsonFromFile("magiestralsMilkyWay.json");
        String markersJson = loadJsonFromFile("markersMilkyWay.json");

        if (magiestralsJson != null && markersJson != null) {
            CustomMapViewMilkyWay customMapView = ((Activity) context).findViewById(R.id.imageView);
            customMapView.clear();
            customMapView.loadMagiestrals(magiestralsJson);
            customMapView.loadMarkers(markersJson);
            customMapView.invalidate();
        } else {
            Log.e("JsonFileUpdater", "Ошибка: не удалось загрузить данные магистралей или маркеров.");
        }
    }

    // Метод для загрузки JSON из локального файла
    private String loadJsonFromFile(String fileName) {
        File file = new File(context.getFilesDir(), "MilkyWay/" + fileName);
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader reader = new BufferedReader(isr)) {

            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            return jsonContent.toString();
        } catch (IOException e) {
            Log.e("JsonFileUpdater", "Ошибка при загрузке файла: " + fileName, e);
            return null;
        }
    }
}