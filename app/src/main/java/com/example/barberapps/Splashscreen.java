package com.example.barberapps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.HashMap;

public class Splashscreen extends AppCompatActivity {

    ImageView logo;
    SessionUser session;
    String getJenisUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        session         = new SessionUser(getApplicationContext());

        YoYo.with(Techniques.Flash)
                .duration(3100)
                .repeat(0)
                .playOn(findViewById(R.id.logo));

        Thread myThread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);

                    if(!(session.isLoggedIn())){
                        session.checkLogin();
                        finish();
                    } else {
                        HashMap<String, String> user = session.getUserDetails();
                        getJenisUser      = user.get(SessionUser.KEY_JENISUSER);

                        if (getJenisUser.equals("Customer")){
                            finish();
                            startActivity(new Intent(Splashscreen.this, MainActivity.class));
                        } else if (getJenisUser.equals("Kepster")){
                            finish();
                            startActivity(new Intent(Splashscreen.this, MainActivityKepster.class));
                        } else {
                            finish();
                            startActivity(new Intent(Splashscreen.this, MainActivityBarbershop.class));
                        }
                    }
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}