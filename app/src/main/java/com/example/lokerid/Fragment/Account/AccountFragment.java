package com.example.lokerid.Fragment.Account;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lokerid.Database.DatabaseInit;
import com.example.lokerid.Fragment.Auth.LoginActivity;
import com.example.lokerid.MainActivity;
import com.example.lokerid.Model.UserModel;
import com.example.lokerid.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URI;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements View.OnClickListener {

    UserModel userModel = new UserModel();
    private GoogleSignInClient mGoogleSignClient;
    Button btnLogout, btnMyLoker, btnEditProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_account, container, false);
        btnLogout = root.findViewById(R.id.btnLogout);
        btnMyLoker = root.findViewById(R.id.btnMyLoker);
        btnEditProfile = root.findViewById(R.id.btnEditProfile);
        btnLogout.setOnClickListener(this);
        btnMyLoker.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);

        DatabaseInit db = new DatabaseInit();
        db.users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseInit db = new DatabaseInit();
                FirebaseUser user = db.mAuth.getCurrentUser();
                String url = dataSnapshot.child(user.getUid()).child("profile").getValue().toString();
                ImageView img = root.findViewById(R.id.myPict);
                Picasso.with(img.getContext()).load(url).placeholder(R.drawable.ic_person_black_24dp).into(img);

                TextView tvNama = root.findViewById(R.id.tvNama);
                tvNama.setText(dataSnapshot.child(user.getUid()).child("nama").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Inflate the layout for this fragment
        return root;
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnLogout) {
            new android.app.AlertDialog.Builder(view.getContext())
                    .setTitle("Konfirmasi Logout")
                    .setMessage("Yakin ingin Logout aplikasi?")
                    .setCancelable(false)
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DatabaseInit db = new DatabaseInit();
                            db.mAuth.signOut();
                            GoogleSignIn.getClient(
                                    getContext(),
                                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                            ).signOut();
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            getActivity().finish();
                            Toast.makeText(getContext(), "Logout Success!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Batal", null).show();
        } else if (id == R.id.btnMyLoker) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyLokerFragment()).addToBackStack(null).commit();
        } else if (id == R.id.btnEditProfile) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EditProfileFragment()).addToBackStack(null).commit();
        }
    }
}

