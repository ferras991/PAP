package com.example.pedro.pap.PAP2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.pedro.pap.R;

public class DefUser2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_def_user2);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.def_user_2_inserir:
                goToInsert();
                break;

            case R.id.def_user_2_show_def:
                goToShowDef();
                break;

            case R.id.def_user_2_inserir_soft:
                goToInsertSoft();
                break;

            case R.id.def_user_2_ver_soft:
                goToMeusUploads();
                break;
        }
    }

    private void goToMeusUploads() {
        Intent intent = new Intent(DefUser2.this, VerMeusUploads.class);
        startActivity(intent);
    }

    private void goToInsertSoft() {
        Intent intent = new Intent(DefUser2.this, InsertSoftware2.class);
        startActivity(intent);
    }

    public void goToInsert() {
        Intent intent = new Intent(DefUser2.this, InsertUserInfo2.class);
        startActivity(intent);
    }


    public void goToShowDef() {
        Intent intent = new Intent(DefUser2.this, ShowDef2.class);
        startActivity(intent);
    }

}
