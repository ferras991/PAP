package com.example.pedro.pap.PAP2;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pedro.pap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePass extends AppCompatActivity {
    private Button saveNewPass;
    private ProgressBar progressBar;
    private EditText currentPass, newPass, confirmationPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__pass);
        getSupportActionBar().setTitle("MUDAR SENHA");

        progressBar = findViewById(R.id.change_pass_prog_bar);
        saveNewPass = findViewById(R.id.change_pass_update_pass);
        currentPass = findViewById(R.id.change_pass_current_pass);
        newPass = findViewById(R.id.change_pass_new_pass);
        confirmationPass = findViewById(R.id.change_pass_confirmation_pass);
    }

    public void onSaveClick(View v) {
        progressBar.setVisibility(View.VISIBLE);

        String curPass = currentPass.getText().toString();
        String pass = newPass.getText().toString();
        String confPass = confirmationPass.getText().toString();

        if (!curPass.isEmpty() && !pass.isEmpty() && !confPass.isEmpty()) {
            if(pass.equals(confPass)) {
                try{
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final String email = user.getEmail();
                    AuthCredential credential = EmailAuthProvider.getCredential(email,curPass);

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                user.updatePassword(newPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(!task.isSuccessful()){
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(ChangePass.this, "VERIFIQUE SE AS SENHAS TÊM, PELO MENOS, 6 CARACTERES", Toast.LENGTH_SHORT).show();
                                        }else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(ChangePass.this, "SENHA MUDADA COM SUCESSO", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });
                            }else {
                                Toast.makeText(ChangePass.this, "VERIFIQUE A SENHA ATUAL", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            } else {
                Toast.makeText(this, "AS DUAS SENHAS NÃO CORRESPONDEM", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        } else {
            Toast.makeText(this, "TEM DE PREENCHER TODOS OS CAMPOS", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
