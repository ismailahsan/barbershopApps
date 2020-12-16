package com.example.barberapps.GetPost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("alamat")
    @Expose
    private String alamat;
    @SerializedName("hp")
    @Expose
    private String hp;
    @SerializedName("koordinat")
    @Expose
    private String koordinat;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("jenisUser")
    @Expose
    private String jenisUser;
    @SerializedName("barbershop")
    @Expose
    private String barbershop;
    @SerializedName("namaBarber")
    @Expose
    private String namaBarber;

    public String getNamaBarber() {
        return namaBarber;
    }

    public void setNamaBarber(String namaBarber) {
        this.namaBarber = namaBarber;
    }

    public String getBarbershop() {
        return barbershop;
    }

    public void setBarbershop(String barbershop) {
        this.barbershop = barbershop;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp;
    }

    public String getKoordinat() {
        return koordinat;
    }

    public void setKoordinat(String koordinat) {
        this.koordinat = koordinat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJenisUser() {
        return jenisUser;
    }

    public void setJenisUser(String jenisUser) {
        this.jenisUser = jenisUser;
    }
}
