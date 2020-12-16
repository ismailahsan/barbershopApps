package com.example.barberapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.barberapps.GetPost.GetBarber;
import com.example.barberapps.GetPost.GetUser;
import com.example.barberapps.Rest.ApiClient;
import com.example.barberapps.Rest.ApiInterface;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterBarbershop extends AppCompatActivity implements OnMapReadyCallback {

    EditText namaBarber, email, password, hp, alamat, koordinat;
    String getNamaBarber, getEmail, getPassword, getHp, getAlamat, getKoordinat;
    CardView cvRegist;

    ProgressDialog Proses;
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
    MapView mapView;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        namaBarber = findViewById(R.id.et_nama);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_pass);
        hp = findViewById(R.id.et_noHp);
        alamat = findViewById(R.id.et_alamat);
        cvRegist = findViewById(R.id.cv_regist);
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        mapView = findViewById(R.id.map);

        getLatLng();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

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
                getNamaBarber = namaBarber.getText().toString();
                getEmail = email.getText().toString();
                getAlamat = alamat.getText().toString();
                getHp = hp.getText().toString();
                getPassword = password.getText().toString();

                if (getNamaBarber.equals("") || getEmail.equals("") || getHp.equals("") || getAlamat.equals("") || getPassword.equals("")) {
                    Toast.makeText(RegisterBarbershop.this, "Lengkapi data anda", Toast.LENGTH_SHORT).show();
                } else {
                    Proses = ProgressDialog.show(RegisterBarbershop.this, null, "Memproses...", true, false);

                    Call<GetUser> barberCall = mApiInterface.registerBarber(getNamaBarber, getEmail, getHp, getAlamat, getPassword, String.valueOf(currentLocation.getLatitude()), String.valueOf(currentLocation.getLongitude()));
                    barberCall.enqueue(new Callback<GetUser>() {
                        @Override
                        public void onResponse(Call<GetUser> call, Response<GetUser> response) {
                            GetUser responBarber = (GetUser) response.body();

                            if (responBarber.isSuccess()) {
                                Proses.dismiss();
                                Toast.makeText(RegisterBarbershop.this, "Berhasil membuat akun", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterBarbershop.this, Login.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Proses.dismiss();
                                Toast.makeText(RegisterBarbershop.this, "Jaringan Gagal", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<GetUser> call, Throwable t) {
                            Proses.dismiss();
                            Toast.makeText(RegisterBarbershop.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

    private void getLatLng() {
        gps();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(RegisterBarbershop.this);
        fetchLocation();

        latitude = 0;
        longitude = 0;
    }

    private void gps() {
        LocationManager lm = (LocationManager) RegisterBarbershop.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterBarbershop.this);
            dialog.setMessage("GPS tidak aktif");
            dialog.setPositiveButton("Aktifkan GPS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
        googleMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Lokasi barbershopmu"));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}