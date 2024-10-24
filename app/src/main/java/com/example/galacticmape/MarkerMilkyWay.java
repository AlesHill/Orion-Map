package com.example.galacticmape;

import android.os.Parcel;
import android.os.Parcelable;

public class MarkerMilkyWay implements Parcelable {
    private String title;
    private String system;
    private String temperatureClass;
    private String stateClass;
    private String planetClass;
    private String economy;
    private String owner;
    private String corporationInfluence;
    private String markerType;
    private boolean isCapital;

    // Конструктор
    public MarkerMilkyWay(String title, String system, String temperatureClass, String stateClass, String planetClass, String economy, String owner, String corporationInfluence, String markerType, boolean isCapital) {
        this.title = title;
        this.system = system;
        this.temperatureClass = temperatureClass;
        this.stateClass = stateClass;
        this.planetClass = planetClass;
        this.economy = economy;
        this.owner = owner;
        this.corporationInfluence = corporationInfluence;
        this.markerType = markerType;
        this.isCapital = isCapital;
    }

    // Parcelable implementation
    protected MarkerMilkyWay(Parcel in) {
        title = in.readString();
        system = in.readString();
        temperatureClass = in.readString();
        stateClass = in.readString();
        planetClass = in.readString();
        economy = in.readString();
        owner = in.readString();
        corporationInfluence = in.readString();
        markerType = in.readString();
        isCapital = in.readByte() != 0;
    }

    public static final Creator<MarkerMilkyWay> CREATOR = new Creator<MarkerMilkyWay>() {
        @Override
        public MarkerMilkyWay createFromParcel(Parcel in) {
            return new MarkerMilkyWay(in);
        }

        @Override
        public MarkerMilkyWay[] newArray(int size) {
            return new MarkerMilkyWay[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(system);
        dest.writeString(temperatureClass);
        dest.writeString(stateClass);
        dest.writeString(planetClass);
        dest.writeString(economy);
        dest.writeString(owner);
        dest.writeString(corporationInfluence);
        dest.writeString(markerType);
        dest.writeByte((byte) (isCapital ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Геттеры для всех полей
    public String getTitle() {
        return title;
    }

    public String getSystem() {
        return system;
    }

    public String getTemperatureClass() {
        return temperatureClass;
    }

    public String getStateClass() {
        return stateClass;
    }

    public String getPlanetClass() {
        return planetClass;
    }

    public String getEconomy() {
        return economy;
    }

    public String getOwner() {
        return owner;
    }

    public String getCorporationInfluence() {
        return corporationInfluence;
    }

    public String getMarkerType() {
        return markerType;
    }

    public boolean isCapital() {
        return isCapital;
    }
}