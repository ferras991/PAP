package com.example.pedro.pap.PAP2;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.pedro.pap.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;

public class DefUser2 extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_def_user2);
        getSupportActionBar().setTitle("DEFINIÇÕES");

        try {

        }catch (Exception e1) {
            Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.def_user_2_show_def:
                goToShowDef();
                break;

            case R.id.def_user_2_inserir_soft:
                goToInsertSoft();
                break;

            case R.id.def_user_2_ver_soft:
                goToMeusUploads();
                break;

            case R.id.def_user_2_share:
                gotoShare();
                break;

            case R.id.def_user_2_change_pass:
                goToChangePass();
                break;

            case R.id.def_user_2_delete_user:
                goToDeleteUser();
                break;
        }
    }

    private void goToChangePass() {
        Intent i = new Intent(DefUser2.this, Change_Pass.class);
        startActivity(i);
    }

    private void goToDeleteUser() {
        new AlertDialog.Builder(DefUser2.this)
                .setTitle("ELIMINAÇÃO DE CONTA")
                .setMessage("TEM A CERTEZA QUE QUER ELIMINAR A SUA CONTA? \nAPÓS A ELIMINAÇÃO DA MESMA OS SEUS DADOS SERÃO PERDIDOS.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();

                        currentUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DefUser2.this, "UTILIZADOR ELIMINADO COM SUCESSO", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(DefUser2.this, MainPage2.class);
                                startActivity(i);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DefUser2.this, "ERRO AO ELIMINAR UTILIZADOR", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void gotoShare() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Instale o 'SOFTVIRTUAL'");
        share.putExtra(Intent.EXTRA_TEXT, Globais2.apk_link);
        startActivity(Intent.createChooser(share, "PARTILHE O LINK!"));
    }

    private void goToMeusUploads() {
        Intent intent = new Intent(DefUser2.this, VerMeusUploads.class);
        startActivity(intent);
    }

    private void goToInsertSoft() {
        Intent intent = new Intent(DefUser2.this, InsertSoftware2.class);
        startActivity(intent);
    }

    public void goToShowDef() {
        Intent intent = new Intent(DefUser2.this, ShowDef2.class);
        startActivity(intent);
    }

}
