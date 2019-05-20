package com.example.pedro.pap.PAP2;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedro.pap.PAP.Globais;
import com.example.pedro.pap.PAP.MainActivity;
import com.example.pedro.pap.PAP.SetupActivity;
import com.example.pedro.pap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class SetupUser2 extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    private TextView showId, showName, showImg;
    private EditText insertName;
    private ImageView insertImage;
    private Button insertBtn;

    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_user2);

        showId = findViewById(R.id.setup_2_id);
        showName = findViewById(R.id.setup_2_name);
        showName.setText(Globais2.user_name);
        showImg = findViewById(R.id.setup_2_img);
        showImg.setText(Globais2.user_img);

        insertName = findViewById(R.id.setup_2_insert_name);
        insertImage = findViewById(R.id.setup_2_insert_img);
        insertBtn = findViewById(R.id.setup_2_insert_btn);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
        mStorageRef = FirebaseStorage.getInstance().getReference("users");



        insertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BringImagePicker();
            }
        });

        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = insertName.getText().toString();
                if (mUploadTask != null && mUploadTask.isInProgress() && !name.equals("") && mImageUri != null) {
                    Toast.makeText(SetupUser2.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                }else{
                    uploadUser(name, mImageUri);
                }
            }
        });
    }

    private void BringImagePicker(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupUser2.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            //Toast.makeText(this, "Entra 1 image", Toast.LENGTH_SHORT).show();

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //Toast.makeText(this, "Entra 2 image", Toast.LENGTH_SHORT).show();

                mImageUri = result.getUri();

                insertImage.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Não deu", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadUser(final String name, Uri image) {
        if (!name.equals("") && image != null) {
            final StorageReference fileReference = mStorageRef.child("profile_images").child(mAuth.getUid() + ".jpg");

            mUploadTask = fileReference.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        storeFireStore(name,fileReference);
                    }else{
                        String error = task.getException().getMessage();
                        Toast.makeText(SetupUser2.this, "(ERROR IMAGE): " + error, Toast.LENGTH_LONG).show();
//                            setupProgress.setVisibility(View.INVISIBLE);
//                            setupBtn.setEnabled(true);
                    }
                }
            });
        }
    }

    private void storeFireStore(final String name, final StorageReference image_path) {
        if (image_path != null) {
            image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    final String caminho = uri.toString();

                    Map<String, CreateUser2Upload> user = new HashMap<>();
                    user.put(mAuth.getUid(), new CreateUser2Upload(name, caminho));

                    mDatabaseRef.setValue(user);

                    Toast.makeText(SetupUser2.this, "Já está", Toast.LENGTH_SHORT).show();

                    insertName.setText("");
                    insertImage.setImageResource(0);

                    Globais.user_name = name;
                    Globais.user_img = caminho;

                    Toast.makeText(SetupUser2.this, "Id: " + Globais2.user_id, Toast.LENGTH_SHORT).show();
                    Toast.makeText(SetupUser2.this, "Name: " + Globais2.user_name, Toast.LENGTH_SHORT).show();
                    Toast.makeText(SetupUser2.this, "Img: " + Globais2.user_img, Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    public void getInfo(View v) {
        Toast.makeText(SetupUser2.this, "Id: " + Globais2.user_id, Toast.LENGTH_SHORT).show();
        Toast.makeText(SetupUser2.this, "Name: " + Globais2.user_name, Toast.LENGTH_SHORT).show();
        Toast.makeText(SetupUser2.this, "Img: " + Globais2.user_img, Toast.LENGTH_SHORT).show();
    }


}
