package com.example.barberapps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barberapps.GetPost.Barber;
import com.example.barberapps.GetPost.GetBarber;
import com.example.barberapps.GetPost.GetPesanan;
import com.example.barberapps.GetPost.Pesanan;
import com.example.barberapps.RecyclerView.RecyclerViewBarbershop;
import com.example.barberapps.Rest.ApiClient;
import com.example.barberapps.Rest.ApiInterface;
import com.google.android.gms.common.ConnectionResult;
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

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    String nama;

    TextView tv_nama, tvBarbershopTerdekat, tvBarbershop, tv_status;
    CardView cari, profile;
    RelativeLayout rlAwal, rlMenunggu;

    SessionUser session;
    ApiInterface mApiInterface;

    //isinya dialog
    AlertDialog dialog;
    LayoutInflater inflater;
    View dialogView;

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
        setContentView(R.layout.activity_main);

        tv_nama                 = findViewById(R.id.tv_nama);
        tv_status               = findViewById(R.id.tv_status);
        tvBarbershopTerdekat    = findViewById(R.id.tv_lokasiTerdekat); //di rl awal
        tvBarbershop            = findViewById(R.id.tv_customer); //di rl menunggu
        cari                = findViewById(R.id.cv_cari);
        profile             = findViewById(R.id.cv_profile);
        rlAwal              = findViewById(R.id.rl_awal);
        rlMenunggu          = findViewById(R.id.rl_menunggu);
        session             = new SessionUser(getApplicationContext());
        mApiInterface       = ApiClient.getClient().create(ApiInterface.class);

        HashMap<String, String> user = session.getUserDetails();
        nama    = user.get(SessionUser.KEY_NAMA);

        tv_nama.setText(nama);

        tv_nama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUri = "http://maps.google.com/maps?q=loc:" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + " (Barberhopnya ini)";
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });

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

        if(!(session.isLoggedIn())){
            session.checkLogin();
            finish();
        } else {
            cekPesananProses();
            cekPesananTunggu();
//            tampilDataUser();
        }

