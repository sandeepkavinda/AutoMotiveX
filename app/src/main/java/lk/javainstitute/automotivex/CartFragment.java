package lk.javainstitute.automotivex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lk.javainstitute.automotivex.dto.UserDto;
import lk.javainstitute.automotivex.model.CartDetails;
import lk.javainstitute.automotivex.model.FormatNumbers;
import lk.javainstitute.automotivex.model.Ngrok;
import lk.javainstitute.automotivex.model.ProductDetails;

public class CartFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --------------- Variables  ---------------
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("lk.javainstitute.automotivex.automotivex.data", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        // --------------- Variables  ---------------


        // --------------- Start Get Logged User  ---------------
        UserDto userDto = gson.fromJson(sharedPreferences.getString("userData", null), UserDto.class);
        // --------------- End Get Logged User ---------------


        // --------------- Start Load Cart Items  ---------------
        ArrayList<CartDetails> cartItemArrayList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(linearLayoutManager);

        final int[] subtotal = {0};

        firestore.collection("cart")
                .whereEqualTo("userDocId",userDto.getDocId())
                .orderBy("addedDateTime", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                        int listSize = documentSnapshotList.size();
                        int[] currentlyRunningIndex = {0};

                        for (DocumentSnapshot documentSnapshot : documentSnapshotList){
                            CartDetails  cartDetails = documentSnapshot.toObject(CartDetails.class);
                            cartDetails.setDocId(documentSnapshot.getId());

                            firestore.collection("product")
                                    .document(cartDetails.getProductDocId())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot2) {
                                            ProductDetails productDetails = documentSnapshot2.toObject(ProductDetails.class);
                                            productDetails.setDocId(documentSnapshot2.getId());
                                            cartDetails.setProductDetails(productDetails);

                                            cartItemArrayList.add(cartDetails);

                                            subtotal[0] += (cartDetails.getProductDetails().getPrice()*cartDetails.getQuantity());
                                            currentlyRunningIndex[0] = currentlyRunningIndex[0]+1;

                                            //Log.i("AutoMotiveXLog",cartDetails.getDocId());
                                            //Log.i("AutoMotiveXLog",cartDetails.getUserDocId());
                                            //Log.i("AutoMotiveXLog",cartDetails.getProductDocId());
                                            //Log.i("AutoMotiveXLog",cartDetails.getProductDetails().getTitle());
                                            //Log.i("AutoMotiveXLog",String.valueOf(cartDetails.getProductDetails().getPrice()));
                                            //Log.i("AutoMotiveXLog",String.valueOf(cartDetails.getQuantity()));

                                            cartRecyclerView.setAdapter(new CartAdapter(cartItemArrayList));
                                            cartRecyclerView.setNestedScrollingEnabled(false);
                                            cartRecyclerView.setHasFixedSize(true);

                                            if(currentlyRunningIndex[0]==listSize){
                                                Log.i("AutoMotiveXLog",String.valueOf(subtotal[0]));
                                                TextView subTotalTextView =  view.findViewById(R.id.textView36);
                                                TextView deliveryFeeTextView =  view.findViewById(R.id.textView38);
                                                TextView grandTotalTextView =  view.findViewById(R.id.textView39);

                                                int processingFee = subtotal[0]*10/100;

                                                subTotalTextView.setText(FormatNumbers.formatPriceWithCurrencyCode(subtotal[0]));
                                                deliveryFeeTextView.setText(FormatNumbers.formatPriceWithCurrencyCode(processingFee));
                                                grandTotalTextView.setText(FormatNumbers.formatPriceWithCurrencyCode(subtotal[0]+processingFee));


                                            }

                                        }
                                    });
                        }

                    }
                })
        ;
        // --------------- End Load Cart Items  ---------------


        // --------------- Start Reload Process  ---------------
        ConstraintLayout reloadConstraintLayout = view.findViewById(R.id.reloadConstraintLayout);
        reloadConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadCart();
            }
        });
        // --------------- End Reload Process  ---------------



        // --------------- Start Checkout Process  ---------------
        Button checkoutButton = view.findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get Cart Items
                firestore.collection("cart")
                        .whereEqualTo("userDocId",userDto.getDocId())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();

                                if(documentSnapshotList.isEmpty()){
                                    Toast.makeText(view.getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
                                }else{
                                    int cartDataCount = documentSnapshotList.size();
                                    int runningDataIndex[] = {0};
                                    boolean isReadyToCheckout[] = {true};
                                    int total[]= {0};

                                    // Get Product Details
                                    for (DocumentSnapshot documentSnapshot:documentSnapshotList){
                                        CartDetails cartDetails = documentSnapshot.toObject(CartDetails.class);
                                        cartDetails.setDocId(documentSnapshot.getId());

                                        firestore.collection("product")
                                                .document(cartDetails.getProductDocId())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        ProductDetails productDetails =  task.getResult().toObject(ProductDetails.class);
                                                        productDetails.setDocId(task.getResult().getId());
                                                        cartDetails.setProductDetails(productDetails);

                                                        total[0] = total[0]+(cartDetails.getProductDetails().getPrice()*cartDetails.getQuantity());
                                                        runningDataIndex[0]++;

                                                        if(cartDetails.getQuantity() > cartDetails.getProductDetails().getQuantity()){

                                                            isReadyToCheckout[0] = false;

                                                            // Alert Box
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                                            builder.setTitle("Available Quantity Exceed");
                                                            builder.setMessage("Only "
                                                                    +cartDetails.getProductDetails().getQuantity()
                                                                    +" "
                                                                    +cartDetails.getProductDetails().getTitle()
                                                                    +" are available. Do you want to set the maximum available count to it")
                                                            ;

                                                            // Adjust Button
                                                            builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    HashMap<String, Object> updateMap =  new HashMap<>();
                                                                    updateMap.put("quantity", cartDetails.getProductDetails().getQuantity());

                                                                    firestore.collection("cart")
                                                                            .document(cartDetails.getDocId())
                                                                            .update(updateMap)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    Toast.makeText(view.getContext(), "Cart Updated Successfully", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            })
                                                                    ;
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

                                                            //Here exit

                                                        }else{
                                                            if(cartDataCount == runningDataIndex[0] && isReadyToCheckout[0]){
                                                                //Ready To Checkout
                                                                Log.i("AutoMotiveXLog","Ready To Checkout");
                                                                Intent intent = new Intent(view.getContext(), CheckoutActivity.class);
                                                                intent.putExtra("type","cart");
                                                                intent.putExtra("total",total[0]);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    }
                                                })
                                        ;

                                    }
                                }
                            }
                        })
                ;


            }
        });
        // --------------- End Checkout Process  ---------------

    }

    public void reloadCart (){
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CartFragment())
                .commit();
    }

}


