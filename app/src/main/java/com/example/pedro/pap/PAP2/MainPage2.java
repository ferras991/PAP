package com.example.pedro.pap.PAP2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pedro.pap.Adapters.CreateUser2Upload;
import com.example.pedro.pap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainPage2 extends AppCompatActivity {

    private Button loginBtn;
    private Button regBtn;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");

    private String userID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page2);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainPage2.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainPage2.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }

            if (ContextCompat.checkSelfPermission(MainPage2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainPage2.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }

            if (ContextCompat.checkSelfPermission(MainPage2.this, Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainPage2.this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 1);
            }
        }


        loginBtn = findViewById(R.id.main_page_2_login_btn);
        regBtn = findViewById(R.id.main_page_2_reg_btn);

    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            userID = mAuth.getUid();
            sendToMain();
        }
    }


    private void sendToMain(){
        getNameAndImage();

        Intent mainIntent = new Intent(MainPage2.this, InitialPage2.class);
        startActivity(mainIntent);
        finish();
    }


    private void getNameAndImage() {
        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                CreateUser2Upload newUser = dataSnapshot.getValue(CreateUser2Upload.class);

                try {

                    CreateUser2Upload newUser = dataSnapshot.getValue(CreateUser2Upload.class);

//                    Toast.makeText(LoginUser2.this, "UserID: " + userID, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(LoginUser2.this, "UserID: " + newUser.getId(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(LoginUser2.this, "UserName: " + newUser.getName(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(LoginUser2.this, "UserImage: " + newUser.getImg(), Toast.LENGTH_SHORT).show();

                    if(userID.equals(newUser.getId())) {
//                        Toast.makeText(LoginUser2.this, "Entrou", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(MainPage2.this, "Id: " + newUser.getId(), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(MainPage2.this, "Name: " + newUser.getName(), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(MainPage2.this, "Img: " + newUser.getImg(), Toast.LENGTH_SHORT).show();

                        Globais2.user_id = userID;
                        Globais2.user_name = newUser.getName();
                        Globais2.user_img = newUser.getImg();
                    }
//
//                    Toast.makeText(LoginUser2.this, "Saiu", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(LoginUser2.this, "UserID: " + Globais2.user_id, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(LoginUser2.this, "UserName: " + Globais2.user_name, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(LoginUser2.this, "UserImage: " + Globais2.user_img, Toast.LENGTH_SHORT).show();

                }catch (Exception e) {
                    Toast.makeText(MainPage2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Main thread Interrupted");
        }
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
