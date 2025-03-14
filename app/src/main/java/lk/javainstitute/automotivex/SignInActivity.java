package lk.javainstitute.automotivex;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.javainstitute.automotivex.dto.UserDto;
import lk.javainstitute.automotivex.model.AppPreferences;
import lk.javainstitute.automotivex.model.SQLiteHelper;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Navigate To Create New Account
        Button createNewAccountButton = findViewById(R.id.navigateToCreateNewAccountButton);
        createNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Check Sign In
        Button signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText emailEditText = findViewById(R.id.signinEmailEditText);
                EditText passwordEditText = findViewById(R.id.signinPasswordEditText);

                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();

                if (email.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Enter Your Email", Toast.LENGTH_LONG).show();
                    emailEditText.requestFocus();
                }else if (password.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Enter Your Password", Toast.LENGTH_LONG).show();
                    passwordEditText.requestFocus();
                }else{

                    ProgressBar progressBar2 = findViewById(R.id.progressBar2);
                    progressBar2.setVisibility(VISIBLE);

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                    firestore.collection("user")
                            .whereEqualTo("email",email)
                            .whereEqualTo("password",password)
                            .whereEqualTo("status","Active")
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                    List<DocumentSnapshot> documentList = queryDocumentSnapshots.getDocuments();

                                    if(!documentList.isEmpty()){
                                        //Get User Document
                                        DocumentSnapshot userDocument = documentList.get(0);
                                        UserDto userDto = userDocument.toObject(UserDto.class);

                                        //Add User Data To a Json Object
                                        AppPreferences.refreshUserData(SignInActivity.this,userDocument.getId(),true);

                                        SQLiteHelper sqLiteHelper = new SQLiteHelper(SignInActivity.this,"AutoMotiveX.db", null,1);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                                                SQLiteDatabase sqLiteDatabase = sqLiteHelper.getWritableDatabase();
                                                sqLiteDatabase.execSQL("INSERT INTO `login_history` (`email`,`first_name`,`last_name`,`logged_date_time`,`status`) " +
                                                        "VALUES('"+userDto.getEmail()+"','"+userDto.getFirst_name()+"','"+userDto.getLast_name()+"','"+currentDateTime+"','Logged')");
                                            }
                                        }).start();


                                    }else{
                                        Toast.makeText(SignInActivity.this, "Invalid credentials. Double-check your email and password.", Toast.LENGTH_LONG).show();
                                    }
                                    progressBar2.setVisibility(View.GONE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignInActivity.this, "Something Went Wrong Try Again Later", Toast.LENGTH_LONG).show();
                                    progressBar2.setVisibility(View.GONE);
                                }

                            });
                }



            }
        });

    }
}