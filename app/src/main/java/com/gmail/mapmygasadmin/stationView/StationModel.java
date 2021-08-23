package com.gmail.mapmygasadmin.stationView;

public class StationModel {
    private String station_id;
    private String station_name;
    private String station_manager_name;
    private String station_manager_email;
    private String joined_at;

    private double latitude;
    private double longitude;

    public StationModel() {}
    public StationModel(String station_id, String station_name, String station_manager_name, String station_manager_email, String joined_at, double latitude, double longitude) {
        this.station_id = station_id;
        this.station_name = station_name;
        this.station_manager_name = station_manager_name;
        this.station_manager_email = station_manager_email;
        this.joined_at = joined_at;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public String getStation_manager_name() {
        return station_manager_name;
    }

    public void setStation_manager_name(String station_manager_name) {
        this.station_manager_name = station_manager_name;
    }

    public String getStation_manager_email() {
        return station_manager_email;
    }

    public void setStation_manager_email(String station_manager_email) {
        this.station_manager_email = station_manager_email;
    }

    public String getJoined_at() {
        return joined_at;
    }

    public void setJoined_at(String joined_at) {
        this.joined_at = joined_at;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
