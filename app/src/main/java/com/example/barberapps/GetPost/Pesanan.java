package com.example.barberapps.GetPost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pesanan {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("customer")
    @Expose
    private String customer;
    @SerializedName("barbershop")
    @Expose
    private String barbershop;
    @SerializedName("kepster")
    @Expose
    private String kepster;
    @SerializedName("tanggal")
    @Expose
    private String tanggal;
    @SerializedName("waktu")
    @Expose
    private String waktu;
    @SerializedName("jenisPesanan")
    @Expose
    private String jenisPesanan;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("alamat")
    @Expose
    private String alamat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("lat")
    @Expose
    private String lat;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getBarbershop() {
        return barbershop;
    }

    public void setBarbershop(String barbershop) {
        this.barbershop = barbershop;
    }

    public String getKepster() {
        return kepster;
    }

    public void setKepster(String kepster) {
        this.kepster = kepster;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJenisPesanan() {
        return jenisPesanan;
    }

    public void setJenisPesanan(String jenisPesanan) {
        this.jenisPesanan = jenisPesanan;
    }

}
