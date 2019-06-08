package com.example.pedro.pap.PAP;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedro.pap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class InserirSoftware extends AppCompatActivity {

    private Button button, buttonUpload;
    private TextView textView;
    private EditText fileName;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseRef;
    private FirebaseFirestore firebaseFirestore;

    private String user_id;

    private String filePath;

    public static final String FB_STORAGE_PATH = "apk/";
    public static final String FB_DATABASE_PATH = "apk";

    private String extension;

    private Uri imgUri;

    String caminho;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserir_software);
        //setTitle("Upload do ficheiro");

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);
        firebaseFirestore = FirebaseFirestore.getInstance();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        }

        textView = findViewById(R.id.textView);
        buttonUpload = findViewById(R.id.button_upload);
        fileName = findViewById(R.id.file_name);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        fileName.setSelection(fileName.getText().length());

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extension = FilenameUtils.getExtension(textView.getText().toString());
                uploadFile();
            }
        });
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialFilePicker()
                        .withActivity(InserirSoftware.this)
                        .withRequestCode(1000)
                        .withHiddenFiles(false) // Show hidden files and folders
                        .start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            if(requestCode == 1000 && resultCode == RESULT_OK){
                filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                textView.setText(filePath);
                extension = FilenameUtils.getExtension(textView.getText().toString());

                if (extension.equals("apk")){
                    imgUri = data.getData();
                }else{
                 Toast.makeText(InserirSoftware.this, "Tem de inserir um apk", Toast.LENGTH_LONG).show();
                 filePath = "";
                 textView.setText("");
                }
            }
        }catch(Exception e1){
            if(textView.getText().toString() == ""){
                filePath = "";
                textView.setText("");
            }

        }
    }

    private void uploadFile(){
        try{
            Uri file = Uri.fromFile(new File(filePath));

            if(file != null){
                String nome = fileName.getText().toString();
                if(nome!=""){
                    final ProgressDialog progressDialog = new ProgressDialog(InserirSoftware.this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();

                    String extension = FilenameUtils.getExtension(file.toString());

                    final StorageReference ref = storageReference.child(FB_STORAGE_PATH + fileName.getText().toString() + "." + extension);

                    ref.putFile(file).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                storeFireStore(task, ref);
                                progressDialog.dismiss();

                                Toast.makeText(getApplicationContext(), "APK uploaded", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + (int)progress + "%");
                        }
                    });
                }else{
                    Toast.makeText(InserirSoftware.this, "Tem de introduzir um nome", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(InserirSoftware.this, "NANA", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){

        }

    }

    public String getAPKExt (Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void storeFireStore(@NonNull Task<UploadTask.TaskSnapshot> task, final StorageReference image_path) {
        if(image_path != null){
            image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    caminho = uri.toString();

                    //APKUpload imageUpload = new APKUpload(fileName.getText().toString(), caminho);

                    String uploadId = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue(new APKUpload(fileName.getText().toString(), caminho));
                }
            });
        }

    }
}