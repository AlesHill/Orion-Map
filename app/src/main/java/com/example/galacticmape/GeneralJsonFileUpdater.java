package com.example.galacticmape;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GeneralJsonFileUpdater {

    private static final String BASE_URL = "https://raw.githubusercontent.com/AlesHill/Orion-Map/refs/heads/main/app/src/main/assets/";
    private Context context;
    private OkHttpClient client;

    // Список файлов для загрузки
    private static final Map<String, String> filesToUpdate = new HashMap<>();

    static {
        filesToUpdate.put("corporationList.json", "corporationList.json");
        filesToUpdate.put("shipsFlagmanList.json", "shipsFlagmanList.json");
        filesToUpdate.put("shipsList.json", "shipsList.json");
        filesToUpdate.put("statesList.json", "statesList.json");
        filesToUpdate.put("timeShipsBuildMod.json", "timeShipsBuildMod.json");
        filesToUpdate.put("variebl.json", "variebl.json");
        filesToUpdate.put("markersMilkyWay.json", "markersMilkyWay.json");
        filesToUpdate.put("magiestralsMilkyWay.json", "magiestralsMilkyWay.json");
    }

    public GeneralJsonFileUpdater(Context context) {
        this.context = context;
        this.client = new OkHttpClient();
    }

    // Метод для обновления всех файлов
    public void updateAllFiles() {
        for (Map.Entry<String, String> entry : filesToUpdate.entrySet()) {
            String fileName = entry.getKey();
            String fileUrl = BASE_URL + fileName;
            updateFile(fileUrl, entry.getValue());
        }
    }

    // Метод для обновления отдельного файла
    private void updateFile(String url, String fileName) {
        new Thread(() -> {
            try {
                String jsonContent = downloadJsonFile(url);
                if (jsonContent != null) {
                    saveToFile(fileName, jsonContent);
                } else {
                    Log.e("GeneralJsonFileUpdater", "Не удалось загрузить файл: " + fileName);
                }
            } catch (IOException e) {
                Log.e("GeneralJsonFileUpdater", "Ошибка при загрузке файла: " + fileName, e);
            }
        }).start();
    }

    // Метод для загрузки JSON файла с сервера
    private String downloadJsonFile(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Ошибка при запросе: " + response);
            }
            return response.body().string();
        }
    }

    // Метод для сохранения содержимого JSON в файл во внутреннем хранилище
    private void saveToFile(String fileName, String jsonContent) {
        File directory = new File(context.getFilesDir(), "assets");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(jsonContent.getBytes());
            Log.d("GeneralJsonFileUpdater", "Файл сохранён: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e("GeneralJsonFileUpdater", "Ошибка при сохранении файла: " + fileName, e);
        }
    }
}