package com.example.barberapps;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.barberapps.GetPost.Barber;
import com.example.barberapps.GetPost.GetBarber;
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

public class WaitingList extends AppCompatActivity {

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    SwipeRefreshLayout sr;
    RecyclerView rv;

    ProgressDialog Proses;
    ApiInterface mApiInterface;
    SessionUser session;

    String kepster, jenisUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_list);

        mApiInterface   = ApiClient.getClient().create(ApiInterface.class);
        sr              = findViewById(R.id.sr_waitingList);
        rv              = findViewById(R.id.rv_waitingList);
        session         = new SessionUser(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();
        kepster = user.get(SessionUser.KEY_NAMA);
        jenisUser = user.get(SessionUser.KEY_JENISUSER);

        layoutManager   = new LinearLayoutManager(WaitingList.this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);

        tampilData();
    }

    private void tampilData() {
        Call<GetPesanan> userCall = mApiInterface.getCekPesanan("kepster", kepster, "menunggu");
        userCall.enqueue(new Callback<GetPesanan>() {
            @Override
            public void onResponse(Call<GetPesanan> call, Response<GetPesanan> response) {
                try {
                    List<Pesanan> GetPesanan =response.body().getResult();

                    adapter = new RecyclerViewPesanan(WaitingList.this, GetPesanan);
                    rv.setAdapter(adapter);
                    sr.setEnabled(false);
                    } catch (NullPointerException e){
                        sr.setEnabled(false);
                        Toast.makeText(WaitingList.this, "Tidak ada pesanan", Toast.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onFailure(Call<GetPesanan> call, Throwable t) {
                sr.setEnabled(false);
                Toast.makeText(WaitingList.this, "Kesalahan dalam mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(WaitingList.this, MainActivityKepster.class));
    }
}