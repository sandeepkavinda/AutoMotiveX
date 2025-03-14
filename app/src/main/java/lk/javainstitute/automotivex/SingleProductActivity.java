package lk.javainstitute.automotivex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import lk.javainstitute.automotivex.dto.UserDto;
import lk.javainstitute.automotivex.model.CartDetails;
import lk.javainstitute.automotivex.model.FormatNumbers;
import lk.javainstitute.automotivex.model.Ngrok;
import lk.javainstitute.automotivex.model.ProductDetails;
import lk.javainstitute.automotivex.model.SQLiteHelper;

public class SingleProductActivity extends AppCompatActivity {

    ProductDetails productDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("lk.javainstitute.automotivex.automotivex.data", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        UserDto userDto = gson.fromJson(sharedPreferences.getString("userData", null), UserDto.class);

        //If have a User
        if (userDto != null) {

            // --------------- Start Load Product Data ---------------
            String productDocId = getIntent().getStringExtra("productDocId");

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("product")
                    .document(productDocId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (documentSnapshot != null) {

                                // --------------- Start Load Single Product View ---------------
                                productDetails = documentSnapshot.toObject(ProductDetails.class);
                                productDetails.setDocId(documentSnapshot.getId());

                                ImageView productImageView = findViewById(R.id.imageView17);
                                TextView mainTitleTextView = findViewById(R.id.textView65);
                                TextView titleTextView = findViewById(R.id.textView58);
                                TextView priceTextView = findViewById(R.id.textView59);
                                TextView ratingsTextView = findViewById(R.id.textView23);
                                TextView stockStatusTextView = findViewById(R.id.textView88);
                                EditText quantityEditText = findViewById(R.id.editTextText9);
                                TextView vehicleTextVIew = findViewById(R.id.textView24);
                                TextView categoryTextVIew = findViewById(R.id.textView25);
                                TextView descriptionTextVIew = findViewById(R.id.textView26);

                                Glide.with(SingleProductActivity.this)
                                        .load(Ngrok.getUrl() + productDetails.getImageUrl())
                                        .placeholder(R.drawable.default_product_image)
                                        .error(R.drawable.default_product_image)
                                        .into(productImageView);
                                mainTitleTextView.setText(productDetails.getTitle());
                                titleTextView.setText(productDetails.getTitle());
                                priceTextView.setText(FormatNumbers.formatPriceWithCurrencyCode(productDetails.getPrice()));

                                String ratingText = productDetails.getRating() + "(" + productDetails.getRatedCount() + ") | " + productDetails.getSoldCount() + " Sold";
                                ratingsTextView.setText(ratingText);

                                quantityEditText.setText("1");
                                vehicleTextVIew.setText(productDetails.getVehicle());
                                categoryTextVIew.setText(productDetails.getCategory());
                                descriptionTextVIew.setText(productDetails.getDescription());

                                if (productDetails.getQuantity() > 0) {
                                    stockStatusTextView.setText("In Stock (" + productDetails.getQuantity() + " items available)");
                                    stockStatusTextView.setTextColor(ContextCompat.getColor(SingleProductActivity.this, R.color.primary));
                                } else {
                                    stockStatusTextView.setText("Out Of Stock");
                                    stockStatusTextView.setTextColor(ContextCompat.getColor(SingleProductActivity.this, R.color.red));
                                }
                                // --------------- End Load Single Product View ---------------


                                // --------------- Start Buy Now Process ---------------
                                Button buyNowButton = findViewById(R.id.buyNowButton);
                                buyNowButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        try {
                                            String enteredQuantity = quantityEditText.getText().toString();

                                            if(enteredQuantity.isEmpty()){
                                                Toast.makeText(SingleProductActivity.this, "Enter Quantity", Toast.LENGTH_SHORT).show();
                                            }else if(Integer.parseInt(enteredQuantity)==0){
                                                Toast.makeText(SingleProductActivity.this, "Quantity Cannot be 0", Toast.LENGTH_SHORT).show();
                                            }else if(productDetails.getQuantity() < Integer.parseInt(enteredQuantity)){
                                                Toast.makeText(SingleProductActivity.this, "Only " + productDetails.getQuantity() + " items available in stock", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Intent intent = new Intent(SingleProductActivity.this, CheckoutActivity.class);
                                                intent.putExtra("type","singleProduct");
                                                intent.putExtra("productDocId",productDetails.getDocId());
                                                intent.putExtra("purchaseQty",Integer.parseInt(enteredQuantity));
                                                intent.putExtra("total",productDetails.getPrice()*Integer.parseInt(enteredQuantity));
                                                startActivity(intent);
                                            }

                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }


                                    }
                                });
                                // --------------- End Buy Now Process ---------------


                                // --------------- Start Add TO cart Process ---------------
                                Button addToCartButton = findViewById(R.id.addToCartButton);
                                addToCartButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String enteredQuantity = quantityEditText.getText().toString();

                                        //Validate Entered Quantity
                                        if (enteredQuantity.isEmpty()) {
                                            Toast.makeText(SingleProductActivity.this, "Enter Quantity", Toast.LENGTH_SHORT).show();
                                        }else if(Integer.parseInt(enteredQuantity)==0){
                                            Toast.makeText(SingleProductActivity.this, "Quantity Cannot be 0", Toast.LENGTH_SHORT).show();
                                        } else if (Integer.parseInt(enteredQuantity) > productDetails.getQuantity()) {
                                            Toast.makeText(SingleProductActivity.this, "Only " + productDetails.getQuantity() + " items available in stock", Toast.LENGTH_SHORT).show();
                                        } else {

                                            firestore
                                                    .collection("cart")
                                                    .whereEqualTo("userDocId",userDto.getDocId())
                                                    .whereEqualTo("productDocId",productDetails.getDocId())
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                                                            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                                                            if(!documentSnapshotList.isEmpty()){
                                                                //Already Have Cart Item
                                                                Log.i("AutoMotiveXLog","Already Have Cart Data");
                                                                DocumentSnapshot cartDocument = documentSnapshotList.get(0);
                                                                CartDetails cartDetails = cartDocument.toObject(CartDetails.class);
                                                                cartDetails.setDocId(cartDocument.getId());

                                                                int newQuantity = cartDetails.getQuantity() + Integer.parseInt(enteredQuantity);

                                                                if (newQuantity > productDetails.getQuantity()) {
                                                                    Toast.makeText(SingleProductActivity.this, "Available quantity exceeded. You've already added " + String.valueOf(cartDetails.getQuantity() ) + " items to your cart.", Toast.LENGTH_LONG).show();
                                                                } else {
                                                                    HashMap<String, Object> updateDataHashMap = new HashMap<>();
                                                                    updateDataHashMap.put("quantity", newQuantity);
                                                                    updateDataHashMap.put("addedDateTime", currentDateTime);

                                                                    firestore.collection("cart")
                                                                            .document(cartDetails.getDocId())
                                                                            .update(updateDataHashMap)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    View mainView = findViewById(R.id.main);
                                                                                    Snackbar.make(mainView, "Cart Updated successfully", Snackbar.LENGTH_LONG)
                                                                                            .setAction("View Cart", new View.OnClickListener() {
                                                                                                @Override
                                                                                                public void onClick(View view) {
                                                                                                    Intent intent = new Intent(SingleProductActivity.this, HomeActivity.class);
                                                                                                    intent.putExtra("loadCart", true);
                                                                                                    startActivity(intent);
                                                                                                }
                                                                                            }).show();
                                                                                }
                                                                            });
                                                                }

                                                            }else{
                                                                //Already Don't Have Cart Item
                                                                Log.i("AutoMotiveXLog","Already Don't Have Cart Data");

                                                                HashMap<String, Object> updateDataHashMap = new HashMap<>();
                                                                updateDataHashMap.put("quantity", Integer.parseInt(enteredQuantity));
                                                                updateDataHashMap.put("userDocId", userDto.getDocId());
                                                                updateDataHashMap.put("productDocId", productDetails.getDocId());
                                                                updateDataHashMap.put("addedDateTime", currentDateTime);

                                                                firestore.collection("cart")
                                                                        .add(updateDataHashMap)
                                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentReference documentReference) {
                                                                                View mainView = findViewById(R.id.main);
                                                                                Snackbar.make(mainView, "Successfully added to your cart", Snackbar.LENGTH_LONG)
                                                                                        .setAction("View Cart", new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View view) {
                                                                                                Intent intent = new Intent(SingleProductActivity.this, HomeActivity.class);
                                                                                                intent.putExtra("loadCart", true);
                                                                                                startActivity(intent);
                                                                                            }
                                                                                        }).show();
                                                                            }
                                                                        });
                                                            }

                                                        }
                                                    });


                                        }


                                    }
                                });
                                // --------------- End Add TO cart Process ---------------

                            } else {
                                finish();
                            }

                        }
                    });


            // --------------- End Load Product Data ---------------


            // --------------- Back Process ---------------
            ConstraintLayout constraintLayout = findViewById(R.id.backButtonConstraintLayout);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            // --------------- Back Process ---------------

            // --------------- Start Load Related Products ---------------
            ArrayList<ProductDetails> relatedProductDetailsArrayList = new ArrayList<>();
            firestore
                    .collection("product")
                    .orderBy("soldCount", Query.Direction.DESCENDING)
                    .limit(6)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot document : documentSnapshotList) {
                                ProductDetails productDetails = document.toObject(ProductDetails.class);
                                productDetails.setDocId(document.getId());
                                relatedProductDetailsArrayList.add(productDetails);
                            }

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SingleProductActivity.this);
                            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            RecyclerView recyclerView1 = findViewById(R.id.recyclerView1);
                            recyclerView1.setLayoutManager(linearLayoutManager);

                            recyclerView1.setAdapter(new ProductAdapter(relatedProductDetailsArrayList));

                        }
                    });
            // --------------- End Load Related Products ---------------


        } else {
            finish();
        }


    }
}