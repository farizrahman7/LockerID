package com.example.lokerid.Service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.lokerid.Database.DatabaseInit;
import com.example.lokerid.Fragment.Auth.LoginActivity;
import com.example.lokerid.Notification.App;
import com.example.lokerid.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BackgroundService extends Service {
    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    int hitungBooking = 0;
    int hitungDanger = 0;
    int array = 0;
    boolean notifDanger = false;
    private String loker[] = new String[99];
    private String stand[] = new String[99];

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                changeHour();
                checkDanger();
                checkBooking();
                handler.postDelayed(runnable, 60000);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
    }

    @Override
    public void onStart(Intent intent, int startid) {
        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        for (int i = 0; i <= 50; i++) {
            mNotificationManager.cancel(i);
        }
    }

    private void changeHour() {
        final DatabaseInit db = new DatabaseInit();
        final FirebaseUser user = db.mAuth.getCurrentUser();
        if (user != null) {
            db.booking.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child("uid").getValue().toString().equals(user.getUid()) && ds.child("status").getValue().toString().equals("Datang")) {
                            try {
                                String[] date = ds.child("time").getValue().toString().split("\\s+");
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                Calendar calendar = Calendar.getInstance();
                                String tss = android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", calendar.getTime()).toString();

                                Date mDate = simpleDateFormat.parse(date[1] + " " + date[2]);
                                Date timeNow = simpleDateFormat.parse(tss);

                                int hour = Integer.parseInt(ds.child("hour").getValue().toString());

                                long mTimeLeftInMillis = mDate.getTime() + (hour * 60 * 60000) - timeNow.getTime();

                                if (mTimeLeftInMillis <= 0) {
                                    final String stand = ds.child("stand").getValue().toString();
                                    final String hour1 = Integer.toString(hour + 1);
                                    db.booking.child(ds.getKey()).child("hour").setValue(hour1);
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

    private void checkDanger() {
        final DatabaseInit db = new DatabaseInit();
        final FirebaseUser user = db.mAuth.getCurrentUser();
        if (user != null) {
            db.booking.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child("uid").getValue().toString().equals(user.getUid()) && ds.child("status").getValue().toString().equals("Datang")) {
                            db.stand.child(ds.child("stand").getValue().toString())
                                    .child(ds.child("loker").getValue().toString())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.child("keamanan").getValue().toString().equals("danger")) {
                                                if (array == 0) {
                                                    loker[array] = ds.child("loker").getValue().toString();
                                                    stand[array] = ds.child("stand").getValue().toString();
                                                    array++;
                                                    notifDanger = false;
                                                } else {
                                                    boolean sama = false;
                                                    for (int i = 1; i <= array; i++) {
                                                        if (!(loker[i - 1].equals(ds.child("loker").getValue().toString())
                                                                && stand[i - 1].equals(ds.child("stand").getValue().toString()))) {
                                                            if (sama == false) {
                                                                sama = false;
                                                            } else {
                                                                sama = true;
                                                            }
                                                        } else {
                                                            sama = true;
                                                        }
                                                    }
                                                    if (sama == false) {
                                                        hitungDanger++;
                                                        notifDanger = false;
                                                        loker[array] = ds.child("loker").getValue().toString();
                                                        stand[array] = ds.child("stand").getValue().toString();
                                                        array++;
                                                    }
                                                }

                                                if (notifDanger == false) {
                                                    for (int a = 0; a < array; a++) {
                                                        sendNotifications(10 + hitungDanger, "Locker Danger", "Loker " +
                                                                loker[a] + " di Stand " +
                                                                stand[a].substring(5) +
                                                                " dalam keadaan bahaya! Segera cek loker anda!");
                                                    }
                                                    notifDanger = true;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void checkBooking() {
        final DatabaseInit db = new DatabaseInit();
        final FirebaseUser user = db.mAuth.getCurrentUser();
        if (user != null) {
            db.booking.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child("uid").getValue().toString().equals(user.getUid()) && ds.child("status").getValue().toString().equals("Booking")) {
                            try {
                                String[] date = ds.child("time").getValue().toString().split("\\s+");
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                Calendar calendar = Calendar.getInstance();
                                String tss = android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", calendar.getTime()).toString();

                                Date mDate = simpleDateFormat.parse(date[1] + " " + date[2]);
                                Date timeNow = simpleDateFormat.parse(tss);

                                long mTimeLeftInMillis = mDate.getTime() + (30 * 60000) - timeNow.getTime();

                                String stand = "Stand " + ds.child("stand").getValue().toString().substring(5);
                                String loker = "Loker " + ds.child("loker").getValue().toString();
                                hitungBooking++;

                                int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
                                if (minutes == 15) {
                                    sendNotifications(20 + hitungBooking, "Konfirmasi Booking",
                                            "Harap Konfirmasi Kedatangan Anda di " + stand + " dan " + loker);
                                } else if (mTimeLeftInMillis <= 0 && minutes == 0) {
                                    sendNotifications(20 + hitungBooking, "Waktu Konfirmasi Booking Habis",
                                            "Waktu Konfirmasi Booking Anda di " + stand + " dan " + loker + " Sudah Habis! Harap Booking Kembali!");
                                    db.booking.child(ds.getKey()).removeValue();
                                    db.stand.child(ds.child("stand").getValue().toString()).child(ds.child("loker").getValue().toString()).child("status").setValue("available");
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

    private void sendNotifications(int id, String title, String message) {
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

        App app = new App(getApplicationContext());
        app.onCreate();
        Intent ActivityIntent = new Intent(this, LoginActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, ActivityIntent, 0);

        NotificationCompat.Builder mBuilder = null;
        if (cn.toShortString().equals("{com.example.loker/com.example.loker.MainActivity}")) {
            mBuilder =
                    new NotificationCompat.Builder(this, App.CHANNEL_ID)
                            .setSmallIcon(R.drawable.logo)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true)
                            .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message));
        } else {
            mBuilder =
                    new NotificationCompat.Builder(this, App.CHANNEL_ID)
                            .setSmallIcon(R.drawable.logo)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true)
                            .setContentIntent(contentIntent)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message));
        }

        NotificationManagerCompat mManager = NotificationManagerCompat.from(this);
        mManager.notify(id, mBuilder.build());
    }
}
