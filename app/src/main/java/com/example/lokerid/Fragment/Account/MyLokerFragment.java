package com.example.lokerid.Fragment.Account;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lokerid.Adapter.MyLokerAdapter;
import com.example.lokerid.Database.DatabaseInit;
import com.example.lokerid.Model.MyLokerModel;
import com.example.lokerid.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyLokerFragment extends Fragment {

    RecyclerView myLoker;
    ArrayList<MyLokerModel> list;
    MyLokerAdapter myLokerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_my_loker, container, false);

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

        myLoker = root.findViewById(R.id.myLoker);
        int col = 2;
        myLoker.setLayoutManager(new GridLayoutManager(getActivity(), col));
        list = new ArrayList<MyLokerModel>();

        db.booking.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    DatabaseInit db = new DatabaseInit();
                    FirebaseUser user = db.mAuth.getCurrentUser();
                    if (ds.child("uid").getValue().toString().equals(user.getUid()) && ds.child("status").getValue().toString().equals("Datang")) {
                        i++;
                        String[] timestamp = ds.child("time").getValue().toString().split("\\s+");
                        String stand = "Stand " + ds.child("stand").getValue().toString().substring(5);
                        String loker = "Loker " + ds.child("loker").getValue().toString();

                        MyLokerModel p = new MyLokerModel(stand + " - " + loker, timestamp[0] + " " + timestamp[1], timestamp[2]);
                        list.add(p);
                    }
                }
                myLokerAdapter = new MyLokerAdapter(getActivity(), list);
                myLoker.setAdapter(myLokerAdapter);
                myLokerAdapter.notifyDataSetChanged();
                TextView tvJumlah = root.findViewById(R.id.tvJumlah);
                tvJumlah.setText(Integer.toString(i) + " Loker");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Inflate the layout for this fragment
        return root;
    }
}
