package com.example.lokerid.Database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseInit {
    //Autentikasi
    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;

    //Database
    public FirebaseDatabase database;
    public DatabaseReference pegawai;
    public DatabaseReference users;
    public DatabaseReference stand;
    public DatabaseReference booking;
    public DatabaseReference daftar;

    //User
    public FirebaseUser user;

    public DatabaseInit() {
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        pegawai = database.getReference("pegawai");
        booking = database.getReference("booking");
        users = database.getReference("user");
        stand = database.getReference("stand");
        daftar = database.getReference("daftar");
    }
}
