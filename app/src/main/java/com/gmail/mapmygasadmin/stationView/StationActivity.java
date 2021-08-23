package com.gmail.mapmygasadmin.stationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gmail.mapmygasadmin.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import static androidx.recyclerview.widget.RecyclerView.*;

public class StationActivity extends AppCompatActivity {

    private Bundle bundle;
    private String station_id;

    private Toolbar nStation_toolbar;
    private TextView nStation_name_tv;
    private TextView nManager_name_tv;
    private TextView nStation_id_tv;
    private TextView nDays_tv;
    private TextView nHours_tv;
    private RecyclerView nServices_rv;
    private CardView nEmail_btn;
    private CardView nDel_btn;

    private FirebaseFirestore firestore;
    private CollectionReference stationsCollection;
    private CollectionReference myServicesCollection;
    private DocumentReference stationDoc;
    private DocumentReference daysDoc;
    private DocumentReference hoursDoc;

    private FirestoreRecyclerAdapter<ServiceModel, ServiceViewHolder> adapter;
    private FirestoreRecyclerOptions<ServiceModel> options;
    private Query query;

    private GridLayoutManager layoutManager;

    private ArrayList<String> daysCheck;
    private ArrayList<String> hoursNewline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);
        init();
        showToolbar();

        stationDoc.addSnapshotListener((value, error) -> {
            if (Objects.requireNonNull(value).exists()){
                if (value.get("station_name") != null){ nStation_name_tv.setText(String.valueOf(value.get("station_name")));}
                if (value.get("station_manager_name") != null){ nManager_name_tv.setText(String.valueOf(value.get("station_manager_name")));}
                if (value.get("station_id") != null){ nStation_id_tv.setText("ID: "+value.get("station_id"));}
            }
        });

        daysDocument();
        hoursDocument();

        serviceRV();
        nEmail_btn.setOnClickListener(view -> sendEmail());
        nDel_btn.setOnClickListener(view -> delDialog());
    }

    private void delDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.del_dialog, null, false);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        TextView nDel_dialog_msg = view.findViewById(R.id.del_dialog_msg);
        Button nDialog_cancel_btn = view.findViewById(R.id.dialog_cancel_btn);
        Button nDialog_del_btn = view.findViewById(R.id.dialog_del_btn);
        stationDoc.get().addOnCompleteListener(task -> {
            if (task.isComplete()){if (task.isSuccessful()){
                String station_name = String.valueOf(task.getResult().get("station_name"));
                nDel_dialog_msg.setText("Are you sure you want delete "+station_name);
            }else { nDel_dialog_msg.setText("Are you sure you want delete this station");}
            }
        });
        nDialog_cancel_btn.setOnClickListener(view1 -> dialog.dismiss());
        nDialog_del_btn.setOnClickListener(view1 -> delStation(dialog));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void init() {
        bundle = getIntent().getExtras();
        station_id = String.valueOf(bundle.get("station_id"));

        nStation_toolbar = findViewById(R.id.station_toolbar);
        nStation_name_tv = findViewById(R.id.station_name_tv);
        nManager_name_tv = findViewById(R.id.manager_name_tv);
        nStation_id_tv = findViewById(R.id.station_id_tv);
        nDays_tv = findViewById(R.id.days_tv);
        nHours_tv = findViewById(R.id.hours_tv);
        nServices_rv = findViewById(R.id.servcies_rv);
        nEmail_btn = findViewById(R.id.email_btn);
        nDel_btn = findViewById(R.id.del_btn);

        daysCheck = new ArrayList<>();
        hoursNewline = new ArrayList<>();

        firestore = FirebaseFirestore.getInstance();
        stationsCollection = firestore.collection("stations");
        stationDoc = stationsCollection.document(station_id);
        daysDoc = stationDoc.collection("working_days_time").document("days");
        hoursDoc = stationDoc.collection("working_days_time").document("hours");
        myServicesCollection = stationDoc.collection("my_services");

        query = myServicesCollection;
        options = new FirestoreRecyclerOptions.Builder<ServiceModel>()
                .setQuery(query, ServiceModel.class)
                .build();

        layoutManager = new GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false);
    }

    private void showToolbar() {
        setSupportActionBar(nStation_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        stationDoc.addSnapshotListener((value, error) -> {
            if (Objects.requireNonNull(value).exists()){
                if (value.get("station_name") != null){
                    String station_name = String.valueOf(value.get("station_name"));
                    getSupportActionBar().setTitle(station_name);
                }else { getSupportActionBar().setTitle("station"); }
            }
        });
    }

    private void hoursDocument() {
        hoursDoc.addSnapshotListener((value, error) -> {
            if (Objects.requireNonNull(value).exists()){
                if (value.get("start_at") != null && value.get("end_at") != null){
                    try {
                        if (daysCheck.get(0) != null && daysCheck.get(1) != null && daysCheck.get(2) != null
                                && daysCheck.get(3) != null && daysCheck.get(4) != null && daysCheck.get(5) != null
                                && daysCheck.get(6) != null){
                            String start_at = String.valueOf(value.get("start_at"));
                            String end_at = String.valueOf(value.get("end_at"));
                            for (int i = 0; i <= daysCheck.size(); i++){
                                hoursNewline.add(i, start_at.replace("start at ", "")+
                                        " - "+end_at.replace("end at ", "")+"\n\n"); }
                            String a = hoursNewline.get(0)+hoursNewline.get(1)+hoursNewline.get(2)+hoursNewline.get(3)+hoursNewline.get(4)+
                                    hoursNewline.get(5)+hoursNewline.get(6);
                            nHours_tv.setText(a);
                        }
                    }catch (IndexOutOfBoundsException e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void daysDocument() {
        daysDoc.addSnapshotListener((value, error) -> {
           if (Objects.requireNonNull(value).exists()){
               try {
                   if (value.get("Saturday") != null){
                       if (Objects.equals(value.get("Saturday"), "ON")){ daysCheck.add(0,"Saturday\n\n"); }
                       else {daysCheck.add(0,"");}
                   }
                   if (value.get("Sunday") != null){
                       if (Objects.equals(value.get("Sunday"), "ON")){ daysCheck.add(1,"Sunday\n\n"); }
                       else {daysCheck.add(1,"");}
                   }
                   if (value.get("Monday") != null){
                       if (Objects.equals(value.get("Monday"), "ON")){ daysCheck.add(2,"Monday\n\n"); }
                       else {daysCheck.add(2,"");}
                   }
                   if (value.get("Tuesday") != null){
                       if (Objects.equals(value.get("Tuesday"), "ON")){ daysCheck.add(3,"Tuesday\n\n"); }
                       else {daysCheck.add(3,"");}
                   }
                   if (value.get("Wednesday") != null){
                       if (Objects.equals(value.get("Wednesday"), "ON")){ daysCheck.add(4,"Wednesday\n\n"); }
                       else {daysCheck.add(4,"");}
                   }
                   if (value.get("Thursday") != null){
                       if (Objects.equals(value.get("Thursday"), "ON")){ daysCheck.add(5,"Thursday\n\n"); }
                       else {daysCheck.add(5,"");}
                   }
                   if (value.get("Friday") != null){
                       if (Objects.equals(value.get("Friday"), "ON")){ daysCheck.add(6,"Friday\n\n"); }
                       else {daysCheck.add(6,"");}
                   }
                   if (daysCheck.get(0) != null && daysCheck.get(1) != null && daysCheck.get(2) != null
                           && daysCheck.get(3) != null && daysCheck.get(4) != null && daysCheck.get(5) != null
                            && daysCheck.get(6) != null){

                       String s = daysCheck.get(0)+daysCheck.get(1)+daysCheck.get(2)+daysCheck.get(3)
                               +daysCheck.get(4)+daysCheck.get(5)+daysCheck.get(6);
                       nDays_tv.setText(s);
                   }
               }catch (NullPointerException e){ e.printStackTrace(); }
           }
        });
    }

    private void serviceRV() {
        adapter = new FirestoreRecyclerAdapter<ServiceModel, ServiceViewHolder>(options) {
            @NonNull
            @Override
            public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.srevice_item, parent, false);
                return new ServiceViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ServiceViewHolder holder, int position, @NonNull ServiceModel model) {
                Picasso.get().load(model.getService_image()).placeholder(R.drawable.ic_logo_logo_large2_place_holder_copy)
                        .into(holder.nVh_service_iv);
                holder.nVh_title_tv.setText(model.getService_title());
            }
        };
        nServices_rv.setHasFixedSize(true);
        nServices_rv.setLayoutManager(layoutManager);
        nServices_rv.setAdapter(adapter);
    }

    private static class ServiceViewHolder extends ViewHolder {


        private final ImageView nVh_service_iv;
        private final TextView nVh_title_tv;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            nVh_service_iv = itemView.findViewById(R.id.vh_service_iv);
            nVh_title_tv = itemView.findViewById(R.id.vh_service_tv);
        }
    }

    private void delStation(AlertDialog dialog) {
        stationDoc.get().addOnCompleteListener(task -> {
            if (task.isComplete()){if (task.isSuccessful()){
                stationDoc.delete().addOnCompleteListener(task1 -> {
                    if (task1.isComplete()){if (task1.isSuccessful()){
                        String station_name = String.valueOf(task.getResult().get("station_name"));
                        Toast.makeText(this, station_name+" has deleted successfully", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }else {
                        Toast.makeText(this, "could not delete! please try again later", Toast.LENGTH_LONG).show();
                    }
                    }
                });
            }else {
                String station_name = String.valueOf(task.getResult().get("station_name"));
                Toast.makeText(this,
                        "getting "+station_name+" information\ntry again in a minute",
                        Toast.LENGTH_LONG).show();
            }
            }
        });
    }

    private void sendEmail() {
        stationDoc.get().addOnCompleteListener(task -> {
            if (task.isComplete()){if (task.isSuccessful()){
                String emailAddress = String.valueOf(task.getResult().get("station_manager_email"));

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});

                startActivity(Intent.createChooser(intent, "Send Email"));
            }else {
                Toast.makeText(this,
                        "getting manager email, please try again in a minute",
                        Toast.LENGTH_LONG).show();
            }
            }
        });
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