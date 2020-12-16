package com.example.barberapps.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.example.barberapps.GetPost.GetPesanan;
import com.example.barberapps.GetPost.Pesanan;
import com.example.barberapps.R;
import com.example.barberapps.Rest.ApiInterface;
import com.example.barberapps.SessionUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecyclerViewPesananDone extends RecyclerView.Adapter<RecyclerViewPesananDone.ViewHolder> {

    private List<Pesanan> mArrayList;
    private Context context;

    ProgressDialog Proses;
    ApiInterface mApiInterface;
    SessionUser session;

    //isinya dialog
    AlertDialog dialog;
    LayoutInflater dialogInflater;
    View dialogView;

    public RecyclerViewPesananDone(Context context, List<Pesanan> inputData){
        this.context    = context;
        mArrayList      = inputData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_orderdone,parent, false);
        RecyclerViewPesananDone.ViewHolder vh = new RecyclerViewPesananDone.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String customer     = mArrayList.get(position).getCustomer();
        String alamat       = mArrayList.get(position).getAlamat();
        String layanan      = mArrayList.get(position).getJenisPesanan();

        holder.tvAlamat.setText(alamat);
        holder.tvCustomer.setText(customer);
        holder.tvLayanan.setText(layanan);
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCustomer, tvAlamat, tvLayanan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCustomer  = itemView.findViewById(R.id.tv_customer);
            tvAlamat    = itemView.findViewById(R.id.tv_alamat);
            tvLayanan   = itemView.findViewById(R.id.tv_layanan);
        }
    }
}
