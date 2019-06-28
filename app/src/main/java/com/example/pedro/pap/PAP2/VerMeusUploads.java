package com.example.pedro.pap.PAP2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedro.pap.CLASSES.CreateProjectClass;
import com.example.pedro.pap.Adapters.SoftwareAdapter;
import com.example.pedro.pap.Comentarios.SeeProjectsComments;
import com.example.pedro.pap.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class VerMeusUploads extends AppCompatActivity {

    private DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("apk");
    private DatabaseReference ref;

    private String firebaseID;
    private String apkId;

    ArrayList<CreateProjectClass> list;

    RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvImageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_meus_uploads);
        getSupportActionBar().setTitle("MEUS PROJETOS");

        ref = FirebaseDatabase.getInstance().getReference().child("apk");

        recyclerView = findViewById(R.id.ver_meus_uploads_rv);
        progressBar = findViewById(R.id.ver_meus_uploads_2_progress_bar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meus_uploads_menu, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.meus_uploads_search));
        searchView.setQueryHint("PESQUISAR...");
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                progressBar.setVisibility(View.INVISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                progressBar.setVisibility(View.INVISIBLE);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.meus_uploads_search:
                break;

            default:
                return false;
        }
        return true;
    }

    @Override
    protected void onStart () {
        super.onStart();

        if (ref != null) {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if(list != null) { list.clear(); }

                        if (dataSnapshot.exists()) {
                            list = new ArrayList<>();

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                try{
                                    firebaseID = ds.child("userId").getValue().toString();

                                    if(firebaseID.equals(Globais2.user_id)) {
                                        list.add(ds.getValue(CreateProjectClass.class));
                                    }
                                }catch (Exception e){
                                    Toast.makeText(VerMeusUploads.this, e.getMessage() , Toast.LENGTH_SHORT).show();
                                }
                            }

                            if (list.isEmpty()){
                                Toast.makeText(VerMeusUploads.this, "NÃO DEU UPLOAD DE NENHUM FICHEIRO!!!!!", Toast.LENGTH_SHORT).show();
                            }else{
                                SoftwareAdapter adapterClass = new SoftwareAdapter(VerMeusUploads.this, "myShow", list);
                                recyclerView.setAdapter(adapterClass);
                            }
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }catch (Exception e) {
                        Toast.makeText(VerMeusUploads.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(VerMeusUploads.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }else{
            Toast.makeText(this, "Não existe", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void search(String str) {
        ArrayList<CreateProjectClass> myList = new ArrayList<>();

        for (CreateProjectClass object : list) {
            if(object.getName().toLowerCase().contains(str.toLowerCase())) {
                myList.add(object);
            }
        }

        SoftwareAdapter adapterClass = new SoftwareAdapter(VerMeusUploads.this, "myShow", myList);
        recyclerView.setAdapter(adapterClass);
    }

    public void deleteSoftware(View view) {
        try{
            tvImageName = findViewById(R.id.show_card_holder_softName);

            RelativeLayout vwParentRow = (RelativeLayout)view.getParent();
            TextView child = (TextView)vwParentRow.getChildAt(0);

            getId(child.getText().toString());

            try{
                DatabaseReference delApk = FirebaseDatabase.getInstance().getReference("apk").child(apkId);
                delApk.removeValue();

                Toast.makeText(this, "PROJETO ELIMINADO COM SUCESSO", Toast.LENGTH_SHORT).show();
            }catch (Exception e) {
                Toast.makeText(this, "ERRO AO ELIMINAR O PROJETO", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e1) {
            Toast.makeText(VerMeusUploads.this, e1.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getId(final String apkName) {

        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    CreateProjectClass apk = dataSnapshot.getValue(CreateProjectClass.class);

                    if (apkName.equals(apk.getName())) {
                        Globais2.apkId = apk.getId();
                    }

                }catch (Exception e) {
                    Toast.makeText(VerMeusUploads.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
    }

    public void goToProjectComments(View view) {
        try{
            tvImageName = findViewById(R.id.show_card_holder_softName);

            RelativeLayout vwParentRow = (RelativeLayout)view.getParent();
            TextView child = (TextView)vwParentRow.getChildAt(0);

            getId(child.getText().toString());

            try{
                Intent i = new Intent(VerMeusUploads.this, SeeProjectsComments.class);
                startActivity(i);

            }catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e1) {
            Toast.makeText(VerMeusUploads.this, e1.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
