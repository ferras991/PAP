package com.example.pedro.pap.PAP2;

import android.content.Context;
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

import com.example.pedro.pap.CLASSES.Adapters.CreateUserClass;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class InsertUserInfo2 extends AppCompatActivity {

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    private Uri mImageUri;

    private EditText userName;
    private ImageView userImage;

    private Context mContext = this;

    public static final String FB_DATABASE_PATH = "users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_user_info_2);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

        userName = findViewById(R.id.insert_user_2_editText);
        userImage = findViewById(R.id.insert_user_2_imageView);

        Picasso.with(mContext)
                .load(Globais2.user_img)
                .placeholder(R.mipmap.ic_launcher)
                .resize(1200, 1200)
                .centerCrop()
                .into(userImage);

        Toast.makeText(mContext, "1", Toast.LENGTH_SHORT).show();
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

                Picasso.with(mContext)
                        .load(mImageUri)
                        .placeholder(R.mipmap.ic_launcher)
                        .resize(1200, 1200)
                        .centerCrop()
                        .into(userImage);

                Toast.makeText(mContext, "2", Toast.LENGTH_SHORT).show();

//                userImage.setImageURI(mImageUri);
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

                                CreateUserClass softUpload = new CreateUserClass(Globais2.user_id, userName.getText().toString(), url);

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

}
