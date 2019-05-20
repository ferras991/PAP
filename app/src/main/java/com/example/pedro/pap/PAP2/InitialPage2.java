package com.example.pedro.pap.PAP2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedro.pap.PAP.Globais;
import com.example.pedro.pap.PAP.MainActivity;
import com.example.pedro.pap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InitialPage2 extends AppCompatActivity {
    private DatabaseReference ref;
    private StorageReference mStorageRef;

    ArrayList<SoftUpload> list;

    RecyclerView recyclerView;
    SearchView searchView;
    private ProgressBar progressBar;

    private Button btnDownload;
    private TextView tvImageName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_page_2);

        ref = FirebaseDatabase.getInstance().getReference().child("apk");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        recyclerView = findViewById(R.id.rv);
        searchView = findViewById(R.id.initial_page_2_searchView);
        progressBar = findViewById(R.id.initial_page_2_progress_bar);


    }



    public void goToDef(View v) {
        Toast.makeText(InitialPage2.this, "Id: " + Globais2.user_id, Toast.LENGTH_SHORT).show();
        Toast.makeText(InitialPage2.this, "Name: " + Globais2.user_name, Toast.LENGTH_SHORT).show();
        Toast.makeText(InitialPage2.this, "Img: " + Globais2.user_img, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(InitialPage2.this, DefUser2.class);
        startActivity(intent);
    }


    @Override
    protected void onStart () {
        super.onStart();

        if (ref != null) {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    try {

                        Toast.makeText(InitialPage2.this, "Entrou 1", Toast.LENGTH_SHORT).show();


                        if(list != null) { list.clear(); }


                        if (dataSnapshot.exists()) {



                            Toast.makeText(InitialPage2.this, "Entrou 2", Toast.LENGTH_SHORT).show();
                            list = new ArrayList<>();

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Toast.makeText(InitialPage2.this, "Entrou 3", Toast.LENGTH_SHORT).show();
                                list.add(ds.getValue(SoftUpload.class));
                            }

                            Toast.makeText(InitialPage2.this, "Entrou 4", Toast.LENGTH_SHORT).show();

                            SoftwareAdapter adapterClass = new SoftwareAdapter(InitialPage2.this, list);
                            recyclerView.setAdapter(adapterClass);
                        }

                        progressBar.setVisibility(View.INVISIBLE);
                    }catch (Exception e) {
                        Toast.makeText(InitialPage2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(InitialPage2.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

        }else{
            Toast.makeText(this, "Não existe", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
        }

        if (searchView != null) {
            progressBar.setVisibility(View.VISIBLE);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    progressBar.setVisibility(View.INVISIBLE);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    search(s);
                    progressBar.setVisibility(View.INVISIBLE);
                    return true;
                }
            });
        }
    }

    private void search(String str) {
        ArrayList<SoftUpload> myList = new ArrayList<>();

        for (SoftUpload object : list) {
            if(object.getName().toLowerCase().contains(str.toLowerCase())) {
                myList.add(object);
            }
        }

        SoftwareAdapter adapterClass = new SoftwareAdapter(InitialPage2.this, myList);
        recyclerView.setAdapter(adapterClass);

    }



    public void downloadSoftware(View view) {
        try{
            tvImageName = findViewById(R.id.show_card_holder_softName);
            btnDownload = findViewById(R.id.show_card_holder_btnDownload);


            RelativeLayout vwParentRow = (RelativeLayout)view.getParent();  //get the row the clicked button is in

            TextView child = (TextView)vwParentRow.getChildAt(0);

            StorageReference riversRef = mStorageRef.child("apk/" + child.getText().toString() + ".apk");

            File rootPath = new File(Environment.getExternalStorageDirectory(), "SOFTVIRTUAL");
            final File myFile = new File(rootPath,  tvImageName.getText().toString()+ ".apk");

            final ProgressDialog progressDialog = new ProgressDialog(InitialPage2.this);

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
                        Toast.makeText(InitialPage2.this, "Error", Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                       double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage(((int)progress) + "% Downloaded...");


                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationManager notificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            int notifyId = 1;
                            String channelId = "1";

                            Notification notification = new Notification.Builder(InitialPage2.this)
                                    .setContentTitle("Some Message")
                                    .setContentText("You've received new messages!")
                                    .setSmallIcon(R.drawable.ic_android_black_24dp)
                                    .setChannelId(channelId)
                                    .setVibrate(new long[]{0L})

                                    .setProgress(100, (int)progress, false)
                                    .build();

                            notificationManager.notify(notifyId, notification);
                        }

                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel notificationChannel = new NotificationChannel("1", "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

                            // Configure the notification channel.
                            notificationChannel.setDescription("Channel description");
                            notificationChannel.enableLights(true);
                            notificationChannel.setLightColor(Color.RED);
                            notificationChannel.setVibrationPattern(new long[]{0, 0, 0, 0});
                            notificationChannel.enableVibration(false);
                            notificationManager.createNotificationChannel(notificationChannel);
                        }

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(InitialPage2.this, "1")
                                .setVibrate(new long[]{0, 0, 0, 0, 0, 0})
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setProgress(100, (int)progress, false)
                                .setContentTitle("Content Title")
                                .setContentText("Content Text");

                        notificationManager.notify(1, builder.build());


                    }
                });


            }catch (Exception e1){
                Toast.makeText(InitialPage2.this, e1.getMessage(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e1) {
            Toast.makeText(InitialPage2.this, e1.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(InitialPage2.this, e1.getMessage(), Toast.LENGTH_LONG).show();
        }
    }




}
