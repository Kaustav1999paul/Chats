package com.example.chats.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.chats.BottomSheetFragment;
import com.example.chats.EditAccountActivity;
import com.example.chats.HomeActivity;
import com.example.chats.LogRegActivity;
import com.example.chats.Login;
import com.example.chats.MoreSettingsActivity;
import com.example.chats.NotificationsActivity;
import com.example.chats.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import id.voela.actrans.AcTrans;

public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }

    FloatingActionButton editAccount;
    FirebaseUser user;
    ImageView avatar, weatherIcon;
    Toolbar tabanim_toolbar;
    LinearLayout logout,notifications,settings;
    TextView HolderName, emailHolder, bioAcc,temperature,mes,wish;
    DatabaseReference userRef;
    public static Context context;
    private String url;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    double latitude, longitude;
    private Context contextT;
    @Override
    public void onStart() {
        super.onStart();
       contextT  = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        bioAcc = view.findViewById(R.id.bioAcc);
        logout = view.findViewById(R.id.logout);
        weatherIcon = view.findViewById(R.id.weatherIcon);
        temperature = view.findViewById(R.id.temperature);
        mes = view.findViewById(R.id.mes);
        wish = view.findViewById(R.id.wish);
        tabanim_toolbar = view.findViewById(R.id.tabanim_toolbar);
        contextT  = getActivity();

        if (ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION
            );
        } else {
            getLocation();
        }

        getTemperature();

        Date date = new Date();
        // Pattern
        SimpleDateFormat sdf = new SimpleDateFormat("a");

        String time = sdf.format(date);
        if (time.equals("am"))
            wish.setText("Good Morning,");
        else
            wish.setText("Good Evening,");



        context = getActivity().getApplicationContext();
        notifications = view.findViewById(R.id.notifications);
        settings = view.findViewById(R.id.settings);
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        editAccount = view.findViewById(R.id.editAccount);
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NotificationsActivity.class);
                startActivity(intent);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MoreSettingsActivity.class);
                startActivity(intent);
            }
        });

        editAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditAccountActivity.class);
                startActivity(intent);
                new AcTrans.Builder(getActivity()).performSlideToLeft();
//                BottomSheetFragment bottomSheetDialog = BottomSheetFragment.newInstance();
//                bottomSheetDialog.show(getActivity().getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
            }
        });
        HolderName = view.findViewById(R.id.holderName);
        emailHolder = view.findViewById(R.id.holderEmail);
        avatar = view.findViewById(R.id.avatar);
        emailHolder.setText(user.getEmail());


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(),LogRegActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetFragment bottomSheetDialog = BottomSheetFragment.newInstance();
                bottomSheetDialog.show(getActivity().getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
            }
        });
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String na = snapshot.child("username").getValue().toString();
                HolderName.setText(na);
                String bio = snapshot.child("bio").getValue().toString();
                bioAcc.setText("Bio: "+bio);
                String ph = snapshot.child("imageURL").getValue().toString();
                Glide.with(context).load(ph).into(avatar);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocation() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(getContext())
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices
                                .getFusedLocationProviderClient(contextT)
                                .removeLocationUpdates(this);

                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                        }
                    }
                }, Looper.getMainLooper());
    }

    private void getTemperature() {
        url ="https://api.openweathermap.org/data/2.5/onecall?lat="+latitude+"&lon="+longitude+"&units=metric&exclude=hourly,daily&appid=24fadf0771f572e79650afaf3373566e";


        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject object = new JSONObject(response);
                    try{
                        JSONObject op = object.getJSONObject("current");
                        double temp = Double.parseDouble(op.getString("temp"));
                        int aa = (int) Math.round(temp);
                        String bb = String.valueOf(aa);
                        temperature.setText(bb);

                        JSONArray jArray3 = op.getJSONArray("weather");
                        for(int i = 0; i < jArray3.length(); i++){
                            JSONObject object3 = jArray3.getJSONObject(i);

                            String state = object3.getString("main");

                            if (state.equals("Clear")){
                                mes.setText(state);
                                weatherIcon.setBackgroundResource(R.drawable.ic_round_wb_sunny_24);
                            }else if (state.equals("Clouds")){
                                mes.setText(state);
                                weatherIcon.setBackgroundResource(R.drawable.ic_baseline_cloud_24);
                            }else if (state.equals("Atmosphere")){
                                mes.setText(state);
                                weatherIcon.setBackgroundResource(R.drawable.ic_round_waves_24);
                            }else if (state.equals("Snow")){
                                mes.setText(state);
                                weatherIcon.setBackgroundResource(R.drawable.ic_round_ac_unit_24);
                            }else if (state.equals("Rain")){
                                mes.setText(state);
                                weatherIcon.setBackgroundResource(R.drawable.ic_rain_svgrepo_com);
                            }else if (state.equals("Drizzle")){
                                mes.setText(state);
                                weatherIcon.setBackgroundResource(R.drawable.ic_drizzle_svgrepo_com);
                            }else if (state.equals("Thunderstorm")){
                                mes.setText(state);
                                weatherIcon.setBackgroundResource(R.drawable.ic_storm_thunder_svgrepo_com);
                            }
                        }




                    }catch(JSONException e){
                        String x = e.getMessage();
                        Toast.makeText(getContext(), "Error0: "+x, Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException ex){
                    String a = ex.getMessage();
                    Toast.makeText(getContext(), "Error1: "+a, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errormessage = error.getMessage().toString();

                Toast.makeText(getContext(), "Error2: "+errormessage, Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue req = Volley.newRequestQueue(getContext());
        req.add(request);
    }

}