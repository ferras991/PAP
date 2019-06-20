package com.example.pedro.pap.PAP2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedro.pap.Adapters.CreateUser2Upload;
import com.example.pedro.pap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginUser2 extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;

    private EditText emailField, passField;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView passReset;

    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user2);

        emailField = findViewById(R.id.login_user_2_email);
        passField = findViewById(R.id.login_user_2_senha);
        btnLogin = findViewById(R.id.login_user_2_btn);
        progressBar = findViewById(R.id.login_user_2_prog_bar);
        passReset = findViewById(R.id.login_user_2_pass_reset);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");

        passReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginUser2.this, ResetPass.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email = emailField.getText().toString();
                String pass = passField.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){
//                    loginProgress.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        userID = mAuth.getUid();
                                        sendToInitPage2();
                                    } else {
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(LoginUser2.this,"Error: " + errorMessage, Toast.LENGTH_LONG).show();
                                    }
//                                    loginProgress.setVisibility(View.INVISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }else{
                    Toast.makeText(LoginUser2.this,"Tem que preencher todos os campos", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void sendToInitPage2() {
        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                CreateUser2Upload newUser = dataSnapshot.getValue(CreateUser2Upload.class);

                try {
                    CreateUser2Upload newUser = dataSnapshot.getValue(CreateUser2Upload.class);

                    if (userID.equals(newUser.getId())) {
                        Globais2.user_id = userID;
                        Globais2.user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        Globais2.user_name = newUser.getName();
                        Globais2.user_img = newUser.getImg();
                    }
                }catch (Exception e) {
                    Toast.makeText(LoginUser2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Main thread Interrupted");
        }

        progressBar.setVisibility(View.INVISIBLE);

        System.out.println("Main thread exiting.");
        Intent intent = new Intent(this, InitialPage2.class);
        startActivity(intent);
        finish();
    }

}
