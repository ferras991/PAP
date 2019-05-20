package com.example.pedro.pap.PAP;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import static com.example.pedro.pap.R.id.tvAPKName;

public class APKListAdapter extends ArrayAdapter<APKUpload> {

    private Activity context;
    private int resource;
    private List<APKUpload> listAPK;

    public APKListAdapter(@NonNull Activity context, int resource, @NonNull List<APKUpload> objects) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;

        listAPK = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(resource, null);

        TextView tvName = v.findViewById(tvAPKName);

        //ImageView img = v.findViewById(R.id.imgView);

        tvName.setText(listAPK.get(position).getName());

        //Glide.with(context).load(listImage.get(position).getUrl()).into(img);

        return v;
    }

}
