package com.example.lokerid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lokerid.Database.DatabaseInit;
import com.example.lokerid.Fragment.Account.AccountFragment;
import com.example.lokerid.Fragment.Auth.LoginActivity;
import com.example.lokerid.Fragment.Booking.BookingFragment;
import com.example.lokerid.Fragment.History.HistoryFragment;
import com.example.lokerid.Fragment.Home.HomeFragment;
import com.example.lokerid.Notification.App;
import com.example.lokerid.Scan.CaptureAct;
import com.example.lokerid.Service.BackgroundService;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends FragmentActivity {

    private boolean click = false;
    private SpaceNavigationView nav;
    private CountDownTimer countDownTimer;
    private long[] mTimeLeftInMillis, timeInMillis;
    Handler customHandler = new Handler();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private String[] loker, stand;
    int length = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customHandler.removeCallbacks(updateTimerThread);

        startService(new Intent(this, BackgroundService.class));

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        nav = findViewById(R.id.space);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        nav.initWithSaveInstanceState(savedInstanceState);
        nav.addSpaceItem(new SpaceItem("", R.drawable.ic_home_black_24dp));
        nav.addSpaceItem(new SpaceItem("", R.drawable.ic_format_list_bulleted_black_24dp));
        nav.addSpaceItem(new SpaceItem("", R.drawable.ic_history_black_24dp));
        nav.addSpaceItem(new SpaceItem("", R.drawable.ic_person_black_24dp));

        nav.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                final DatabaseInit db = new DatabaseInit();
                db.booking.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (click == true) {
                            int i = 0;
                            boolean check = false;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                i++;
                                FirebaseUser user = db.mAuth.getCurrentUser();
                                if (user.getUid().equals(ds.child("uid").getValue().toString()) && ds.child("status").getValue().toString().equals("Booking")) {
                                    scanCode();
                                    check = true;
                                } else {
                                    check = false;
                                }
                            }
                            if (check == false) {
                                new android.app.AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Warning")
                                        .setMessage("Anda belum booking!")
                                        .setCancelable(false)
                                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        }).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                click = true;
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                int count = getSupportFragmentManager().getBackStackEntryCount();
                if (count > 0) {
                    getSupportFragmentManager().popBackStack();
                }
                Fragment selectedFragment = null;
                switch (itemIndex) {
                    case 0:
                        selectedFragment = new HomeFragment();
                        break;
                    case 1:
                        selectedFragment = new BookingFragment();
                        break;
                    case 2:
                        selectedFragment = new HistoryFragment();
                        break;
                    case 3:
                        selectedFragment = new AccountFragment();
                        break;
                    default:
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            new android.app.AlertDialog.Builder(this)
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
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

    @Override
    protected void onStart() {
        super.onStart();
        customHandler.removeCallbacksAndMessages(updateTimerThread);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                final DatabaseInit db = new DatabaseInit();

                db.booking.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        int hitung = 0;
                        boolean datang = false;
                        String loker = "";

                        if (click == true) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                final FirebaseUser user = db.mAuth.getCurrentUser();

                                if (user.getUid().equals(ds.child("uid").getValue().toString()) && datang == false) {
                                    String[] res = result.getContents().split("\\s+");
                                    final String stand = res[0].toLowerCase() + res[1];

                                    if (stand.equals(ds.child("stand").getValue().toString())) {
                                        if (ds.child("status").getValue().equals("Booking")) {
                                            datang = true;
                                            hitung = 1;
                                            loker = ds.child("loker").getValue().toString();
                                            db.daftar.child(ds.child("stand").getValue().toString()).child("id").setValue(ds.child("loker").getValue().toString());
                                            db.booking.child(ds.getKey()).child("status").setValue("Datang");
                                            Calendar calendar = Calendar.getInstance();
                                            String tss = DateFormat.format("EEE, dd-MM-yyyy HH:mm:ss", calendar.getTime()).toString();
                                            db.booking.child(ds.getKey()).child("time").setValue(tss);
                                            db.stand.child(ds.child("stand").getValue().toString()).child(ds.child("loker").getValue().toString()).child("status").setValue("not available");
                                            db.booking.child(ds.getKey()).child("hour").setValue("1");

                                            db.users.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    final int saldo = Integer.parseInt(dataSnapshot.child("saldo").getValue().toString());

                                                    db.stand.child(stand).child("harga").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            int harga = Integer.parseInt(dataSnapshot.getValue().toString());
                                                            db.users.child(user.getUid()).child("saldo").setValue(saldo - harga);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    } else {
                                        if (datang == false) {
                                            hitung = 2;
                                        } else {
                                            hitung = 1;
                                        }
                                    }
                                }
                            }

                            if (hitung == 2) {
                                builder.setTitle("Warning")
                                        .setMessage("Anda Salah Stand!")
                                        .setCancelable(true);
                            } else if (hitung == 1){
                                builder.setTitle("Success");
                                builder.setMessage("Silahkan Tempel Jari Anda di Sensor Fingerprint untuk Loker " + loker + "!");
                                builder.setCancelable(true);
                            }
                            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    click = false;
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(this, "No Results", Toast.LENGTH_SHORT).show();
                click = false;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            click = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        final DatabaseInit db = new DatabaseInit();
        final FirebaseUser user = db.mAuth.getCurrentUser();

        if (user != null) {
            db.booking.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int i = 0;
                    loker = new String[Long.bitCount(dataSnapshot.getChildrenCount())];
                    stand = new String[Long.bitCount(dataSnapshot.getChildrenCount())];
                    mTimeLeftInMillis = new long[Long.bitCount(dataSnapshot.getChildrenCount())];
                    timeInMillis = new long[Long.bitCount(dataSnapshot.getChildrenCount())];
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child("uid").getValue().toString().equals(user.getUid())) {
                            try {
                                if (ds.child("status").getValue().toString().equals("Datang")) {
                                    length++;
                                    String[] date = ds.child("time").getValue().toString().split("\\s+");
                                    loker[i] = ds.child("loker").getValue().toString();
                                    stand[i] = ds.child("stand").getValue().toString();

                                    Date mDate = simpleDateFormat.parse(date[1] + " " + date[2]);
                                    mTimeLeftInMillis[i] = mDate.getTime();
                                    customHandler.postDelayed(updateTimerThread, 0);
                                    i++;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void sendNotifications(int i, String message) {
        App app = new App(getApplicationContext());
        app.onCreate();
        Intent ActivityIntent = new Intent(this, LoginActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, ActivityIntent, 0);

        Notification mBuilder =
                new NotificationCompat.Builder(this, App.CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Penyewaan Loker " + loker[i] + " di Stand " + stand[i].substring(5))
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .setOngoing(true)
                        .build()
                ;

        NotificationManagerCompat mManager = NotificationManagerCompat.from(this);
        mManager.notify(i, mBuilder);
    }

    Runnable updateTimerThread = new Runnable() {
        int i = 0;
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void run() {
            try {
                ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    cn = am.getRunningTasks(1).get(0).topActivity;
                } else {
                    cn = am.getRunningTasks(1).get(0).topActivity;
                }

                if (cn.toShortString().equals("{com.example.loker/com.example.loker.MainActivity}")) {
                    customHandler.removeCallbacks(updateTimerThread);
                } else {
                    if (i < length) {
                        Calendar calendar = Calendar.getInstance();
                        String tss = android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", calendar.getTime()).toString();
                        Date timeNow = simpleDateFormat.parse(tss);
                        timeInMillis[i] = timeNow.getTime() - mTimeLeftInMillis[i];
                        int secs = (int) (timeInMillis[i] / 1000);
                        int min = secs / 60;
                        int hour = min / 60;
                        min %= 60;
                        sendNotifications(i, String.format("%02d", hour) + ":" + String.format("%02d", min) + ":00");
                        customHandler.postDelayed(updateTimerThread, 60000);
                        i++;
                    } else {
                        i = 0;
                        customHandler.postDelayed(updateTimerThread, 60000);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };
}
