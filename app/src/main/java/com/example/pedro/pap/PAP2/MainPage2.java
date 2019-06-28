package com.example.pedro.pap.PAP2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pedro.pap.CLASSES.CreateUserClass;
import com.example.pedro.pap.R;
import com.example.pedro.pap.UpdateHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainPage2 extends AppCompatActivity implements UpdateHelper.OnUpdateCheckListener{
    private Button loginBtn;
    private Button regBtn;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");

    private String userID = "";
    private boolean isUpToDate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_page2);
        UpdateHelper.with(this)
                .onUpdateCheck(this)
                .check();

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

        UpdateHelper.with(this)
                .onUpdateCheck(this)
                .check();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            userID = mAuth.getUid();
            sendToMain();
//            Intent i = new Intent(MainPage2.this, CreateProjectsComments.class);
//            startActivity(i);
            finish();
        }
    }

    @Override
    public void onUpdateCheckListener (final String urlApp) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("New Version Available")
                .setMessage("Please update to new version to continue use")
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlApp));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        isUpToDate = true;
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).create();
        alertDialog.show();

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
//                CreateUserClass newUser = dataSnapshot.getValue(CreateUserClass.class);

                try {
                    CreateUserClass newUser = dataSnapshot.getValue(CreateUserClass.class);

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
                        Globais2.user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
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
        Intent intent =  new Intent(this, CreateUser.class);
        startActivity(intent);
        //finish();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Toast.makeText(this, "Permissions: " + permissions.length, Toast.LENGTH_SHORT).show();
//
//        if(permissions.length == 0){
//            return;
//        }
//        boolean allPermissionsGranted = true;
//        if(grantResults.length>0){
//            for(int grantResult: grantResults){
//                if(grantResult != PackageManager.PERMISSION_GRANTED){
//                    allPermissionsGranted = false;
//                    break;
//                }
//            }
//        }
//        Toast.makeText(this, "All permissions: " + allPermissionsGranted, Toast.LENGTH_SHORT).show();
////        if(!allPermissionsGranted){
////            boolean somePermissionsForeverDenied = false;
////            for(String permission: permissions){
////                if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
////                    Toast.makeText(this, "DENIED", Toast.LENGTH_SHORT).show();
////                    somePermissionsForeverDenied = true;
////                }else{
////                    if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
////                        Toast.makeText(this, "NEVER", Toast.LENGTH_SHORT).show();
////                        somePermissionsForeverDenied = true;
////                    }
////                }
////            }
////            if(somePermissionsForeverDenied){
////                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
////                alertDialogBuilder.setTitle("PERMISSÕES NECESSÁRIAS")
////                        .setMessage("PARA PODER CONTINUAR A UTILIZAR O 'SOFTVIRTUAL' TEM DE CONCEDER AS PERMISSÕES" +
////                                "POR FAVOR VÁ ÀS DEFINIÇÕES, VÁ ÀS PERMISSÇÕES E CONCEDA AS MESMAS, POR FAVOR.")
////                        .setPositiveButton("DEFINIÇÕES", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
////                                        Uri.fromParts("package", getPackageName(), null));
////                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                startActivity(intent);
////                            }
////                        })
////                        .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                                finish();
////                            }
////                        })
////                        .setCancelable(false)
////                        .create()
////                        .show();
////            }
////        } else {
////            switch (requestCode) {
////                //act according to the request code used while requesting the permission(s).
////            }
////        }
//    }
}
