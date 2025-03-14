package lk.javainstitute.automotivex;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import lk.javainstitute.automotivex.model.Ngrok;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminProductFragment extends Fragment {

    Uri imageUri;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        // --------------- Start Load Spinners ---------------
        loadVehicles(view);
        loadCategories(view);
        // --------------- End Load Spinners ---------------


        // --------------- Start Select Product Image Button ---------------
        Button selectProductImageButton = view.findViewById(R.id.selectProductImageButton);
        selectProductImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
            }
        });
        // --------------- End Select Product Image Button ---------------


        // --------------- Start Add Product Process ---------------
        Button addProductButton = view.findViewById(R.id.addProductButton);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText productTitleEditText = view.findViewById(R.id.editTextText);
                Spinner vehicleSpinner = view.findViewById(R.id.vehicleSpinner2);
                Spinner categorySpinner = view.findViewById(R.id.categorySpinner2);
                EditText priceEditText = view.findViewById(R.id.priceEditText);
                EditText quantityTextText = view.findViewById(R.id.quantityTextText);
                EditText productDescriptionEditText = view.findViewById(R.id.productDescriptionEditText);

                String title = productTitleEditText.getText().toString().trim();
                String vehicle = vehicleSpinner.getSelectedItem().toString();
                String category = categorySpinner.getSelectedItem().toString();
                String price = priceEditText.getText().toString();
                String quantity = quantityTextText.getText().toString();
                String description = productDescriptionEditText.getText().toString();

                Log.i("AutomotiveXLog", title);
                Log.i("AutomotiveXLog", vehicle);
                Log.i("AutomotiveXLog", category);
                Log.i("AutomotiveXLog", price);
                Log.i("AutomotiveXLog", quantity);
                Log.i("AutomotiveXLog", description);

                // Check for empty fields
                if (title.isEmpty()) {
                    showToast("Please enter the product title.");
                    return;
                }
                if (vehicle.isEmpty() || vehicle.equals("Select Vehicle")) {
                    showToast("Please select a vehicle.");
                    return;
                }
                if (category.isEmpty() || category.equals("Select Category")) {
                    showToast("Please select a category.");
                    return;
                }
                if (price.isEmpty()) {
                    showToast("Please enter the product price.");
                    return;
                }

                // Validate price and quantity as numbers
                try {
                    double priceValue = Double.parseDouble(price);
                    if (priceValue <= 0) {
                        showToast("Price must be greater than 0.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    showToast("Invalid price. Please enter a valid number.");
                    return;
                }

                if (quantity.isEmpty()) {
                    showToast("Please enter the quantity.");
                    return;
                }

                try {
                    int quantityValue = Integer.parseInt(quantity);
                    if (quantityValue <= 0) {
                        showToast("Quantity must be greater than 0.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    showToast("Invalid quantity. Please enter a valid number.");
                    return;
                }

                if (description.isEmpty()) {
                    showToast("Please enter the product description.");
                    return;
                }

                if (imageUri==null) {
                    showToast("Please Select a Product Image");
                    return;
                }

                String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                HashMap<String,Object> productMap = new HashMap<>();
                productMap.put("addedDate",currentDateTime);
                productMap.put("category",category);
                productMap.put("description",description);
                productMap.put("price",Integer.parseInt(price));
                productMap.put("quantity",Integer.parseInt(quantity));
                productMap.put("ratedCount",0);
                productMap.put("rating",0);
                productMap.put("soldCount",0);
                productMap.put("title",title);
                productMap.put("vehicle",vehicle);
                productMap.put("status","Active");
                productMap.put("imageUrl","no url");

                firestore.collection("product")
                        .add(productMap)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                String docId = documentReference.getId();
                                uploadImage(imageUri,docId);
                            }
                        });

            }
        });
        // --------------- End Add Product Process ---------------


        // --------------- Start Clear All ---------------
        Button clearAllButton = view.findViewById(R.id.clearAllButton);
        clearAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.admin_fragment_container, new AdminProductFragment())
                        .commit();
            }
        });
        // --------------- End Clear All ---------------


    }

    // Toast function for reusability
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    private void loadVehicles(View view){

        Spinner vehicleSpinner = view.findViewById(R.id.vehicleSpinner2);
        ArrayList<String> vehicleArraylist = new ArrayList<>();
        vehicleArraylist.add("Select Vehicle");

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

        Spinner categorySpinner = view.findViewById(R.id.categorySpinner2);
        ArrayList<String> categoryArraylist = new ArrayList<>();
        categoryArraylist.add("Select Category");

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




    private void checkPermissions() {
        boolean allPermissionAllowed = ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED ;

        if(allPermissionAllowed){
            Log.i("MyLog","Access Grained Already");
            pickImage();
        }else{
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
            }, 100);
        }
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                Log.i("MyLog","Access Grained Now");
                pickImage();
            } else {
                Toast.makeText(getContext(), "Camera Permissions Denied! Please allow it in settings.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void pickImage() {
        ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .galleryOnly()
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData(); // Get selected image URI

            // Set the image to ImageView
            ImageView imageView = requireView().findViewById(R.id.imageView25);
            imageView.setImageURI(imageUri);
        } else {
            Toast.makeText(requireContext(), "Image selection failed!", Toast.LENGTH_SHORT).show();
        }
    }

    //Get Real Image Path From URI
    private String getRealPathFromURI(Uri uri) {
        String result = null;
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void uploadImage(Uri imageUri, String docId) {

        File file = getFileFromUri(imageUri);

        // Create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

        // Create MultipartBody.Part
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        // Build Multipart Request
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName(), requestFile)
                .addFormDataPart("documentId", docId) //
                .build();

        // Create OkHttp Request
        Request request = new Request.Builder()
                .url(Ngrok.getUrl()+"/AutomotiveX_Backend/SaveProductImage")
                .post(requestBody)
                .build();

        // Execute the request in a background thread
        OkHttpClient client = new OkHttpClient();
        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseJsonText = response.body().string();
                    Log.d("AutoMotiveXLog", "Success: " + responseJsonText);
                    JsonObject jsonObject = new Gson().fromJson(responseJsonText,JsonObject.class);

                    Boolean success = jsonObject.get("success").getAsBoolean();

                    //Responce Success
                    if(success){
                        HashMap<String, Object> updatingDataHashMap = new HashMap<>();
                        updatingDataHashMap.put("imageUrl", jsonObject.get("content").getAsString());

                        firestore.collection("product")
                                .document(docId)
                                .update(updatingDataHashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        requireActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                // Make A Alert For Success
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Success");
                                                builder.setMessage("Product Added Successfully");

                                                // OK Button
                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        requireActivity().getSupportFragmentManager().beginTransaction()
                                                                .replace(R.id.admin_fragment_container, new AdminProductFragment())
                                                                .commit();
                                                    }
                                                });

                                                // Show the AlertDialog
                                                AlertDialog alert = builder.create();
                                                alert.show();
                                            }
                                        });
                                        Log.i("AutoMotiveXLog","Image Url Added To Database");
                                    }
                                });
                    }else{
                        firestore.collection("product")
                                .document(docId).
                                delete();
                        Toast.makeText(getContext(), "Some thing Went Wrong", Toast.LENGTH_SHORT).show();
                        Log.i("AutoMotiveXLog","Back-End response not success");
                    }

                } else {
                    Log.e("Upload", "Failed: " + response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private File getFileFromUri(Uri uri) {
        File file = new File(requireContext().getCacheDir(), "temp_image.jpg");
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }



}