package com.example.lokerid.Fragment.Booking;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lokerid.Adapter.StandAdapter;
import com.example.lokerid.Database.DatabaseInit;
import com.example.lokerid.Database.StandDatabase;
import com.example.lokerid.Model.StandModel;
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
public class BookingFragment extends Fragment {

    RecyclerView myStand;
    ArrayList<StandModel> list;
    StandAdapter standAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_booking, container, false);

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

        myStand = root.findViewById(R.id.myStand);
        int col = 2;
        myStand.setLayoutManager(new GridLayoutManager(getActivity(), col));
        list = new ArrayList<StandModel>();

        db.stand.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    i++;
                    StandModel p = ds.getValue(StandModel.class);
                    list.add(p);
                }
                standAdapter = new StandAdapter(getActivity(), list);
                myStand.setAdapter(standAdapter);
                standAdapter.notifyDataSetChanged();
                TextView tvJumlah = root.findViewById(R.id.tvJumlah);
                tvJumlah.setText(Integer.toString(i) + " Stand");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Inflate the layout for this fragment
        return root;
    }
}
