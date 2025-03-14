package lk.javainstitute.automotivex;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import lk.javainstitute.automotivex.dto.UserDto;
import lk.javainstitute.automotivex.model.FormatNumbers;
import lk.javainstitute.automotivex.model.OrderDetails;
import lk.javainstitute.automotivex.model.ProductDetails;

public class AdminDashboardFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("lk.javainstitute.automotivex.automotivex.data", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        UserDto userDto = gson.fromJson(sharedPreferences.getString("userData", null), UserDto.class);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Load Admin Name
        TextView textView = view.findViewById(R.id.textView69);
        String welcomeText = "Welcome <b>"+ userDto.getFirst_name()+" "+userDto.getLast_name()+" !</b>";
        textView.setText(Html.fromHtml(welcomeText));
        // Load Admin Name



        // --------------- Start Logout Process ---------------
        ConstraintLayout constraintLayout = view.findViewById(R.id.reloadConstraintLayout);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
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
                        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("lk.javainstitute.automotivex.automotivex.data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("userData");
                        editor.apply();
                        Intent intent = new Intent(view.getContext(),WelcomeActivity.class);
                        startActivity(intent);
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
        // --------------- End Logout Process ---------------







        // --------------- Start Load Dashboard Summery ---------------
        TextView totalRevenueTextView = view.findViewById(R.id.textView84);
        TextView totalOrdersTextView = view.findViewById(R.id.textView11);
        TextView totalAppointmentsTextView = view.findViewById(R.id.textView81);
        TextView userCountTextView = view.findViewById(R.id.textView79);

        firestore.collection("order")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();

                        long netTotal = 0 ;

                        totalOrdersTextView.setText(String.valueOf(documentSnapshotList.size())+ " Orders");

                        for(DocumentSnapshot documentSnapshot:documentSnapshotList){
                            long total = (long) documentSnapshot.get("total");
                            long processingFee = (long) documentSnapshot.get("processingFee");

                            netTotal = total + processingFee;
                            totalRevenueTextView.setText(FormatNumbers.formatPriceWithCurrencyCode(netTotal));
                        }

                    }
                });

        firestore.collection("appointment")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                        totalAppointmentsTextView.setText(String.valueOf(documentSnapshotList.size())+ " Appt.");

                    }
                });

        firestore.collection("user")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                        userCountTextView.setText(String.valueOf(documentSnapshotList.size())+ " Users");

                    }
                });
        // --------------- End Load Dashboard Summery ---------------




        // --------------- Start Pie Chart Loading Process ---------------

        HashMap<String, Integer> categoryValuesMap = new HashMap<>();

        firestore.collection("category")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots1) {
                        List<DocumentSnapshot> categoryDocumentSnapshotList = queryDocumentSnapshots1.getDocuments();
                        int loopSize =  categoryDocumentSnapshotList.size();
                        int currentlyRunningIndex = 0;

                        for (DocumentSnapshot categoryDocumentSnapshots: categoryDocumentSnapshotList){
                            currentlyRunningIndex++;
                            String categoryName = categoryDocumentSnapshots.get("name").toString();
                            categoryValuesMap.put(categoryName,0);


                            if(loopSize == currentlyRunningIndex){

                                //Loop over
                                firestore.collection("order")
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots1) {
                                                List<DocumentSnapshot> documentSnapshotList1 = queryDocumentSnapshots1.getDocuments();

                                                int loopSize2 =  documentSnapshotList1.size();
                                                int currentlyRunningIndex2 = 0;


                                                for(DocumentSnapshot documentSnapshot1 : documentSnapshotList1){
                                                    currentlyRunningIndex2++;
                                                    OrderDetails orderDetails = documentSnapshot1.toObject(OrderDetails.class);
                                                    orderDetails.setOrderDocId(documentSnapshot1.getId());

                                                    ArrayList<ProductDetails> productArrayList = orderDetails.getProduct();

                                                    //Log.i("AutoMotiveXLog", String.valueOf(productArrayList.size()));

                                                    for (ProductDetails productDetails:productArrayList) {

                                                        int shouldUpdate = categoryValuesMap.get(productDetails.getCategory()) + productDetails.getPurchaseQty();
                                                        categoryValuesMap.put(productDetails.getCategory() , shouldUpdate);
                                                    }


                                                    if(loopSize2 == currentlyRunningIndex2){
                                                        updateChart(categoryValuesMap,view);
                                                    }


                                                }

                                            }
                                        })
                                ;

                            }


                        }

                    }
                });

        // --------------- End Pie Chart Loading Process ---------------





    }

    private void updateChart(HashMap<String,Integer> categoryViseSoldQuantityMap, View view ){

        Log.i("AutoMotiveXLog", new Gson().toJson(categoryViseSoldQuantityMap));

        //Color Arraylist
        ArrayList<Integer> colorArrayList = new ArrayList<>();
        colorArrayList.add(ContextCompat.getColor(view.getContext(), R.color.chartColor2));
        colorArrayList.add(ContextCompat.getColor(view.getContext(), R.color.chartColor3));
        colorArrayList.add(ContextCompat.getColor(view.getContext(), R.color.chartColor4));

        PieChart pieChart1 = view.findViewById(R.id.pieChart1);
        ArrayList<PieEntry> pieEntries = new ArrayList<PieEntry>();


        for (HashMap.Entry<String, Integer> entry : categoryViseSoldQuantityMap.entrySet()) {
            if(entry.getValue()!=0){
            pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }
            //Log.i("AutoMotiveXLog", entry.getKey()+" : "+ entry.getValue());
        }


        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colorArrayList);

        PieData pieData = new PieData();
        pieData.setDataSet(pieDataSet);
        pieData.setValueTextSize(12);
        pieData.setValueTextColor(ContextCompat.getColor(view.getContext(), R.color.grey3));

        pieChart1.setData(pieData);
        pieChart1.setCenterText("");
        pieChart1.setHoleRadius(35f);
        pieChart1.setTransparentCircleRadius(0);
        pieChart1.setHoleColor(Color.TRANSPARENT);
        pieChart1.setDescription(null);
        pieChart1.animateY(2000, Easing.EaseInCubic);
        pieChart1.invalidate();
    }

}