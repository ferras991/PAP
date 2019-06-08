package com.example.pedro.pap.PAP2;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pedro.pap.PAP.Globais;
import com.example.pedro.pap.PAP.SetupActivity;
import com.example.pedro.pap.R;

public class ShowDef2 extends AppCompatActivity {

    private TextView userId, userName, userImage;
    private ImageView userImageShow;

    private Uri mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_def2);

        //userId = findViewById(R.id.show_def_2_user_id);
        userName = findViewById(R.id.show_def_2_user_name);
        //userImage = findViewById(R.id.show_def_2_user_image);
        userImageShow = findViewById(R.id.show_def_2_user_image_show);

        showUserInfo();
    }

    private void showUserInfo() {
        //userId.setText(Globais2.user_id);
        userName.setText("Nome: " + Globais2.user_name);
//        userImage.setText(Globais2.user_img);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.logo_image)
                .error(R.drawable.logo_image);

        Glide.with(ShowDef2.this).setDefaultRequestOptions(options).load(Globais2.user_img).into(userImageShow);
    }

}
