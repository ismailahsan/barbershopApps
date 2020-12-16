package com.example.barberapps.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberapps.GetPost.Barber;
import com.example.barberapps.GetPost.GetPesanan;
import com.example.barberapps.GetPost.Pesanan;
import com.example.barberapps.Kepster;
import com.example.barberapps.MainActivityKepster;
import com.example.barberapps.R;
import com.example.barberapps.RegisterCustomer;
import com.example.barberapps.Rest.ApiClient;
import com.example.barberapps.Rest.ApiInterface;
import com.example.barberapps.SessionUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecyclerViewPesanan extends RecyclerView.Adapter<RecyclerViewPesanan.ViewHolder> {

    private List<Pesanan> mArrayList;
    private Context context;

    ProgressDialog Proses;
    ApiInterface mApiInterface;
    SessionUser session;

    //isinya dialog
    AlertDialog dialog;
    LayoutInflater dialogInflater;
    View dialogView;

    public RecyclerViewPesanan(Context context, List<Pesanan> inputData){
        this.context    = context;
        mArrayList      = inputData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_waitinglist,parent, false);
        RecyclerViewPesanan.ViewHolder vh = new RecyclerViewPesanan.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        mApiInterface   = ApiClient.getClient().create(ApiInterface.class);

        final String id           = mArrayList.get(position).getId();
        String tanggal      = mArrayList.get(position).getTanggal();
        String waktu        = mArrayList.get(position).getWaktu();
        String customer     = mArrayList.get(position).getCustomer();
        String alamat       = mArrayList.get(position).getAlamat();
        String layanan      = mArrayList.get(position).getJenisPesanan();

        holder.tvAlamat.setText(alamat);
        holder.tvCustomer.setText(customer);
        holder.tvWaktu.setText(waktu);
        holder.tvTanggal.setText(tanggal);
        holder.tvLayanan.setText("Cukur di : "+layanan);

        if (mArrayList.get(position).getStatus().equals("selesai")){

        } else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog      = new AlertDialog.Builder(context).create();
                    dialogView  = LayoutInflater.from(context).inflate(R.layout.dialog_ambil_order,null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(true);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    TextView proses = dialogView.findViewById(R.id.tv_proses);

                    proses.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Proses = ProgressDialog.show(context, null, "Memproses...", true, false);

                            Call<GetPesanan> pesananCall = mApiInterface.updatePesanan(id, "diproses");
                            pesananCall.enqueue(new Callback<GetPesanan>() {
                                @Override
                                public void onResponse(Call<GetPesanan> call, Response<GetPesanan> response) {
                                    GetPesanan getPesanan = response.body();

                                    if (getPesanan.isSuccess()){
                                        Proses.dismiss();
                                        Toast.makeText(context, "Pesanan telah diproses", Toast.LENGTH_SHORT).show();
                                        ((Activity)context).finish();
                                        context.startActivity(new Intent(context, MainActivityKepster.class));
                                    } else {
                                        Proses.dismiss();
                                        Toast.makeText(context, "Gagal memproses pesanan", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onFailure(Call<GetPesanan> call, Throwable t) {
                                    Proses.dismiss();
                                    Toast.makeText(context, "Jaringan bermasalah", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    dialog.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTanggal, tvWaktu, tvCustomer, tvAlamat, tvLayanan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTanggal   = itemView.findViewById(R.id.tv_tanggal);
            tvWaktu     = itemView.findViewById(R.id.tv_waktu);
            tvCustomer  = itemView.findViewById(R.id.tv_customer);
            tvAlamat    = itemView.findViewById(R.id.tv_alamat);
            tvLayanan   = itemView.findViewById(R.id.tv_layanan);
        }
    }
}
