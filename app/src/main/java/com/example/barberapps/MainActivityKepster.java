package com.example.barberapps;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barberapps.GetPost.GetPesanan;
import com.example.barberapps.GetPost.Pesanan;
import com.example.barberapps.Rest.ApiClient;
import com.example.barberapps.Rest.ApiInterface;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityKepster extends AppCompatActivity {

    String nama;

    CardView profile;
    TextView tvNama;
    ImageView imgWaitingList, imgDone;

    ProgressDialog Proses;
    SessionUser session;
    ApiInterface mApiInterface;

    //isinya dialog
    AlertDialog dialog;
    LayoutInflater inflater;
    View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_kepster);

        profile         = findViewById(R.id.cv_profile);
        tvNama          = findViewById(R.id.tv_nama);
        imgWaitingList  = findViewById(R.id.img_waitingList);
        imgDone         = findViewById(R.id.img_orderDone);
        session         = new SessionUser(getApplicationContext());
        mApiInterface       = ApiClient.getClient().create(ApiInterface.class);

        HashMap<String, String> user = session.getUserDetails();
        nama    = user.get(SessionUser.KEY_NAMA);
        tvNama.setText(nama);

        cekPesanan(nama);

        imgWaitingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MainActivityKepster.this, WaitingList.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityKepster.this, ProfilKepster.class));
            }
        });

        imgDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivityKepster.this, OrderDone.class));
            }
        });
    }

    private void cekPesanan(String getNama) {
        Call<GetPesanan> pesananCall = mApiInterface.getCekPesanan("kepster", getNama, "diproses");
        pesananCall.enqueue(new Callback<GetPesanan>() {
            @Override
            public void onResponse(Call<GetPesanan> call, Response<GetPesanan> response) {
                List<Pesanan> pesananList = response.body().getResult();
                GetPesanan responPesanan = response.body();

                try {
                    String tanggal  = pesananList.get(0).getTanggal();
                    String waktu    = pesananList.get(0).getWaktu();
                    String alamat   = pesananList.get(0).getAlamat();
                    String jenisLayanan     = pesananList.get(0).getJenisPesanan();
                    final String customer   = pesananList.get(0).getCustomer();
                    final String lat        = pesananList.get(0).getLat();
                    final String lng        = pesananList.get(0).getLng();
                    final String id         = pesananList.get(0).getId();

                    dialog      = new AlertDialog.Builder(MainActivityKepster.this).create();
                    inflater    = getLayoutInflater();
                    dialogView  = inflater.inflate(R.layout.dialog_inprogress_barber,null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    TextView tvTanggal1  = dialogView.findViewById(R.id.tv_tanggal1);
                    TextView tvWaktu1    = dialogView.findViewById(R.id.tv_waktu1);
                    TextView tvCustomer1 = dialogView.findViewById(R.id.tv_customer1);
                    TextView tvTanggal  = dialogView.findViewById(R.id.tv_tanggal);
                    TextView tvWaktu    = dialogView.findViewById(R.id.tv_waktu);
                    TextView tvCustomer = dialogView.findViewById(R.id.tv_customer);
                    TextView tvAlamat   = dialogView.findViewById(R.id.tv_alamat);
                    TextView tekanMaps  = dialogView.findViewById(R.id.tv_tekanMaps);
                    CardView cvSelesai  = dialogView.findViewById(R.id.cv_selesai);
                    RelativeLayout diBarber = dialogView.findViewById(R.id.cukurDibarber);
                    RelativeLayout diRumah  = dialogView.findViewById(R.id.cukurDiruamh);

                    if (jenisLayanan.equals("barbershop")){
                        diBarber.setVisibility(View.VISIBLE);
                        diRumah.setVisibility(View.INVISIBLE);
                    } else if (jenisLayanan.equals("rumah")){
                        diBarber.setVisibility(View.INVISIBLE);
                        diRumah.setVisibility(View.VISIBLE);
                    }

                    tvTanggal.setText(tanggal);
                    tvWaktu.setText(waktu);
                    tvCustomer.setText(customer);

                    tvTanggal1.setText(tanggal);
                    tvWaktu1.setText(waktu);
                    tvCustomer1.setText(customer);
                    tvAlamat.setText(alamat);

                    MapView mMapView = (MapView) dialogView.findViewById(R.id.map);
                    MapsInitializer.initialize(MainActivityKepster.this);

                    mMapView.onCreate(dialog.onSaveInstanceState());
                    mMapView.onResume();

                    mMapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(final GoogleMap googleMap) {
                            LatLng posisiabsen = new LatLng(Double.valueOf(lat), Double.valueOf(lng)); ////your lat lng
                            googleMap.addMarker(new MarkerOptions().position(posisiabsen).title(customer));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                            googleMap.getUiSettings().setZoomControlsEnabled(true);
                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                        }
                    });

                    tekanMaps.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String strUri = "http://maps.google.com/maps?q=loc:" + Double.valueOf(lat) + "," + Double.valueOf(lng) + " (" + customer + ")";
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            startActivity(intent);
                        }
                    });

                    cvSelesai.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Proses = ProgressDialog.show(MainActivityKepster.this, null, "Memproses...", true, false);

                            Call<GetPesanan> pesananCall = mApiInterface.updatePesanan(id, "selesai");
                            pesananCall.enqueue(new Callback<GetPesanan>() {
                                @Override
                                public void onResponse(Call<GetPesanan> call, Response<GetPesanan> response) {
                                    GetPesanan getPesanan = response.body();

                                    if (getPesanan.isSuccess()){
                                        dialog.dismiss();
                                        Proses.dismiss();
                                        Toast.makeText(MainActivityKepster.this, "Pesanan telah selesai", Toast.LENGTH_SHORT).show();
                                    } else {
                                        dialog.dismiss();
                                        Proses.dismiss();
                                        Toast.makeText(MainActivityKepster.this, "Gagal memproses pesanan", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onFailure(Call<GetPesanan> call, Throwable t) {
                                    dialog.dismiss();
                                    Proses.dismiss();
                                    Toast.makeText(MainActivityKepster.this, "Jaringan bermasalah", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    dialog.show();

                } catch (IndexOutOfBoundsException e){

                }
            }

            @Override
            public void onFailure(Call<GetPesanan> call, Throwable t) {
                Toast.makeText(MainActivityKepster.this, "Gagal menagmbil data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}