package com.example.lokerid.Fragment.Auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lokerid.Database.DatabaseInit;
import com.example.lokerid.MainActivity;
import com.example.lokerid.R;
import com.google.firebase.auth.FirebaseUser;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

public class TutorialActivity extends AppCompatActivity {

    DatabaseInit db = new DatabaseInit();
    FirebaseUser user = db.mAuth.getCurrentUser();

    private int[] img = new int[] {
            R.mipmap.stand, R.mipmap.loker, R.mipmap.history, R.mipmap.account, R.mipmap.edit, R.mipmap.myloker, R.mipmap.permission, R.mipmap.qrscan
    };

    private String[] title = new String[] {
            "Stand List", "Locker List", "History", "Account", "Edit Profile", "My Locker", "Permission for Camera", "QR Scan"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        CarouselView carouselView = findViewById(R.id.carouselView);
        final Button btnContinue = findViewById(R.id.btnContinue);
        final TextView tvSkip = findViewById(R.id.tvSkip);
        final TextView tvTitle = findViewById(R.id.tvTitle);

        btnContinue.setVisibility(View.INVISIBLE);

        carouselView.setPageCount(img.length);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(img[position]);
            }
        });

        carouselView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == img.length - 1) {
                    btnContinue.setVisibility(View.VISIBLE);
                    tvSkip.setVisibility(View.INVISIBLE);
                }
                tvTitle.setText(title[position]);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.users.child(user.getUid()).child("status").removeValue();
                new AlertDialog.Builder(TutorialActivity.this)
                        .setTitle("Konfirmasi Skip Tutorial")
                        .setMessage("Yakin ingin melanjutkan?")
                        .setCancelable(false)
                        .setPositiveButton("Lanjut", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                                startActivity(new Intent(TutorialActivity.this, MainActivity.class));
                                Toast.makeText(TutorialActivity.this, "Tutorial Finished", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Tidak", null).show();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.users.child(user.getUid()).child("status").removeValue();
                finish();
                startActivity(new Intent(TutorialActivity.this, MainActivity.class));
                Toast.makeText(TutorialActivity.this, "Tutorial Finished", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(TutorialActivity.this, GuideActivity.class));
    }
}
