package lk.javainstitute.automotivex;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import lk.javainstitute.automotivex.dto.UserDto;


public class AppiontmentFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appiontment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("lk.javainstitute.automotivex.automotivex.data", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        UserDto userDto = gson.fromJson(sharedPreferences.getString("userData",null), UserDto.class);

        // --------------- Disable Previous Data On Date Picker ---------------
        DatePicker datePicker = view.findViewById(R.id.datePicker);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datePicker.setMinDate(calendar.getTimeInMillis());
        // --------------- Disable Previous Data On Date Picker ---------------

        // --------------- Start Save Appointment ---------------

        Button requestAppointmentsButton = view.findViewById(R.id.requestAppointmentsButton);
        requestAppointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                EditText firstNameEditText = view.findViewById(R.id.appointmentFirstNameEditText);
                EditText lastNameEditText = view.findViewById(R.id.appointmentLastNameEditText);
                EditText mobileEditText = view.findViewById(R.id.appointmentMobileEditText);
                EditText vehicleModelNameEditText = view.findViewById(R.id.editTextText2);
                EditText serviceDescriptionEditText = view.findViewById(R.id.serviceDescriptionEditText);

                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String mobile = mobileEditText.getText().toString().trim();
                String vehicleModel = vehicleModelNameEditText.getText().toString().trim();
                String serviceDescription = serviceDescriptionEditText.getText().toString().trim();


                DatePicker datePicker = view.findViewById(R.id.datePicker);

                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String selectedDate = sdf.format(calendar.getTime());

                if(firstName.isEmpty()){
                    Toast.makeText(view.getContext(), "Enter your First Name", Toast.LENGTH_SHORT).show();
                    firstNameEditText.requestFocus();
                }else if(lastName.isEmpty()){
                    Toast.makeText(view.getContext(), "Enter your Last Name", Toast.LENGTH_SHORT).show();
                    lastNameEditText.requestFocus();
                }else  if(mobile.isEmpty()){
                    Toast.makeText(view.getContext(), "Enter your Mobile", Toast.LENGTH_SHORT).show();
                    mobileEditText.requestFocus();
                }else  if(vehicleModel.isEmpty()){
                    Toast.makeText(view.getContext(), "Enter your Vehicle Model", Toast.LENGTH_SHORT).show();
                    vehicleModelNameEditText.requestFocus();
                }else if (serviceDescription.isEmpty()) {
                    Toast.makeText(view.getContext(), "Enter your Service Description", Toast.LENGTH_SHORT).show();
                    serviceDescriptionEditText.requestFocus();
                } else {
                    String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                    HashMap<String, Object> appointmentDataHashMap = new HashMap<String, Object>();
                    appointmentDataHashMap.put("firstName", firstName);
                    appointmentDataHashMap.put("lastName", lastName);
                    appointmentDataHashMap.put("mobile", mobile);
                    appointmentDataHashMap.put("vehicleModel", vehicleModel);
                    appointmentDataHashMap.put("serviceDescription", serviceDescription);
                    appointmentDataHashMap.put("appointmentDate", selectedDate);
                    appointmentDataHashMap.put("requestedDateTime", currentDateTime);
                    appointmentDataHashMap.put("status", "Pending");
                    appointmentDataHashMap.put("userDocId", userDto.getDocId());

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("appointment")
                            .add(appointmentDataHashMap)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.i("AutoMotiveXLog","Appointment Request Added Successfully");
                                    Intent intent = new Intent(view.getContext(), SuccessAppointmentActivity.class);
                                    startActivity(intent);

                                    //Reload Appointment Fragment
                                    requireActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, new AppiontmentFragment())
                                            .commit()
                                    ;

                                }
                            })
                    ;

                }


            }
        });

        // --------------- End Save Appointment ---------------

    }
}