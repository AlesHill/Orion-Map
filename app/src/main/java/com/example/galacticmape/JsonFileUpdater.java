package com.example.galacticmape;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JsonFileUpdater {

    private static final String MAGIESTRALS_URL = "https://raw.githubusercontent.com/AlesHill/Orion-Map/refs/heads/main/app/src/main/assets/magiestralsMilkyWay.json";
    private static final String MARKERS_URL = "https://raw.githubusercontent.com/AlesHill/Orion-Map/refs/heads/main/app/src/main/assets/markersMilkyWay.json";
    private Context context;

    public JsonFileUpdater(Context context) {
        this.context = context;
    }

    // Метод для загрузки и обновления JSON файлов
    public void updateJsonFiles() {
        new DownloadJsonTask().execute(MAGIESTRALS_URL, MARKERS_URL);
    }

    private class DownloadJsonTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            try {
                String magiestralsJson = downloadJsonFile(urls[0]);
                String markersJson = downloadJsonFile(urls[1]);

                saveToFile("magiestralsMilkyWay.json", magiestralsJson);
                saveToFile("markersMilkyWay.json", markersJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // После завершения загрузки данных, перерисовываем карту
            reloadMap();
        }

        private String downloadJsonFile(String url) throws IOException {
            Request request = new Request.Builder().url(url).build();
            try (Response response = new OkHttpClient().newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Ошибка при запросе: " + response);
                return response.body().string();
            }
        }

        private void saveToFile(String fileName, String jsonContent) {
            try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
                fos.write(jsonContent.getBytes());
                Log.d("JsonFileUpdater", "Файл сохранён: " + fileName);
            } catch (IOException e) {
                Log.e("JsonFileUpdater", "Ошибка при сохранении файла: " + fileName, e);
            }
        }
    }

    // Метод для полной перезагрузки карты
    private void reloadMap() {
        // Очистите данные карты (если нужно)
        CustomMapViewMilkyWay customMapView = ((Activity) context).findViewById(R.id.imageView);
        customMapView.clear(); // Очистите текущие данные карты (магистрали, маркеры и т.д.)

        // Перезагрузите данные из файлов
        String magiestralsJson = loadJsonFromFile("magiestralsMilkyWay.json");
        String markersJson = loadJsonFromFile("markersMilkyWay.json");

        // Используйте новые данные для перерисовки карты
        customMapView.loadMagiestrals(magiestralsJson);
        customMapView.loadMarkers(markersJson);

        // Принудительно перерисуйте карту
        customMapView.invalidate(); // Перерисовка
    }

    // Метод для загрузки JSON из локального файла
    private String loadJsonFromFile(String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            Log.e("JsonFileUpdater", "Ошибка при загрузке файла: " + fileName, e);
            return null;
        }
    }
}