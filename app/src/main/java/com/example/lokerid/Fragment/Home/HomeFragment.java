package com.example.lokerid.Fragment.Home;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lokerid.Adapter.HomeAdapter;
import com.example.lokerid.Database.DatabaseInit;
import com.example.lokerid.Model.HomeModel;
import com.example.lokerid.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    TextView tvDay, tvWelcome, tvDate;
    RecyclerView myHome;
    ArrayList<HomeModel> list;
    HomeAdapter homeAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_home2, container, false);

        DatabaseInit db = new DatabaseInit();
        db.users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseInit db = new DatabaseInit();
                FirebaseUser user = db.mAuth.getCurrentUser();
                String url = dataSnapshot.child(user.getUid()).child("profile").getValue().toString();
                ImageView img = root.findViewById(R.id.ivUser);
                Picasso.with(img.getContext()).load(url).placeholder(R.drawable.ic_person_black_24dp).into(img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myHome = root.findViewById(R.id.myBooking);
        myHome.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = new ArrayList<HomeModel>();

        db.booking.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    DatabaseInit db = new DatabaseInit();
                    FirebaseUser user = db.mAuth.getCurrentUser();
                    if (ds.child("uid").getValue().toString().equals(user.getUid()) && ds.child("status").getValue().toString().equals("Booking")) {
                        try {
                            String[] date = ds.child("time").getValue().toString().split("\\s+");
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            Calendar calendar = Calendar.getInstance();
                            String tss = android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", calendar.getTime()).toString();

                            Date mDate = simpleDateFormat.parse(date[1] + " " + date[2]);
                            Date timeNow = simpleDateFormat.parse(tss);
                            long mTimeLeftInMillis = mDate.getTime() + (30 * 60000) - timeNow.getTime();

                            String loker = "Loker " + ds.child("loker").getValue().toString();
                            String stand = "Stand " + ds.child("stand").getValue().toString().substring(5);

                            HomeModel p = new HomeModel(loker, stand, mTimeLeftInMillis);
                            list.add(p);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        homeAdapter = new HomeAdapter(getActivity(), list);
                        myHome.setAdapter(homeAdapter);
                        homeAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        swipeRefreshLayout = root.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                final DatabaseInit db = new DatabaseInit();
                db.booking.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        FirebaseUser user = db.mAuth.getCurrentUser();
                        if (ds.child("uid").getValue().toString().equals(user.getUid()) && ds.child("status").getValue().toString().equals("Booking")) {
                            try {
                                String[] date = ds.child("time").getValue().toString().split("\\s+");
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                Calendar calendar = Calendar.getInstance();
                                String tss = android.text.format.DateFormat.format("dd-MM-yyyy HH:mm:ss", calendar.getTime()).toString();

                                Date mDate = simpleDateFormat.parse(date[1] + " " + date[2]);
                                Date timeNow = simpleDateFormat.parse(tss);
                                long mTimeLeftInMillis = mDate.getTime() + (30 * 60000) - timeNow.getTime();

                                String loker = "Loker " + ds.child("loker").getValue().toString();
                                String stand = "Stand " + ds.child("stand").getValue().toString().substring(5);

                                HomeModel p = new HomeModel(loker, stand, mTimeLeftInMillis);
                                list.add(p);
                                homeAdapter.notifyDataSetChanged();
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
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        myHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeAdapter.notifyDataSetChanged();
            }
        });

        // Get time now in HomeFragment
        tvDay = root.findViewById(R.id.tvDay);
        tvWelcome = root.findViewById(R.id.tvWelcome);
        tvDate = root.findViewById(R.id.tvDate);
//        tvCount = root.findViewById(R.id.tvCount);

        final Date date;
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat day = new SimpleDateFormat("EEEE");

        date = cal.getTime();

        String formattedDate = dateFormat.format(date);
        String formattedDay = day.format(date).substring(0,3);
        String hour = sdf.format(new Date());

        int hourNow = Integer.valueOf(hour);

//        Log.d("date", String.valueOf(hourNow));
        tvDay.setText(formattedDay);
        tvDate.setText(formattedDate);

        if (hourNow >= 3 && hourNow <= 11) {
            tvWelcome.setText("Good Morning");
        }
        if (hourNow >= 12 && hourNow <= 17) {
            tvWelcome.setText("Good Afternoon");
        }
        if (hourNow >= 18 && hourNow <= 23) {
            tvWelcome.setText("Good Evening");
        }
        if (hourNow >= 0 && hourNow <= 2) {
            tvWelcome.setText("Good Night");
        }

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (homeAdapter != null) {
            homeAdapter.notifyDataSetChanged();
        }
    }


}
