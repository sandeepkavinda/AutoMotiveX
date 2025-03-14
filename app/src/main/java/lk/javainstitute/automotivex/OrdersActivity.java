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
import android.util.Log;
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

import lk.javainstitute.automotivex.dto.UserDto;
import lk.javainstitute.automotivex.model.AppointmentDetails;
import lk.javainstitute.automotivex.model.FormatNumbers;
import lk.javainstitute.automotivex.model.OrderDetails;

public class OrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_orders);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --------------- Variables  ---------------
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("lk.javainstitute.automotivex.automotivex.data", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        // --------------- Variables  ---------------

        // --------------- Start Get Logged User  ---------------
        UserDto userDto = gson.fromJson(sharedPreferences.getString("userData",null), UserDto.class);
        // --------------- End Get Logged User ---------------


        // --------------- Load Order Start ---------------
        ArrayList<OrderDetails> orderDetailsArrayList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrdersActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        ordersRecyclerView.setLayoutManager(linearLayoutManager);

        firestore.collection("order")
                .whereEqualTo("userDocId", userDto.getDocId())
                .orderBy("orderedDateTime", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();

                        Log.i("AutoMotiveXLog",String.valueOf(documentSnapshotList.size()));

                        for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                            OrderDetails orderDetails = documentSnapshot.toObject(OrderDetails.class);
                            orderDetails.setOrderDocId(documentSnapshot.getId());

                            orderDetailsArrayList.add(orderDetails);

                        }
                        ordersRecyclerView.setAdapter(new OrdersAdapter(orderDetailsArrayList, userDto));

                    }
                })
        ;



        // --------------- Load Order End ---------------



        // --------------- Back Process ---------------
        ConstraintLayout constraintLayout = findViewById(R.id.backButtonConstraintLayout);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // --------------- Back Process ---------------





    }
}


class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView orderIdTextView;
        TextView nameTextView;
        TextView addressTextView;
        TextView cityTextView;
        TextView postalCodeTextView;
        TextView mobileTextView;
        TextView statusTextView;
        TextView orderedDateTextView;
        TextView totalTextView;
        TextView processingFeeTextView;
        TextView grandTotalTextView;
        FrameLayout deliveredButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.textView91);
            nameTextView = itemView.findViewById(R.id.textView92);
            addressTextView = itemView.findViewById(R.id.textView94);
            cityTextView = itemView.findViewById(R.id.textView101);
            postalCodeTextView = itemView.findViewById(R.id.textView102);
            mobileTextView = itemView.findViewById(R.id.textView103);
            statusTextView = itemView.findViewById(R.id.textView99);
            orderedDateTextView = itemView.findViewById(R.id.textView97);
            totalTextView = itemView.findViewById(R.id.textView104);
            processingFeeTextView = itemView.findViewById(R.id.textView95);
            grandTotalTextView = itemView.findViewById(R.id.textView96);
            deliveredButton = itemView.findViewById(R.id.acceptButtonFrameLayout);
        }
    }

    ArrayList<OrderDetails> orderDetailsArrayList;
    UserDto userDto;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public OrdersAdapter(ArrayList<OrderDetails> orderDetailsArrayList,  UserDto userDto) {
        this.orderDetailsArrayList = orderDetailsArrayList;
        this.userDto = userDto;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_order_item, parent, false);
        return new OrdersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetails orderDetails = orderDetailsArrayList.get(position);
        Context context = holder.orderIdTextView.getContext();

        holder.orderIdTextView.setText("Order Id : " + orderDetails.getOrderDocId());
        holder.nameTextView.setText(orderDetails.getFirst_name()+" "+orderDetails.getLast_name());
        holder.addressTextView.setText(orderDetails.getAddress_line1()+" "+orderDetails.getAddress_line2());
        holder.cityTextView.setText(orderDetails.getCity());
        holder.postalCodeTextView.setText(orderDetails.getPostal_code());
        holder.mobileTextView.setText(orderDetails.getMobile());
        holder.statusTextView.setText(orderDetails.getStatus());
        holder.totalTextView.setText(FormatNumbers.formatPriceWithCurrencyCode(orderDetails.getTotal()));
        holder.processingFeeTextView.setText( FormatNumbers.formatPriceWithCurrencyCode(orderDetails.getProcessingFee()));
        int grandTotal = orderDetails.getTotal() + orderDetails.getProcessingFee();
        holder.grandTotalTextView.setText("Total : "+ FormatNumbers.formatPriceWithCurrencyCode(grandTotal));

        if(userDto.getType().equals("Admin")){

            holder.deliveredButton.setVisibility(GONE);

        }else if(userDto.getType().equals("User")){

            if(orderDetails.getStatus().equals("Payment Confirmed")){
                holder.deliveredButton.setVisibility(VISIBLE);
            }else if(orderDetails.getStatus().equals("Delivered")){
                holder.deliveredButton.setVisibility(GONE);
            }

        }

        holder.deliveredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to set this to delivered order");

                // OK Button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String, Object> updateMap = new HashMap<>();
                        updateMap.put("status", "Delivered");

                        firestore.collection("order")
                                .document(orderDetails.getOrderDocId())
                                .update(updateMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Product Delivered. Thank You fo your Order.", Toast.LENGTH_LONG).show();
                                    }
                                });
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





    }

    @Override
    public int getItemCount() {
        return orderDetailsArrayList.size();
    }


}