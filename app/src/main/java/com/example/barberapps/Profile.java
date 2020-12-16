package com.example.barberapps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.barberapps.Rest.ApiClient;
import com.example.barberapps.Rest.ApiInterface;

import java.util.HashMap;

public class Profile extends AppCompatActivity {

    EditText namaBarber, email, password, hp, alamat;
    String getNamaBarber, getEmail, getPassword, getHp, getAlamat;
    CardView cvSignOut;

    ProgressDialog Proses;
    private ApiInterface mApiInterface;
    SessionUser session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        namaBarber      = findViewById(R.id.et_nama);
        email           = findViewById(R.id.et_email);
        password        = findViewById(R.id.et_pass);
        hp              = findViewById(R.id.et_noHp);
        alamat          = findViewById(R.id.et_alamat);
        cvSignOut       = findViewById(R.id.cv_logout);
        mApiInterface   = ApiClient.getClient().create(ApiInterface.class);
        session         = new SessionUser(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();
        getNamaBarber   = user.get(SessionUser.KEY_NAMA);
        getEmail        = user.get(SessionUser.KEY_EMAIL);
        getPassword     = user.get(SessionUser.KEY_PASSWORD);
        getHp           = user.get(SessionUser.KEY_HP);
        getAlamat       = user.get(SessionUser.KEY_ALAMAT);

        namaBarber.setText(getNamaBarber);
        email.setText(getEmail);
        password.setText(getPassword);
        hp.setText(getHp);
        alamat.setText(getAlamat);

        cvSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logoutUser();
                finish();
            }
        });
    }
}