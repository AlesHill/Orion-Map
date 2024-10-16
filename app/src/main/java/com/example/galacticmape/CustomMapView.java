package com.example.galacticmape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;

public class CustomMapView extends SubsamplingScaleImageView {

    private JSONObject markersData;
    private Paint paint;
    private JSONObject magiestralsData;


    public CustomMapView(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Логирование вызова onDraw()
        //Log.d("CustomMapView", "onDraw() called");

        if (magiestralsData != null) {
            drawMagiestrals(canvas);
        } else {
            Log.d("CustomMapView", "No markers data available.");
        }

        if (markersData != null) {
            drawMarkers(canvas);
        } else {
            Log.d("CustomMapView", "No markers data available.");
        }
    }

    // Метод для очистки карты
    public void clear() {
        magiestralsData = null;
        markersData = null;
        invalidate(); // Принудительная перерисовка
    }

    public void setMagiestralsData(JSONObject magiestralsData) {
        this.magiestralsData = magiestralsData;
    }

    public void setMarkersData(JSONObject markersData) {
        this.markersData = markersData;
    }

    // Метод для загрузки магистралей
    public void loadMagiestrals(String magiestralsJson) {
        try {
            magiestralsData = new JSONObject(magiestralsJson);
            invalidate(); // Принудительно перерисовать карту
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Метод для загрузки маркеров
    public void loadMarkers(String markersJson) {
        try {
            markersData = new JSONObject(markersJson);
            invalidate(); // Принудительно перерисовать карту
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void drawMagiestrals(Canvas canvas) {
        try {

            Iterator<String> keys = magiestralsData.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject magiestral = magiestralsData.getJSONObject(key);

                // Получаем координаты начала и конца
                String startMarkerId = magiestral.getString("start_marker_id");
                String endMarkerId = magiestral.getString("end_marker_id");

                // Получаем координаты маркеров
                String[] startCoords = startMarkerId.replace("(", "").replace(")", "").split(", ");
                float startX = Float.parseFloat(startCoords[0]);
                float startY = Float.parseFloat(startCoords[1]);

                String[] endCoords = endMarkerId.replace("(", "").replace(")", "").split(", ");
                float endX = Float.parseFloat(endCoords[0]);
                float endY = Float.parseFloat(endCoords[1]);

                // Преобразуем координаты в экранные
                PointF startPosition = sourceToViewCoord(startX, startY);
                PointF endPosition = sourceToViewCoord(endX, endY);

                // Добавляем проверку на null
                if (startPosition == null || endPosition == null) {
                    Log.e("CustomMapView", "Invalid coordinates: start or end is null");
                    continue;
                }

                // Определяем цвет линии в зависимости от типа магистрали
                String magiestralType = magiestral.getString("magiestral_type");
                switch (magiestralType) {
                    case "Главная":
                        paint.setColor(Color.YELLOW);
                        break;
                    case "Второстепенная":
                        paint.setColor(Color.BLUE);
                        break;
                    case "Побочная":
                        paint.setColor(Color.GRAY);
                        break;
                    default:
                        paint.setColor(Color.WHITE);
                        break;
                }

                paint.setStrokeWidth(3 * getScale());  // Настраиваем ширину линии в зависимости от масштаба
                paint.setStyle(Paint.Style.STROKE);

                // Рисуем линию между стартовой и конечной точками
                canvas.drawLine(startPosition.x, startPosition.y, endPosition.x, endPosition.y, paint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawMarkers(Canvas canvas) {
        try {
            Iterator<String> keys = markersData.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject marker = markersData.getJSONObject(key);

                // Удаляем круглые скобки и разделяем координаты
                String[] coords = key.replace("(", "").replace(")", "").split(", ");
                float x = Float.parseFloat(coords[0]);
                float y = Float.parseFloat(coords[1]);

                // Преобразуем координаты изображения в экранные координаты
                PointF markerPosition = sourceToViewCoord(x, y);

                // Добавляем проверку на null
                if (markerPosition == null) {
                    Log.e("CustomMapView", "Invalid marker coordinates: null position");
                    continue;
                }

                // Получаем параметры метки
                String title = marker.getString("title");
                String markerType = marker.getString("marker_type");
                String owner = marker.getString("owner");
                String economy = marker.getString("economy");
                String corporation = marker.getString("corporation_influence");
                boolean isCapital = marker.getBoolean("is_capital");

                // Определяем размеры метки
                int markerSize = 2;  // По умолчанию
                switch (markerType) {
                    case "Первичная":
                        markerSize = 6;
                        break;
                    case "Вторичная":
                        markerSize = 4;
                        break;
                    case "Третичная":
                        markerSize = 2;
                        break;
                }

                // 1. Рисуем заливку метки (владелец)
                switch (owner) {
                    case "Орион":
                        paint.setColor(Color.BLUE);
                        break;
                    case "Империя":
                        paint.setColor(Color.rgb(255, 165, 0));  // Orange
                        break;
                    case "Сайдония":
                        paint.setColor(Color.GREEN);
                        break;
                    case "Содружество Звёзд":
                        paint.setColor(Color.RED);
                        break;
                    case "Свободный Эйдем":
                        paint.setColor(Color.YELLOW);
                        break;
                    case "ССМП":
                        paint.setColor(Color.CYAN);
                        break;
                    case "Соц.Интерн":
                        paint.setColor(Color.MAGENTA);
                        break;
                    case "Тени":
                        paint.setColor(Color.BLACK);
                        break;
                    case "NeIRo Gestalt":
                        paint.setColor(Color.rgb(128, 0, 0));  // Claret
                        break;
                    case "Нейтралы":
                        paint.setColor(Color.GRAY);
                        break;
                    case "Отсутствует":
                        paint.setColor(Color.WHITE);
                        break;
                    default:
                        paint.setColor(Color.WHITE);
                        break;
                }
                paint.setStyle(Paint.Style.FILL);  // Заливка
                canvas.drawCircle(markerPosition.x, markerPosition.y, markerSize * getScale(), paint);

                // 2. Рисуем первую обводку по экономике
                switch (economy) {
                    case "Пищевая":
                        paint.setColor(Color.GREEN);
                        break;
                    case "Ресурсная":
                        paint.setColor(Color.GRAY);
                        break;
                    case "Завод":
                        paint.setColor(Color.rgb(255, 140, 0));  // Dark Orange
                        break;
                    case "Экуменополис":
                        paint.setColor(Color.YELLOW);
                        break;
                    default:
                        paint.setColor(Color.WHITE);
                        break;
                }
                paint.setStyle(Paint.Style.STROKE);  // Обводка
                paint.setStrokeWidth(2 * getScale());  // Тонкая обводка по экономике
                canvas.drawCircle(markerPosition.x, markerPosition.y, (markerSize + 3) * getScale(), paint);

                // 3. Рисуем вторую обводку по корпорации
                switch (corporation) {
                    case "Картана":
                        paint.setColor(Color.rgb(255, 140, 0));  // Dark Orange
                        break;
                    case "Гиалис":
                        paint.setColor(Color.rgb(242, 120, 185));
                        break;
                    case "Сегинус":
                        paint.setColor(Color.rgb(0, 0, 139));  // Dark Blue
                        break;
                    case "Доксфор":
                        paint.setColor(Color.rgb(0, 139, 139));  // Dark Cyan
                        break;
                    case "Великий Легион":
                        paint.setColor(Color.rgb(0, 100, 0));  // Dark Green
                        break;
                    case "Багровый Закат":
                        paint.setColor(Color.rgb(139, 0, 0));  // Dark Red
                        break;
                    default:
                        paint.setColor(Color.WHITE);
                        break;
                }
                paint.setStrokeWidth(2 * getScale());  // Сделаем обводку тоньше
                canvas.drawCircle(markerPosition.x, markerPosition.y, (markerSize + 5) * getScale(), paint);


                // 4. Рисуем текст (название метки)
                paint.setColor(Color.WHITE);  // Цвет текста
                paint.setStyle(Paint.Style.FILL);  // Устанавливаем заливку для текста
                paint.setFakeBoldText(false);  // Отключаем жирное начертание
                paint.setStrokeWidth(1f);  // Ширина обводки текста
                paint.setSubpixelText(true);  // Включаем субпиксельный рендеринг
                paint.setHinting(Paint.HINTING_ON);  // Оптимизация рендеринга
                paint.setLetterSpacing(0.1f);  // Межбуквенный интервал
                paint.setTextAlign(Paint.Align.CENTER);  // Выравнивание по центру

                // Загрузка пользовательского шрифта
                Typeface customTypeface = ResourcesCompat.getFont(getContext(), R.font.xolonium);
                if (isCapital) {
                    paint.setTypeface(Typeface.create(customTypeface, Typeface.ITALIC));  // Курсив для столицы
                } else {
                    paint.setTypeface(customTypeface);  // Обычный текст
                }

                paint.setTextSize(9 * getScale());  // Размер текста
                canvas.drawText(title, markerPosition.x, markerPosition.y + markerSize + 20 * getScale(), paint);  // Рисуем текст
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}