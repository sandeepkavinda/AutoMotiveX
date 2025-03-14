package lk.javainstitute.automotivex;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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

import lk.javainstitute.automotivex.model.ProductDetails;

public class SearchFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Gson gson = new Gson();


        loadVehicles(view);
        loadCategories(view);


        // --------------- Start Load All Products ---------------


        firestore.collection("product")
                .whereEqualTo("status","Active")
                .orderBy("price", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                        ArrayList<ProductDetails> allProductDetailsArrayList = new ArrayList<>();

                        for(DocumentSnapshot document : documentSnapshotList){
                            ProductDetails productDetails = document.toObject(ProductDetails.class);
                            productDetails.setDocId(document.getId());

                            allProductDetailsArrayList.add(productDetails);

                        }
                        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 2);
                        RecyclerView recyclerView1 = view.findViewById(R.id.recylclerView5);
                        recyclerView1.setLayoutManager(layoutManager);
                        recyclerView1.setAdapter(new ProductAdapter(allProductDetailsArrayList));
                        recyclerView1.setNestedScrollingEnabled(false);
                        recyclerView1.setHasFixedSize(true);
                    }
                });
        // --------------- End Load All Products ---------------


        Button searchButton2 = view.findViewById(R.id.searchButton2);
        searchButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText searchEditText = view.findViewById(R.id.editTextText6);
                Spinner vehcleSpinner = view.findViewById(R.id.vehicleSpinner);
                Spinner categorySpinner = view.findViewById(R.id.categorySpinner);
                EditText priceFromEditText = view.findViewById(R.id.editTextText8);
                EditText priceToEditText = view.findViewById(R.id.editTextText7);

                String searchText = searchEditText.getText().toString().trim();
                String priceFrom = priceFromEditText.getText().toString().trim();
                String priceTo = priceToEditText.getText().toString().trim();
                String vehicleType = vehcleSpinner.getSelectedItem().toString();
                String category = categorySpinner.getSelectedItem().toString();

                Log.i("AutomotiveXLog", "filterShopItems: " + searchText + ", " + priceFrom + ", " + priceTo + ", " + vehicleType+ ", " + category);

                Query query = firestore.collection("product").whereEqualTo("status","Active");

                if(!priceFrom.isEmpty()){
                    query = query.whereGreaterThanOrEqualTo("price", Integer.parseInt(priceFrom));
                }

                if(!priceTo.isEmpty()){
                    query = query.whereLessThanOrEqualTo("price", Integer.parseInt(priceTo));
                }

                if(vehicleType!="All Vehicles"){
                    query = query.whereEqualTo("vehicle", vehicleType);
                }

                if(category!="All Categories"){
                    query = query.whereEqualTo("category", category);
                }

                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                        ArrayList<ProductDetails> searchProductDetailsArrayList = new ArrayList<>();

                        for(DocumentSnapshot document : documentSnapshotList){
                            ProductDetails productDetails = document.toObject(ProductDetails.class);
                            productDetails.setDocId(document.getId());

                            if(productDetails.getTitle().toLowerCase().contains(searchText.toLowerCase())){
                                searchProductDetailsArrayList.add(productDetails);
                            }

                        }
                        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 2);
                        RecyclerView recyclerView1 = view.findViewById(R.id.recylclerView5);
                        TextView noItemsTextView = view.findViewById(R.id.textView110);
                        recyclerView1.setLayoutManager(layoutManager);
                        recyclerView1.setAdapter(new ProductAdapter(searchProductDetailsArrayList));
                        recyclerView1.setNestedScrollingEnabled(false);
                        recyclerView1.setHasFixedSize(true);

                        if(documentSnapshotList.size()==0){
                            noItemsTextView.setVisibility(View.VISIBLE);
                            recyclerView1.setVisibility(View.GONE);
                        }else{
                            noItemsTextView.setVisibility(View.GONE);
                            recyclerView1.setVisibility(View.VISIBLE);
                        }


                    }
                });


            }
        });



    }

    private void loadVehicles(View view){

        Spinner vehicleSpinner = view.findViewById(R.id.vehicleSpinner);
        ArrayList<String> vehicleArraylist = new ArrayList<>();
        vehicleArraylist.add("All Vehicles");

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("vehicle")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot document: queryDocumentSnapshots){
                            String vehicleName = document.getString("name");
                            if (vehicleName != null) {
                                vehicleArraylist.add(vehicleName);
                            }
                        }
                    }
                });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.simple_spinner_item,
                vehicleArraylist
        );

        vehicleSpinner.setAdapter(arrayAdapter);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void loadCategories(View view){

        Spinner categorySpinner = view.findViewById(R.id.categorySpinner);
        ArrayList<String> categoryArraylist = new ArrayList<>();
        categoryArraylist.add("All Categories");

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("category")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot document: queryDocumentSnapshots){
                            String categoryName = document.getString("name");
                            if (categoryName != null) {
                                categoryArraylist.add(categoryName);
                            }
                        }
                    }
                });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                view.getContext(),
                android.R.layout.simple_spinner_item,
                categoryArraylist
        );

        categorySpinner.setAdapter(arrayAdapter);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

}