package com.example.barberapps.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberapps.GetPost.Barber;
import com.example.barberapps.Kepster;
import com.example.barberapps.R;
import com.example.barberapps.Rest.ApiInterface;
import com.example.barberapps.SessionUser;

import java.util.List;

public class RecyclerViewBarbershop extends RecyclerView.Adapter<RecyclerViewBarbershop.ViewHolder> {

    private List<Barber> mArrayList;
    private Context context;

    ProgressDialog Proses;
    ApiInterface mApiInterface;
    SessionUser session;

    public RecyclerViewBarbershop(Context context, List<Barber> inputData){
        this.context    = context;
        mArrayList      = inputData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_barbershop,parent, false);
        RecyclerViewBarbershop.ViewHolder vh = new RecyclerViewBarbershop.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.barbershop.setText(mArrayList.get(position).getNama());
        holder.alamat.setText(mArrayList.get(position).getAlamat());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Kepster.class);
                intent.putExtra("barbershop", mArrayList.get(position).getNama());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView barbershop, alamat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            barbershop = itemView.findViewById(R.id.tv_customer);
            alamat     = itemView.findViewById(R.id.tv_alamat);
        }
    }
}
