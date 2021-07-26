package com.example.lokerid.Fragment.Auth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lokerid.Database.DatabaseInit;
import com.example.lokerid.MainActivity;
import com.example.lokerid.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseInit db = new DatabaseInit();
    private Button btnLogin;

    //Inisialisasi Google Sign In
    private GoogleSignInClient mGoogleSignClient;
    private int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Inisialisasi variabel button
        btnLogin = findViewById(R.id.btnLogin);

        //set on click untuk btn
        btnLogin.setOnClickListener(LoginActivity.this);

        final FirebaseUser user = db.mAuth.getCurrentUser();
        if (user != null) {
            ProgressDialog.show(LoginActivity.this, "Loading", "Harap menunggu...");
            db.users.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("status")) {
                        finish();
                        startActivity(new Intent(LoginActivity.this, GuideActivity.class));
                    } else {
                        finish();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        setCount(1);
    }

    @Override
    public void onClick(View v) {
        ProgressDialog.show(this, "Loading", "Harap menunggu...");
        GoogleSignInOptions opt = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignClient = GoogleSignIn.getClient(this, opt);
        login();
    }

    private void login() {
        Intent signInIntent = mGoogleSignClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            FirebaseGoogleAuth(acc);
        } catch (ApiException e) {
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        if (acct != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            db.mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        final FirebaseUser user = db.mAuth.getCurrentUser();

                        db.users.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                db.users.child(user.getUid()).child("uid").setValue(user.getUid());
                                db.users.child(user.getUid()).child("nama").setValue(user.getDisplayName());
                                db.users.child(user.getUid()).child("email").setValue(user.getEmail());
                                db.users.child(user.getUid()).child("profile").setValue(user.getPhotoUrl().toString());
                                db.users.child(user.getUid()).child("saldo").setValue(100000);

                                if (!dataSnapshot.hasChild(user.getUid())) {
                                    db.users.child(user.getUid()).child("status").setValue("Baru");
                                    finish();
                                    startActivity(new Intent(LoginActivity.this, GuideActivity.class));
                                } else if(dataSnapshot.child(user.getUid()).hasChild("status")) {
                                    finish();
                                    startActivity(new Intent(LoginActivity.this, GuideActivity.class));
                                } else {
                                    finish();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            finish();
            startActivity(getIntent());
            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Keluar Aplikasi")
                .setMessage("Yakin ingin keluar aplikasi?")
                .setCancelable(false)
                .setPositiveButton("Keluar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("Batal", null).show();
    }
}
