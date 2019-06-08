package com.example.pedro.pap.PAP2;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pedro.pap.Adapters.CreateUser2Upload;
import com.example.pedro.pap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class InsertUserInfo2 extends AppCompatActivity {

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    private Uri mImageUri;

    private EditText userName;
    private ImageView userImage;

    public static final String FB_DATABASE_PATH = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_user_info_2);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

        userName = findViewById(R.id.insert_user_2_editText);
        userImage = findViewById(R.id.insert_user_2_imageView);
    }

    public void InsertImage(View view) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(InsertUserInfo2.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                userImage.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Não deu", Toast.LENGTH_SHORT).show();
        }
    }

    public void InsertUserInfo(View view) {
        String name = userName.getText().toString();

        if (mUploadTask != null && mUploadTask.isInProgress()){
            Toast.makeText(InsertUserInfo2.this, "Upload em progresso", Toast.LENGTH_SHORT).show();
        }else{
            uploadUser(name, mImageUri);
        }

    }

    private void uploadUser(final String name, Uri image) {
        if (!name.equals("") && image != null) {
            final StorageReference ref = mStorageRef.child("usersImages/" + Globais2.user_id + ".jpg");
            mUploadTask = ref.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = String.valueOf(uri);

                                CreateUser2Upload softUpload = new CreateUser2Upload(Globais2.user_id, userName.getText().toString(), url);

                                Globais2.user_name =  userName.getText().toString();
                                Globais2.user_img = url;

                                mDatabaseRef.child(Globais2.user_id).setValue(softUpload);

                                Toast.makeText(getApplicationContext(), "UPLOAD DO FICHEIRO COM SUCESSO", Toast.LENGTH_LONG).show();

                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        });
                    }
                }
            });
        }
    }

    private void storeFireStore(final String name, final StorageReference image_path) {
        try {
            if (image_path != null) {
//                Toast.makeText(this, "entrou1", Toast.LENGTH_SHORT).show();
                image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String caminho = uri.toString();

//                        Toast.makeText(InsertUserInfo2.this, "entrou2", Toast.LENGTH_SHORT).show();

                        Map<String, CreateUser2Upload> user = new HashMap<>();
                        user.put(Globais2.user_id, new CreateUser2Upload(Globais2.user_id, name, caminho));


                        mDatabaseRef.child("users").setValue(user);
                        String uploadId = mDatabaseRef.push().getKey();
                        mDatabaseRef.child(uploadId).setValue(user);

                        Toast.makeText(InsertUserInfo2.this, "Já está", Toast.LENGTH_SHORT).show();

                        Globais2.user_name = name;
                        Globais2.user_img = caminho;

//                        Toast.makeText(InsertUserInfo2.this, "Id: " + Globais2.user_id, Toast.LENGTH_SHORT).show();
//                        Toast.makeText(InsertUserInfo2.this, "Name: " + Globais2.user_name, Toast.LENGTH_SHORT).show();
//                        Toast.makeText(InsertUserInfo2.this, "Img: " + Globais2.user_img, Toast.LENGTH_SHORT).show();

                    }
                });
            } else {
                Toast.makeText(this, "NANA", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
