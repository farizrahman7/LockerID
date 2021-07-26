package com.example.lokerid.Fragment.Booking;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lokerid.Adapter.LokerAdapter;
import com.example.lokerid.Database.DatabaseInit;
import com.example.lokerid.Model.LokerModel;
import com.example.lokerid.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookingLokerFragment extends Fragment implements View.OnClickListener {

    RecyclerView myStand;
    ArrayList<LokerModel> list;
    LokerAdapter standAdapter;
    Button btnMaps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_booking_loker, container, false);
        btnMaps = root.findViewById(R.id.btnMaps);
        btnMaps.setOnClickListener(this);

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
        list = new ArrayList<LokerModel>();

        final String res = getArguments().getString("nama");

        db.stand.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("nama").getValue().toString().equals(res)) {
                        for (DataSnapshot ds1 : ds.getChildren()) {
                            if (ds1.getKey().matches("[0-9]+")) {
                                i++;
                                String key = ds1.getKey();
                                LokerModel p = ds.child(key).getValue(LokerModel.class);
                                list.add(p);
                            }
                        }
                    }
                }
                standAdapter = new LokerAdapter(getActivity(), list, res);
                myStand.setAdapter(standAdapter);
                standAdapter.notifyDataSetChanged();
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

    public void onClick(View view) {
        DatabaseInit db = new DatabaseInit();
        db.stand.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String[] res = getArguments().getString("nama").split("\\s+");
                String stand = res[0].toLowerCase() + res[1];
                String coordinates = dataSnapshot.child(stand).child("coordinates").getValue().toString();
                String[] name = dataSnapshot.child(stand).child("lokasi").getValue().toString().split("\\s+");
                String val = "";
                for (int i = 0; i < name.length; i++) {
                    if (i < name.length - 1) {
                        val = val + name[i] + "+";
                    } else {
                        val = val + name[i];
                    }
                }
                Uri gmmIntentUri = Uri.parse("geo:" + coordinates + "?q=" + val);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
