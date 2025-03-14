package lk.javainstitute.automotivex;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import lk.javainstitute.automotivex.dto.UserDto;
import lk.javainstitute.automotivex.model.AppPreferences;

public class EditProfileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --------------- Declare Variables ---------------
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("lk.javainstitute.automotivex.automotivex.data", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        // --------------- Declare Variables ---------------


        // --------------- Start Get User Data ---------------
        UserDto userDto = gson.fromJson(sharedPreferences.getString("userData", null), UserDto.class);
        // --------------- End Get User Data ---------------


        // Check whether Logged User
        if (userDto != null) {

            // --------------- Start Back Button Process ---------------
            ConstraintLayout constraintLayout = findViewById(R.id.backButtonConstraintLayout);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            // --------------- End Back Button Process ---------------


            // --------------- Start Load User Data ---------------
            TextView userNameTextView = findViewById(R.id.userNameTextView);
            TextView emailTextView = findViewById(R.id.emailTextView);
            EditText firstNameEditText = findViewById(R.id.editProfileFirstNameEditText);
            EditText lastNameEditText = findViewById(R.id.editProfileLastNameEditText);

            String userName = userDto.getFirst_name() + " " + userDto.getLast_name();
            userNameTextView.setText(userName);
            emailTextView.setText(userDto.getEmail());
            firstNameEditText.setText(userDto.getFirst_name());
            lastNameEditText.setText(userDto.getLast_name());
            // --------------- End Load User Data ---------------

            // --------------- Start Update User Data ---------------
            Button button = findViewById(R.id.updateEditProfileDetailsButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    EditText firstNameEditText = findViewById(R.id.editProfileFirstNameEditText);
                    EditText lastNameEditText = findViewById(R.id.editProfileLastNameEditText);
                    EditText currentPasswordEditText = findViewById(R.id.currentPasswordEditText);

                    String firstName = firstNameEditText.getText().toString().trim();
                    String lastName = lastNameEditText.getText().toString().trim();
                    String password = currentPasswordEditText.getText().toString();

                    if (firstName.isEmpty() || !firstName.matches("[a-zA-Z]+")) {
                        Toast.makeText(EditProfileActivity.this, "Enter a Valid First Name", Toast.LENGTH_LONG).show();
                        firstNameEditText.requestFocus();
                    } else if (lastName.isEmpty() || !lastName.matches("[a-zA-Z]+")) {
                        Toast.makeText(EditProfileActivity.this, "Enter a Valid Last Name", Toast.LENGTH_LONG).show();
                        lastNameEditText.requestFocus();
                    } else if (password.isEmpty()) {
                        Toast.makeText(EditProfileActivity.this, "Enter your current password to update this details", Toast.LENGTH_LONG).show();
                        currentPasswordEditText.requestFocus();
                    } else {

                        String email = userDto.getEmail();

                        //Check whether password is correct
                        firestore.collection("user")
                                .whereEqualTo("email", email)
                                .whereEqualTo("password", password)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        List<DocumentSnapshot> userDocument = queryDocumentSnapshots.getDocuments();

                                        HashMap<String, Object> updatingDataHashMap = new HashMap<>();
                                        updatingDataHashMap.put("first_name", firstName);
                                        updatingDataHashMap.put("last_name", lastName);

                                        if (!userDocument.isEmpty()) {

                                            //Check whether the user has entered a new password
                                            EditText newPasswordEditText = findViewById(R.id.newPasswordEditText);
                                            EditText confirmNewPasswordEditText = findViewById(R.id.confirmNewPasswordEditText);

                                            String newPassword = newPasswordEditText.getText().toString();
                                            String confirmNewPassword = confirmNewPasswordEditText.getText().toString();
                                            if (!newPassword.isEmpty()) {
                                                if (newPassword.length() < 6) {
                                                    Toast.makeText(EditProfileActivity.this, "New Password must be at least 6 characters", Toast.LENGTH_LONG).show();
                                                    newPasswordEditText.requestFocus();
                                                } else if (!confirmNewPassword.equals(newPassword)) {
                                                    Toast.makeText(EditProfileActivity.this, "New Password and Confirmed Password do not match", Toast.LENGTH_LONG).show();
                                                    confirmNewPasswordEditText.requestFocus();
                                                } else {
                                                    //Add Updating Data
                                                    updatingDataHashMap.put("password", newPassword);
                                                }
                                            }

                                            //Update User Data
                                            firestore.collection("user")
                                                    .document(userDto.getDocId())
                                                    .update(updatingDataHashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {

                                                            //Pop Alert For Success
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                                            builder.setTitle("Success");
                                                            builder.setMessage("Your profile has been updated successfully!");
                                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {


                                                                    //Save Data to Shared Preferences
                                                                    AppPreferences.refreshUserData(EditProfileActivity.this,userDto.getDocId(),false);

                                                                    // Close Edit Profile
                                                                    finish();

                                                                }
                                                            });
                                                            builder.create().show();

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(EditProfileActivity.this, "Something Went Wrong Try Again Later", Toast.LENGTH_LONG).show();
                                                        }
                                                    });


                                            Log.i("Aotomotivelog", "Ok");
                                        } else {
                                            Toast.makeText(EditProfileActivity.this, "Incorrect Password", Toast.LENGTH_LONG).show();

                                        }

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditProfileActivity.this, "Something Went Wrong Try Again Later", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
            });
            // --------------- End Update User Data ---------------


        } else {
            Intent intent = new Intent(EditProfileActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }


    }

}