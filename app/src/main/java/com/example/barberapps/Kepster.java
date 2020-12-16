package com.example.barberapps;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barberapps.GetPost.GetUser;
import com.example.barberapps.GetPost.User;
import com.example.barberapps.RecyclerView.RecyclerViewKepster;
import com.example.barberapps.Rest.ApiClient;
import com.example.barberapps.Rest.ApiInterface;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Kepster extends AppCompatActivity {

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    SwipeRefreshLayout sr;
    RecyclerView rv;

    ProgressDialog Proses;
    ApiInterface mApiInterface;
    SessionUser session;

    CardView daftarkan;
    EditText nama, email, password, hp, alamat;
    TextView tvToolbar;
    String getNama, getEmail, getPassword, getHp, getAlamat;
    String barbershop, jenisUser;

    FloatingActionButton fabKepster;

    //isinya dialog
    AlertDialog dialog;
    LayoutInflater inflater;
    View dialogView;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kepster);

        mApiInterface   = ApiClient.getClient().create(ApiInterface.class);
        sr              = findViewById(R.id.sr_kepster);
        rv              = findViewById(R.id.rv_kepster);
        fabKepster      = findViewById(R.id.fab_kepster);
        tvToolbar       = findViewById(R.id.tv_toolbar);
        session         = new SessionUser(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();
        jenisUser = user.get(SessionUser.KEY_JENISUSER);

        Intent intent = getIntent();
        barbershop  = intent.getStringExtra("barbershop");

        tvToolbar.setText("Daftar kepster "+barbershop);

        layoutManager   = new LinearLayoutManager(Kepster.this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);

        tampilData();

        sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sr.setRefreshing(false);
                tampilData();
            }
        });

        if (jenisUser.equals("")){
            fabKepster.setVisibility(View.VISIBLE);
        } else {
            fabKepster.setVisibility(View.INVISIBLE);
        }

        fabKepster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog      = new AlertDialog.Builder(Kepster.this).create();
                inflater    = getLayoutInflater();
                //dialogView  = inflater.inflate(R.layout.dialog_pilih_bencana,null);
                dialogView  = inflater.inflate(R.layout.dialog_tambah_kepster,null);
                dialog.setView(dialogView);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                CardView daftar = dialogView.findViewById(R.id.cv_daftarkan);
                nama            = dialogView.findViewById(R.id.et_nama);
                email           = dialogView.findViewById(R.id.et_email);
                password        = dialogView.findViewById(R.id.et_pass);
                hp              = dialogView.findViewById(R.id.et_noHp);
                alamat          = dialogView.findViewById(R.id.et_alamat);

                daftar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getNama         = nama.getText().toString();
                        getEmail        = email.getText().toString();
                        getAlamat       = alamat.getText().toString();
                        getHp           = hp.getText().toString();
                        getPassword     = password.getText().toString();

                        if (getNama.equals("") || getEmail.equals("") || getHp.equals("") || getAlamat.equals("") || getPassword.equals("")){
                            Toast.makeText(Kepster.this, "Lengkapi datanya", Toast.LENGTH_SHORT).show();
                        } else {
                            Proses = ProgressDialog.show(Kepster.this, null, "Memproses...", true, false);

                            Call<GetUser> barberCall = mApiInterface.registerKepster(getNama, getEmail, getHp, getAlamat, getPassword, "Kepster", barbershop);
                            barberCall.enqueue(new Callback<GetUser>() {
                                @Override
                                public void onResponse(Call<GetUser> call, Response<GetUser> response) {
                                    GetUser responBarber = (GetUser) response.body();

                                    if (responBarber.isSuccess()){
                                        Proses.dismiss();
                                        Toast.makeText(Kepster.this, "Berhasil membuat akun kepster", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        finish();
                                    } else {
                                        Proses.dismiss();
                                        Toast.makeText(Kepster.this, "Jaringan Gagal", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<GetUser> call, Throwable t) {
                                    Proses.dismiss();
                                    Toast.makeText(Kepster.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

                dialog.show();
            }
        });
    }

    private void tampilData() {
        Call<GetUser> userCall = mApiInterface.getKepsterBarber(barbershop);
        userCall.enqueue(new Callback<GetUser>() {
            @Override
            public void onResponse(Call<GetUser> call, Response<GetUser> response) {
                try {
                    List<User> getUser =response.body().getResult();

                    adapter = new RecyclerViewKepster(Kepster.this, getUser);
                    rv.setAdapter(adapter);
                    sr.setEnabled(false);
                } catch (NullPointerException e){
                    sr.setEnabled(false);
                    Toast.makeText(Kepster.this, "Data kosong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetUser> call, Throwable t) {
                sr.setEnabled(false);
                Toast.makeText(Kepster.this, "Kesalahan dalam mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}