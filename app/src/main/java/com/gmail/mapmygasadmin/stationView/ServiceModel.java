package com.gmail.mapmygasadmin.stationView;

public class ServiceModel {

    private String service_image;
    private String service_title;

    public ServiceModel() {}
    public ServiceModel(String service_image, String service_title) {
        this.service_image = service_image;
        this.service_title = service_title;
    }

    public String getService_image() {
        return service_image;
    }

    public void setService_image(String service_image) {
        this.service_image = service_image;
    }

    public String getService_title() {
        return service_title;
    }

    public void setService_title(String service_title) {
        this.service_title = service_title;
    }
}
