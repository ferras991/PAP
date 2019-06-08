package com.example.pedro.pap.PAP2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedro.pap.Adapters.SoftUpload;
import com.example.pedro.pap.Adapters.SoftwareAdapter;
import com.example.pedro.pap.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class VerMeusUploads extends AppCompatActivity {
    private DatabaseReference ref;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ArrayList<SoftUpload> list;

    RecyclerView recyclerView;
    private ProgressBar progressBar;

    private Button btnDownload;
    private TextView tvImageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_meus_uploads);

        ref = FirebaseDatabase.getInstance().getReference().child("apk");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        recyclerView = findViewById(R.id.ver_meus_uploads_rv);
        progressBar = findViewById(R.id.ver_meus_uploads_2_progress_bar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meus_uploads_menu, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.meus_uploads_search));
        searchView.setQueryHint("Pesquisar...");
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.meus_uploads_search:
//                return true;
                break;

            default:
                return false;
        }
        return true;
    }

    @Override
    protected void onStart () {
        super.onStart();

        if (ref != null) {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if(list != null) { list.clear(); }

                        if (dataSnapshot.exists()) {
                            list = new ArrayList<>();

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                list.add(ds.getValue(SoftUpload.class));
                            }

                            SoftwareAdapter adapterClass = new SoftwareAdapter(VerMeusUploads.this, list);
                            recyclerView.setAdapter(adapterClass);
                        }

                        progressBar.setVisibility(View.INVISIBLE);
                    }catch (Exception e) {
                        Toast.makeText(VerMeusUploads.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(VerMeusUploads.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }else{
            Toast.makeText(this, "Não existe", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void search(String str) {
        ArrayList<SoftUpload> myList = new ArrayList<>();

        for (SoftUpload object : list) {
            if(object.getName().toLowerCase().contains(str.toLowerCase())) {
                myList.add(object);
            }
        }

        SoftwareAdapter adapterClass = new SoftwareAdapter(VerMeusUploads.this, myList);
        recyclerView.setAdapter(adapterClass);
    }

    public void downloadSoftware(View view) {
        try{
            tvImageName = findViewById(R.id.show_card_holder_softName);
            btnDownload = findViewById(R.id.show_card_holder_btnDownload);

            RelativeLayout vwParentRow = (RelativeLayout)view.getParent();
            TextView child = (TextView)vwParentRow.getChildAt(0);

            StorageReference riversRef = mStorageRef.child("apk/" + child.getText().toString() + ".apk");

            File rootPath = new File(Environment.getExternalStorageDirectory(), "SOFTVIRTUAL");
            final File myFile = new File(rootPath,  tvImageName.getText().toString()+ ".apk");

            final ProgressDialog progressDialog = new ProgressDialog(VerMeusUploads.this);

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
                        Toast.makeText(VerMeusUploads.this, "Error", Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage(((int)progress) + "% Downloaded...");
                    }
                });
            }catch (Exception e1){
                Toast.makeText(VerMeusUploads.this, e1.getMessage(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e1) {
            Toast.makeText(VerMeusUploads.this, e1.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(VerMeusUploads.this, e1.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}