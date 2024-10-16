package com.example.galacticmape;

public class MarkerMilkyWay {
    public int x, y;
    public String title;
    public String markerType;
    public String owner;
    public String economy;
    public String corporationInfluence;
    public boolean isCapital;

    public MarkerMilkyWay(int x, int y, String title, String markerType, String owner, String economy, String corporationInfluence, boolean isCapital) {
        this.x = x;
        this.y = y;
        this.title = title;
        this.markerType = markerType;
        this.owner = owner;
        this.economy = economy;
        this.corporationInfluence = corporationInfluence;
        this.isCapital = isCapital;
    }
}