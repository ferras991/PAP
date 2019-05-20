package com.example.pedro.pap.PAP;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedro.pap.R;
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

public class APKListActivity extends AppCompatActivity{

    private StorageReference storageReference;
    private DatabaseReference mDatabaseRef;
    private List<APKUpload> imgList;
    private ListView lv;
    private APKListAdapter adapter;
    private ProgressDialog progressDialog;
    private TextView tvImageName;
    private Button btnDownload;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_list);

        imgList = new ArrayList<>();

        //lv = findViewById(R.id.listViewImage);

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

                imgList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    APKUpload img = snapshot.getValue(APKUpload.class);

                    imgList.add(img);

                    //Toast.makeText(ImageListActivity.this, "Nome: " + snapshot.child("name").getValue().toString() + "\nUrl: " + snapshot.child("url").getValue().toString(), Toast.LENGTH_SHORT).show();
                }


                adapter = new APKListAdapter(APKListActivity.this, R.layout.apk_item, imgList);



                lv.setAdapter(adapter);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(ImageListActivity.this, imgList, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progressDialog.dismiss();
            }
        });
    }

    public void btnDownload_Click(View v) {
        tvImageName = findViewById(R.id.tvAPKName);
        btnDownload = findViewById(R.id.btn_download);



        //get the row the clicked button is in
        LinearLayout vwParentRow = (LinearLayout)v.getParent();

        TextView child = (TextView)vwParentRow.getChildAt(0);
        //Button btnChild = (Button)vwParentRow.getChildAt(1);

        StorageReference riversRef = storageReference.child("apk/" + child.getText().toString() + ".apk");
        //Toast.makeText(ImageListActivity.this, riversRef.toString(), Toast.LENGTH_LONG).show();


        File rootPath = new File(Environment.getExternalStorageDirectory(), "Download");
        final File myFile = new File(rootPath,  tvImageName.getText().toString()+ ".apk");


        //Toast.makeText(ImageListActivity.this, rootPath.toString(), Toast.LENGTH_SHORT).show();
        final ProgressDialog progressDialog = new ProgressDialog(APKListActivity.this);


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

                    finish();

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(APKListActivity.this, "Aplicação não encontrada", Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage(((int)progress) + "% Downloaded...");
                }
            });


        }catch (Exception e1){
            Toast.makeText(APKListActivity.this, e1.getMessage(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(APKListActivity.this, e1.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}
