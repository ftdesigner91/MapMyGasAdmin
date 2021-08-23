package com.gmail.mapmygasadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class InviteActivity extends AppCompatActivity {

    private Toolbar nInvite_toolbar;
    private LinearLayout nInvite_parent_layout;
    private EditText nEmail_address_et;
    private EditText nSubject_et;
    private EditText nStation_name_et;
    private EditText nStation_address_et;
    private TextView nCurrent_date_tv;
    private TextView nEmail_temp_tv;
    private Button nSent_btn;

    private SimpleDateFormat sdf;
    private Date date;
    private String dateToday;

    private String stationName, stationAddress, subjectHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        init();
        showToolbar();
        new MyTouchEvent(this).collapseKeyboard(nInvite_parent_layout);
        nSubject_et.setHint(subjectHint);

        setStationName(nStation_name_et, nEmail_temp_tv);
        setStationAddress(nStation_address_et, nEmail_temp_tv);

        nCurrent_date_tv.setText(dateToday);
        nEmail_temp_tv.setText(
                stationName+"\n"+
                stationAddress+"\n\n"+
                dateToday+"\n\n\n\n"+
                EmailTemplate.emailTemplate);

        nSent_btn.setOnClickListener(view -> sendEmailIntent());
    }

    private void init() {
        stationName = "[ insert gasoline name ]";
        stationAddress = "[ insert gasoline address ]";
        subjectHint = "Email subject: Invitation from MapMyGas";

        nInvite_toolbar = findViewById(R.id.invite_toolbar);
        nInvite_parent_layout = findViewById(R.id.inivte_parent_layout);
        nEmail_address_et = findViewById(R.id.email_address_et);
        nSubject_et = findViewById(R.id.subject_et);
        nStation_name_et = findViewById(R.id.station_name_et);
        nStation_address_et = findViewById(R.id.station_address_et);
        nCurrent_date_tv = findViewById(R.id.current_date_tv);
        nEmail_temp_tv = findViewById(R.id.emai_temp_tv);
        nSent_btn = findViewById(R.id.send_btn);

        sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        date = new Date();
        dateToday = sdf.format(date);
    }

    private void sendEmailIntent() {
        String email = String.valueOf(nEmail_address_et.getText());
        String body = String.valueOf(nEmail_temp_tv.getText());

        if (!TextUtils.isEmpty(nEmail_address_et.getText())
        && !TextUtils.isEmpty(nStation_name_et.getText())
        && !TextUtils.isEmpty(nStation_address_et.getText()))
        {
            if (!TextUtils.isEmpty(nSubject_et.getText())){
                String subject = String.valueOf(nSubject_et.getText());
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, body);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});

                startActivity(Intent.createChooser(intent, "Send Email"));
            }else {
                String subject = "Invitation from MapMyGas";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, body);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});

                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        }else {
            if (TextUtils.isEmpty(nStation_address_et.getText())){
                Toast.makeText(this, "Station addres is missing", Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(nStation_name_et.getText())){
                Toast.makeText(this, "Station name is missing", Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(nEmail_address_et.getText())){
                Toast.makeText(this, "Email address is missing", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setStationName(EditText editText, TextView textView) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // real-time result
                stationName = String.valueOf(charSequence);
                textView.setText(
                        stationName+"\n"+
                        stationAddress+"\n\n"+
                        dateToday+"\n\n\n\n"+
                        EmailTemplate.emailTemplate);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }
    private void setStationAddress(EditText editText, TextView textView) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // real-time result
                stationAddress = String.valueOf(charSequence);
                textView.setText(
                        stationName+"\n"+
                        stationAddress+"\n\n"+
                        dateToday+"\n\n\n\n"+
                        EmailTemplate.emailTemplate);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void showToolbar() {
        setSupportActionBar(nInvite_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}