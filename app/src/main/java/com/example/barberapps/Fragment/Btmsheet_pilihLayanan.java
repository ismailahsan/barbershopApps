package com.example.barberapps.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.barberapps.GetPost.Barber;
import com.example.barberapps.GetPost.GetBarber;
import com.example.barberapps.GetPost.GetPesanan;
import com.example.barberapps.MainActivity;
import com.example.barberapps.R;
import com.example.barberapps.Rest.ApiClient;
import com.example.barberapps.Rest.ApiInterface;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Btmsheet_pilihLayanan extends BottomSheetDialogFragment {

    ImageView keBarber, potongDirumah;

    public static final String TAG = "TAG";

    String barbershop, kepster, customer;
    String tanggalnya, waktunya, latBarber, lngBarber;

    //isinya dialog
    AlertDialog dialog;
    LayoutInflater dialogInflater;
    View dialogView;

    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    EditText txt_tgl, txt_jam;
    Button btn_get_datetime;

    ProgressDialog Proses;
    private ApiInterface mApiInterface;

    //loc
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 99;

    private double longitude;
    private double latitude;

    private GoogleMap mMap;

    public static Btmsheet_pilihLayanan newInstance(){
        return new Btmsheet_pilihLayanan();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottomsheet_pilih_layanan, container, false);

        keBarber        = v.findViewById(R.id.img_keBarber);
        potongDirumah   = v.findViewById(R.id.img_potongDirumah);
        mApiInterface   = ApiClient.getClient().create(ApiInterface.class);

        //get poinnya hadiah
        Bundle bundle = this.getArguments();
        if (bundle != null){
            barbershop      = bundle.getString("barbershop");
            kepster         = bundle.getString("kepster");
            customer        = bundle.getString("customer");
        }

//        tikor barber
        getTikorBarber();

//        tikor customer
        getLatLng();

        keBarber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog              = new AlertDialog.Builder(getActivity()).create();
                dialogInflater      = getLayoutInflater();
                //dialogView  = inflater.inflate(R.layout.dialog_pilih_bencana,null);
                dialogView  = dialogInflater.inflate(R.layout.dialog_pesan,null);
                dialog.setView(dialogView);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                final TextView tanggal    = dialogView.findViewById(R.id.tv_tanggal);
                final TextView waktu      = dialogView.findViewById(R.id.tv_waktu);
                TextView pesan      = dialogView.findViewById(R.id.tv_pesan);
                TextView batal      = dialogView.findViewById(R.id.tv_batal);

                myCalendar = Calendar.getInstance();
                date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String myFormat = "dd MMM yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        tanggal.setText(sdf.format(myCalendar.getTime()));
                        tanggalnya = sdf.format(myCalendar.getTime());
                    }
                };

                tanggal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new DatePickerDialog(getActivity(), date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                waktu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String time;
                                if(selectedHour>=0 && selectedHour<12){
                                    time = selectedHour + " : " + selectedMinute + " AM";
                                } else {
                                    if(selectedHour == 12){
                                        time = selectedHour + " : " + selectedMinute + " PM";
                                    } else{
                                        selectedHour = selectedHour -12;
                                        time = selectedHour + " : " + selectedMinute + " PM";
                                    }
                                }

                                waktu.setText(time);
                                waktunya = time;
                            }
                        }, hour, minute, false);
                        mTimePicker.setTitle("Pilih waktu");
                        mTimePicker.show();
                    }
                });

                MapView mMapView = (MapView) dialogView.findViewById(R.id.map);
                MapsInitializer.initialize(getActivity());

                mMapView.onCreate(dialog.onSaveInstanceState());
                mMapView.onResume();

                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {
                        LatLng posisiabsen = new LatLng(Double.valueOf(latBarber), Double.valueOf(lngBarber)); ////your lat lng
                        googleMap.addMarker(new MarkerOptions().position(posisiabsen).title("Lokasi " +barbershop));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    }
                });

                pesan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (waktunya==null || tanggalnya==null){
                            Toast.makeText(getContext(), "Lengkapi data pesanan", Toast.LENGTH_SHORT).show();
                        } else {
                            Proses = ProgressDialog.show(getActivity(), null, "Memproses...", true, false);
                            kirimDataPesanan("barbershop", latBarber, lngBarber, "");
                        }
                    }
                });

                batal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        potongDirumah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog              = new AlertDialog.Builder(getActivity()).create();
                dialogInflater      = getLayoutInflater();
                //dialogView  = inflater.inflate(R.layout.dialog_pilih_bencana,null);
                dialogView  = dialogInflater.inflate(R.layout.dialog_pesan_dirumah,null);
                dialog.setView(dialogView);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                final TextView tanggal    = dialogView.findViewById(R.id.tv_tanggal);
                final TextView waktu      = dialogView.findViewById(R.id.tv_waktu);
                final EditText alamat     = dialogView.findViewById(R.id.et_alamat);
                TextView pesan      = dialogView.findViewById(R.id.tv_pesan);
                TextView batal      = dialogView.findViewById(R.id.tv_batal);

                myCalendar = Calendar.getInstance();
                date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String myFormat = "dd MMM yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        tanggal.setText(sdf.format(myCalendar.getTime()));
                        tanggalnya = sdf.format(myCalendar.getTime());
                    }
                };

                tanggal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new DatePickerDialog(getActivity(), date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                waktu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String time;
                                if(selectedHour>=0 && selectedHour<12){
                                    time = selectedHour + " : " + selectedMinute + " AM";
                                } else {
                                    if(selectedHour == 12){
                                        time = selectedHour + " : " + selectedMinute + " PM";
                                    } else{
                                        selectedHour = selectedHour -12;
                                        time = selectedHour + " : " + selectedMinute + " PM";
                                    }
                                }

                                waktu.setText(time);
                                waktunya = time;
                            }
                        }, hour, minute, false);
                        mTimePicker.setTitle("Pilih waktu");
                        mTimePicker.show();
                    }
                });

                MapView mMapView = (MapView) dialogView.findViewById(R.id.map);
                MapsInitializer.initialize(getActivity());

                mMapView.onCreate(dialog.onSaveInstanceState());
                mMapView.onResume();

                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {
                        LatLng posisiabsen = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()); ////your lat lng
                        googleMap.addMarker(new MarkerOptions().position(posisiabsen).title("Current location"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    }
                });

                pesan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (waktunya==null || tanggalnya==null || alamat.getText().toString().equals("")){
                            Toast.makeText(getContext(), "Lengkapi data pesanan", Toast.LENGTH_SHORT).show();
                        } else {
                            Proses = ProgressDialog.show(getActivity(), null, "Memproses...", true, false);
                            kirimDataPesanan("rumah", String.valueOf(currentLocation.getLatitude()), String.valueOf(currentLocation.getLongitude()), alamat.getText().toString());
                        }
                    }
                });

                batal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        return v;
    }

    private void getTikorBarber() {
        Call<GetBarber> barberCall = mApiInterface.getDataBarber(barbershop);
        barberCall.enqueue(new Callback<GetBarber>() {
            @Override
            public void onResponse(Call<GetBarber> call, Response<GetBarber> response) {
                List<Barber> barberList = response.body().getResult();
                latBarber = barberList.get(0).getlat();
                lngBarber = barberList.get(0).getLng();
            }

            @Override
            public void onFailure(Call<GetBarber> call, Throwable t) {

            }
        });
    }

    private void getLatLng() {
//        gps();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fetchLocation();

        latitude = 0;
        longitude = 0;
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
//                    Toast.makeText(getContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

//                    currentLocation.getLatitude()
//                    currentLocation.getLongitude()

                }
            }
        });
    }

    private void gps() {
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
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

    private void kirimDataPesanan(String postBarber, String postLat, String postLng, String postAlamat) {
        Call<GetPesanan> pesananCall = mApiInterface.postPesanan(customer, this.barbershop, kepster, tanggalnya, waktunya, postBarber,"menunggu", latBarber, lngBarber, postAlamat);
        pesananCall.enqueue(new Callback<GetPesanan>() {
            @Override
            public void onResponse(Call<GetPesanan> call, Response<GetPesanan> response) {
                GetPesanan respon = response.body();

                if (respon.isSuccess()){
                    Proses.dismiss();
                    Toast.makeText(getActivity(), "Berhasil membuat pesanan", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    ((Activity)getActivity()).finish();
                } else {
                    Proses.dismiss();
                    Toast.makeText(getActivity(), "Gagal membuat pesanan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetPesanan> call, Throwable t) {
                Proses.dismiss();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
}
