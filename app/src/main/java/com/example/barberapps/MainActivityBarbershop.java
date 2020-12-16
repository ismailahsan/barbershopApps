package com.example.barberapps;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.barberapps.Rest.ApiClient;
import com.example.barberapps.Rest.ApiInterface;

import java.util.HashMap;

public class MainActivityBarbershop extends AppCompatActivity {

    ImageView kepster, orderDone;
    TextView nama;
    String barbershop;
    CardView profile;

    //isinya dialog
    AlertDialog dialog;
    LayoutInflater inflater;
    View dialogView;

    ProgressDialog Proses;
    ApiInterface mApiInterface;
    SessionUser session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_barbershop);

        profile         = findViewById(R.id.cv_profile);
        kepster         = findViewById(R.id.img_kepster);
        orderDone       = findViewById(R.id.img_orderDone);
        nama            = findViewById(R.id.tv_nama);
        mApiInterface   = ApiClient.getClient().create(ApiInterface.class);
        session         = new SessionUser(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();
        barbershop  = user.get(SessionUser.KEY_NAMA);

        nama.setText(barbershop);

        nama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog      = new AlertDialog.Builder(MainActivityBarbershop.this).create();
                inflater    = getLayoutInflater();
                //dialogView  = inflater.inflate(R.layout.dialog_pilih_bencana,null);
                dialogView  = inflater.inflate(R.layout.dialog_inprogress_barber,null);
                dialog.setView(dialogView);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        kepster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityBarbershop.this, Kepster.class);
                intent.putExtra("barbershop", barbershop);
                startActivity(intent);
            }
        });

        orderDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivityBarbershop.this, OrderDone.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityBarbershop.this, ProfilBarbershop.class));
            }
        });
    }
}