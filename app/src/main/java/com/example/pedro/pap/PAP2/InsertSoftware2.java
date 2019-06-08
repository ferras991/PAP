package com.example.pedro.pap.PAP2;

import android.app.ProgressDialog;
import android.content.ContentResolver;
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

import org.apache.commons.io.FilenameUtils;

public class InsertSoftware2 extends AppCompatActivity {

    private Button buttonUpload, btnChoose;
    private TextView textView;
    private EditText fileName;
    private ImageView softImage;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private String user_id;

    private String filePath;

    public static final String FB_STORAGE_PATH = "apk/";
    public static final String FB_DATABASE_PATH = "apk";

    private String extension;
    private String imageUrl;

    private Uri softUri = null;
    private Uri mImageUri = null;
    private boolean imageChoose = false;

    private static final int PICK_IMAGE_REQUEST = 1;

    String caminho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_software2);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

        textView = findViewById(R.id.insert_software_2_soft_path);
        buttonUpload = findViewById(R.id.insert_software_2_soft_btn);
        fileName = findViewById(R.id.insert_software_2_soft_name);
        softImage = findViewById(R.id.insert_software_2_soft_image);
        btnChoose = findViewById(R.id.insert_software_2_browseAPK);

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extension = FilenameUtils.getExtension(textView.getText().toString());
                Toast.makeText(InsertSoftware2.this, "Extensão: " + extension, Toast.LENGTH_LONG).show();
                Toast.makeText(InsertSoftware2.this, filePath, Toast.LENGTH_LONG).show();
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

//        Toast.makeText(this, "ImageChoose: " + imageChoose, Toast.LENGTH_SHORT).show();

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null && softImage != null && imageChoose == true) {
            mImageUri = data.getData();
//            Toast.makeText(InsertSoftware2.this, "Image Path: " + mImageUri.getPath(), Toast.LENGTH_SHORT).show();

            softImage.setImageURI(mImageUri);

//            Toast.makeText(InsertSoftware2.this, "file Path: " + filePath, Toast.LENGTH_SHORT).show();

            imageChoose = false;
        }else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Toast.makeText(this, "entrou soft 1", Toast.LENGTH_SHORT).show();
            softUri = data.getData();

            if (getAPKExt(softUri).equals("apk")) {
//                Toast.makeText(this, "entrou soft 2", Toast.LENGTH_SHORT).show();
                filePath = softUri.getPath();

                //
//                Toast.makeText(InsertSoftware2.this, filePath, Toast.LENGTH_LONG).show();
            }else {
//                Toast.makeText(this, "entrou soft 3", Toast.LENGTH_SHORT).show();
//                Toast.makeText(InsertSoftware2.this, getAPKExt(softUri), Toast.LENGTH_LONG).show();
            }

        }


    }

    private void uploadFile(){
        if (mImageUri != null && softUri != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading apk");
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
                                //storeLink(url);
                                imageUrl = url;

                                ref2.putFile(softUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if(task.isSuccessful()){
                                            ref2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    caminho = uri.toString();

//                                                    Toast.makeText(InsertSoftware2.this, "Id: " + Globais2.user_id, Toast.LENGTH_SHORT).show();
//                                                    Toast.makeText(InsertSoftware2.this, "Name: " + fileName.getText().toString().trim(), Toast.LENGTH_SHORT).show();
//                                                    Toast.makeText(InsertSoftware2.this, "Url: " + caminho, Toast.LENGTH_SHORT).show();
//                                                    Toast.makeText(InsertSoftware2.this, "ImageUrl: " + imageUrl, Toast.LENGTH_SHORT).show();

                                                    SoftUpload softUpload = new SoftUpload(Globais2.user_id,
                                                            fileName.getText().toString().trim(),
                                                            Globais2.user_name,
                                                            Globais2.user_id,
                                                            caminho,
                                                            imageUrl);

                                                    String uploadId = mDatabaseRef.push().getKey();
                                                    mDatabaseRef.child(uploadId).setValue(softUpload);
                                                }
                                            });
                                            dialog.dismiss();

                                            Toast.makeText(getApplicationContext(), "APK uploaded", Toast.LENGTH_LONG).show();

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
                                        //Notifications.createNotification(mcontext, progress, "Uploading APK", txtImageName.getText().toString());
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "SELECIONE UM FICHEIRO DO TIPO 'APK'", Toast.LENGTH_LONG).show();
        }
    }

    public String getAPKExt (Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}
