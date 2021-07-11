package com.example.chats.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androdocs.httprequest.HttpRequest;
import com.bumptech.glide.Glide;
import com.example.chats.BottomSheetFragment;
import com.example.chats.EditAccountActivity;
import com.example.chats.FriendsActivity;
import com.example.chats.LogRegActivity;
import com.example.chats.Login;
import com.example.chats.MessageActivity;
import com.example.chats.MoreSettingsActivity;
import com.example.chats.NotificationsActivity;
import com.example.chats.PeopleNearActivity;
import com.example.chats.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import id.voela.actrans.AcTrans;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }

    FloatingActionButton editAccount;
    FirebaseUser user;
    ImageView avatar, weatherIcon,refresh;
    Toolbar tabanim_toolbar;
    LinearLayout logout,notifications,videoAdd,yourFriends, peopleNear,contactUs;
    TextView HolderName, emailHolder, bioAcc,temperature,mes,wish;
    DatabaseReference userRef;
    public static Context context;
    private Context contextT;
    String locality, API="24fadf0771f572e79650afaf3373566e";
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    BottomSheetDialog logoutDialog;

    public static final String MY_PREFS_NAME = "LOCATION";
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    String localCity;


    @Override
    public void onStart() {
        super.onStart();
       contextT  = getActivity();
        editor = contextT.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        prefs = contextT.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
       localCity = prefs.getString("locality", "kolkata");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        bioAcc = view.findViewById(R.id.bioAcc);
        logout = view.findViewById(R.id.logout);
        refresh = view.findViewById(R.id.refresh);
        yourFriends = view.findViewById(R.id.yourFriends);
        contactUs = view.findViewById(R.id.contactUs);
        weatherIcon = view.findViewById(R.id.weatherIcon);
        temperature = view.findViewById(R.id.temperature);
        peopleNear = view.findViewById(R.id.findFriendsNear);
        mes = view.findViewById(R.id.mes);
        wish = view.findViewById(R.id.wish);
        tabanim_toolbar = view.findViewById(R.id.tabanim_toolbar);
        contextT  = getActivity();
        editor = contextT.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        prefs = contextT.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        localCity = prefs.getString("locality", "kolkata");

        Date date = new Date();
        // Pattern
        SimpleDateFormat sdf = new SimpleDateFormat("a");

        SimpleDateFormat sdfH = new SimpleDateFormat("hh");

        String AmPm = sdf.format(date);
        String hour = sdfH.format(date);


        handleTime(AmPm, hour);

        //Get Coordinates
        if (ContextCompat.checkSelfPermission(
                contextT.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION
            );
        } else {
            getCurrentLocation();

        }

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusCheck();
            }
        });

        context = getActivity().getApplicationContext();
        notifications = view.findViewById(R.id.notifications);
        videoAdd = view.findViewById(R.id.settings);
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        editAccount = view.findViewById(R.id.editAccount);

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NotificationsActivity.class);
                startActivity(intent);
                new AcTrans.Builder(getContext()).performSlideToLeft();
            }
        });

        videoAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MoreSettingsActivity.class);
                startActivity(intent);
                new AcTrans.Builder(getContext()).performSlideToLeft();
            }
        });

        peopleNear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PeopleNearActivity.class);
                startActivity(intent);
                new AcTrans.Builder(getContext()).performSlideToLeft();
            }
        });

        yourFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FriendsActivity.class);
                startActivity(intent);
                new AcTrans.Builder(getContext()).performSlideToLeft();
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

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    View sheetView = getActivity().getLayoutInflater().inflate(R.layout.about_us_dialog, null);
                    logoutDialog = new BottomSheetDialog(contextT);
                    logoutDialog.setContentView(sheetView);
                    logoutDialog.show();

                    LinearLayout close = sheetView.findViewById(R.id.close);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            logoutDialog.dismiss();
                        }
                    });

                    // Remove default white color background
                    FrameLayout bottomSheet = (FrameLayout) logoutDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                    bottomSheet.setBackground(null);
                }
                catch (Exception e){


                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    View sheetView = getActivity().getLayoutInflater().inflate(R.layout.logout_confirm_layout, null);
                    logoutDialog = new BottomSheetDialog(contextT);
                    logoutDialog.setContentView(sheetView);
                    logoutDialog.show();

                    LinearLayout close = sheetView.findViewById(R.id.close);
                    LinearLayout logout = sheetView.findViewById(R.id.logout);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            logoutDialog.dismiss();
                        }
                    });

                    logout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            logoutDialog.dismiss();
                            LogoutUser();
                        }
                    });

                    // Remove default white color background
                    FrameLayout bottomSheet = (FrameLayout) logoutDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                    bottomSheet.setBackground(null);
                }
                catch (Exception e){


                }
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
                bioAcc.setText(bio);
                String ph = snapshot.child("imageURL").getValue().toString();
                Glide.with(context).load(ph).into(avatar);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
        new weatherTask().execute();

        return view;
    }

    private void statusCheck() {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    private void handleTime(String amPm, String hour) {
        if (amPm.equals("am")){
            if (hour.equals("12")){
                wish.setText("Good Night,");
            }else if (Integer.valueOf(hour) < 5 && Integer.valueOf(hour) > 1){
                wish.setText("Good Night,");
            }
            else if (Integer.valueOf(hour) > 5){
                wish.setText("Good Morning,");
            }
        }
        else{
            if (hour.equals("12")){
                wish.setText("Good Afternoon,");
            }else if (Integer.valueOf(hour) < 4 && Integer.valueOf(hour) > 1){
                wish.setText("Good Afternoon,");
            }
            else if (Integer.valueOf(hour) > 4){
                wish.setText("Good Evening,");
            }
        }
    }

    private void LogoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(),LogRegActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(contextT, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(contextT,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.getFusedLocationProviderClient(contextT)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(contextT)
                                .removeLocationUpdates(this);

                        if (locationResult != null && locationResult.getLocations().size() > 0){
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            getCity(latitude, longitude);
                        }
                    }
                }, Looper.getMainLooper());
    }

    private final void getCity(double d2, double d3) {
        List list;
        try {
            list = new Geocoder(contextT.getApplicationContext(),
                    Locale.getDefault()).getFromLocation(d2, d3, 1);
            if (list != null && (!list.isEmpty())) {
                locality = ((Address) list.get(0)).getLocality();
                editor.putString("locality", locality);
                editor.apply();
                new weatherTask().execute();
                updateDatabaseLocation(locality);
            }
        } catch (NullPointerException e2) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateDatabaseLocation(String locality) {
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        HashMap<String, Object> map = new HashMap<>();
        map.put("locality", locality);
        userRef.updateChildren(map);
    }

    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            /* Showing the ProgressBar, Making the main design GONE */
        }

        protected String doInBackground(String... args) {


            localCity = prefs.getString("locality", "kolkata");
            String response =
                    HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + localCity
                            + "&units=metric&appid=" + API);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {


            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                String temp = main.getString("temp");
                String weatherDescription = weather.getString("main");

                double currentTemp =  Double.parseDouble(temp);

                /* Populating extracted data into our views */
                mes.setText(weatherDescription);
                int aa = (int) Math.round(currentTemp);
                temperature.setText(String.valueOf(aa));

                if (weatherDescription.equals("Clear")){
                    weatherIcon.setBackgroundResource(R.drawable.ic_round_wb_sunny_24);
                }else if (weatherDescription.equals("Clouds")){
                    weatherIcon.setBackgroundResource(R.drawable.ic_baseline_cloud_24);
                }else if (weatherDescription.equals("Atmosphere")){
                    weatherIcon.setBackgroundResource(R.drawable.ic_round_waves_24);
                }else if (weatherDescription.equals("Haze")){
                    weatherIcon.setBackgroundResource(R.drawable.ic_round_waves_24);
                }
                else if (weatherDescription.equals("Snow")){
                    weatherIcon.setBackgroundResource(R.drawable.ic_round_ac_unit_24);
                }else if (weatherDescription.equals("Rain")){
                    weatherIcon.setBackgroundResource(R.drawable.ic_rain_svgrepo_com);
                }else if (weatherDescription.equals("Drizzle")){
                    weatherIcon.setBackgroundResource(R.drawable.ic_drizzle_svgrepo_com);
                }else if (weatherDescription.equals("Thunderstorm")){
                    weatherIcon.setBackgroundResource(R.drawable.ic_storm_thunder_svgrepo_com);
                }
            } catch (JSONException e) {

            }

        }
    }

}