package com.example.pedro.pap.PAP2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pedro.pap.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ShowDef2 extends AppCompatActivity {
//    private TextView userName, userEmail;
    private ImageView userImageShow;
    private EditText emailEdit, nameEdit;
    private Button btnEditSave;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_def2);
        getSupportActionBar().setTitle("INFORMAÇÕES PESSOAIS");

//        userName = findViewById(R.id.show_def_2_user_name);
//        userEmail = findViewById(R.id.show_def_2_email_text);
        userImageShow = findViewById(R.id.show_def_2_user_image_show);
        emailEdit = findViewById(R.id.email_edit_show_def);
        nameEdit = findViewById(R.id.name_edit_show_def);
        btnEditSave = findViewById(R.id.show_def_btn_save_changes);

        Picasso.with(mContext)
                .load(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .resize(600, 600)
                .centerCrop()
                .into(userImageShow);

        showUserInfo();
    }

    private void showUserInfo() {
        try {
            emailEdit.setText(Globais2.user_email);
            nameEdit.setText(Globais2.user_name);

            Picasso.with(mContext)
                    .load(Globais2.user_img)
                    .placeholder(R.mipmap.ic_launcher)
                    .resize(1200, 1200)
                    .centerCrop()
                    .into(userImageShow);
        }catch (Exception e1) {
            Toast.makeText(mContext, "ERRO AO CARREGAR INFORMAÇÕES. \nPOR FAVOR, CONTACTE O ADMINISTRADOR", Toast.LENGTH_LONG).show();
        }
    }

    public void editAndSaveInfo(View v) {

        String btnText = btnEditSave.getText().toString();

        if(btnText.equals("EDITAR INFORMAÇÕES")) {
            btnEditSave.setText("GUARDAR INFORMAÇÕES");
            emailEdit.setEnabled(true);
        } else if(btnText.equals("GUARDAR INFORMAÇÕES")) {
            Toast.makeText(mContext, "Entra", Toast.LENGTH_SHORT).show();
            final String emailFiled = emailEdit.getText().toString();

            if (Globais2.user_email.equals(emailFiled)) {
                emailEdit.setEnabled(false);
                btnEditSave.setText("EDITAR INFORMAÇÕES");
                Toast.makeText(mContext, "Entra2", Toast.LENGTH_SHORT).show();
            } else if (!Globais2.user_email.equals(emailFiled) && !emailFiled.isEmpty()){
                FirebaseUser currentUser = mAuth.getCurrentUser();
                currentUser.updateEmail(emailFiled).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, "INFORMAÇÕES GUARDADAS COM SUCESSO", Toast.LENGTH_LONG).show();
                        Globais2.user_email = emailFiled;
                        emailEdit.setEnabled(false);
                        btnEditSave.setText("EDITAR INFORMAÇÕES");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "ERRO GUARDAR INFORMAÇÕES", Toast.LENGTH_LONG).show();
                        emailEdit.setEnabled(false);
                        btnEditSave.setText("EDITAR INFORMAÇÕES");
                    }
                });
            }
        }
    }


//    private void sendToInitPage2() {
//        mDatabaseRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
////                CreateUserClass newUser = dataSnapshot.getValue(CreateUserClass.class);
//
//                try {
//                    CreateUserClass newUser = dataSnapshot.getValue(CreateUserClass.class);
//
//                    if (userID.equals(newUser.getId())) {
//                        Globais2.user_id = userID;
//                        Globais2.user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//                        Globais2.user_name = newUser.getName();
//                        Globais2.user_img = newUser.getImg();
//                    }
//                }catch (Exception e) {
//                    Toast.makeText(LoginUser2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            System.out.println("Main thread Interrupted");
//        }
//
//        progressBar.setVisibility(View.INVISIBLE);
//
//        System.out.println("Main thread exiting.");
//        Intent intent = new Intent(this, InitialPage2.class);
//        startActivity(intent);
//        finish();
//    }
}
