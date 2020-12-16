package com.example.barberapps;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barberapps.GetPost.Barber;
import com.example.barberapps.GetPost.GetPesanan;
import com.example.barberapps.GetPost.Pesanan;
import com.example.barberapps.RecyclerView.RecyclerViewBarbershop;
import com.example.barberapps.RecyclerView.RecyclerViewPesanan;
import com.example.barberapps.Rest.ApiClient;
import com.example.barberapps.Rest.ApiInterface;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDone extends AppCompatActivity {

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    SwipeRefreshLayout sr;
    RecyclerView rv;

    ProgressDialog Proses;
    ApiInterface mApiInterface;
    SessionUser session;

    String getNama, getJenis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_done);

        mApiInterface   = ApiClient.getClient().create(ApiInterface.class);
        sr              = findViewById(R.id.sr_done);
        rv              = findViewById(R.id.rv_done);
        session         = new SessionUser(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();
        getNama     = user.get(SessionUser.KEY_NAMA);
        getJenis    = user.get(SessionUser.KEY_JENISUSER).toLowerCase();

        layoutManager   = new LinearLayoutManager(OrderDone.this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);

        tampilOrder(getNama);
    }

    private void tampilOrder(String getNama) {
        Call<GetPesanan> pesananCall = mApiInterface.getCekPesanan(getJenis, getNama, "selesai");
        pesananCall.enqueue(new Callback<GetPesanan>() {
            @Override
            public void onResponse(Call<GetPesanan> call, Response<GetPesanan> response) {
                try {
                    List<Pesanan> GetPesanan =response.body().getResult();

                    adapter = new RecyclerViewPesanan(OrderDone.this, GetPesanan);
                    rv.setAdapter(adapter);
                    sr.setEnabled(false);
                } catch (NullPointerException e){
                    sr.setEnabled(false);
                    Toast.makeText(OrderDone.this, "Data kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetPesanan> call, Throwable t) {
                Toast.makeText(OrderDone.this, "Gagal menagmbil data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}