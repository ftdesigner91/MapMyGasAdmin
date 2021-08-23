package com.gmail.mapmygasadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ViewAdminsActivity extends AppCompatActivity {

    private Toolbar nAdmin_toolbar;
    private RecyclerView nAdmins_rv;

    private FirebaseFirestore firestore;
    private CollectionReference adminsCollection;
    private FirestoreRecyclerOptions<AdminsModel> options;
    private FirestoreRecyclerAdapter<AdminsModel, AdminViewHolder> adapter;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_admins);
        init();
        showToolbar();

        adapter = new FirestoreRecyclerAdapter<AdminsModel, AdminViewHolder>(options) {
            @NonNull
            @Override
            public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item, parent, false);
                return new AdminViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AdminViewHolder holder, int position, @NonNull AdminsModel model) {
                holder.nAdmin_id_tv.setText("ID: "+model.getAdmin_id());
                holder.nAdmin_name_tv.setText(cap1stLetter(model.getAdmin_name()));
                holder.nAdmin_email_tv.setText(model.getAdmin_email());

                holder.nAdmin_email_btn.setOnClickListener(view -> sendEmail(model.getAdmin_email()));
            }
        };
        nAdmins_rv.setHasFixedSize(true);
        nAdmins_rv.setLayoutManager(new LinearLayoutManager(this));
        nAdmins_rv.setAdapter(adapter);
    }

    private void init() {
        nAdmin_toolbar = findViewById(R.id.admins_toolbar);
        nAdmins_rv = findViewById(R.id.admins_rv);

        firestore = FirebaseFirestore.getInstance();
        adminsCollection = firestore.collection("admins");
        query = adminsCollection;
        options = new FirestoreRecyclerOptions.Builder<AdminsModel>()
                .setQuery(query, AdminsModel.class)
                .build();
    }

    private void showToolbar() {
        setSupportActionBar(nAdmin_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private String cap1stLetter(String fullName){
        String[] lowCaps = fullName.split(" ", 3);

        int fLength = lowCaps[0].length();
        String firstNameInit = lowCaps[0].substring(0, 1).toUpperCase();
        String firstName = lowCaps[0].substring(1, fLength);

        int sLength = lowCaps[1].length();
        String secondNameInit = lowCaps[1].substring(0, 1).toUpperCase();
        String secondName = lowCaps[1].substring(1, sLength);

        return firstNameInit+firstName+" "+secondNameInit+secondName;
    }

    private static class AdminViewHolder extends RecyclerView.ViewHolder {

        private final TextView nAdmin_name_tv;
        private final TextView nAdmin_email_tv;
        private final TextView nAdmin_id_tv;
        private final ImageButton nAdmin_email_btn;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            nAdmin_name_tv = itemView.findViewById(R.id.admin_name_tv);
            nAdmin_email_tv = itemView.findViewById(R.id.admin_email_tv);
            nAdmin_id_tv = itemView.findViewById(R.id.admin_id_tv);
            nAdmin_email_btn = itemView.findViewById(R.id.admin_email_btn);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void sendEmail(String admin_email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{admin_email});

        startActivity(Intent.createChooser(intent, "Send Email"));
    }
}
