package com.example.pedro.pap.CLASSES.Adapters;

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
    ArrayList<CreateProjectClass> list;
    String activity = "";

    public SoftwareAdapter(Context context, String activity, ArrayList<CreateProjectClass> list) {
        mContext = context;
        this.list = list;

        if (activity.equals("mainShow")) {
            this.activity = "mainShow";
        }else {
            this.activity = "myShow";
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (activity.equals("myShow")) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_my_soft_card_holder, viewGroup, false);
            return new MyViewHolder(view);
        }

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_soft_card_holder, viewGroup, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        CreateProjectClass uploadCurrent = list.get(i);
        myViewHolder.softName.setText(uploadCurrent.getName());
        myViewHolder.userName.setText(uploadCurrent.getUserName());

        Picasso.with(mContext)
                .load(uploadCurrent.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .resize(600, 600)
                .centerCrop()
                .into(myViewHolder.softImage);
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

            if (activity.equals("myShow")) {
                softName = itemView.findViewById(R.id.show_my_card_holder_softName);
                userName = itemView.findViewById(R.id.show_my_card_holder_softCreator);
                softImage = itemView.findViewById(R.id.show_my_card_holder_softImg);
            } else {
                softName = itemView.findViewById(R.id.show_card_holder_softName);
                userName = itemView.findViewById(R.id.show_card_holder_softCreator);
                softImage = itemView.findViewById(R.id.show_card_holder_softImg);
            }


        }
    }
}
