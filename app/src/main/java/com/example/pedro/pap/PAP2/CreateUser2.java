package com.example.pedro.pap.PAP2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pedro.pap.Adapters.CreateUser2Upload;
import com.example.pedro.pap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;

public class CreateUser2 extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    public static final String FB_STORAGE_PATH = "users/";
    public static final String FB_DATABASE_PATH = "users";

    private EditText emailField, passField, checkPassField, nameField;
    private Button createBtn;
    private ProgressBar progressBar;

    Context mContext = this;

    private String defaultImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user2);

        emailField = findViewById(R.id.create_user_2_email);
        passField= findViewById(R.id.create_user_2_senha);
        checkPassField = findViewById(R.id.create_user_2_confirmar_senha);
        createBtn = findViewById(R.id.create_user_2_btn);
        progressBar = findViewById(R.id.create_user_2_progress);
        nameField = findViewById(R.id.create_user_2_name);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameField.getText().toString();
                String email = emailField.getText().toString();
                String pass = passField.getText().toString();
                String passCheck = checkPassField.getText().toString();
                checkInfo(name, email, pass, passCheck);
            }
        });
    }

    private void checkInfo(String name, String email, String pass, String passCheck) {
        progressBar.setVisibility(View.VISIBLE);
        if (!email.equals("")) {
            if (!pass.equals("") && !passCheck.equals("")) {
                if (pass.equals(passCheck)) {
                    Toast.makeText(this, "Tudo certo", Toast.LENGTH_SHORT).show();
                    createUser(name, email, pass, passCheck);
                }else {
                    Toast.makeText(this, "As senhas não correspondem", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            } else {
                Toast.makeText(this, "Tem de preencher todos os campos", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }else {
            Toast.makeText(this, "Tem de preencher todos os campos", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    private void createUser(final String name, String email, final String pass, String passCheck) {
        if(!name.isEmpty()) {
            if (!email.isEmpty()) {
                if (!pass.isEmpty()) {
                    if (!passCheck.isEmpty()) {
                        if (pass.equals(passCheck)) {
                            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Globais2.user_id = mAuth.getUid();

                                        try {
                                            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo_image);
                                            File f = new File(getExternalCacheDir() + "/defaultUserImage.png");

                                            defaultImagePath = f.getPath();
                                            try {
                                                FileOutputStream outStream = new FileOutputStream(f);
                                                bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                                                outStream.flush();
                                                outStream.close();
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }

                                        }catch (Exception e1) {
                                            Toast.makeText(mContext, e1.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                        keepChanges(name, defaultImagePath);

                                        progressBar.setVisibility(View.INVISIBLE);

                                        Intent intent = new Intent(CreateUser2.this, InitialPage2.class);
                                        startActivity(intent);

                                        finish();
                                    } else {
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(getApplicationContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
        if (email.isEmpty() || pass.isEmpty() || passCheck.isEmpty()) {
            Toast.makeText(mContext, "TEM DE PREENCHER TODOS OS CAMPOS", Toast.LENGTH_SHORT).show();
        }
    }

    private void keepChanges(final String name, String image) {
        try{
            final StorageReference ref = mStorageRef.child("usersImages/" + Globais2.user_id + ".jpg");
            UploadTask uploadTask;

            File file = new File(image);

            uploadTask = ref.putFile(Uri.fromFile(file));

            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = String.valueOf(uri);

                                CreateUser2Upload softUpload = new CreateUser2Upload(Globais2.user_id, name, url);

                                Globais2.user_name =  name;
                                Globais2.user_img = url;

                                mDatabaseRef.child(Globais2.user_id).setValue(softUpload);

                                Toast.makeText(getApplicationContext(), "Utilizador Criado", Toast.LENGTH_LONG).show();

                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                    System.out.println("Main thread Interrupted");
                                }

                                Globais2.user_name =  name;
                                Globais2.user_img = url;

                                //finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }else {
                        Toast.makeText(CreateUser2.this, "NANA", Toast.LENGTH_SHORT).show();
                        Toast.makeText(CreateUser2.this, "Exception: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception e1) {
            Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
