package lk.javainstitute.automotivex;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lk.javainstitute.automotivex.model.SQLiteHelper;

public class LoginHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout linearLayout = findViewById(R.id.linearLayout1);

        LayoutInflater layoutInflater = LayoutInflater.from(LoginHistoryActivity.this);

        SQLiteHelper sqLiteHelper = new SQLiteHelper(LoginHistoryActivity.this,"AutoMotiveX.db", null,1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM `login_history` ORDER BY `logged_date_time` DESC", new String[]{});

                while(cursor.moveToNext()){

                    View view = layoutInflater.inflate(R.layout.login_history_single_item,null,false);
                    TextView userNameTextView = view.findViewById(R.id.textView106);
                    TextView emailTextView = view.findViewById(R.id.textView107);
                    TextView dateTimeTextView = view.findViewById(R.id.textView108);

                    String name = cursor.getString(2)+" "+cursor.getString(3);

                    userNameTextView.setText(name);
                    emailTextView.setText(cursor.getString(1));
                    dateTimeTextView.setText(cursor.getString(4));
                    linearLayout.addView(view);

                }

            }
        }).start();



    }
}