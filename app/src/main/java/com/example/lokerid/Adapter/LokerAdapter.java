package com.example.lokerid.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lokerid.Database.DatabaseInit;
import com.example.lokerid.Fragment.Booking.BookingFragment;
import com.example.lokerid.Fragment.Booking.BookingLokerFragment;
import com.example.lokerid.Fragment.Home.HomeFragment;
import com.example.lokerid.MainActivity;
import com.example.lokerid.Model.LokerModel;
import com.example.lokerid.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class LokerAdapter extends RecyclerView.Adapter<LokerAdapter.MyViewHolder> {

    Context context;
    ArrayList<LokerModel> myStand;
    String res;

    public LokerAdapter(Context context, ArrayList<LokerModel> myStand, String res) {
        this.context = context;
        this.myStand = myStand;
        this.res = res;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LokerAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_booking_loker, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.tvId.setText("Loker " + Integer.toString(myStand.get(position).getId()));
        String status = "";
        if (myStand.get(position).getStatus().equals("available")) {
            status = "Available";
        } else if (myStand.get(position).getStatus().equals("not available")) {
            status = "Not Available";
            holder.cvLoker.setCardBackgroundColor(ColorStateList.valueOf(Color.RED));
        } else if (myStand.get(position).getStatus().equals("booked")) {
            status = "Booked";
            holder.cvLoker.setCardBackgroundColor(ColorStateList.valueOf(Color.GRAY));
        }
        holder.tvStatus.setText(status);

        final String getNama = Integer.toString(myStand.get(position).getId());
        final String getStatus = myStand.get(position).getStatus();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                DatabaseInit db = new DatabaseInit();
                FirebaseUser user = db.mAuth.getCurrentUser();
                db.users.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild("saldo") || dataSnapshot.child("saldo").getValue().toString().equals("0")) {
                            builder.setMessage("Silahkan Isi Saldo Terlebih Dahulu!")
                                    .setTitle("Warning Topup")
                                    .setCancelable(false)
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                        } else {
                            if (holder.tvStatus.getText().toString().equals("Available")) {
                                builder.setMessage("Ingin Menyewa Loker Ini?")
                                        .setTitle("Sewa Loker")
                                        .setCancelable(false)
                                        .setPositiveButton("Sewa", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                DatabaseInit db = new DatabaseInit();
                                                FirebaseUser user = db.mAuth.getCurrentUser();
                                                Calendar calendar = Calendar.getInstance();
                                                String tss = DateFormat.format("EEE, dd-MM-yyyy HH:mm:ss", calendar.getTime()).toString();
                                                String ts = Long.toString(System.currentTimeMillis() * 1000);

                                                String[] stand = res.split("\\s+");
                                                String[] loker = holder.tvId.getText().toString().split("\\s+");

                                                db.booking.child(user.getUid() + ts).child("uid").setValue(user.getUid());
                                                db.booking.child(user.getUid() + ts).child("stand").setValue("stand" + stand[1]);
                                                db.booking.child(user.getUid() + ts).child("loker").setValue(loker[1]);
                                                db.booking.child(user.getUid() + ts).child("status").setValue("Booking");
                                                db.booking.child(user.getUid() + ts).child("time").setValue(tss);

                                                db.stand.child("stand" + stand[1]).child(loker[1]).child("status").setValue("booked");

                                                MainActivity activity = (MainActivity) view.getContext();
                                                Fragment fragment = new BookingFragment();
//                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                                activity.getSupportFragmentManager().popBackStack();
                                                Toast.makeText(view.getContext(), "Booking Berhasil!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton("Batal", null);
                            } else if (holder.tvStatus.getText().toString().equals("Not Available")) {
                                builder.setMessage("Loker Tidak Tersedia")
                                        .setTitle("Sewa Loker")
                                        .setCancelable(false)
                                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                            } else {
                                builder.setMessage("Loker Sudah Terbooking")
                                        .setTitle("Sewa Loker")
                                        .setCancelable(false)
                                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                            }
                        }
                        builder.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return myStand.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvId, tvStatus;
        CardView cvLoker;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            cvLoker = itemView.findViewById(R.id.cvLoker);
        }
    }
}
