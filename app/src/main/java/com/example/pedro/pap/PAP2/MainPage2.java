package com.example.pedro.pap.PAP2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.pedro.pap.R;

public class MainPage2 extends AppCompatActivity {

    private Button loginBtn;
    private Button regBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page2);

        loginBtn = findViewById(R.id.main_page_2_login_btn);
        regBtn = findViewById(R.id.main_page_2_reg_btn);

    }

    public void onClickLogin(View v) {
        Intent intent =  new Intent(this, LoginUser2.class);
        startActivity(intent);
        //finish();
    }

    public void onClickReg(View v) {
        Intent intent =  new Intent(this, CreateUser2.class);
        startActivity(intent);
        //finish();
    }
}
