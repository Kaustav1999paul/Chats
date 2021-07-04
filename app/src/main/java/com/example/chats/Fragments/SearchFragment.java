package com.example.chats.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chats.MessageActivity;
import com.example.chats.Model.User;
import com.example.chats.R;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.voela.actrans.AcTrans;


public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }

    EditText searchBox;
    RecyclerView searchList;
    LinearLayoutManager layoutManager;
    RelativeLayout noChats;
    FirebaseUser user;
    String CURRENT_STATE="not_friends";
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference friendRequestRef = FirebaseDatabase.getInstance().getReference("FriendRequests");
    DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference("Friends");
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
    DatabaseReference friendListRef = FirebaseDatabase.getInstance().getReference("FriendsList");
    DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("Notifications");
    String saveCurrentDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        searchList = view.findViewById(R.id.SearchRecyclerView);
        searchBox = view.findViewById(R.id.searchBox);
        noChats = view.findViewById(R.id.noChats);

        user = FirebaseAuth.getInstance().getCurrentUser();
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        searchList.setLayoutManager(layoutManager);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();
                if (text.equals("")){
                    noChats.setVisibility(View.VISIBLE);
                    searchList.setVisibility(View.INVISIBLE);
                }else {
                    searchList.setVisibility(View.VISIBLE);
                    searchProduct(text);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });


        return view;
    }

    private void searchProduct(String text) {
        Query query = ref.child("Users").orderByChild("email").startAt(text).endAt(text + "\uf8ff");
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class).build();

        FirebaseRecyclerAdapter<User, SearchVH> adapter = new FirebaseRecyclerAdapter<User, SearchVH>(options) {

            @Override
            protected void onBindViewHolder(@NonNull SearchVH holder, int position, @NonNull User model) {
                String state = model.getStatus();
                holder.accReq.setVisibility(View.GONE);
                holder.notific.setVisibility(View.GONE);
                maintaiState(model.getId(), holder.sendReq, holder.accReq);


                if (state.equals("online")){
                    holder.statusOnOff.setColorFilter(getContext().getResources().getColor(R.color.online));
                }else {
                    holder.statusOnOff.setColorFilter(getContext().getResources().getColor(R.color.offline));
                }
                noChats.setVisibility(View.GONE);
                holder.timeLast.setVisibility(View.GONE);

//                If the search person is same to the current user then the user will not be shown.
                if (model.getId().equals(user.getUid())){
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
                Glide.with(holder.img).load(model.getImageURL()).into(holder.img);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


//                            Here the condition should be placed if the user exists on the friends list or not

                            friendRef.child(user.getUid()).child(model.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        Intent intent = new Intent(getContext(), MessageActivity.class);
                                        intent.putExtra("userid", model.getId());
                                        intent.putExtra("photo", model.getImageURL());
                                        intent.putExtra("name", model.getUsername());
                                        startActivity(intent);
                                        new AcTrans.Builder(getContext()).performSlideToLeft();
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
            public SearchVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.user_item, parent, false);
                return new SearchVH(v);
            }
        };
        searchList.setAdapter(adapter);
        adapter.updateOptions(options);
        adapter.startListening();

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


    public class SearchVH extends RecyclerView.ViewHolder{

        TextView nameUser, statusUser, timeLast;
        ImageView img, sendReq;
        CircleImageView statusOnOff,notific;
        Button accReq;

        public SearchVH(@NonNull View itemView) {
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
}