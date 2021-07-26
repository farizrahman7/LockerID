package com.example.lokerid.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lokerid.Database.DatabaseInit;
import com.example.lokerid.Fragment.Account.AccountFragment;
import com.example.lokerid.MainActivity;
import com.example.lokerid.Model.MyLokerModel;
import com.example.lokerid.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyLokerAdapter extends RecyclerView.Adapter <MyLokerAdapter.MyViewHolder> {

    Context context;
    ArrayList<MyLokerModel> myLoker;

    public MyLokerAdapter(Context context, ArrayList<MyLokerModel> myLoker) {
        this.context = context;
        this.myLoker = myLoker;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyLokerAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_myloker, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.tvLoker.setText(myLoker.get(position).getLoker());
        holder.tvTanggal.setText(myLoker.get(position).getTanggal());
        holder.tvJam.setText(myLoker.get(position).getJam());


        final DatabaseInit db = new DatabaseInit();
        final FirebaseUser user = db.mAuth.getCurrentUser();
        String[] res = myLoker.get(position).getLoker().split(" - ");
        final String stand = "stand" + res[0].substring(6);
        final String loker = res[1].substring(6);
        db.stand.child(stand).child(loker).child("keamanan").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().toString().equals("danger")) {
                    holder.myCard.setCardBackgroundColor(Color.RED);
                } else {
                    holder.myCard.setCardBackgroundColor(Color.WHITE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.btnSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                db.stand.child(stand).child(loker).child("keamanan").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue().toString().equals("danger")) {
                            new AlertDialog.Builder(view.getContext())
                                    .setTitle("Konfirmasi")
                                    .setMessage("Sudah cek loker anda?")
                                    .setCancelable(false)
                                    .setPositiveButton("Sudah", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            db.stand.child(stand).child(loker).child("keamanan").setValue("safe");
                                            MainActivity activity = (MainActivity) view.getContext();
                                            Fragment fragment = new AccountFragment();
                                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                            activity.getSupportFragmentManager().popBackStack();
                                            Toast.makeText(view.getContext(), "Loker " + loker + " di Stand " + stand.substring(6) + " Sudah Aman!",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNegativeButton("Belum", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).show();
                        } else {
                            new android.app.AlertDialog.Builder(view.getContext())
                                    .setTitle("Konfirmasi")
                                    .setMessage("Sudah selesai menyewa?")
                                    .setCancelable(false)
                                    .setPositiveButton("Selesai", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            final ProgressDialog mDialog = new ProgressDialog(view.getContext());
                                            mDialog.setTitle("Loading");
                                            mDialog.setMessage("Harap menunggu...");
                                            mDialog.show();
                                            final DatabaseInit db = new DatabaseInit();
                                            final FirebaseUser user = db.mAuth.getCurrentUser();
                                            db.booking.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        if (ds.child("uid").getValue().toString().equals(user.getUid())) {
                                                            String[] res = myLoker.get(position).getLoker().split(" - ");
                                                            final String[] stand = res[0].split("\\s+");
                                                            final String[] loker = res[1].split("\\s+");
                                                            if (ds.child("stand").getValue().toString().equals(stand[0].toLowerCase() + stand[1])) {
                                                                if (ds.child("loker").getValue().toString().equals(loker[1])) {
                                                                    if (ds.child("status").getValue().toString().equals("Datang")) {
                                                                        db.users.child(user.getUid()).child("saldo").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                if (Integer.parseInt(dataSnapshot.getValue().toString()) < 0) {
                                                                                    new AlertDialog.Builder(view.getContext())
                                                                                            .setTitle("Saldo Kurang")
                                                                                            .setMessage("Harap Topup Saldo Terlebih Dahulu!")
                                                                                            .setCancelable(true)
                                                                                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                                                                @Override
                                                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                                                    dialogInterface.dismiss();
                                                                                                    mDialog.dismiss();
                                                                                                }
                                                                                            }).show();
                                                                                } else {
                                                                                    db.booking.child(ds.getKey()).child("status").setValue("Finish");
                                                                                    db.stand.child(ds.child("stand").getValue().toString())
                                                                                            .child(ds.child("loker").getValue().toString())
                                                                                            .child("status").setValue("available");

                                                                                    MainActivity activity = (MainActivity) view.getContext();
                                                                                    Fragment fragment = new AccountFragment();
                                                                                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                                                                                    activity.getSupportFragmentManager().popBackStack();
                                                                                    Toast.makeText(view.getContext(), "Penyewaan Loker " + loker[1] + " di Stand " + stand[1] + " Selesai!",
                                                                                            Toast.LENGTH_SHORT).show();
                                                                                    mDialog.dismiss();
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("Batal", null).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        holder.btnBuka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
//        holder.btnKamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View view) {
//                db.stand.child(stand).child(loker).child("ipcam").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return myLoker.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTanggal, tvJam, tvLoker;
        Button btnSelesai,btnKamera,btnBuka;
        CardView myCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLoker = itemView.findViewById(R.id.tvLoker);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvJam = itemView.findViewById(R.id.tvJam);
            btnSelesai = itemView.findViewById(R.id.btnSelesai);
            btnKamera = itemView.findViewById(R.id.btnKamera);
            btnBuka = itemView.findViewById(R.id.btnBuka);
            myCard = itemView.findViewById(R.id.myCard);
        }
    }
}
