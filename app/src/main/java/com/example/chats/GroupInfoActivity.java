package com.example.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chats.Fragments.FriendsFragment;
import com.example.chats.Model.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import id.voela.actrans.AcTrans;

public class GroupInfoActivity extends AppCompatActivity {

    TextView gName, gDate, groupAddPeople, countt;
    ImageView gAvatar;
    Intent intent;
    FirebaseUser user;
    String groupId,a,b, c;
    ImageView back;
    DatabaseReference groupReference;
    BottomSheetDialog mBottomDialogNotificationAction;
    DatabaseReference friendsRef, userRef;
    LinearLayoutManager layoutManager, linearLayoutMan1;
    RecyclerView peopleGroupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        gAvatar = findViewById(R.id.gAvatar);
        gName = findViewById(R.id.gName);
        gDate = findViewById(R.id.gDate);
        intent = getIntent();
        countt = findViewById(R.id.countt);
        peopleGroupList = findViewById(R.id.peopleGroupList);
        peopleGroupList.setHasFixedSize(true);
        linearLayoutMan1 = new LinearLayoutManager(this);
        linearLayoutMan1.setReverseLayout(true);
        linearLayoutMan1.setStackFromEnd(true);
        peopleGroupList.setLayoutManager(linearLayoutMan1);

        groupAddPeople = findViewById(R.id.groupAddPeople);
        back = findViewById(R.id.back);
        user = FirebaseAuth.getInstance().getCurrentUser();
        groupId = intent.getStringExtra("groupId");
        groupReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
        groupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                a = snapshot.child("photo").getValue().toString();
                Glide.with(getApplicationContext()).load(a).into(gAvatar);
                b = snapshot.child("name").getValue().toString();
                c = snapshot.child("date").getValue().toString();
                gName.setText(b);
                gDate.setText("Group created in: " + c);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                new AcTrans.Builder(GroupInfoActivity.this).performSlideToTop();
            }
        });
        friendsRef = FirebaseDatabase.getInstance().getReference("Friends").child(user.getUid());
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        showGList(countt);

        groupAddPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomActionBar();
            }
        });
    }

    private void showGList(TextView countt) {

        DatabaseReference ref = groupReference.child("People");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int count = (int) snapshot.getChildrenCount();

                    countt.setText(String.valueOf(count));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(ref, Friends.class).build();

        FirebaseRecyclerAdapter<Friends, FriendsVH> adapter = new FirebaseRecyclerAdapter<Friends, FriendsVH>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsVH holder, int position, @NonNull Friends model) {
                final String userIDs = getRef(position).getKey();

                userRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            final String userName = dataSnapshot.child("username").getValue().toString();
                            final String userEmail = dataSnapshot.child("email").getValue().toString();
                            final String userImage = dataSnapshot.child("imageURL").getValue().toString();
                            final String id = dataSnapshot.child("id").getValue().toString();

                            holder.accReq.setVisibility(View.GONE);
                            holder.sendReq.setVisibility(View.GONE);
                            holder.statusOnOff.setVisibility(View.GONE);
                            holder.timeLast.setVisibility(View.GONE);
                            holder.nameUser.setText(userName);
                            holder.statusUser.setText(userEmail);
                            Glide.with(getApplicationContext()).load(userImage).into(holder.img);
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(GroupInfoActivity.this, MessageActivity.class);
                                    intent.putExtra("userid", id);
                                    startActivity(intent);
                                    new AcTrans.Builder(GroupInfoActivity.this).performSlideToLeft();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public FriendsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(GroupInfoActivity.this).inflate(R.layout.user_item, parent, false);
                return new FriendsVH(v);
            }
        };
        peopleGroupList.setAdapter(adapter);
        adapter.updateOptions(options);
        adapter.startListening();


    }

    private void showBottomActionBar() {
        try {
            View sheetView = GroupInfoActivity.this.getLayoutInflater().inflate(R.layout.add_people_bottombar, null);
            mBottomDialogNotificationAction = new BottomSheetDialog(this);
            mBottomDialogNotificationAction.setContentView(sheetView);
            mBottomDialogNotificationAction.show();

            LinearLayout close = sheetView.findViewById(R.id.close);
            RecyclerView pList = sheetView.findViewById(R.id.pList);
            pList.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            pList.setLayoutManager(layoutManager);

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomDialogNotificationAction.dismiss();
                }
            });
            theme();
            showSecondaryFriendList(pList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSecondaryFriendList(RecyclerView pListX) {
        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(friendsRef, Friends.class).build();

        FirebaseRecyclerAdapter<Friends, FriendsSecondVH> adapter = new FirebaseRecyclerAdapter<Friends, FriendsSecondVH>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsSecondVH holder, int position, @NonNull Friends model) {
                final String userIDs = getRef(position).getKey();

                userRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            final String userName = dataSnapshot.child("username").getValue().toString();
                            final String userImage = dataSnapshot.child("imageURL").getValue().toString();
                            final String id = dataSnapshot.child("id").getValue().toString();

                            maintainState(id, holder.addd);
                            holder.pName.setText(userName);
                            Glide.with(getApplicationContext()).load(userImage).into(holder.pAvatar);
                            holder.addd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    addPersonToGroupList(id, holder.addd);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public FriendsSecondVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(GroupInfoActivity.this).inflate(R.layout.add_people, parent, false);
                return new FriendsSecondVH(v);
            }
        };
        pListX.setAdapter(adapter);
        adapter.updateOptions(options);
        adapter.startListening();

    }

    private void maintainState(String id, FloatingActionButton addd) {
        groupReference.child("People").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(id)){
                    addd.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void addPersonToGroupList(String id, FloatingActionButton addd) {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        String saveCurrentDate = currentDate.format(calForDate.getTime());


        groupReference.child("People")
                .child(id)
                .child("date")
                .setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    addd.setVisibility(View.GONE);
                }
            }
        });
    }

    private void theme() {
        // Remove default white color background
        FrameLayout bottomSheet = (FrameLayout) mBottomDialogNotificationAction.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        bottomSheet.setBackground(null);
    }

    public class FriendsSecondVH extends RecyclerView.ViewHolder{
        TextView pName;
        ImageView pAvatar;
        FloatingActionButton addd;

        public FriendsSecondVH(@NonNull View itemView) {
            super(itemView);
            addd = itemView.findViewById(R.id.addd);
            pAvatar = itemView.findViewById(R.id.pAvatar);
            pName = itemView.findViewById(R.id.pName);
        }
    }

    public class FriendsVH extends RecyclerView.ViewHolder{
        TextView nameUser, statusUser, timeLast;
        ImageView img, sendReq;
        CircleImageView statusOnOff;
        Button accReq;
        View divider;

        public FriendsVH(@NonNull View itemView) {
            super(itemView);
            accReq = itemView.findViewById(R.id.accReq);
            img = itemView.findViewById(R.id.personAvatar);
            divider = itemView.findViewById(R.id.divider);
            nameUser = itemView.findViewById(R.id.personHolderName);
            statusUser = itemView.findViewById(R.id.personHolderEmail);
            timeLast = itemView.findViewById(R.id.timeLast);
            sendReq = itemView.findViewById(R.id.sendReq);
            statusOnOff = itemView.findViewById(R.id.img_on);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        new AcTrans.Builder(GroupInfoActivity.this).performSlideToTop();
    }
}