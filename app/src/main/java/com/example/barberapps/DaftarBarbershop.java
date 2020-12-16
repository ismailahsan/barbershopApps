package com.example.barberapps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.barberapps.Fragment.Btmsheet_pilihLayanan;
import com.example.barberapps.GetPost.Barber;
import com.example.barberapps.GetPost.GetBarber;
import com.example.barberapps.GetPost.GetUser;
import com.example.barberapps.GetPost.User;
import com.example.barberapps.RecyclerView.RecyclerViewBarbershop;
import com.example.barberapps.RecyclerView.RecyclerViewKepster;
import com.example.barberapps.Rest.ApiClient;
import com.example.barberapps.Rest.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DaftarBarbershop extends AppCompatActivity {

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    SwipeRefreshLayout sr;
    RecyclerView rv;

    ProgressDialog Proses;
    ApiInterface mApiInterface;
    SessionUser session;

    String getLat, getLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_barbershop);

        mApiInterface   = ApiClient.getClient().create(ApiInterface.class);
        sr              = findViewById(R.id.sr_barbershop);
        rv              = findViewById(R.id.rv_barbershop);
        session         = new SessionUser(getApplicationContext());

        Intent intent = getIntent();
        getLat  = intent.getStringExtra("lat");
        getLng  = intent.getStringExtra("lng");

        layoutManager   = new LinearLayoutManager(DaftarBarbershop.this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);

        tampilBarbershop();
    }

    private void tampilBarbershop() {
        Call<GetBarber> userCall = mApiInterface.getBarberTerdekat(getLat, getLng);
        userCall.enqueue(new Callback<GetBarber>() {
            @Override
            public void onResponse(Call<GetBarber> call, Response<GetBarber> response) {
                try {
                    List<Barber> GetBarber =response.body().getResult();

                    adapter = new RecyclerViewBarbershop(DaftarBarbershop.this, GetBarber);
                    rv.setAdapter(adapter);
                    sr.setEnabled(false);
                } catch (NullPointerException e){
                    sr.setEnabled(false);
                    Toast.makeText(DaftarBarbershop.this, "Data kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetBarber> call, Throwable t) {
                sr.setEnabled(false);
                Toast.makeText(DaftarBarbershop.this, "Kesalahan dalam mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}