//        gpsActivation();
        checkLocationPermission();
        getLatLng();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Profile.class));
            }
        });
        tv_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DaftarBarbershop.class);
                intent.putExtra("lat", String.valueOf(currentLocation.getLatitude()));
                intent.putExtra("lng", String.valueOf(currentLocation.getLongitude()));
                startActivity(intent);
            }
        });
    }

    private void barberTerdekat() {
        Call<GetBarber> userCall = mApiInterface.getBarberTerdekat(String.valueOf(currentLocation.getLatitude()), String.valueOf(currentLocation.getLongitude()));
        userCall.enqueue(new Callback<GetBarber>() {
            @Override
            public void onResponse(Call<GetBarber> call, Response<GetBarber> response) {
                List<Barber> barberList = response.body().getResult();
                final String namaBarber = barberList.get(0).getNama();
                tvBarbershopTerdekat.setText(namaBarber);
                tvBarbershopTerdekat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Kepster.class);
                        intent.putExtra("barbershop", namaBarber);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<GetBarber> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Kesalahan dalam mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cekPesananProses() {
        final Call<GetPesanan> pesananCall = mApiInterface.getCekPesanan("customer", nama, "diproses");
        pesananCall.enqueue(new Callback<GetPesanan>() {
            @Override
            public void onResponse(Call<GetPesanan> call, Response<GetPesanan> response) {
                List<Pesanan> pesananList = response.body().getResult();
                GetPesanan responPesanan = response.body();

                try {
                    rlAwal.setVisibility(View.VISIBLE);
                    rlMenunggu.setVisibility(View.GONE);

                    String tanggal  = pesananList.get(0).getTanggal();
                    String waktu    = pesananList.get(0).getWaktu();
                    String jenisLayanan     = pesananList.get(0).getJenisPesanan();
                    final String barbershop = pesananList.get(0).getBarbershop();
                    final String lat        = pesananList.get(0).getLat();
                    final String lng        = pesananList.get(0).getLng();
                    final String id         = pesananList.get(0).getId();

                    //dialog pesanan diproses
                    dialog      = new AlertDialog.Builder(MainActivity.this).create();
                    inflater    = getLayoutInflater();
                    //dialogView  = inflater.inflate(R.layout.dialog_pilih_bencana,null);
                    dialogView  = inflater.inflate(R.layout.dialog_inprogress,null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    TextView tvTanggal1  = dialogView.findViewById(R.id.tv_tanggal1);
                    TextView tvWaktu1    = dialogView.findViewById(R.id.tv_waktu1);
                    TextView tvBarber1   = dialogView.findViewById(R.id.tv_barbershop1);
                    TextView tvTanggal  = dialogView.findViewById(R.id.tv_tanggal);
                    TextView tvWaktu    = dialogView.findViewById(R.id.tv_waktu);
                    TextView tvBarber   = dialogView.findViewById(R.id.tv_barbershop);
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
                    tvBarber.setText(barbershop);

                    tvTanggal1.setText(tanggal);
                    tvWaktu1.setText(waktu);
                    tvBarber1.setText(barbershop);

                    MapView mMapView = (MapView) dialogView.findViewById(R.id.map);
                    MapsInitializer.initialize(MainActivity.this);

                    mMapView.onCreate(dialog.onSaveInstanceState());
                    mMapView.onResume();

                    mMapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(final GoogleMap googleMap) {
                            LatLng posisiabsen = new LatLng(Double.valueOf(lat), Double.valueOf(lng)); ////your lat lng
                            googleMap.addMarker(new MarkerOptions().position(posisiabsen).title(barbershop));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                            googleMap.getUiSettings().setZoomControlsEnabled(true);
                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                        }
                    });

                    tekanMaps.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String strUri = "http://maps.google.com/maps?q=loc:" + Double.valueOf(lat) + "," + Double.valueOf(lng) + " (" + barbershop + ")";
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            startActivity(intent);
                        }
                    });

                    dialog.show();

                } catch (IndexOutOfBoundsException e){
                    //tidak ada pesanan
                    rlAwal.setVisibility(View.VISIBLE);
                    rlMenunggu.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<GetPesanan> call, Throwable t) {

            }
        });
    }

    private void cekPesananTunggu() {
        final Call<GetPesanan> pesananCall = mApiInterface.getCekPesanan("customer", nama, "menunggu");
        pesananCall.enqueue(new Callback<GetPesanan>() {
            @Override
            public void onResponse(Call<GetPesanan> call, Response<GetPesanan> response) {
                List<Pesanan> pesananList = response.body().getResult();
                GetPesanan responPesanan = response.body();

                try {
                    //customer ada pesanan - menunggu
                    rlAwal.setVisibility(View.GONE);
                    rlMenunggu.setVisibility(View.VISIBLE);

                    String barbershop = pesananList.get(0).getBarbershop();
                    tvBarbershop.setText(barbershop);

                    tv_status.setText("Menunggu konfirmasi pesanan ...");
                    tv_status.setEnabled(false);
                } catch (IndexOutOfBoundsException e){
                    //tidak ada pesanan
                    rlAwal.setVisibility(View.VISIBLE);
                    rlMenunggu.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<GetPesanan> call, Throwable t) {

            }
        });
    }

    private void getLatLng() {
        gps();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        fetchLocation();

        latitude = 0;
        longitude = 0;
    }

    private void gps() {
        LocationManager lm = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
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
                    barberTerdekat();
//                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

//                    currentLocation.getLatitude()
//                    currentLocation.getLongitude()

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

    public void gpsActivation() {
        LocationManager lm = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage("GPS tidak aktif");
            dialog.setPositiveButton("Aktifkan", new DialogInterface.OnClickListener() {
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}