package com.example.barberapps.GetPost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetBarber {

    private boolean success;

    @SerializedName("result")
    @Expose
    private List<Barber> result;

    public List<Barber> getResult() {
        return result;
    }

    public void setResult(List<Barber> result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
