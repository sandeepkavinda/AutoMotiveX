package lk.javainstitute.automotivex;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import lk.javainstitute.automotivex.dto.UserDto;
import lk.javainstitute.automotivex.model.AppointmentDetails;

public class AdminAppontmentsFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_appontments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("lk.javainstitute.automotivex.automotivex.data", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        UserDto userDto = gson.fromJson(sharedPreferences.getString("userData",null), UserDto.class);

        // -------------- Load Appointments Start ---------------

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        ArrayList<AppointmentDetails> appointmentDetailsArrayList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView myAppontmentsRecyclerView = view.findViewById(R.id.adminAppointmentsRecyclerView);
        myAppontmentsRecyclerView.setLayoutManager(linearLayoutManager);

        firestore.collection("appointment")
                .orderBy("requestedDateTime", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot documentSnapshot : documentSnapshotList){
                            AppointmentDetails appointmentDetails = documentSnapshot.toObject(AppointmentDetails.class);
                            appointmentDetails.setDocId(documentSnapshot.getId());

                            appointmentDetailsArrayList.add(appointmentDetails);
                            myAppontmentsRecyclerView.setAdapter(new AppointmentAdapter(appointmentDetailsArrayList,requireActivity(),userDto));

                        }

                    }
                })
        ;

        // -------------- Load Appointments End ---------------




    }
}