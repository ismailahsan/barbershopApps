package com.example.barberapps.GetPost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetPesanan {
    private boolean success;

    @SerializedName("result")
    @Expose
    private List<Pesanan> result;

    public List<Pesanan> getResult() {
        return result;
    }

    public void setResult(List<Pesanan> result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
