package com.example.lokerid.Fragment.Account;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lokerid.Database.DatabaseInit;
import com.example.lokerid.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment implements View.OnClickListener {

    TextView tvNama, tvEmail;
    EditText etNama, etEmail, etLocation, etPhone, etBirth;
    Button btnUpdate;
    final Calendar calendar = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        DatabaseInit db = new DatabaseInit();
        etNama = root.findViewById(R.id.etNama);
        etEmail = root.findViewById(R.id.etEmail);
        etLocation = root.findViewById(R.id.etLocation);
        etPhone = root.findViewById(R.id.etPhone);
        etBirth = root.findViewById(R.id.etBirth);
        btnUpdate = root.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(this);

        db.users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseInit db = new DatabaseInit();
                FirebaseUser user = db.mAuth.getCurrentUser();
                String url = dataSnapshot.child(user.getUid()).child("profile").getValue().toString();
                ImageView img = root.findViewById(R.id.ivUser);
                Picasso.with(img.getContext()).load(url).placeholder(R.drawable.ic_person_black_24dp).into(img);

                tvNama = root.findViewById(R.id.tvNama);
                tvNama.setText(dataSnapshot.child(user.getUid()).child("nama").getValue().toString());

                tvEmail = root.findViewById(R.id.tvEmail);
                tvEmail.setText(dataSnapshot.child(user.getUid()).child("email").getValue().toString());

                etNama.setText(dataSnapshot.child(user.getUid()).child("nama").getValue().toString());
                etEmail.setText(dataSnapshot.child(user.getUid()).child("email").getValue().toString());
                etEmail.setFocusable(false);
                etEmail.setEnabled(false);
                etEmail.setTextColor(Color.BLACK);
                etEmail.setCursorVisible(false);
                etEmail.setKeyListener(null);
                etEmail.setBackgroundColor(Color.TRANSPARENT);

                if (dataSnapshot.child(user.getUid()).hasChild("address")) {
                    etLocation.setText(dataSnapshot.child(user.getUid()).child("address").getValue().toString());
                }

                if (dataSnapshot.child(user.getUid()).hasChild("phone")) {
                    etPhone.setText(dataSnapshot.child(user.getUid()).child("phone").getValue().toString());
                }

                if (dataSnapshot.child(user.getUid()).hasChild("birth")) {
                    etBirth.setText(dataSnapshot.child(user.getUid()).child("birth").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        etBirth = root.findViewById(R.id.etBirth);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                updateLabel();
            }
        };

        etBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(view.getContext(), date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onClick(View view) {
        if (TextUtils.isEmpty(etNama.getText())) {
            etNama.setError("Full Name Field is required!");
        } else {
            if (TextUtils.isEmpty(etLocation.getText())) {
                etLocation.setError("Location Field is required!");
            } else {
                if (TextUtils.isEmpty(etPhone.getText())) {
                    etPhone.setError("Phone Number Field is required!");
                } else {
                    if (TextUtils.isEmpty(etBirth.getText())) {
                        etBirth.setError("Birthday Field is required!");
                    } else {
                        DatabaseInit db = new DatabaseInit();
                        FirebaseUser user = db.mAuth.getCurrentUser();

                        db.users.child(user.getUid()).child("nama").setValue(etNama.getText().toString());
                        db.users.child(user.getUid()).child("address").setValue(etLocation.getText().toString());
                        db.users.child(user.getUid()).child("phone").setValue(etPhone.getText().toString());
                        db.users.child(user.getUid()).child("birth").setValue(etBirth.getText().toString());

                        Toast.makeText(view.getContext(), "Edit Profile Success!", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            }
        }
    }

    private void updateLabel() {
        String myFormat = "dd-MMMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        etBirth.setText(sdf.format(calendar.getTime()));
    }

}