class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>{


    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView;
        TextView priceTextView;
        TextView quantityTextView;
        TextView totalTextView;
        ImageView productImageView;
        ImageView closeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textView44);
            priceTextView = itemView.findViewById(R.id.textView45);
            quantityTextView = itemView.findViewById(R.id.textView46);
            totalTextView = itemView.findViewById(R.id.textView47);
            productImageView = itemView.findViewById(R.id.imageView6);
            closeButton = itemView.findViewById(R.id.imageView27);
        }
    }



    ArrayList<CartDetails> cartDetailsArrayList;


    public CartAdapter(ArrayList<CartDetails> cartDetailsArrayList) {
        this.cartDetailsArrayList = cartDetailsArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cart_item_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartDetails cartDetails = cartDetailsArrayList.get(position);
        holder.titleTextView.setText(cartDetails.getProductDetails().getTitle());
        holder.priceTextView.setText(FormatNumbers.formatPriceWithCurrencyCode(cartDetails.getProductDetails().getPrice()));
        String quantityText = "Qty : "+ cartDetails.getQuantity();
        holder.quantityTextView.setText(quantityText);

        double total = cartDetails.getProductDetails().getPrice()*cartDetails.getQuantity();

        holder.totalTextView.setText(FormatNumbers.formatPriceWithCurrencyCode(total));

        Glide.with(holder.productImageView.getContext())
                .load(Ngrok.getUrl()+cartDetails.getProductDetails().getImageUrl())
                .placeholder(R.drawable.default_product_image)
                .error(R.drawable.default_product_image)
                .into(holder.productImageView);

        holder.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to remove cart item?");

                // OK Button
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Remove Cart Item
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        firestore
                                .collection("cart")
                                .document(cartDetails.getDocId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(v.getContext(),"Cart Item Deleted",Toast.LENGTH_SHORT).show();
                                    }
                                })
                        ;
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
        return cartDetailsArrayList.size();
    }

}