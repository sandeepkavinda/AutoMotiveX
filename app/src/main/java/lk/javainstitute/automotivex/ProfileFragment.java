package lk.javainstitute.automotivex;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import lk.javainstitute.automotivex.dto.UserDto;

public class ProfileFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --------------- Start Get User Data ---------------
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("lk.javainstitute.automotivex.automotivex.data", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        UserDto userDto = gson.fromJson(sharedPreferences.getString("userData", null), UserDto.class);
        // --------------- End Get User Data ---------------


        // --------------- Start Load Data ---------------
        TextView nameTextView = view.findViewById(R.id.textView49);
        TextView emailTextView = view.findViewById(R.id.textView50);

        String name = userDto.getFirst_name()+" "+ userDto.getLast_name();
        nameTextView.setText(name);
        emailTextView.setText(userDto.getEmail());

        // --------------- Start Load Data ---------------


        // --------------- Start Edit Profile Button Action ---------------
        ConstraintLayout constraintLayout = view.findViewById(R.id.editProfileConstraintLayout);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),EditProfileActivity.class);
                startActivity(intent);
            }
        });
        // --------------- End Edit Profile Button Action ---------------


        // --------------- Start Log Out Button Action ---------------
        ConstraintLayout logoutConstraintLayout = view.findViewById(R.id.logoutConstraintLayout);
        logoutConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to Logout?");

                // OK Button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Logging Out
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("userData");
                        editor.apply();
                        Intent intent = new Intent(view.getContext(),WelcomeActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
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
        // --------------- End Log Out Button Action ---------------

        // --------------- Start My Appointments ---------------

        ConstraintLayout myApplicationLayout = view.findViewById(R.id.constraintLayout9);
        myApplicationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), AppointmentsActivity.class);
                startActivity(intent);
            }
        });

        // --------------- End My Appointments ---------------


        // --------------- Start Contact Us ---------------

        ConstraintLayout contactConstraintLayout = view.findViewById(R.id.contactConstraintLayout);
        contactConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), ContactUsActivity.class);
                startActivity(intent);
            }
        });

        // --------------- End Contact Us ---------------


        // --------------- Start Device Login ---------------
        ConstraintLayout deviceLoginsConstraintLayout = view.findViewById(R.id.contactConstraintLayout3);
        deviceLoginsConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), LoginHistoryActivity.class);
                startActivity(intent);
            }
        });
        // --------------- End Start Device Login ---------------



        // --------------- Start My Orders ---------------
        ConstraintLayout ordersConstraintLayout = view.findViewById(R.id.constraintLayout8);
        ordersConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), OrdersActivity.class);
                startActivity(intent);
            }
        });
        // --------------- End My Orders---------------





    }
}