package com.example.pedro.pap.PAP;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pedro.pap.R;


public class DefinicoesActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private Button mudarNomeImagem, verId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definicoes);

        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        mudarNomeImagem = findViewById(R.id.mudar_nome_imagem);
        verId = findViewById(R.id.ver_id_def);


        mudarNomeImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DefinicoesActivity.this, SetupActivity.class);
                startActivity(i);
            }
        });


        verId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DefinicoesActivity.this, "User ID: " + Globais.user_id, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
