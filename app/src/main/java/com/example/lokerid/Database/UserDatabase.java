package com.example.lokerid.Database;

import androidx.annotation.NonNull;

import com.example.lokerid.Model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UserDatabase {
    DatabaseInit db = new DatabaseInit();
    UserModel userModel = new UserModel();

    public String uID;

    public interface DataStatus {
        void DataIsLoaded(UserModel userModel);
    }

    public void getData(final DataStatus dataStatus) {
        uID = db.user.getUid();
        db.users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModel.setEmail(dataSnapshot.child(uID).getValue(UserModel.class).getEmail());
                userModel.setUsername(dataSnapshot.child(uID).getValue(UserModel.class).getUsername());

                dataStatus.DataIsLoaded(userModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
