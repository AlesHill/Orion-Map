package com.example.galacticmape;

public class DataUpdater {

    private static final String MARKERS_URL = "https://github.com/AlesHill/Orion-Map/blob/main/app/src/main/assets/markersMilkyWay.json";
    private static final String MAGISTRALS_URL = "https://github.com/AlesHill/Orion-Map/blob/main/app/src/main/assets/magiestralsMilkyWay.json";

    public static void updateData() {
        // Загрузка маркеров
        String markersData = JsonDownloader.downloadJson(MARKERS_URL);
        if (markersData != null) {
            // Обработка данных маркеров
            System.out.println("Markers loaded: " + markersData);
        }

        // Загрузка магистралей
        String magiestralsData = JsonDownloader.downloadJson(MAGISTRALS_URL);
        if (magiestralsData != null) {
            // Обработка данных магистралей
            System.out.println("Magistrals loaded: " + magiestralsData);
        }
    }
}