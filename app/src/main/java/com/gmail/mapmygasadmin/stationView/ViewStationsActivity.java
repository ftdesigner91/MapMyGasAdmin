package com.gmail.mapmygasadmin.stationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gmail.mapmygasadmin.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ViewStationsActivity extends AppCompatActivity {

    private Toolbar nStations_toolbar;
    private RecyclerView nStations_rv;

    private FirebaseFirestore firestore;
    private CollectionReference stationsCollection;
    private FirestoreRecyclerOptions<StationModel> options;
    private FirestoreRecyclerAdapter<StationModel, StationViewHolder> adapter;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stations);
        init();
        showToolbar();

        adapter = new FirestoreRecyclerAdapter<StationModel, StationViewHolder>(options) {
            @NonNull
            @Override
            public StationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_item, parent, false);
                return new StationViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull StationViewHolder holder, int position, @NonNull StationModel model) {
                holder.nVstation_iv.setImageResource(R.drawable.ic_logo_logo_large_no_txt);
                holder.nVstation_manager_name_tv.setText(firstSecondName(model.getStation_manager_name()));
                holder.nVstation_name_tv.setText(model.getStation_name());
                holder.nVstation_joind_at_tv.setText("joined: "+splitter(model.getJoined_at()));
                holder.nVstation_view_btn.setOnClickListener(view -> openStationActivity(model.getStation_id()));
            }
        };
        nStations_rv.setHasFixedSize(true);
        nStations_rv.setLayoutManager(new LinearLayoutManager(this));
        nStations_rv.setAdapter(adapter);
    }
    private void init() {
        nStations_toolbar = findViewById(R.id.stations_toolbar);
        nStations_rv = findViewById(R.id.stations_rv);

        firestore = FirebaseFirestore.getInstance();
        stationsCollection = firestore.collection("stations");
        query = stationsCollection;
        options = new FirestoreRecyclerOptions.Builder<StationModel>()
                .setQuery(query, StationModel.class)
                .build();
    }
    private void showToolbar() {
        setSupportActionBar(nStations_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private String splitter(String timeline){
        String[] split = timeline.split(" ", 2);
        return split[1];
    }
    private String firstSecondName(String fullName){
        String[] firstName = fullName.split(" ", 2);
        String cap1stLetter = firstName[0].substring(0, 1).toUpperCase();
        int firstNameLength = firstName[0].length();
        String get1stName = firstName[0].substring(1, firstNameLength);
        return cap1stLetter+get1stName;
    }

    private void openStationActivity(String station_id) {
        Intent stationIntent = new Intent(ViewStationsActivity.this, StationActivity.class);
        stationIntent.putExtra("station_id", station_id);
        startActivity(stationIntent);
    }

    private static class StationViewHolder extends RecyclerView.ViewHolder {

        private final ImageView nVstation_iv;
        private final TextView nVstation_manager_name_tv;
        private final TextView nVstation_name_tv;
        private final TextView nVstation_joind_at_tv;
        private final TextView nVstation_view_btn;

        public StationViewHolder(@NonNull View itemView) {
            super(itemView);
            nVstation_iv = itemView.findViewById(R.id.vstation_iv);
            nVstation_manager_name_tv = itemView.findViewById(R.id.vstation_manager_name_tv);
            nVstation_name_tv = itemView.findViewById(R.id.vstation_namet_tv);
            nVstation_joind_at_tv = itemView.findViewById(R.id.vstation_joind_at_tv);
            nVstation_view_btn = itemView.findViewById(R.id.vstation_view_btn);
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
}