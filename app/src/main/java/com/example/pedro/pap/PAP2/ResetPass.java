package com.example.pedro.pap.PAP2;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pedro.pap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPass extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private EditText emailResetField;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        emailResetField = findViewById(R.id.reset_pass_email);
        progressBar = findViewById(R.id.reset_pass_prog_bar);
    }

    public void onSendEmail(View v) {
        progressBar.setVisibility(View.VISIBLE);
        String email = emailResetField.getText().toString();

        if (!email.isEmpty()) {
            sendEmail(email);
        } else {
            Toast.makeText(this, "TEM DE INSERIR UM EMAIL", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
        }

    }

    private void sendEmail(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ResetPass.this, "EMAIL ENVIADO COM SUCESSO", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ResetPass.this, "EMAIL NÃO ENCONTRADO", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }


}
