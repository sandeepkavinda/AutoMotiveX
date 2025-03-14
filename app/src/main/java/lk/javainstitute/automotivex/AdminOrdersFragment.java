package lk.javainstitute.automotivex;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import lk.javainstitute.automotivex.dto.UserDto;
import lk.javainstitute.automotivex.model.OrderDetails;

public class AdminOrdersFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --------------- Variables  ---------------
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("lk.javainstitute.automotivex.automotivex.data", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        // --------------- Variables  ---------------

        // --------------- Start Get Logged User  ---------------
        UserDto userDto = gson.fromJson(sharedPreferences.getString("userData",null), UserDto.class);
        // --------------- End Get Logged User ---------------


        // --------------- Load Order Start ---------------
        ArrayList<OrderDetails> orderDetailsArrayList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView2);
        ordersRecyclerView.setLayoutManager(linearLayoutManager);

        firestore.collection("order")
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




    }
}