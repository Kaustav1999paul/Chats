package com.example.chats.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;
import com.example.chats.GroupMessageActivity;
import com.example.chats.Model.Groups;
import com.example.chats.R;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import id.voela.actrans.AcTrans;

public class GroupFragment extends Fragment {

    public GroupFragment() {
        // Required empty public constructor
    }

    FloatingActionButton addGroup;
    BottomSheetDialog mBottomDialogNotificationAction;
    DatabaseReference groupReference = FirebaseDatabase.getInstance().getReference("Groups");
    FirebaseUser user;
    LinearLayoutManager layoutManager;
    RelativeLayout loading;
    RecyclerView groupList;
    PullRefreshLayout swip;
    TextView messageNo;
    public static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        swip = view.findViewById(R.id.swip);
        swip.setColor(Color.parseColor("#0C89ED"));
        groupList = view.findViewById(R.id.groupList);
        messageNo = view.findViewById(R.id.messageNo);
        loading = view.findViewById(R.id.loading);
        addGroup = view.findViewById(R.id.addGroup);
        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogNotificationAction();
            }
        });
        groupList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        context = getActivity().getApplicationContext();
        groupList.setLayoutManager(layoutManager);

        groupReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    loading.setVisibility(View.GONE);
                    messageNo.setVisibility(View.VISIBLE);
                }else {
                    messageNo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        displayGroups();
        swip.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayGroups();
                swip.setRefreshing(false);
            }
        });
        return view;
    }

    private void displayGroups() {

        FirebaseRecyclerOptions<Groups> options = new FirebaseRecyclerOptions.Builder<Groups>()
                .setQuery(groupReference, Groups.class).build();

        FirebaseRecyclerAdapter<Groups, GroupVH> adapter = new FirebaseRecyclerAdapter<Groups, GroupVH>(options) {
            @Override
            protected void onBindViewHolder(@NonNull GroupVH holder, int position, @NonNull Groups model) {
                loading.setVisibility(View.GONE);
                groupList.setVisibility(View.VISIBLE);

                DatabaseReference Gr = groupReference.child(model.getId()).child("People");

                Gr.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user.getUid())){
                            holder.groupName.setText(model.getName());
                            Glide.with(context).load(model.getPhoto()).into(holder.img);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), GroupMessageActivity.class);
                                    intent.putExtra("groupId", model.getId());
                                    startActivity(intent);
                                    new AcTrans.Builder(getContext()).performSlideToLeft();
                                }
                            });
                        }else {

                            holder.lau.getLayoutParams().height = 0;
                            holder.lau.setVisibility(View.GONE);
                        }
                        loading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @NonNull
            @Override
            public GroupVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.group_layout, parent, false);
                return new GroupVH(v);
            }
        };
        groupList.setAdapter(adapter);
        adapter.updateOptions(options);
        adapter.startListening();

    }

    private void showDialogNotificationAction() {
        try {
            View sheetView = getLayoutInflater().inflate(R.layout.create_group_alert, null);
            mBottomDialogNotificationAction = new BottomSheetDialog(getContext());
            mBottomDialogNotificationAction.setContentView(sheetView);
            mBottomDialogNotificationAction.show();

            LinearLayout create = sheetView.findViewById(R.id.create);
            LinearLayout close = sheetView.findViewById(R.id.close);
            EditText groupName = sheetView.findViewById(R.id.groupName);

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomDialogNotificationAction.dismiss();
                }
            });
            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = groupName.getText().toString().trim();
                    if (TextUtils.isEmpty(name)){
                        Toast.makeText(getContext(), "Group should have a name", Toast.LENGTH_SHORT).show();
                    }else {
                        createGroup(name);
                    }

                }
            });

            // Remove default white color background
            FrameLayout bottomSheet = (FrameLayout) mBottomDialogNotificationAction.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            bottomSheet.setBackground(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGroup(String name) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
        String saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(calendar.getTime());
        String RandomKey = user.getUid() + saveCurrentDate + saveCurrentTime;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat curDate = new SimpleDateFormat("dd-MMMM-yyyy");
        String saveCDate = curDate.format(calForDate.getTime());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", RandomKey);
        hashMap.put("name", name);
        hashMap.put("time", saveCurrentTime);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("photo", "https://firebasestorage.googleapis.com/v0/b/chats-ec34c.appspot.com/o/logo.png?alt=media&token=b6ca1c76-a04b-4de5-a4a5-d3b0e265e334");

        groupReference.child(RandomKey).setValue(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            groupReference.child(RandomKey).child("People")
                                    .child(user.getUid())
                                    .child("date")
                                    .setValue(saveCDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mBottomDialogNotificationAction.dismiss();
                                }
                            });
                        }
                    }
                });
    }

    public class GroupVH extends RecyclerView.ViewHolder{
        TextView groupName;
        ImageView img;
        RelativeLayout lau;

        public GroupVH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.groupAvatar);
            groupName = itemView.findViewById(R.id.groupName);
            lau = itemView.findViewById(R.id.lau);
        }
    }
}