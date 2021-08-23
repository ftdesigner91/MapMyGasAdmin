package com.gmail.mapmygasadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.mapmygasadmin.stationView.ViewStationsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Toolbar nMain_toolbar;
    private TextView nAdmin_name_tv;
    private LinearLayout nInvite_btn;
    private CardView nView_stations_btn;
    private CardView nViewAdmins_btn;
    private TextView nAdmins_ctr_tv;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseFirestore firestore;
    private DocumentReference adminsDoc;
    private CollectionReference adminsCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        showToolbar();

        getAdminName();
        getNumberOfAdmins();

        nInvite_btn.setOnClickListener(view -> openInviteActivity());
        nView_stations_btn.setOnClickListener(view -> openStationsIntent());
        nViewAdmins_btn.setOnClickListener(view -> openAdminsIntent());
        updatedUI(mUser);
    }

    private void openAdminsIntent() {
        Intent adminsIntent = new Intent(MainActivity.this, ViewAdminsActivity.class);
        startActivity(adminsIntent);
    }

    private void openStationsIntent() {
        Intent stationsIntent = new Intent(MainActivity.this, ViewStationsActivity.class);
        startActivity(stationsIntent);
    }


    private void init() {
        nMain_toolbar = findViewById(R.id.main_toolbar);
        nAdmin_name_tv = findViewById(R.id.admin_name_tv);
        nInvite_btn = findViewById(R.id.invite_btn);
        nView_stations_btn = findViewById(R.id.view_stations_btn);
        nViewAdmins_btn = findViewById(R.id.view_admins_btn);
        nAdmins_ctr_tv = findViewById(R.id.admins_ctr_tv);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        firestore = FirebaseFirestore.getInstance();
        if (mUser != null)
        { adminsDoc = firestore.collection("admins").document(mUser.getUid()); }
        adminsCollection = firestore.collection("admins");
    }

    private void showToolbar() {
        setSupportActionBar(nMain_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name)+" Panel");
    }

    private void openInviteActivity() {
        Intent inviteActivity = new Intent(MainActivity.this, InviteActivity.class);
        startActivity(inviteActivity);
    }

    private void getAdminName() {
        adminsDoc.addSnapshotListener((value, error) -> {
            if (Objects.requireNonNull(value).exists()){
                if (value.get("admin_name") != null){
                    String admin_name = String.valueOf(value.get("admin_name"));
                    nAdmin_name_tv.setText(getFirstName(admin_name));
                }
            }
        });
    }
    private void getNumberOfAdmins(){
        adminsCollection.get().addOnCompleteListener(task -> {
            if (task.isComplete()){if (task.isSuccessful()){
                int size = task.getResult().size();
                if (size <= 1){ nAdmins_ctr_tv.setText("there is only one admin, you"); }
                if (size > 1){ nAdmins_ctr_tv.setText("there are ("+size+") including you"); }

            }}
        });
    }
    private String getFirstName(String fullName){
        String[] firstName = fullName.split(" ", 2);
        String cap1stLetter = firstName[0].substring(0, 1).toUpperCase();
        int firstNameLength = firstName[0].length();
        String get1stName = firstName[0].substring(1, firstNameLength);
        return cap1stLetter+get1stName;
    }
    private void updatedUI(FirebaseUser mUser) {
        if (mUser == null){
            Intent signInIntent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(signInIntent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
        updatedUI(mUser);
    }
}