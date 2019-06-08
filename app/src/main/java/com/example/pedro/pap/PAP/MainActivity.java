package com.example.pedro.pap.PAP;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.pedro.pap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mainToolbar;

    private FirebaseAuth mAuth;
    public static final String FB_STORAGE_PATH = "apk/";
    public static final String FB_DATABASE_PATH = "apk";
    public static final int REQUEST_CODE = 1234;

    private StorageReference storageReference;
    private DatabaseReference mDatabaseRef;

    private List<APKUpload> imgList;
    private ListView lv;
    private APKListAdapter adapter;
    private ProgressDialog progressDialog;
    private TextView tvImageName;
    private Button btnDownload;
    private Context context = this;

    private double progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_list);


        mAuth = FirebaseAuth.getInstance();

        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("SoftVirtual");


        imgList = new ArrayList<>();

        lv = findViewById(R.id.listViewImage);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Aguarde...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(MainActivity.FB_DATABASE_PATH);
        storageReference = FirebaseStorage.getInstance().getReference();

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    APKUpload img = snapshot.getValue(APKUpload.class);

                    imgList.add(img);
                }

                adapter = new APKListAdapter(MainActivity.this, R.layout.apk_item, imgList);

                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser == null){
            sendToLogin();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_logout_btn:
                Globais.user_id = "";
                Globais.user_name = "";
                Globais.user_img = "";
                logOut();
                break;

            case R.id.action_settings_btn:
                Intent i = new Intent(MainActivity.this, DefinicoesActivity.class);
                startActivity(i);
                break;

//            case R.id.action_download:
//                Intent i1 = new Intent(MainActivity.this, InserirSoftware.class);
//                startActivity(i1);
//                break;

            default:
                return false;
        }

        return true;
    }

    private void logOut() {
        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    public void btnDownload_Click(View v) {
        try{
            tvImageName = findViewById(R.id.tvAPKName);
            btnDownload = findViewById(R.id.btn_download);


            RelativeLayout vwParentRow = (RelativeLayout)v.getParent();  //get the row the clicked button is in

            TextView child = (TextView)vwParentRow.getChildAt(0);

            StorageReference riversRef = storageReference.child("apk/" + child.getText().toString() + ".apk");

            File rootPath = new File(Environment.getExternalStorageDirectory(), "SOFTVIRTUAL");
            final File myFile = new File(rootPath,  tvImageName.getText().toString()+ ".apk");

            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);


            final double progress2 = 0;

            try{


                if (!rootPath.exists()) {
                    rootPath.mkdirs();
                }

                progressDialog.setTitle("Downloading...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                riversRef.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        install(myFile);

                        myFile.delete();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage(((int)progress) + "% Downloaded...");


                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationManager notificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            int notifyId = 1;
                            String channelId = "1";

                            Notification notification = new Notification.Builder(MainActivity.this)
                                    .setContentTitle("Some Message")
                                    .setContentText("You've received new messages!")
                                    .setSmallIcon(R.drawable.ic_android_black_24dp)
                                    .setChannelId(channelId)
                                    .setVibrate(new long[]{0L})
                                    .setProgress(100, (int)progress, false)
                                    .build();

                            notificationManager.notify(notifyId, notification);
                        }


                    }
                });


            }catch (Exception e1){
                Toast.makeText(MainActivity.this, e1.getMessage(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e1) {
            Toast.makeText(context, e1.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    private void install(File myFile){
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        Intent intent;

        try{
            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(Uri.fromFile(myFile));
            startActivity(intent);

            Thread.sleep(10000);
        }catch (Exception e1){
            Toast.makeText(MainActivity.this, e1.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}
