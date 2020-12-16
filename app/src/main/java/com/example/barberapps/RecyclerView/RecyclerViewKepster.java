package com.example.barberapps.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberapps.Fragment.Btmsheet_pilihLayanan;
import com.example.barberapps.GetPost.User;
import com.example.barberapps.R;
import com.example.barberapps.Rest.ApiInterface;
import com.example.barberapps.SessionUser;

import java.util.HashMap;
import java.util.List;

public class RecyclerViewKepster extends RecyclerView.Adapter<RecyclerViewKepster.ViewHolder> {

    private List<User> mArrayList;
    private Context context;

    ProgressDialog Proses;
    ApiInterface mApiInterface;
    SessionUser session;

    public RecyclerViewKepster (Context context, List<User> inputData){
        this.context    = context;
        mArrayList      = inputData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_kepster,parent, false);
        RecyclerViewKepster.ViewHolder vh = new RecyclerViewKepster.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        session         = new SessionUser(context);
        HashMap<String, String> user = session.getUserDetails();
        String jenisUser    = user.get(SessionUser.KEY_JENISUSER);
        final String nama         = user.get(SessionUser.KEY_NAMA);

        holder.namaKepster.setText(mArrayList.get(position).getNama());
        holder.barbershop.setText("Barbershop : "+mArrayList.get(position).getBarbershop());
        holder.hp.setText(mArrayList.get(position).getHp());

        if (jenisUser.equals("")){

        } else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    args.putString("barbershop", mArrayList.get(position).getBarbershop());
                    args.putString("kepster", mArrayList.get(position).getNama());
                    args.putString("customer", nama);
                    Btmsheet_pilihLayanan addPhotoBottomDialogFragment = Btmsheet_pilihLayanan.newInstance();
                    addPhotoBottomDialogFragment.show(((FragmentActivity)context).getSupportFragmentManager(), Btmsheet_pilihLayanan.TAG);
                    addPhotoBottomDialogFragment.setArguments(args);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView namaKepster, hp, barbershop;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            barbershop = itemView.findViewById(R.id.tv_customer);
            namaKepster     = itemView.findViewById(R.id.tv_namaKepster);
            hp              = itemView.findViewById(R.id.tv_hp);
        }
    }
}
