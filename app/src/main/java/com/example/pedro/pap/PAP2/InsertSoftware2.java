package com.example.pedro.pap.PAP2;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pedro.pap.Adapters.SoftUpload;
import com.example.pedro.pap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FilenameUtils;

public class InsertSoftware2 extends AppCompatActivity {

    private Button buttonUpload, btnChoose;
    private TextView textView;
    private EditText fileName;
    private ImageView softImage;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    public static final String FB_STORAGE_PATH = "apk/";
    public static final String FB_DATABASE_PATH = "apk";

    private String extension;
    private String imageUrl;
    private String caminho;
    private String user_id;
    private String filePath;

    private Uri softUri = null;
    private Uri mImageUri = null;
    private boolean imageChoose = false;
    private Context mContext = this;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_software2);
        getSupportActionBar().setTitle("INSERIR PROJETO");

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

        textView = findViewById(R.id.insert_software_2_soft_path);
        buttonUpload = findViewById(R.id.insert_software_2_soft_btn);
        fileName = findViewById(R.id.insert_software_2_soft_name);
        softImage = findViewById(R.id.insert_software_2_soft_image);
        btnChoose = findViewById(R.id.insert_software_2_browseAPK);

        Picasso.with(mContext)
                .load(R.drawable.default_thumb)
                .placeholder(R.mipmap.ic_launcher)
                .resize(600, 600)
                .centerCrop()
                .into(softImage);

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extension = FilenameUtils.getExtension(textView.getText().toString());
                uploadFile();
            }
        });

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        softImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChoose = true;

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    System.out.println("Main thread Interrupted");
                }

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null && softImage != null && imageChoose == true) {
            mImageUri = data.getData();

            Picasso.with(mContext)
                    .load(mImageUri)
                    .placeholder(R.mipmap.ic_launcher)
                    .resize(600, 600)
                    .centerCrop()
                    .into(softImage);

            imageChoose = false;
        }else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            softUri = data.getData();

            if (getAPKExt(softUri).equals("apk")) {
                filePath = softUri.getPath();
            }else {
                Toast.makeText(InsertSoftware2.this, "INSIRA UM SOFTWARE", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadFile(){
        boolean exist = false;

        for (int i = 0; i < Globais2.apkNames.size(); i ++) {
            if (fileName.getText().toString().equals(Globais2.apkNames.get(i))) {
                exist = true;
                Toast.makeText(mContext, "Existe", Toast.LENGTH_SHORT).show();
                break;
            }else {
                Toast.makeText(mContext, "Não existe", Toast.LENGTH_SHORT).show();
            }
        }

        try {
            if (mImageUri != null && softUri != null && exist == false) {
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("UPLOADING DO SOFTWARE");
                dialog.show();

                final StorageReference ref = mStorageRef.child("apkImages/" + fileName.getText().toString() + "." + getAPKExt(mImageUri));
                final StorageReference ref2 = mStorageRef.child(FB_STORAGE_PATH + fileName.getText().toString() + "." + getAPKExt(softUri));

                ref.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = String.valueOf(uri);
                                    imageUrl = url;

                                    ref2.putFile(softUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                ref2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        caminho = uri.toString();
                                                        String uploadId = mDatabaseRef.push().getKey();

                                                        SoftUpload softUpload = new SoftUpload(uploadId,
                                                                fileName.getText().toString().trim(),
                                                                Globais2.user_name,
                                                                Globais2.user_id,
                                                                caminho,
                                                                imageUrl);

                                                        mDatabaseRef.child(uploadId).setValue(softUpload);
                                                    }
                                                });
                                                dialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "UPLOAD DO SOFTWARE COM SUCESSO", Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.dismiss();
                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                            dialog.setMessage("Uploaded " + (int)progress + "%");
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }else if (exist == true){
                Toast.makeText(mContext, "JÁ EXISTE UM PROJETO COM ESSE NOME!!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "SELECIONE UM FICHEIRO DO TIPO 'APK'", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e1) {

        }
    }

    public String getAPKExt (Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}
