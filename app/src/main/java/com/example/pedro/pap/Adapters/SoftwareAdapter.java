package com.example.pedro.pap.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pedro.pap.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SoftwareAdapter extends RecyclerView.Adapter<SoftwareAdapter.MyViewHolder> {

    private Context mContext;
    ArrayList<SoftUpload> list;

    public SoftwareAdapter(Context context, ArrayList<SoftUpload> list) {
        mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_soft_card_holder, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        SoftUpload uploadCurrent = list.get(i);
        myViewHolder.softName.setText(uploadCurrent.getName());
        myViewHolder.userName.setText(uploadCurrent.getUserName());
        //myViewHolder.imageShow.setText(uploadCurrent.getImageUrl());
        Picasso.with(mContext)
                .load(uploadCurrent.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .resize(600, 600)
                .centerCrop()
                .into(myViewHolder.softImage);

        // Toast.makeText(mContext, "ImageURl: " + uploadCurrent.getImageUrl(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView softName, userName;
        ImageView softImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            softName = itemView.findViewById(R.id.show_card_holder_softName);
            userName = itemView.findViewById(R.id.show_card_holder_softCreator);
            softImage = itemView.findViewById(R.id.show_card_holder_softImg);
        }
    }
}
