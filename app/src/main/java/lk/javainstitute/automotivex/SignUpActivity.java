package lk.javainstitute.automotivex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Navigate To LogIn
        Button alreadyHaveAccountButton = findViewById(R.id.alreadyHaveAccountButton);
        alreadyHaveAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Show Terms And Conditions
        TextView termsAndConditionsTextView = findViewById(R.id.termsAndConditionsTextView);
        termsAndConditionsTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(SignUpActivity.this);
                View viewTermsAndConditions = layoutInflater.inflate(R.layout.terms_and_conditions, null, false);

                AlertDialog dialog = new AlertDialog.Builder(SignUpActivity.this).setView(viewTermsAndConditions).show();
                Button acceptTermsButton = viewTermsAndConditions.findViewById(R.id.acceptTermsButton);
                acceptTermsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox termsAndConditionCheckBox = findViewById(R.id.agreeCheckBox);
                        termsAndConditionCheckBox.setChecked(true);
                        dialog.dismiss();
                    }
                });
            }
        });

        //Signup Process
        Button createAccountButton = findViewById(R.id.createAccountButton);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText firstNameEditText = findViewById(R.id.firstNameEditText);
                EditText lastNameEditText = findViewById(R.id.lastNameEditText);
                EditText emailEditText = findViewById(R.id.emailEditText);
                EditText passwordEditText = findViewById(R.id.passwordEditText);
                EditText confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
                CheckBox agreeCheckBox = findViewById(R.id.agreeCheckBox);

                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();


                // Name validation (Not empty & only letters)
                if (firstName.isEmpty() || !firstName.matches("[a-zA-Z]+")) {
                    Toast.makeText(SignUpActivity.this, "Enter a Valid First Name", Toast.LENGTH_LONG).show();
                    firstNameEditText.requestFocus();
                    //firstNameEditText.setError("Invalid First Name");
                }else if (lastName.isEmpty() || !lastName.matches("[a-zA-Z]+")) {
                    Toast.makeText(SignUpActivity.this, "Enter a Valid Last Name", Toast.LENGTH_LONG).show();
                    lastNameEditText.requestFocus();
                    //lastNameEditText.setError("Invalid Last Name");
                }else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignUpActivity.this, "Enter a Valid Email", Toast.LENGTH_LONG).show();
                    emailEditText.requestFocus();
                    //emailEditText.setError("Invalid Email");
                }else if (password.isEmpty() || password.length() < 6) {
                    Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_LONG).show();
                    passwordEditText.requestFocus();
                    //passwordEditText.setError("Invalid Email");
                }else if (!confirmPassword.equals(password)) {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                    confirmPasswordEditText.requestFocus();
                    //confirmPasswordEditText.setError("Passwords do not match");
                }else if (!agreeCheckBox.isChecked()){
                    Toast.makeText(SignUpActivity.this, "Please read and accept the Terms and Conditions", Toast.LENGTH_LONG).show();
                }else{
                    ProgressBar progressBar3 = findViewById(R.id.progressBar3);
                    progressBar3.setVisibility(View.VISIBLE);


                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                    //Search Email Already Registered
                    firestore.collection("user")
                            .whereEqualTo("email",email)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    if(!list.isEmpty()){
                                        Toast.makeText(SignUpActivity.this, "Email Already Registered", Toast.LENGTH_LONG).show();
                                    }else{
                                        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                                        HashMap<String,Object> userMap = new HashMap<>();
                                        userMap.put("first_name",firstName);
                                        userMap.put("last_name",lastName);
                                        userMap.put("email",email);
                                        userMap.put("password",password);
                                        userMap.put("registered_date",currentDateTime);
                                        userMap.put("status","Active");
                                        userMap.put("type","User");

                                        Intent intent = new Intent(SignUpActivity.this,SuccessSignupActivity.class);


                                        firestore.collection("user")
                                                .add(userMap)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        startActivity(intent);
                                                        progressBar3.setVisibility(View.GONE);
                                                    }
                                                })
                                        ;
                                    }
                                }

                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpActivity.this, "Something Went Wrong Try Again Later", Toast.LENGTH_LONG).show();
                                    progressBar3.setVisibility(View.GONE);
                                }
                            });


                }



            }
        });



    }
}