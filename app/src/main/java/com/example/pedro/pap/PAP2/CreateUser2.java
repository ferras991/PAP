package com.example.pedro.pap.PAP2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pedro.pap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateUser2 extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText emailField, passField, checkPassField;
    private Button createBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user2);

        emailField = findViewById(R.id.create_user_2_email);
        passField= findViewById(R.id.create_user_2_senha);
        checkPassField = findViewById(R.id.create_user_2_confirmar_senha);
        createBtn = findViewById(R.id.create_user_2_btn);

        mAuth = FirebaseAuth.getInstance();

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String pass = passField.getText().toString();
                String passCheck = checkPassField.getText().toString();
                checkInfo(email, pass, passCheck);
            }
        });
    }

    private void checkInfo(String email, String pass, String passCheck) {
        if (!email.equals("")) {
            if (!pass.equals("") && !passCheck.equals("")) {
                if (pass.equals(passCheck)) {
                    Toast.makeText(this, "Tudo certo", Toast.LENGTH_SHORT).show();
                    createUser(email, pass, passCheck);
                }else {
                    Toast.makeText(this, "As senhas não correspondem", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Tem de preencher todos os campos", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Tem de preencher todos os campos", Toast.LENGTH_SHORT).show();
        }

    }

    private void createUser(String email, String pass, String passCheck) {
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CreateUser2.this, "User Id: " + mAuth.getUid(), Toast.LENGTH_SHORT).show();

                    Intent setupToolbar = new Intent(CreateUser2.this, SetupUser2.class);
                    startActivity(setupToolbar);
                    finish();
                }else{
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
