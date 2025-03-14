package lk.javainstitute.automotivex.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lk.javainstitute.automotivex.AdminDashboardActivity;
import lk.javainstitute.automotivex.HomeActivity;
import lk.javainstitute.automotivex.SignInActivity;

public class AppPreferences {
    public static void refreshUserData(Context context, String userDocId, boolean isSignIn) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("user")
                .document(userDocId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        JsonObject userJsonObject = new JsonObject();
                        userJsonObject.addProperty("docId",document.getId());
                        userJsonObject.addProperty("email",String.valueOf(document.get("email")));
                        userJsonObject.addProperty("first_name",String.valueOf(document.get("first_name")));
                        userJsonObject.addProperty("last_name",String.valueOf(document.get("last_name")));
                        userJsonObject.addProperty("registered_date",String.valueOf(document.get("registered_date")));
                        userJsonObject.addProperty("status",String.valueOf(document.get("status")));
                        userJsonObject.addProperty("type",String.valueOf(document.get("type")));

                        SharedPreferences sharedPreferences = context.getSharedPreferences("lk.javainstitute.automotivex.automotivex.data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Gson gson = new Gson();
                        editor.putString("userData", gson.toJson(userJsonObject));
                        editor.apply();
                        Log.i("userData", gson.toJson(userJsonObject));

                        if(isSignIn){

                            if(String.valueOf(document.get("type")).equals("Admin")){
                                Intent intent = new Intent(context, AdminDashboardActivity.class);
                                context.startActivity(intent);
                                Activity activity = (Activity) context;
                                activity.finish();
                            }else{
                                Intent intent = new Intent(context, HomeActivity.class);
                                Activity activity = (Activity) context;
                                context.startActivity(intent);
                                activity.finish();
                            }

                        }

                    }
                });


    }
}
