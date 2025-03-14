package lk.javainstitute.automotivex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import lk.javainstitute.automotivex.dto.UserDto;
import lk.javainstitute.automotivex.model.CartDetails;
import lk.javainstitute.automotivex.model.FormatNumbers;
import lk.javainstitute.automotivex.model.ProductDetails;
import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;

public class CheckoutActivity extends AppCompatActivity {

    // --------------- Start Define Variables ---------------
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String orderId;
    ArrayList<ProductDetails> defaultProductDetailsArrayList = new ArrayList<>();
    UserDto userDto;
    // --------------- End Define Variables ---------------



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --------------- Start Save User Dto ---------------
        SharedPreferences sharedPreferences = getSharedPreferences("lk.javainstitute.automotivex.automotivex.data", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        UserDto userDto = gson.fromJson(sharedPreferences.getString("userData", null), UserDto.class);
        this.userDto = userDto;
        // --------------- End Save User Dto ---------------

        // --------------- Start Get Intent Data ---------------
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        // --------------- End Get Intent Data ---------------


        // --------------- Start Load Data ---------------
        int total = getIntent().getIntExtra("total",0);
        int processingFee = total*10/100;
        int grandTotal = total+processingFee;

        TextView totalTextView = findViewById(R.id.textView36);
        TextView processingFree = findViewById(R.id.textView38);
        TextView grandTotalTextView = findViewById(R.id.textView39);

        totalTextView.setText(FormatNumbers.formatPriceWithCurrencyCode(total));
        processingFree.setText(FormatNumbers.formatPriceWithCurrencyCode(processingFee));
        grandTotalTextView.setText(FormatNumbers.formatPriceWithCurrencyCode(grandTotal));

        // --------------- End Load Data ---------------



        // --------------- Back Process ---------------
        ConstraintLayout constraintLayout = findViewById(R.id.backButtonConstraintLayout);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // --------------- Back Process ---------------



        // --------------- Start Cancel Button ---------------
        Button cancelButton = findViewById(R.id.alreadyHaveAccountButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // --------------- End Cancel Button ---------------


        //Check User Logged
        if (userDto != null) {

            // -------------- Start Onclick Confirm Button --------------
            Button confirmCheckoutButton = findViewById(R.id.confirmCheckoutButton);
            confirmCheckoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // -------------- Start Get text fields and entered data --------------
                    EditText firstNameEditText = findViewById(R.id.checkoutFirstNameEditText);
                    EditText lastNameEditText = findViewById(R.id.checkoutLastNameEditText);
                    EditText addressLine1EditText = findViewById(R.id.addressLine1EditText);
                    EditText addressLine2EditText = findViewById(R.id.addressLine2EditText);
                    EditText cityEditText = findViewById(R.id.cityEditText);
                    EditText postalCodeEditText = findViewById(R.id.postalCodeEditText);
                    EditText mobileEditText = findViewById(R.id.mobileEditText);
                    EditText emailEditText = findViewById(R.id.checkoutEmailEditText);
                    EditText noteEditText = findViewById(R.id.notesEditText);

                    String firstName = firstNameEditText.getText().toString().trim();
                    String lastName = lastNameEditText.getText().toString().trim();
                    String addressLine1 = addressLine1EditText.getText().toString().trim();
                    String addressLine2 = addressLine2EditText.getText().toString().trim();
                    String city = cityEditText.getText().toString().trim();
                    String postalCode = postalCodeEditText.getText().toString().trim();
                    String mobile = mobileEditText.getText().toString().trim();
                    String email = emailEditText.getText().toString().trim();
                    String note = noteEditText.getText().toString().trim();
                    // -------------- End Get text fields and entered data --------------

                    //Validate entered data
                    if (firstName.isEmpty()) {
                        showToast("Please enter your first name");
                    } else if (lastName.isEmpty()) {
                        showToast("Please enter your last name");
                    } else if (addressLine1.isEmpty()) {
                        showToast("Please enter address line 1");
                    } else if (city.isEmpty()) {
                        showToast("Please enter city");
                    } else if (postalCode.isEmpty()) {
                        showToast("Please enter postal code");
                    } else if (mobile.isEmpty()) {
                        showToast("Please enter mobile");
                    } else
                    if (email.isEmpty()) {
                        showToast("Please enter email");
                    } else {

                        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                        // ------------ Create Hash Map To Insert Order ------------
                        HashMap<String, Object> insertMap = new HashMap<>();
                        insertMap.put("first_name", firstName);
                        insertMap.put("last_name", lastName);
                        insertMap.put("address_line1", addressLine1);
                        insertMap.put("address_line2", addressLine2);
                        insertMap.put("postal_code", postalCode);
                        insertMap.put("city", city);
                        insertMap.put("mobile", mobile);
                        insertMap.put("note", note);
                        insertMap.put("orderedDateTime", currentDateTime);
                        insertMap.put("status", "Payment Confirmed");
                        insertMap.put("userDocId", userDto.getDocId());


                        //Check Checkout type (Cart or Single product)
                        if (type.equals("singleProduct")) {

                            // ---------------- Start Product Checkout ----------------
                            //  Get Inent data
                            String productDocId = intent.getStringExtra("productDocId");
                            int purchaseQty = intent.getIntExtra("purchaseQty", 0);

                            //Get Product by productDocId
                            firestore.collection("product")
                                    .document(productDocId)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            // --- Store Product Details ---
                                            ProductDetails productDetails = documentSnapshot.toObject(ProductDetails.class);
                                            productDetails.setDocId(documentSnapshot.getId());
                                            // --- Store Product Details ---

                                            //Save Default Product Data
                                            defaultProductDetailsArrayList.add(productDetails);

                                            // Calculate Total
                                            int total = productDetails.getPrice() * purchaseQty;
                                            int processingFee = total * 10 / 100;
                                            insertMap.put("total", total);
                                            insertMap.put("processingFee", processingFee);

                                            HashMap<String, Object> productHashMap = new HashMap<>();
                                            productHashMap.put("docId", productDetails.getDocId());
                                            productHashMap.put("title", productDetails.getTitle());
                                            productHashMap.put("imageUrl", productDetails.getImageUrl());
                                            productHashMap.put("price", productDetails.getPrice());
                                            productHashMap.put("purchaseQty", purchaseQty);
                                            productHashMap.put("category", productDetails.getCategory());

                                            ArrayList<HashMap<String, Object>> productsArrayList = new ArrayList<>();
                                            productsArrayList.add(productHashMap);
                                            insertMap.put("product", productsArrayList);
                                            // ------------ End Create Hash Map To Insert Order ------------


                                            // Insert order
                                            firestore
                                                    .collection("order")
                                                    .add(insertMap)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Log.i("AutoMotiveXLog", "Order Insert Added");

                                                            //Update New Poduct Quantity
                                                            HashMap<String, Object> updateMap = new HashMap<>();
                                                            int quantityToUpdate = productDetails.getQuantity() - purchaseQty;
                                                            updateMap.put("quantity", quantityToUpdate);

                                                            firestore
                                                                    .collection("product")
                                                                    .document(productDetails.getDocId())
                                                                    .update(updateMap);

                                                            orderId = documentReference.getId();
                                                            pay(total, orderId);
                                                        }
                                                    });


                                        }
                                    });
                            // ---------------- End Product Checkout ----------------

                        } else if (type.equals("cart")) {

                            // ---------------- Start Cart Checkout ----------------
                            firestore.collection("cart")
                                    .whereEqualTo("userDocId", userDto.getDocId())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();

                                            if (!documentSnapshotList.isEmpty()) {

                                                ArrayList<HashMap<String, Object>> productsArrayList = new ArrayList<>();
                                                int cartDataCount = documentSnapshotList.size();
                                                int runningDataIndex[] = {0};
                                                int total[] = {0};

                                                for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                                                    CartDetails cartDetails = documentSnapshot.toObject(CartDetails.class);
                                                    cartDetails.setDocId(documentSnapshot.getId());

                                                    firestore.collection("product")
                                                            .document(cartDetails.getProductDocId())
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    ProductDetails productDetails = task.getResult().toObject(ProductDetails.class);
                                                                    productDetails.setDocId(task.getResult().getId());
                                                                    cartDetails.setProductDetails(productDetails);

                                                                    //Save Default Product Data
                                                                    defaultProductDetailsArrayList.add(productDetails);

                                                                    //Calculate Order Total
                                                                    total[0] = total[0] + cartDetails.getProductDetails().getPrice() * cartDetails.getQuantity();

                                                                    // Create Product Map To Order Insert
                                                                    HashMap<String, Object> productHashMap = new HashMap<>();
                                                                    productHashMap.put("docId", cartDetails.getProductDetails().getDocId());
                                                                    productHashMap.put("title", cartDetails.getProductDetails().getTitle());
                                                                    productHashMap.put("imageUrl", cartDetails.getProductDetails().getImageUrl());
                                                                    productHashMap.put("price", cartDetails.getProductDetails().getPrice());
                                                                    productHashMap.put("purchaseQty", cartDetails.getQuantity());
                                                                    productHashMap.put("category", cartDetails.getProductDetails().getCategory());
                                                                    productsArrayList.add(productHashMap);

                                                                    //--------------- Start Update New Product Quantity ---------------
                                                                    HashMap<String, Object> updateMap = new HashMap<>();
                                                                    int quantityToUpdate = cartDetails.getProductDetails().getQuantity() - cartDetails.getQuantity();
                                                                    updateMap.put("quantity", quantityToUpdate);

                                                                    firestore
                                                                            .collection("product")
                                                                            .document(productDetails.getDocId())
                                                                            .update(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    Log.i("AutoMotiveXLog", "Product Quantity Updated");
                                                                                }
                                                                            })
                                                                    ;
                                                                    //--------------- End Update New Product Quantity ---------------

                                                                    runningDataIndex[0]++;


                                                                    //------------------ Start if End For Loop --------------------
                                                                    if (cartDataCount == runningDataIndex[0]) {

                                                                        Log.i("AutoMotiveXLog", "Ready To Purchase");

                                                                        // Add Calculated Data
                                                                        int processingFee = total[0] * 10 / 100;
                                                                        insertMap.put("total", total[0]);
                                                                        insertMap.put("processingFee", processingFee);
                                                                        insertMap.put("product", productsArrayList);

                                                                        // Insert order
                                                                        firestore
                                                                                .collection("order")
                                                                                .add(insertMap)
                                                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                    @Override
                                                                                    public void onSuccess(DocumentReference documentReference2) {
                                                                                        Log.i("AutoMotiveXLog", "Order Insert Added");

                                                                                        orderId = documentReference2.getId();
                                                                                        pay(total[0], orderId);
                                                                                    }
                                                                                });
                                                                    }
                                                                    //------------------ End If End For Loop --------------------
                                                                }
                                                            })
                                                    ;

                                                }

                                            } else {
                                                Log.i("AutoMotiveXLog", "No Products Found");
                                                finish();
                                            }


                                        }
                                    })
                            ;

                            // ---------------- End Cart Checkout ----------------


                            Log.i("AutoMotiveXLog", userDto.getDocId());
                        } else {
                            finish();
                            Log.e("AutoMotiveXLog", "Missed Intent Data");
                        }


                    }
                }
            });
            // -------------- End Onclick Confirm Button --------------

        } else {
            finish();
            Log.e("AutoMotiveXLog", "No User");
        }

    }

    // Toast function for reusability
    private void showToast(String message) {
        Toast.makeText(CheckoutActivity.this, message, Toast.LENGTH_LONG).show();
    }

    public void pay(int amount, String orderId) {

        this.orderId = orderId;

        new Thread(new Runnable() {
            @Override
            public void run() {
                InitRequest req = new InitRequest();
                req.setSandBox(true);
                req.setMerchantId("1221632");
                req.setCurrency("LKR");
                req.setAmount(amount);
                req.setOrderId(orderId);
                req.setItemsDescription("AutomotiveX");
                //req.setCustom1("MuscleMate Payment");
                //req.setCustom2("This is the custom message 2");
                req.getCustomer().setFirstName("");
                req.getCustomer().setLastName("");
                req.getCustomer().setEmail("");
                req.getCustomer().setPhone("");
                req.getCustomer().getAddress().setAddress("");
                req.getCustomer().getAddress().setCity("");
                req.getCustomer().getAddress().setCountry("Sri Lanka");

                //req.getCustomer().getDeliveryAddress().setAddress("");
                //req.getCustomer().getDeliveryAddress().setCity("");
                //req.getCustomer().getDeliveryAddress().setCountry("Sri Lanka");
                //req.getItems().add(new Item(null, "Door bell wireless", 1, 1000.0));

                try {
                    Intent intent = new Intent(CheckoutActivity.this, PHMainActivity.class);
                    intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
                    PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);

                    payHereLauncher.launch(intent);

                } catch (Exception e) {
                    Log.e("AutoMotiveXLog", "Payment initialization failed: " + e.getMessage());
                    Toast.makeText(CheckoutActivity.this, "Payment initialization failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }


    private final ActivityResultLauncher<Intent> payHereLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                boolean isPaymentSuccess = false;
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                    Intent data = result.getData();

                    if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {

                        Serializable serializable = data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                        if (serializable instanceof PHResponse) {

                            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) serializable;

                            if (response.isSuccess()) {
                                //Success
                                Log.d("AutoMotiveXLog", "Payment: Success");
                                isPaymentSuccess = true;

                                //Delete Cart if cart Checkout
                                if(getIntent().getStringExtra("type").equals("cart")){
                                    firestore.collection("cart")
                                            .whereEqualTo("userDocId",this.userDto.getDocId())
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();

                                                    for (DocumentSnapshot documentSnapshot:documentSnapshotList){
                                                        firestore.collection("cart")
                                                                .document(documentSnapshot.getId())
                                                                .delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Log.i("AutoMotiveXLog", "Cart Items Deleted");
                                                                    }
                                                                })
                                                        ;
                                                    }

                                                }
                                            });
                                }

                                Intent intent = new Intent(CheckoutActivity.this, SuccessOrderActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Log.d("AutoMotiveXLog", "Payment: Failed");
                            }
                        }
                    }

                } else {
                    Log.d("AutoMotiveXLog", "Payment: Canceled");
                }

                if (!isPaymentSuccess) {
                    // Product Quantity Restore
                    for (int x = 0; x < defaultProductDetailsArrayList.size(); x++) {
                        ProductDetails productDetails = defaultProductDetailsArrayList.get(0);

                        HashMap<String, Object> updateMap = new HashMap<>();
                        updateMap.put("quantity", productDetails.getQuantity());

                        firestore.collection("product")
                                .document(productDetails.getDocId())
                                .update(updateMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("AutoMotiveXLog", "Restored Product Quantity");
                                    }
                                });

                    }

                    // Delete Added Order Raw
                    firestore.collection("order")
                            .document(this.orderId)
                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.i("AutoMotiveXLog", "Order Row deleted");
                                }
                            });

                    Intent intent = new Intent(CheckoutActivity.this, FailedOrderActivity.class);
                    startActivity(intent);
                    finish();

                }

            }
    );


}