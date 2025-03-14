package lk.javainstitute.automotivex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import lk.javainstitute.automotivex.dto.UserDto;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FrameLayout frameLayout = findViewById(R.id.signInButtonFrameLayout);
        Animation scaleAnimation = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.animation1);
        scaleAnimation.setFillAfter(true);
        frameLayout.startAnimation(scaleAnimation);

        //If signed Customer Sent to Home
        SharedPreferences sharedPreferences = getSharedPreferences("lk.javainstitute.automotivex.automotivex.data", Context.MODE_PRIVATE);
        String userData = sharedPreferences.getString("userData",null);
        if(userData!=null){
            Gson gson = new Gson();
            UserDto userDto = gson.fromJson(sharedPreferences.getString("userData",null), UserDto.class);

            if(userDto.getType().equals("Admin")){
                Intent intent = new Intent(WelcomeActivity.this,AdminDashboardActivity.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(WelcomeActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        }

        //Get Started Button
        Button getStartButton = findViewById(R.id.signInButton);
        getStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this,SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}