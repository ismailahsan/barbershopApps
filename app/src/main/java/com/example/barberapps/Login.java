package com.example.barberapps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barberapps.Fragment.Btmsheet_register;
import com.example.barberapps.GetPost.GetUser;
import com.example.barberapps.GetPost.User;
import com.example.barberapps.Rest.ApiClient;
import com.example.barberapps.Rest.ApiInterface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    CardView cvLogin;
    TextView signIn, daftarBaru;
    EditText email, password;
    Spinner jenisUser;
    String getEmail, getPass, getJenisUser;

    ProgressDialog Proses;
    SessionUser session;
    private ApiInterface mApiInterface;

    //loc
    GoogleApiClient mGoogleApiClient;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int PERMISSION_REQUEST_STORAGE = 2;
    private static final int REQUEST_CODE = 99;
    private Marker mCurrLocationMarker;
    //To store longitude and latitude from map
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cvLogin     = findViewById(R.id.cv_login);
        signIn      = findViewById(R.id.tv_signIn);
        email       = findViewById(R.id.et_email);
        password    = findViewById(R.id.et_pass);
        jenisUser   = findViewById(R.id.sp_jenisUser);
        daftarBaru  = findViewById(R.id.tv_register);
        mApiInterface   = ApiClient.getClient().create(ApiInterface.class);
        session         = new SessionUser(getApplicationContext());

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

        cvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(Login.this, email.getText(), Toast.LENGTH_SHORT).show();

                getEmail        = email.getText().toString();
                getPass         = password.getText().toString();
                getJenisUser    = jenisUser.getSelectedItem().toString();

//                Toast.makeText(Login.this, getJenisUser, Toast.LENGTH_SHORT).show();

                if (getEmail.equals("") || getPass.equals("")){
                    Toast.makeText(Login.this, "Lengkapi data anda !", Toast.LENGTH_SHORT).show();
                } else {
                    Proses = ProgressDialog.show(Login.this, null, "Memproses...", true, false);
                    login(getEmail, getPass, getJenisUser);
                }
            }
        });

        daftarBaru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Btmsheet_register btmsheet_register =Btmsheet_register.newInstance();
                btmsheet_register.show(getSupportFragmentManager(), Btmsheet_register.TAG);
            }
        });

        checkLocationPermission();
        getLatLng();

    }

    private void login(String getEmail, String getPass, String getJenisUser) {

        final Call<GetUser> userCall = mApiInterface.postLogin(getEmail, getPass, getJenisUser);
        userCall.enqueue(new Callback<GetUser>() {
            @Override
            public void onResponse(Call<GetUser> call, Response<GetUser> response) {
                List<User> userList = response.body().getResult();
                GetUser responseUser = response.body();

                try {
                    String getId        = userList.get(0).getId();
                    String email        = userList.get(0).getEmail();
                    String alamat       = userList.get(0).getAlamat();
                    String hp           = userList.get(0).getHp();
                    String pass         = userList.get(0).getPassword();
                    String jenisUser    = userList.get(0).getJenisUser();

                    if (jenisUser==null){
                        String namaBarber   = userList.get(0).getNamaBarber();

                        Proses.dismiss();
                        session.createLoginSession(getId, namaBarber, email, hp, alamat, pass, "", "", "","");
                        finish();
                        startActivity(new Intent(Login.this, MainActivityBarbershop.class));
                        Toast.makeText(Login.this, "Selamat Datang "+namaBarber, Toast.LENGTH_SHORT).show();
                    } else if (jenisUser.equals("Kepster")){
                        String barbershop   = userList.get(0).getBarbershop();
                        String nama         = userList.get(0).getNama();

                        Proses.dismiss();
                        session.createLoginSession(getId, nama, email, hp, alamat, pass, "", jenisUser, barbershop,"");
                        finish();
                        startActivity(new Intent(Login.this, MainActivityKepster.class));
                        Toast.makeText(Login.this, "Selamat Datang "+nama, Toast.LENGTH_SHORT).show();
                    } else if (jenisUser.equals("Customer")){
                        String nama         = userList.get(0).getNama();

                        Proses.dismiss();
                        session.createLoginSession(getId, nama, email, hp, alamat, pass, "", jenisUser, "","");
                        finish();
                        startActivity(new Intent(Login.this, MainActivity.class));
                        Toast.makeText(Login.this, "Selamat Datang "+nama, Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e){
                    Proses.dismiss();
                    Toast.makeText(Login.this, "Anda belum terdaftar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetUser> call, Throwable t) {
                Proses.dismiss();
                Toast.makeText(Login.this, "Gagal mengirim data", Toast.LENGTH_SHORT).show();
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

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
//                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

//                    currentLocation.getLatitude()
//                    currentLocation.getLongitude()

                }
            }
        });
    }

    private void getLatLng() {
        gps();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Login.this);
        fetchLocation();

        latitude = 0;
        longitude = 0;
    }

    private void gps() {
        LocationManager lm = (LocationManager) Login.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this);
            dialog.setMessage("GPS tidak aktif");
            dialog.setPositiveButton("Aktifkan GPS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}