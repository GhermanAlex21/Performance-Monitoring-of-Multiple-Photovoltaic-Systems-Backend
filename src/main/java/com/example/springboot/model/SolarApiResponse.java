package com.example.springboot.model;

import java.util.List;

public class SolarApiResponse {
    private List<List<Object>> data;
    private List<String> meta;

    // Constructori
    public SolarApiResponse() {
    }

    public SolarApiResponse(List<List<Object>> data, List<String> meta) {
        this.data = data;
        this.meta = meta;
    }

    // Getteri și setteri
    public List<List<Object>> getData() {
        return data;
    }

    public void setData(List<List<Object>> data) {
        this.data = data;
    }

    public List<String> getMeta() {
        return meta;
    }

    public void setMeta(List<String> meta) {
        this.meta = meta;
    }
}