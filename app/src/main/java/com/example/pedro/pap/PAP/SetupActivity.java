package com.example.pedro.pap.PAP;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pedro.pap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private CircleImageView setupImage;

    private EditText setupName;
    private Button setupBtn;
    private ProgressBar setupProgress;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private boolean isChange = false;
    private boolean exist = false;
    private Uri mainImageURI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setupImage = findViewById(R.id.setup_image);
        setupName = findViewById(R.id.setup_name);
        setupBtn = findViewById(R.id.setup_btn);
        setupProgress = findViewById(R.id.setup_progress);

        //setupName.setBackgroundResource(R.drawable.txt_rounded_border_error);
        //setupBtn.setBackgroundResource(R.drawable.btn_rounded_border_disabled);

        setupProgress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);


        getNameAndImage();

        setupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (exist == true) {
                    if (!setupName.getText().toString().isEmpty() && Globais.user_img != "") {
                        setupBtn.setBackgroundResource(R.drawable.btn_rounded_border_enabled);
                        //setupName.setBackgroundResource(R.drawable.txt_rounded_border_ok);

                        setupBtn.setEnabled(true);
                    }else if (setupName.getText().toString().isEmpty()) {
                        setupName.setBackgroundResource(R.drawable.txt_rounded_border_error);
                        //setupBtn.setBackgroundResource(R.drawable.btn_rounded_border_disabled);

                        setupBtn.setEnabled(true);
                    } else if (Globais.user_img != "") {
                        //setupBtn.setBackgroundResource(R.drawable.btn_rounded_border_disabled);

                        setupBtn.setEnabled(true);
                    } else{
                        setupName.setBackgroundResource(R.drawable.txt_rounded_border_error);
                        //setupBtn.setBackgroundResource(R.drawable.btn_rounded_border_disabled);

                        setupBtn.setEnabled(true);
                    }
                }else {
                    if (!setupName.getText().toString().isEmpty()) {
                        //setupBtn.setBackgroundResource(R.drawable.btn_rounded_border_enabled);
                        setupName.setBackgroundResource(R.drawable.txt_rounded_border_ok);

                        setupBtn.setEnabled(true);
                    }else if (setupName.getText().toString().isEmpty()) {
                        setupName.setBackgroundResource(R.drawable.txt_rounded_border_error);
                        //setupBtn.setBackgroundResource(R.drawable.btn_rounded_border_disabled);

                        setupBtn.setEnabled(true);
                    } else{
                        setupName.setBackgroundResource(R.drawable.txt_rounded_border_error);
                        //setupBtn.setBackgroundResource(R.drawable.btn_rounded_border_disabled);

                        setupBtn.setEnabled(true);
                    }
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(SetupActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(SetupActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    } else {
                        BringImagePicker();
                    }
                }else{
                    BringImagePicker();
                }
            }
        });

        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarInfo();
            }
        });
    }

    private void getNameAndImage() {
        if (Globais.user_name != "" && Globais.user_img != "") {
            setupName.setText(Globais.user_name);

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.logo_image)
                    .error(R.drawable.logo_image);

            Glide.with(SetupActivity.this).setDefaultRequestOptions(options).load(Globais.user_img).into(setupImage);
        }

        if (!setupName.getText().toString().isEmpty()) {
            exist = true;

            setupName.setBackgroundResource(R.drawable.txt_rounded_border_ok);
            setupBtn.setBackgroundResource(R.drawable.btn_rounded_border_enabled);
        }else {
            //setupName.setBackgroundResource(R.drawable.txt_rounded_border_error);
        }

        setupBtn.setEnabled(true);
        setupProgress.setVisibility(View.INVISIBLE);

        setupName.setSelection(setupName.getText().length());
    }

    private void storeFireStore(final String user_name, StorageReference image_path) {
        if(image_path != null){
            image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    final String caminho = uri.toString();

                    final Map<String, String> userMap = new HashMap<>();
                    userMap.put("name", user_name);
                    userMap.put("image", caminho);

                    firebaseFirestore.collection("Users").document(Globais.user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Globais.user_name = user_name;
                                Globais.user_img = caminho;
                                Toast.makeText(SetupActivity.this, "Definições atualizadas com sucesso", Toast.LENGTH_LONG).show();

                                Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                                startActivity(mainIntent);

                                finish();
                            }else{
                                String error = task.getException().getMessage();
                                Toast.makeText(SetupActivity.this, "(FIRESTORE ERROR): " + error, Toast.LENGTH_LONG).show();
                            }

                            setupProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            });
        }else{
            StorageReference image_path2 = storageReference.child("profile_images").child(Globais.user_id + ".jpg");

            image_path2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    final String caminho2 = uri.toString();

                    final Map<String, String> userMap = new HashMap<>();
                    userMap.put("name", user_name);
                    userMap.put("image", caminho2);

                    firebaseFirestore.collection("Users").document(Globais.user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Globais.user_name = user_name;
                                Globais.user_img = caminho2;
                                Toast.makeText(SetupActivity.this, "Definições atualizadas com sucesso", Toast.LENGTH_LONG).show();

                                Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                                startActivity(mainIntent);

                                finish();
                            }else{
                                String error = task.getException().getMessage();
                                Toast.makeText(SetupActivity.this, "(FIRESTORE ERROR): " + error, Toast.LENGTH_LONG).show();
                            }

                            setupProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);
                isChange = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Não deu", Toast.LENGTH_SHORT).show();
        }
    }

    private void BringImagePicker(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupActivity.this);
    }


    public void guardarInfo() {
        final String user_name = setupName.getText().toString();
        setupProgress.setVisibility(View.VISIBLE);
        setupBtn.setEnabled(false);

        if(!TextUtils.isEmpty(user_name) && mainImageURI != null) {

            if (isChange) {
                final StorageReference image_path = storageReference.child("profile_images").child(user_name + ".jpg");

                image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            storeFireStore(user_name,image_path);
                        }else{
                            String error = task.getException().getMessage();
                            Toast.makeText(SetupActivity.this, "(ERROR IMAGE): " + error, Toast.LENGTH_LONG).show();
//                            setupProgress.setVisibility(View.INVISIBLE);
//                            setupBtn.setEnabled(true);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Entrou 3", Toast.LENGTH_SHORT).show();
                storeFireStore(user_name, null);

//                setupProgress.setVisibility(View.INVISIBLE);
//                setupBtn.setEnabled(true);

            }
        } else if (exist == false) {
            if(setupName.getText().toString().isEmpty()) {
                setupName.setBackgroundResource(R.drawable.txt_rounded_border_error);

                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);
            }else {
                Toast.makeText(this, "Tem de inserir uma imagem", Toast.LENGTH_SHORT).show();
                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);
            }
        } else if (!Globais.user_name.equals(setupName.getText().toString())){
            storeFireStore(user_name, null);

//            setupProgress.setVisibility(View.INVISIBLE);
//            setupBtn.setEnabled(true);
        } else if (setupName.getText().toString() == "") {
            Toast.makeText(this, "Tem de introduzir o seu nome", Toast.LENGTH_SHORT).show();

//            setupProgress.setVisibility(View.INVISIBLE);
//            setupBtn.setEnabled(true);
        } else{
            setupProgress.setVisibility(View.INVISIBLE);
            setupBtn.setEnabled(true);

           finish();
        }
    }
}
