package lk.javainstitute.automotivex;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import lk.javainstitute.automotivex.dto.UserDto;
import lk.javainstitute.automotivex.model.AppointmentDetails;
import lk.javainstitute.automotivex.model.CartDetails;

public class AppointmentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointments);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("lk.javainstitute.automotivex.automotivex.data", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        UserDto userDto = gson.fromJson(sharedPreferences.getString("userData", null), UserDto.class);

        // --------------- Back Process ---------------
        ConstraintLayout constraintLayout = findViewById(R.id.backButtonConstraintLayout);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // --------------- Back Process ---------------



        // -------------- Load Appointments Start ---------------

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        ArrayList<AppointmentDetails> appointmentDetailsArrayList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AppointmentsActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView myAppontmentsRecyclerView = findViewById(R.id.myAppontmentsRecyclerView);
        myAppontmentsRecyclerView.setLayoutManager(linearLayoutManager);


        firestore.collection("appointment")
                .whereEqualTo("userDocId", userDto.getDocId())
                .orderBy("requestedDateTime", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                            AppointmentDetails appointmentDetails = documentSnapshot.toObject(AppointmentDetails.class);
                            appointmentDetails.setDocId(documentSnapshot.getId());

                            appointmentDetailsArrayList.add(appointmentDetails);
                            myAppontmentsRecyclerView.setAdapter(new AppointmentAdapter(appointmentDetailsArrayList, AppointmentsActivity.this, userDto));

                        }

                    }
                })
        ;

        // -------------- Load Appointments End ---------------


    }
}


class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView appoinmentIdTextView;
        TextView nameTextView;
        TextView mobileTextView;
        TextView descriptionTextView;
        TextView vehicleTextView;
        TextView appointmentDateTextView;
        TextView requestedDateTextView;
        TextView statusTextView;
        ImageView deleteIcon;
        Button acceptButton;
        Button rejectButton;
        FrameLayout acceptFrameLayout;
        FrameLayout rejectFrameLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appoinmentIdTextView = itemView.findViewById(R.id.textView91);
            nameTextView = itemView.findViewById(R.id.textView92);
            mobileTextView = itemView.findViewById(R.id.textView94);
            descriptionTextView = itemView.findViewById(R.id.textView93);
            vehicleTextView = itemView.findViewById(R.id.textView95);
            appointmentDateTextView = itemView.findViewById(R.id.textView96);
            requestedDateTextView = itemView.findViewById(R.id.textView97);
            statusTextView = itemView.findViewById(R.id.textView99);
            deleteIcon = itemView.findViewById(R.id.imageView28);
            acceptButton = itemView.findViewById(R.id.acceptAppointmentButton);
            rejectButton = itemView.findViewById(R.id.rejectAppointmentButton);
            acceptFrameLayout = itemView.findViewById(R.id.acceptButtonFrameLayout);
            rejectFrameLayout = itemView.findViewById(R.id.rejectButtonFrameLayout);
        }
    }

    ArrayList<AppointmentDetails> appointmentDetailsArrayList;
    Activity activity;
    UserDto userDto;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public AppointmentAdapter(ArrayList<AppointmentDetails> appointmentDetailsArrayList, Activity activity, UserDto userDto) {
        this.appointmentDetailsArrayList = appointmentDetailsArrayList;
        this.activity = activity;
        this.userDto = userDto;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_appointment_view, parent, false);
        return new AppointmentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentDetails appointmentDetails = appointmentDetailsArrayList.get(position);
        Context context = holder.appoinmentIdTextView.getContext();
        holder.appoinmentIdTextView.setText("Appt. Id : " + appointmentDetails.getDocId());
        String name = appointmentDetails.getFirstName() + " " + appointmentDetails.getLastName();
        holder.nameTextView.setText(name);
        holder.mobileTextView.setText(appointmentDetails.getMobile());
        holder.descriptionTextView.setText("Service : " + appointmentDetails.getServiceDescription());
        holder.vehicleTextView.setText("Vehicle : " + appointmentDetails.getVehicleModel());
        holder.appointmentDateTextView.setText("Date : " + appointmentDetails.getAppointmentDate());
        holder.requestedDateTextView.setText(appointmentDetails.getRequestedDateTime());
        holder.statusTextView.setText(appointmentDetails.getStatus());


        int color = 0;

        if (appointmentDetails.getStatus().equals("Pending")) {
            color = ContextCompat.getColor(context, R.color.yellow);
        } else if (appointmentDetails.getStatus().equals("Accepted")) {
            color = ContextCompat.getColor(context, R.color.primary);
        } else if (appointmentDetails.getStatus().equals("Rejected")) {
            color = ContextCompat.getColor(context, R.color.red);
        }

        if (color != 0) {
            holder.statusTextView.setTextColor(color);
        }


        if (userDto.getType().equals("Admin")) {
            // --------------- If User is a Admin ---------------
            holder.deleteIcon.setVisibility(GONE);

            //Set and remove accept,reject buttons
            if (!appointmentDetails.getStatus().equals("Pending")) {
                holder.acceptFrameLayout.setVisibility(GONE);
                holder.rejectFrameLayout.setVisibility(GONE);
            } else {
                holder.acceptFrameLayout.setVisibility(VISIBLE);
                holder.rejectFrameLayout.setVisibility(VISIBLE);
            }

            // Accept Appointment
            holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, Object> updateMap = new HashMap<>();
                    updateMap.put("status", "Accepted");

                    firestore.collection("appointment")
                            .document(appointmentDetails.getDocId())
                            .update(updateMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Appointment Accepted Successfully", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            });

            //Reject Appointment
            holder.rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, Object> updateMap = new HashMap<>();
                    updateMap.put("status", "Rejected");

                    firestore.collection("appointment")
                            .document(appointmentDetails.getDocId())
                            .update(updateMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Appointment Accepted Successfully", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            });


        } else if (userDto.getType().equals("User")) {

            holder.acceptFrameLayout.setVisibility(GONE);
            holder.rejectFrameLayout.setVisibility(GONE);

            // --------------- If User is a normal user ---------------
            if (appointmentDetails.getStatus().equals("Pending")) {
                holder.deleteIcon.setVisibility(VISIBLE);

                //Delete Appointment from user side
                holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("Confirmation");
                        builder.setMessage("Are you sure you want to remove this appointment?");

                        // OK Button
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Remove Appointment
                                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                firestore
                                        .collection("appointment")
                                        .document(appointmentDetails.getDocId())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(v.getContext(), "Appointment Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(v.getContext(), AppointmentsActivity.class);
                                                v.getContext().startActivity(intent);
                                                activity.finish();
                                            }
                                        })
                                ;
                            }
                        });

                        // Cancel Button
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        // Show the AlertDialog
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });


            } else {
                holder.deleteIcon.setVisibility(GONE);
            }

        }


    }

    @Override
    public int getItemCount() {
        return appointmentDetailsArrayList.size();
    }


}