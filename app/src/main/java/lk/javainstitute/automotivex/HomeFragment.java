package lk.javainstitute.automotivex;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import lk.javainstitute.automotivex.dto.UserDto;
import lk.javainstitute.automotivex.model.FormatNumbers;
import lk.javainstitute.automotivex.model.Ngrok;
import lk.javainstitute.automotivex.model.ProductDetails;

public class HomeFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ScrollView homeMainScrollView = view.findViewById(R.id.homeMainScrollView);
        homeMainScrollView.setVisibility(INVISIBLE);

        // --------------- Variables  ---------------
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("lk.javainstitute.automotivex.automotivex.data", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        // --------------- Variables  ---------------


        // --------------- Start Get Logged User  ---------------
        UserDto userDto = gson.fromJson(sharedPreferences.getString("userData",null), UserDto.class);
        // --------------- End Get Logged User ---------------

        if(userDto!=null){
            // --------------- Start Load Welcome Text ---------------
            TextView welcomeTextView = view.findViewById(R.id.textView69);
            String welcomeText = "Welcome <b>"+ userDto.getFirst_name()+" "+userDto.getLast_name()+" !</b>";
            welcomeTextView.setText(Html.fromHtml(welcomeText));
            // --------------- End Load Welcome Text ---------------


            // --------------- Start Load Image Slider ---------------
            ImageSlider imageSlider =  view.findViewById(R.id.imageSlider);
            ArrayList<SlideModel> slideModelArrayList = new ArrayList<>();
            slideModelArrayList.add(new SlideModel(R.drawable.image1, ScaleTypes.FIT));
            slideModelArrayList.add(new SlideModel(R.drawable.image2, ScaleTypes.FIT));
            slideModelArrayList.add(new SlideModel(R.drawable.image3, ScaleTypes.FIT));
            slideModelArrayList.add(new SlideModel(R.drawable.image4, ScaleTypes.FIT));
            slideModelArrayList.add(new SlideModel(R.drawable.image5, ScaleTypes.FIT));
            slideModelArrayList.add(new SlideModel(R.drawable.image6, ScaleTypes.FIT));
            slideModelArrayList.add(new SlideModel(R.drawable.image7, ScaleTypes.FIT));
            imageSlider.setImageList(slideModelArrayList, ScaleTypes.FIT);
            // --------------- End Load Image Slider ---------------


            // --------------- Start Load Featured Products ---------------
            ArrayList<ProductDetails> featuredProductDetailsArrayList = new ArrayList<>();
            firestore
                    .collection("product")
                    .whereEqualTo("status","Active")
                    .orderBy("soldCount", Query.Direction.DESCENDING)
                    .limit(6)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot document : documentSnapshotList){
                                ProductDetails productDetails = document.toObject(ProductDetails.class);
                                productDetails.setDocId(document.getId());
                                featuredProductDetailsArrayList.add(productDetails);

                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
                                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                RecyclerView recyclerView1 = view.findViewById(R.id.recyclerView1);
                                recyclerView1.setLayoutManager(linearLayoutManager);

                                recyclerView1.setAdapter(new ProductAdapter(featuredProductDetailsArrayList));

                                homeMainScrollView.setVisibility(VISIBLE);

                                ProgressBar progressBar = view.findViewById(R.id.progressBar4);
                                progressBar.setVisibility(GONE);
                            }
                        }
                    });
            // --------------- End Load Featured Products ---------------


            // --------------- Start Load New Arrival Products ---------------
            ArrayList<ProductDetails> newArrivalProductDetailsArrayList = new ArrayList<>();
            firestore
                    .collection("product")
                    .orderBy("addedDate", Query.Direction.DESCENDING)
                    .limit(6)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot document : documentSnapshotList){
                                ProductDetails productDetails = document.toObject(ProductDetails.class);
                                productDetails.setDocId(document.getId());
                                newArrivalProductDetailsArrayList.add(productDetails);

                                LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(view.getContext());
                                linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
                                RecyclerView recyclerView2 = view.findViewById(R.id.recyclerView2);
                                recyclerView2.setLayoutManager(linearLayoutManager2);
                                recyclerView2.setAdapter(new ProductAdapter(newArrivalProductDetailsArrayList));

                            }
                        }
                    });
            // --------------- End Load New Arrival Products ---------------




            //Load Recycler 03
//        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(view.getContext());
//        linearLayoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
//        RecyclerView recyclerView3 = view.findViewById(R.id.recyclerView3);
//        recyclerView3.setLayoutManager(linearLayoutManager3);
//        recyclerView3.setAdapter(new ProductAdapter(productDetailsArrayList));

            //Search Text Focus
            TextView searchTextView = view.findViewById(R.id.editTextText6);
            searchTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SearchFragment searchFragment = new SearchFragment();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, searchFragment)
                            .commit();
                }
            });

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
            // --------------- Start Logout Process ---------------
        }else{
            Log.i("AutoMotiveXLog","User Not Logged");
        }




    }
}


class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{


    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView;
        TextView priceTextView;
        TextView ratingTextView;
        TextView stockStatusTextView;
        ImageView productImageView;
        ConstraintLayout productContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textView12);
            priceTextView = itemView.findViewById(R.id.textView14);
            ratingTextView = itemView.findViewById(R.id.textView23);
            productImageView =  itemView.findViewById(R.id.imageView4);
            stockStatusTextView =  itemView.findViewById(R.id.textView87);
            productContainer =  itemView.findViewById(R.id.productConstraintLayout);
        }
    }

    ArrayList<ProductDetails> productDetailsArrayList;

    public ProductAdapter(ArrayList<ProductDetails> productDetailsArrayList) {
        this.productDetailsArrayList = productDetailsArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_product_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductDetails productDetails = productDetailsArrayList.get(position);
        holder.titleTextView.setText(productDetails.getTitle());
        holder.priceTextView.setText(FormatNumbers.formatPriceWithCurrencyCode(productDetails.getPrice()));

        Glide.with(holder.itemView.getContext())
                .load(Ngrok.getUrl()+productDetails.getImageUrl())
                .placeholder(R.drawable.default_product_image)
                .error(R.drawable.default_product_image)
                .into(holder.productImageView);

        String ratingTextView = FormatNumbers.formatRatings(productDetails.getRating())+"("+String.valueOf(productDetails.getRatedCount())+") | " + String.valueOf(productDetails.getSoldCount()) +" Sold";

        holder.ratingTextView.setText(ratingTextView);

        if(productDetails.getQuantity()>0){
            holder.stockStatusTextView.setText("In Stock");
            holder.stockStatusTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary));

        }else{
            holder.stockStatusTextView.setText("Out of Stock");
            holder.stockStatusTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        }

        holder.productContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(),SingleProductActivity.class);
                intent.putExtra("productDocId",productDetails.getDocId());
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productDetailsArrayList.size();
    }





}