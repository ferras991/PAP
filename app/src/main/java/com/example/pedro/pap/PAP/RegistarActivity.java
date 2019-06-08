package com.example.pedro.pap.PAP;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DatabaseReference;

public class RegistarActivity extends AppCompatActivity {

    private Button regBtn;
    private ProgressBar regProgress;
    private EditText emailField, senha, confirmSenha;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabaseRef;
    //public static final String FB_DATABASE_PATH = "apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar);

        mAuth = FirebaseAuth.getInstance();
        //mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

        regBtn = findViewById(R.id.reg_btn);
        regProgress = findViewById(R.id.reg_progress);
        emailField = findViewById(R.id.reg_email_text);
        senha = findViewById(R.id.reg_senha_text);
        confirmSenha = findViewById(R.id.reg_confirm_senha_text);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String pass = senha.getText().toString();
                String confirm_pass = confirmSenha.getText().toString();

                regBtn.setEnabled(false);
                emailField.setEnabled(false);
                senha.setEnabled(false);
                confirmSenha.setEnabled(false);

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirm_pass)) {
                    if(pass.equals(confirm_pass)){
                        regProgress.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Globais.user_id = mAuth.getUid();

                                    Intent setupToolbar = new Intent(RegistarActivity.this,SetupActivity.class);
                                    startActivity(setupToolbar);
                                    finish();
                                }else{
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(getApplicationContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();

                                    regBtn.setEnabled(true);
                                    emailField.setEnabled(true);
                                    senha.setEnabled(true);
                                    confirmSenha.setEnabled(true);
                                }

                                regProgress.setVisibility(View.INVISIBLE);
                            }
                        });
                    }else{
                        Toast.makeText(RegistarActivity.this, "As senhas não correspondem", Toast.LENGTH_LONG).show();
                    }
                }else{
                    if (emailField.getText().toString().isEmpty()) {
                        emailField.setBackgroundResource(R.drawable.txt_rounded_border_error);
                    }

                    if (senha.getText().toString().isEmpty()) {
                        senha.setBackgroundResource(R.drawable.txt_rounded_border_error);
                    }

                    if (confirmSenha.getText().toString().isEmpty()) {
                        confirmSenha.setBackgroundResource(R.drawable.txt_rounded_border_error);
                    }



                }

                regBtn.setEnabled(true);
                emailField.setEnabled(true);
                senha.setEnabled(true);
                confirmSenha.setEnabled(true);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegistarActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }




    /*private void storeFireStore(@NonNull Task<UploadTask.TaskSnapshot> task, final StorageReference image_path) {
        if(image_path != null){
            image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    caminho = uri.toString();

                    Toast.makeText(InserirSoftware.this, caminho, Toast.LENGTH_LONG).show();

                    //APKUpload imageUpload = new APKUpload(fileName.getText().toString(), caminho);

                    String uploadId = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue(new UserAdd(fileName.getText().toString(), caminho));
                }
            });
        }

    }*/

}
