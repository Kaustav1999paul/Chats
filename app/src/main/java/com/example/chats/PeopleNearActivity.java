package com.example.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.example.chats.Fragments.SearchFragment;
import com.example.chats.Model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import id.voela.actrans.AcTrans;

public class PeopleNearActivity extends AppCompatActivity {

    private RecyclerView friendsNearList;
    private RelativeLayout loading;
    private TextView message;
    PullRefreshLayout swip;
    private ImageView back;
    private DatabaseReference userRef, friendRef, friendRequestRef;
    private String currentPersonCity;
    private FirebaseUser user;
    private String CURRENT_STATE="not_friends";
    private LinearLayoutManager layoutManager;
    String saveCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_near);
        Slidr.attach(this);

        statusLocationCheck();
        friendsNearList = findViewById(R.id.friendsNearList);
        message = findViewById(R.id.messageNo);
        back = findViewById(R.id.back);
        loading = findViewById(R.id.loading);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                new AcTrans.Builder(PeopleNearActivity.this).performSlideToRight();
            }
        });
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        friendsNearList.setLayoutManager(layoutManager);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        friendRef = FirebaseDatabase.getInstance().getReference("Friends");
        friendRequestRef = FirebaseDatabase.getInstance().getReference("FriendRequests");
        user = FirebaseAuth.getInstance().getCurrentUser();
        swip = findViewById(R.id.swip);
        swip.setColor(Color.parseColor("#0C89ED"));
        displayAllFriends();

        swip.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayAllFriends();
                swip.setRefreshing(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        new AcTrans.Builder(PeopleNearActivity.this).performSlideToRight();
    }

    private void displayAllFriends() {
        userRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    currentPersonCity = snapshot.child("locality").getValue().toString();
                    showNearbyPeople(currentPersonCity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void showNearbyPeople(String currentPersonCity) {
        Query query = userRef.orderByChild("locality");
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class).build();

        FirebaseRecyclerAdapter<User, PeopleVH> adapter = new FirebaseRecyclerAdapter<User, PeopleVH>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PeopleNearActivity.PeopleVH holder, int position, @NonNull User model) {
                loading.setVisibility(View.GONE);
                friendsNearList.setVisibility(View.VISIBLE);
                holder.statusOnOff.setVisibility(View.GONE);
                holder.timeLast.setVisibility(View.GONE);

                maintaiState(model.getId(), holder.sendReq, holder.accReq);

                if (model.getId().equals(user.getUid())){
                    holder.itemView.getLayoutParams().height = 0;
                    holder.itemView.setVisibility(View.GONE);
                }

                if (!model.getLocality().equals(currentPersonCity)){
                    holder.itemView.getLayoutParams().height = 0;
                    holder.itemView.setVisibility(View.GONE);
                }

                holder.sendReq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.sendReq.setEnabled(false);
                        if (CURRENT_STATE.equals("not_friends")){
                            sendFirendRequest(model.getId(), holder.sendReq, holder.accReq);
                        }
                        if (CURRENT_STATE.equals("request_sent")){
                            cancelFreiendRequest(model.getId(), holder.sendReq);
                        }
                    }
                });

                holder.nameUser.setText(model.getUsername());
                holder.statusUser.setText(model.getEmail());
                holder.sendReq.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).load(model.getImageURL()).into(holder.img);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


//                            Here the condition should be placed if the user exists on the friends list or not

                        friendRef.child(user.getUid()).child(model.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    Intent intent = new Intent(PeopleNearActivity.this, MessageActivity.class);
                                    intent.putExtra("userid", model.getId());
                                    intent.putExtra("photo", model.getImageURL());
                                    intent.putExtra("name", model.getUsername());
                                    startActivity(intent);
                                    new AcTrans.Builder(PeopleNearActivity.this).performSlideToLeft();
                                }else {
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                });

            }

            @NonNull
            @Override
            public PeopleVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(PeopleNearActivity.this).inflate(R.layout.user_item, parent, false);
                return new PeopleVH(v);
            }
        };
        friendsNearList.setAdapter(adapter);
        adapter.updateOptions(options);
        adapter.startListening();
    }

    private void sendFirendRequest(String receiverID, ImageView sendReq, Button accReq) {

        friendRequestRef.child(user.getUid())
                .child(receiverID)
                .child("request_type")
                .setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            friendRequestRef.child(receiverID)
                                    .child(user.getUid())
                                    .child("request_type")
                                    .setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                CURRENT_STATE = "request_sent";
                                                accReq.setVisibility(View.GONE);
                                                sendReq.setImageResource(R.drawable.ic_round_arrow_upward_24);
                                                sendReq.setEnabled(true);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void maintaiState(String receiverID, ImageView sendReq, Button accReq) {
//                               receiverID
        friendRequestRef.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(receiverID)){
                            String request_type = dataSnapshot.child(receiverID).child("request_type").getValue().toString();

                            if (request_type.equals("sent")){
                                CURRENT_STATE = "request_sent";
                                accReq.setVisibility(View.GONE);
                                sendReq.setImageResource(R.drawable.ic_round_arrow_upward_24);
                            }else if (request_type.equals("received")){
                                CURRENT_STATE = "request_received";
                                accReq.setVisibility(View.VISIBLE);
                                sendReq.setVisibility(View.GONE);
                                accReq.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        acceptRequest(accReq, receiverID, sendReq);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

        friendRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(receiverID)){
                    sendReq.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void acceptRequest(Button accReq, String receiverId, ImageView sendReq) {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        friendRef.child(user.getUid())
                .child(receiverId)
                .child("date")
                .setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            friendRef.child(receiverId)
                                    .child(user.getUid())
                                    .child("date")
                                    .setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                friendRequestRef.child(user.getUid())
                                                        .child(receiverId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()){
                                                                    friendRequestRef.child(receiverId)
                                                                            .child(user.getUid())
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){
                                                                                        CURRENT_STATE = "friends";
                                                                                        sendReq.setVisibility(View.GONE);
                                                                                        sendReq.setEnabled(false);
                                                                                        accReq.setVisibility(View.GONE);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });

        //   createFtriendsList(receiverId);
    }

    private void cancelFreiendRequest(String receiverID, ImageView sendReq) {
        friendRequestRef.child(user.getUid())
                .child(receiverID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            friendRequestRef.child(receiverID)
                                    .child(user.getUid())
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                CURRENT_STATE = "not_friends";
                                                sendReq.setImageResource(R.drawable.ic_send_request);
                                                sendReq.setEnabled(true);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public class PeopleVH extends RecyclerView.ViewHolder{

        TextView nameUser, statusUser, timeLast;
        ImageView img, sendReq;
        CircleImageView statusOnOff,notific;
        Button accReq;

        public PeopleVH(@NonNull View itemView) {
            super(itemView);
            accReq = itemView.findViewById(R.id.accReq);
            img = itemView.findViewById(R.id.personAvatar);
            nameUser = itemView.findViewById(R.id.personHolderName);
            statusUser = itemView.findViewById(R.id.personHolderEmail);
            timeLast = itemView.findViewById(R.id.timeLast);
            notific = itemView.findViewById(R.id.notific);
            sendReq = itemView.findViewById(R.id.sendReq);
            statusOnOff = itemView.findViewById(R.id.img_on);
        }
    }

    public void statusLocationCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, enable to find people near you.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}