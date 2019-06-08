package com.example.pedro.pap.PAP;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pedro.pap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn, loginRegBtn;
    private EditText emailField, password;
    private ProgressBar loginProgress;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }

            if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }

            if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 1);
            }
        }

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        loginBtn = findViewById(R.id.login_btn);
        loginRegBtn = findViewById(R.id.login_reg_btn);
        emailField = findViewById(R.id.login_email_text);
        password = findViewById(R.id.login_senha_text);
        loginProgress = findViewById(R.id.login_progress);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setEnabled(false);
                loginRegBtn.setEnabled(false);
                emailField.setEnabled(false);
                password.setEnabled(false);

                String email = emailField.getText().toString();
                String pass = password.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
                    loginProgress.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                       sendToMain();
                                    } else {
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(LoginActivity.this,"Error: " + errorMessage, Toast.LENGTH_LONG).show();

                                        loginBtn.setEnabled(true);
                                        loginRegBtn.setEnabled(true);
                                        emailField.setEnabled(true);
                                        password.setEnabled(true);
                                    }

                                    loginProgress.setVisibility(View.INVISIBLE);
                                }
                            });
                }else{
                    Toast.makeText(LoginActivity.this,"Tem que preencher todos os campos", Toast.LENGTH_LONG).show();

                    loginBtn.setEnabled(true);
                    loginRegBtn.setEnabled(true);
                    emailField.setEnabled(true);
                    password.setEnabled(true);
                }
            }
        });


        loginRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToReg();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }
    }

    private void sendToMain(){
        getNameAndImage(mAuth.getUid());

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void sendToReg(){
        Intent mainIntent = new Intent(LoginActivity.this, RegistarActivity.class);
        startActivity(mainIntent);
    }

    private void getNameAndImage(final String id) {
        firebaseFirestore.collection("Users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                try {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String name = task.getResult().getString("name");
                            String image = task.getResult().getString("image");

                            Globais.user_id = id;
                            Globais.user_name = name;
                            Globais.user_img = image;
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "(FIRESTORE Retrieve ERROR): " + error, Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {


                }
            }

        });
    }
}
