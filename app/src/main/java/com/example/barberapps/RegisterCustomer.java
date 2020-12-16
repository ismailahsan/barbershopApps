package com.example.barberapps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.barberapps.GetPost.GetUser;
import com.example.barberapps.Rest.ApiClient;
import com.example.barberapps.Rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterCustomer extends AppCompatActivity {

    EditText nama, email, password, hp, alamat;
    String getnama, getEmail, getPassword, getHp, getAlamat;
    CardView cvRegist;

    ProgressDialog Proses;
    private ApiInterface mApiInterface;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);

        nama      = findViewById(R.id.et_nama);
        email           = findViewById(R.id.et_email);
        password        = findViewById(R.id.et_pass);
        hp              = findViewById(R.id.et_noHp);
        alamat          = findViewById(R.id.et_alamat);
        cvRegist        = findViewById(R.id.cv_regist);
        mApiInterface   = ApiClient.getClient().create(ApiInterface.class);
        
        //remove status bar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        cvRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getnama   = nama.getText().toString();
                getEmail        = email.getText().toString();
                getAlamat       = alamat.getText().toString();
                getHp           = hp.getText().toString();
                getPassword     = password.getText().toString();

                if (getnama.equals("") || getEmail.equals("") || getHp.equals("") || getAlamat.equals("") || getPassword.equals("")){
                    Toast.makeText(RegisterCustomer.this, "Lengkapi data anda", Toast.LENGTH_SHORT).show();
                } else {
                    Proses = ProgressDialog.show(RegisterCustomer.this, null, "Memproses...", true, false);

                    Call<GetUser> barberCall = mApiInterface.registerCustomer(getnama, getEmail, getHp, getAlamat, getPassword, "Customer");
                    barberCall.enqueue(new Callback<GetUser>() {
                        @Override
                        public void onResponse(Call<GetUser> call, Response<GetUser> response) {
                            GetUser responBarber = (GetUser) response.body();

                            if (responBarber.isSuccess()){
                                Proses.dismiss();
                                Toast.makeText(RegisterCustomer.this, "Berhasil membuat akun", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterCustomer.this, Login.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Proses.dismiss();
                                Toast.makeText(RegisterCustomer.this, "Jaringan Gagal", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<GetUser> call, Throwable t) {
                            Proses.dismiss();
                            Toast.makeText(RegisterCustomer.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public static void setWindowFlag(AppCompatActivity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}