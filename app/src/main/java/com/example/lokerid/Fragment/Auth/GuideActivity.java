package com.example.lokerid.Fragment.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lokerid.Database.DatabaseInit;
import com.example.lokerid.MainActivity;
import com.example.lokerid.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseUser;

public class GuideActivity extends AppCompatActivity {

    Animation alfatogo, alfatogotwo, alfatogothree;
    Button btnGuide;
    ImageView ivGuide;
    TextView tvGuideTitle, tvGuideSubTitle, tvTutorial;
    DatabaseInit db = new DatabaseInit();
    FirebaseUser user = db.mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        // Get animation
        alfatogo = AnimationUtils.loadAnimation(this, R.anim.alfatogo);
        alfatogotwo = AnimationUtils.loadAnimation(this, R.anim.alfatogotwo);
        alfatogothree = AnimationUtils.loadAnimation(this, R.anim.alfatogothree);

        tvGuideTitle = findViewById(R.id.tvGuideTitle);
        tvGuideSubTitle = findViewById(R.id.tvGuideSubtitle);
        tvTutorial = findViewById(R.id.tvTutorial);
        ivGuide = findViewById(R.id.ivGuide);
        btnGuide = findViewById(R.id.btnGuide);

        ivGuide.startAnimation(alfatogo);
        tvGuideTitle.startAnimation(alfatogotwo);
        tvGuideSubTitle.startAnimation(alfatogotwo);
        btnGuide.startAnimation(alfatogothree);
        tvTutorial.startAnimation(alfatogothree);

        tvTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.users.child(user.getUid()).child("status").removeValue();
                new AlertDialog.Builder(GuideActivity.this)
                        .setTitle("Konfirmasi Skip Tutorial")
                        .setMessage("Yakin ingin melanjutkan?")
                        .setCancelable(false)
                        .setPositiveButton("Lanjut", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                                startActivity(new Intent(GuideActivity.this, MainActivity.class));
                                Toast.makeText(GuideActivity.this, "Tutorial Finished", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Tidak", null).show();
            }
        });

        btnGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(GuideActivity.this, TutorialActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setTitle("Konfirmasi Keluar Aplikasi")
            .setMessage("Yakin ingin keluar?")
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
