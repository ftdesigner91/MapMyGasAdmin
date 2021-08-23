package com.gmail.mapmygasadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private static final String SIGN_IN_D = "sign-in d: ";
    private static final String SIGN_IN_W = "sign-in w: ";
    private static final String FIRE_AUTH_W_GOO = "fire auth w goo";
    private static final int RC_SIGN_IN = 120;

    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private ProgressBar nSignin_progress_bar;
    private SignInButton nG_sign_in;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();
        nG_sign_in.setOnClickListener(view -> signIn());
    }

    private void init(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        nSignin_progress_bar = findViewById(R.id.signin_progress_bar);
        nG_sign_in = findViewById(R.id.g_sign_in);

        firestore = FirebaseFirestore.getInstance();
    }

    private void progressbarVisibility(ProgressBar progressBar){
        if (progressBar.getVisibility() == View.INVISIBLE){ progressBar.setVisibility(View.VISIBLE);
        }else { progressBar.setVisibility(View.INVISIBLE); }
    }

    private void signIn(){
        progressbarVisibility(nSignin_progress_bar);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(SIGN_IN_D, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        addAdminTofirestore(user);
                    } else {
                        progressbarVisibility(nSignin_progress_bar);
                        Log.w(SIGN_IN_W, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }
    private void addAdminTofirestore(FirebaseUser user){
        if (user != null){
            HashMap<String, String> adminsMap = new HashMap<>();
            adminsMap.put("admin_id", user.getUid());
            adminsMap.put("admin_name", user.getDisplayName());
            adminsMap.put("admin_email", user.getEmail());
            adminsMap.put("admin_photo", String.valueOf(user.getPhotoUrl()));

            firestore.collection("admins").document(user.getUid())
                    .set(adminsMap, SetOptions.merge()).addOnCompleteListener(task -> {
                if (task.isComplete()){
                    progressbarVisibility(nSignin_progress_bar);
                    if (task.isSuccessful()){ updateUI(user); }
                }
            });
        }
    }
    private void updateUI(FirebaseUser mUser){
        if (mUser != null){
            Intent mainIntent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
            Toast.makeText(this, "Hello "+getFirstName(Objects
                    .requireNonNull(mUser.getDisplayName())), Toast.LENGTH_SHORT).show();
        }
    }
    private String getFirstName(String fullName){
        String[] firstName = fullName.split(" ", 2);
        //String substring = fullName.substring(0, 0).toUpperCase();
        String cap1stLetter = firstName[0].substring(0, 1).toUpperCase();
        int fisrtNameLength = firstName[0].length();
        String get1stName =firstName[0].substring(1, fisrtNameLength);
        return cap1stLetter+get1stName;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(FIRE_AUTH_W_GOO, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(FIRE_AUTH_W_GOO, "Google sign in failed", e);
            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        updateUI(mUser);
    }
}