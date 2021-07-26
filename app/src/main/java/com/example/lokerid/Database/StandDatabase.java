package com.example.lokerid.Database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lokerid.Model.StandModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class StandDatabase {
    DatabaseInit db = new DatabaseInit();
    StandModel standModel = new StandModel();

    public interface DataStatus {
        void DataIsLoaded(StandModel userModel);
    }

    public void getData(final DataStatus dataStatus) {
        db.users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TAG", dataSnapshot.getValue().toString());
//                dataStatus.DataIsLoaded(StandModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
