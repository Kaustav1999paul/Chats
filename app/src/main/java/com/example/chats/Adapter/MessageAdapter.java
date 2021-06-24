package com.example.chats.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chats.AESUtils;
import com.example.chats.FullImage;
import com.example.chats.Model.Chat;
import com.example.chats.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public static final int MSG_TYPE_IMAGE_LEFT = 2;
    public static final int MSG_TYPE_IMAGE_RIGHT = 3;
    public static final int MSG_TYPE_DOCS_LEFT = 4;
    public static final int MSG_TYPE_DOCS_RIGHT = 5;
    public static final int MSG_TYPE_VIDEO_LEFT = 6;
    public static final int MSG_TYPE_VIDEO_RIGHT = 7;
    public static final int MSG_TYPE_LOC_LEFT = 8;
    public static final int MSG_TYPE_LOC_RIGHT = 9;
    private List<Chat> mChat;
    FirebaseUser fuser;
    LayoutInflater inflater;

    public MessageAdapter(Context mContext, List<Chat> mChat, LayoutInflater inflater){
        this.mChat = mChat;
        this.mContext = mContext;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case MSG_TYPE_LEFT:
                View textOnlyLViewHolder = inflater.inflate(R.layout.chat_item_left, parent,false);
                viewHolder = new LeftMessage(textOnlyLViewHolder);
                break;
            case MSG_TYPE_RIGHT:
                View textOnlyRViewHolder = inflater.inflate(R.layout.chat_item_right, parent,false);
                viewHolder = new RightMessage(textOnlyRViewHolder);
                break;
            case MSG_TYPE_IMAGE_LEFT:
                View imageOnlyLViewHolder = inflater.inflate(R.layout.image_left, parent,false);
                viewHolder = new LeftImage(imageOnlyLViewHolder);
                break;
            case MSG_TYPE_IMAGE_RIGHT:
                View imageOnlyRViewHolder = inflater.inflate(R.layout.image_right, parent,false);
                viewHolder = new RightImage(imageOnlyRViewHolder);
                break;
            case MSG_TYPE_DOCS_RIGHT:
                View docsOnlyRViewHolder = inflater.inflate(R.layout.right_docs, parent,false);
                viewHolder = new RightDocs(docsOnlyRViewHolder);
                break;
            case MSG_TYPE_DOCS_LEFT:
                View docsOnlyLViewHolder = inflater.inflate(R.layout.left_docs, parent,false);
                viewHolder = new LeftDocs(docsOnlyLViewHolder);
                break;
            case MSG_TYPE_VIDEO_RIGHT:
                View videoOnlyRViewHolder = inflater.inflate(R.layout.video_right, parent,false);
                viewHolder = new RightVideo(videoOnlyRViewHolder);
                break;
            case MSG_TYPE_VIDEO_LEFT:
                View videoOnlyLViewHolder = inflater.inflate(R.layout.video_left, parent,false);
                viewHolder = new LeftVideo(videoOnlyLViewHolder);
                break;
            case MSG_TYPE_LOC_RIGHT:
                View locOnlyRViewHolder = inflater.inflate(R.layout.right_location, parent,false);
                viewHolder = new RightLoc(locOnlyRViewHolder);
                break;
            case MSG_TYPE_LOC_LEFT:
                View locOnlyLViewHolder = inflater.inflate(R.layout.left_location, parent,false);
                viewHolder = new LeftLoc(locOnlyLViewHolder);
                break;
            default:


        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);

            switch (holder.getItemViewType()){
                case MSG_TYPE_LEFT:
                    LeftMessage leftM = (LeftMessage) holder;
                    leftM.show_messageL.setText(chat.getMessage());
                    if (chat.getIsLiked().equals("true")){
                        leftM.isLiked.setVisibility(View.VISIBLE);
                        leftM.show_messageL.setOnTouchListener(new View.OnTouchListener() {
                            private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                                @Override
                                public boolean onDoubleTap(MotionEvent e) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("isLiked", "false");
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Chats");
                                    ref.child(chat.getId()).updateChildren(map);
                                    return super.onDoubleTap(e);
                                }
                            });

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                gestureDetector.onTouchEvent(event);
                                return true;
                            }
                        });
                    }else {
                        leftM.show_messageL.setOnTouchListener(new View.OnTouchListener() {
                        private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onDoubleTap(MotionEvent e) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("isLiked", "true");
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Chats");
                                ref.child(chat.getId()).updateChildren(map);
                                return super.onDoubleTap(e);
                            }
                        });

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            gestureDetector.onTouchEvent(event);
                            return true;
                        }
                    });
                    }
                    break;
                case MSG_TYPE_RIGHT:
                    RightMessage rightM = (RightMessage) holder;
                    rightM.show_messageR.setText(chat.getMessage());

                    if (chat.getIsLiked().equals("true")){
                        rightM.liked.setVisibility(View.VISIBLE);
                    }

                    if (position == mChat.size() - 1){
                        if (chat.isIsseen()){
                            rightM.text_seen.setColorFilter(mContext.getResources().getColor(R.color.seen));
                        }else {
                            rightM.text_seen.setColorFilter(mContext.getResources().getColor(R.color.not_seen));
                        }
                    }else {
                        rightM.text_seen.setColorFilter(mContext.getResources().getColor(R.color.online));
                    }
                    break;

                case MSG_TYPE_IMAGE_RIGHT:
                    RightImage rightI = (RightImage) holder;

                    if (chat.getIsLiked().equals("true")){
                        rightI.liked.setVisibility(View.VISIBLE);
                    }

                    rightI.show_imageR.setOnTouchListener(new View.OnTouchListener() {
                        private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {

                            @Override
                            public void onLongPress(MotionEvent e) {
                                super.onLongPress(e);

                                Intent intent = new Intent(mContext, FullImage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("image", chat.getMessage());
                                mContext.startActivity(intent);
                            }
                        });

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            gestureDetector.onTouchEvent(event);
                            return true;
                        }
                    });

                    if (position == mChat.size() - 1){
                        if (chat.isIsseen()){
                            rightI.text_seen.setColorFilter(mContext.getResources().getColor(R.color.seen));
                        }else {
                            rightI.text_seen.setColorFilter(mContext.getResources().getColor(R.color.not_seen));
                        }
                    }else {
                        rightI.text_seen.setColorFilter(mContext.getResources().getColor(R.color.online));
                    }
                    break;
                case MSG_TYPE_IMAGE_LEFT:
                    LeftImage leftI = (LeftImage) holder;

                    if (chat.getIsLiked().equals("true")){
                        leftI.isLikedI.setVisibility(View.VISIBLE);
                        leftI.show_imageL.setOnTouchListener(new View.OnTouchListener() {
                            private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                                @Override
                                public boolean onDoubleTap(MotionEvent e) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("isLiked", "false");
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Chats");
                                    ref.child(chat.getId()).updateChildren(map);
                                    return super.onDoubleTap(e);
                                }

                                @Override
                                public void onLongPress(MotionEvent e) {
                                    super.onLongPress(e);

                                    Intent intent = new Intent(mContext, FullImage.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("image", chat.getMessage());
                                    mContext.startActivity(intent);
                                }
                            });

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                gestureDetector.onTouchEvent(event);
                                return true;
                            }
                        });
                    }else {
                        leftI.show_imageL.setOnTouchListener(new View.OnTouchListener() {
                        private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onDoubleTap(MotionEvent e) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("isLiked", "true");
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Chats");
                                ref.child(chat.getId()).updateChildren(map);
                                return super.onDoubleTap(e);
                            }

                            @Override
                            public void onLongPress(MotionEvent e) {
                                super.onLongPress(e);

                                Intent intent = new Intent(mContext, FullImage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("image", chat.getMessage());
                                mContext.startActivity(intent);
                            }
                        });

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            gestureDetector.onTouchEvent(event);
                            return true;
                        }
                    });
                    }
                    break;
                case MSG_TYPE_DOCS_RIGHT:
                    RightDocs rightD = (RightDocs) holder;

                    if (chat.getIsLiked().equals("true")){
                        rightD.liked.setVisibility(View.VISIBLE);
                    }

                    rightD.show_docsR.setOnTouchListener(new View.OnTouchListener() {
                        private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {

                            @Override
                            public void onLongPress(MotionEvent e) {
                                super.onLongPress(e);

                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(browserIntent);
                            }
                        });

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            gestureDetector.onTouchEvent(event);
                            return true;
                        }
                    });

                    if (position == mChat.size() - 1){
                        if (chat.isIsseen()){
                            rightD.text_seen.setColorFilter(mContext.getResources().getColor(R.color.seen));
                        }else {
                            rightD.text_seen.setColorFilter(mContext.getResources().getColor(R.color.not_seen));
                        }
                    }else {
                        rightD.text_seen.setColorFilter(mContext.getResources().getColor(R.color.online));
                    }
                    break;

                case MSG_TYPE_DOCS_LEFT:
                    LeftDocs leftD = (LeftDocs) holder;

                    if (chat.getIsLiked().equals("true")){
                        leftD.isLiked.setVisibility(View.VISIBLE);
                        leftD.show_docsL.setOnTouchListener(new View.OnTouchListener() {
                            private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                                @Override
                                public boolean onDoubleTap(MotionEvent e) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("isLiked", "false");
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Chats");
                                    ref.child(chat.getId()).updateChildren(map);
                                    return super.onDoubleTap(e);
                                }

                                @Override
                                public void onLongPress(MotionEvent e) {
                                    super.onLongPress(e);
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(browserIntent);
                                }
                            });

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                gestureDetector.onTouchEvent(event);
                                return true;
                            }
                        });
                    }else {
                        leftD.show_docsL.setOnTouchListener(new View.OnTouchListener() {
                            private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                                @Override
                                public boolean onDoubleTap(MotionEvent e) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("isLiked", "true");
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Chats");
                                    ref.child(chat.getId()).updateChildren(map);
                                    return super.onDoubleTap(e);
                                }

                                @Override
                                public void onLongPress(MotionEvent e) {
                                    super.onLongPress(e);
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(browserIntent);
                                }
                            });

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                gestureDetector.onTouchEvent(event);
                                return true;
                            }
                        });
                    }
                    break;

                case MSG_TYPE_VIDEO_RIGHT:
                    RightVideo rightV = (RightVideo) holder;

                    if (chat.getIsLiked().equals("true")){
                        rightV.liked.setVisibility(View.VISIBLE);
                    }

                    rightV.show_videoR.setOnTouchListener(new View.OnTouchListener() {
                        private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {

                            @Override
                            public void onLongPress(MotionEvent e) {
                                super.onLongPress(e);

                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(browserIntent);
                            }
                        });

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            gestureDetector.onTouchEvent(event);
                            return true;
                        }
                    });

                    if (position == mChat.size() - 1){
                        if (chat.isIsseen()){
                            rightV.text_seen.setColorFilter(mContext.getResources().getColor(R.color.seen));
                        }else {
                            rightV.text_seen.setColorFilter(mContext.getResources().getColor(R.color.not_seen));
                        }
                    }else {
                        rightV.text_seen.setColorFilter(mContext.getResources().getColor(R.color.online));
                    }
                    break;

                case MSG_TYPE_VIDEO_LEFT:
                    LeftVideo leftV = (LeftVideo) holder;

                    if (chat.getIsLiked().equals("true")){
                        leftV.isLiked.setVisibility(View.VISIBLE);
                        leftV.show_videoL.setOnTouchListener(new View.OnTouchListener() {
                            private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                                @Override
                                public boolean onDoubleTap(MotionEvent e) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("isLiked", "false");
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Chats");
                                    ref.child(chat.getId()).updateChildren(map);
                                    return super.onDoubleTap(e);
                                }

                                @Override
                                public void onLongPress(MotionEvent e) {
                                    super.onLongPress(e);
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(browserIntent);
                                }
                            });

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                gestureDetector.onTouchEvent(event);
                                return true;
                            }
                        });
                    }else {
                        leftV.show_videoL.setOnTouchListener(new View.OnTouchListener() {
                            private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                                @Override
                                public boolean onDoubleTap(MotionEvent e) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("isLiked", "true");
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Chats");
                                    ref.child(chat.getId()).updateChildren(map);
                                    return super.onDoubleTap(e);
                                }

                                @Override
                                public void onLongPress(MotionEvent e) {
                                    super.onLongPress(e);
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(browserIntent);
                                }
                            });

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                gestureDetector.onTouchEvent(event);
                                return true;
                            }
                        });
                    }
                    break;

                case MSG_TYPE_LOC_RIGHT:
                    RightLoc rightL = (RightLoc) holder;

                    if (chat.getIsLiked().equals("true")){
                        rightL.liked.setVisibility(View.VISIBLE);
                    }

                    rightL.show_locR.setOnTouchListener(new View.OnTouchListener() {
                        private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {

                            @Override
                            public void onLongPress(MotionEvent e) {
                                super.onLongPress(e);

                                String ms = chat.getMessage();
                                String latitude = ms.substring(0, 9);
                                String longitude = ms.substring(10, 19);


                                String uri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude;
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                intent.setPackage("com.google.android.apps.maps");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);

//                                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                mContext.startActivity(intent);

//                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
//                                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                mContext.startActivity(browserIntent);
                            }
                        });

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            gestureDetector.onTouchEvent(event);
                            return true;
                        }
                    });

                    if (position == mChat.size() - 1){
                        if (chat.isIsseen()){
                            rightL.text_seen.setColorFilter(mContext.getResources().getColor(R.color.seen));
                        }else {
                            rightL.text_seen.setColorFilter(mContext.getResources().getColor(R.color.not_seen));
                        }
                    }else {
                        rightL.text_seen.setColorFilter(mContext.getResources().getColor(R.color.online));
                    }
                    break;

                case MSG_TYPE_LOC_LEFT:
                    LeftLoc leftL = (LeftLoc) holder;

                    if (chat.getIsLiked().equals("true")){
                        leftL.isLiked.setVisibility(View.VISIBLE);
                        leftL.show_locL.setOnTouchListener(new View.OnTouchListener() {
                            private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                                @Override
                                public boolean onDoubleTap(MotionEvent e) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("isLiked", "false");
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Chats");
                                    ref.child(chat.getId()).updateChildren(map);
                                    return super.onDoubleTap(e);
                                }

                                @Override
                                public void onLongPress(MotionEvent e) {
                                    super.onLongPress(e);
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(browserIntent);
                                }
                            });

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                gestureDetector.onTouchEvent(event);
                                return true;
                            }
                        });
                    }else {
                        leftL.show_locL.setOnTouchListener(new View.OnTouchListener() {
                            private GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                                @Override
                                public boolean onDoubleTap(MotionEvent e) {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("isLiked", "true");
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Chats");
                                    ref.child(chat.getId()).updateChildren(map);
                                    return super.onDoubleTap(e);
                                }

                                @Override
                                public void onLongPress(MotionEvent e) {
                                    super.onLongPress(e);
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chat.getMessage()));
                                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(browserIntent);
                                }
                            });

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                gestureDetector.onTouchEvent(event);
                                return true;
                            }
                        });
                    }
                    break;
            }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        if (mChat.get(position).getSender().equals(fuser.getUid())){
//            Me
            if (mChat.get(position).getType().equals("text")){
                return MSG_TYPE_RIGHT;
            }else if (mChat.get(position).getType().equals("docs")){
                return MSG_TYPE_DOCS_RIGHT;
            }else if (mChat.get(position).getType().equals("video")){
                return MSG_TYPE_VIDEO_RIGHT;
            }else if (mChat.get(position).getType().equals("location")){
                return MSG_TYPE_LOC_RIGHT;
            }
            else {
                return MSG_TYPE_IMAGE_RIGHT;
            }

        }
        else {
            if (mChat.get(position).getType().equals("text")){
                return MSG_TYPE_LEFT;
            }else if (mChat.get(position).getType().equals("docs")){
                return MSG_TYPE_DOCS_LEFT;
            }else if (mChat.get(position).getType().equals("video")){
                return MSG_TYPE_VIDEO_LEFT;
            }else if (mChat.get(position).getType().equals("location")){
                return MSG_TYPE_LOC_LEFT;
            }
            else {
                return MSG_TYPE_IMAGE_LEFT;
            }


        }
    }

    public class LeftDocs extends RecyclerView.ViewHolder{
        public TextView show_docsL;
        public CircleImageView text_seen;
        public ImageView isLiked;

        public LeftDocs(@NonNull View itemView) {
            super(itemView);
            show_docsL = itemView.findViewById(R.id.show_docsL);
            text_seen = itemView.findViewById(R.id.text_seen);
            isLiked = itemView.findViewById(R.id.isLiked);
        }
    }

    public class LeftMessage extends RecyclerView.ViewHolder{
        public TextView show_messageL;
        public CircleImageView text_seen;
        public ImageView isLiked;

        public LeftMessage(@NonNull View itemView) {
            super(itemView);
            show_messageL = itemView.findViewById(R.id.show_messageL);
            text_seen = itemView.findViewById(R.id.text_seen);
            isLiked = itemView.findViewById(R.id.isLiked);
        }
    }

    class RightMessage extends RecyclerView.ViewHolder{
        public TextView show_messageR,liked;
        public CircleImageView text_seen;

        public RightMessage(@NonNull View itemView) {
            super(itemView);
            liked = itemView.findViewById(R.id.liked);
            show_messageR = itemView.findViewById(R.id.show_messageR);
            text_seen = itemView.findViewById(R.id.text_seen);
        }
    }

    class RightDocs extends RecyclerView.ViewHolder{
        public TextView show_docsR,liked;
        public CircleImageView text_seen;

        public RightDocs(@NonNull View itemView) {
            super(itemView);
            liked = itemView.findViewById(R.id.liked);
            show_docsR = itemView.findViewById(R.id.show_docsR);
            text_seen = itemView.findViewById(R.id.text_seen);
        }
    }

    class RightImage extends RecyclerView.ViewHolder{
        public TextView show_imageR;
        public CircleImageView text_seen;
        public TextView liked;

        public RightImage(@NonNull View itemView) {
            super(itemView);
            liked = itemView.findViewById(R.id.liked);
            show_imageR = itemView.findViewById(R.id.show_imageR);
            text_seen = itemView.findViewById(R.id.text_seen);
        }
    }

    class LeftImage extends RecyclerView.ViewHolder{
        public ImageView isLikedI;
        public TextView show_imageL;
        public CircleImageView text_seen;

        public LeftImage(@NonNull View itemView) {
            super(itemView);
            isLikedI = itemView.findViewById(R.id.isLikedI);
            show_imageL = itemView.findViewById(R.id.show_imageL);
            text_seen = itemView.findViewById(R.id.text_seen);
        }
    }

    public class LeftVideo extends RecyclerView.ViewHolder{
        public TextView show_videoL;
        public CircleImageView text_seen;
        public ImageView isLiked;

        public LeftVideo(@NonNull View itemView) {
            super(itemView);
            show_videoL = itemView.findViewById(R.id.show_videoL);
            text_seen = itemView.findViewById(R.id.text_seen);
            isLiked = itemView.findViewById(R.id.isLiked);
        }
    }

    class RightVideo extends RecyclerView.ViewHolder{
        public TextView show_videoR,liked;
        public CircleImageView text_seen;

        public RightVideo(@NonNull View itemView) {
            super(itemView);
            liked = itemView.findViewById(R.id.liked);
            show_videoR = itemView.findViewById(R.id.show_videoR);
            text_seen = itemView.findViewById(R.id.text_seen);
        }
    }

    public class LeftLoc extends RecyclerView.ViewHolder{
        public TextView show_locL;
        public CircleImageView text_seen;
        public ImageView isLiked;

        public LeftLoc(@NonNull View itemView) {
            super(itemView);
            show_locL = itemView.findViewById(R.id.show_locationL);
            text_seen = itemView.findViewById(R.id.text_seen);
            isLiked = itemView.findViewById(R.id.isLiked);
        }
    }

    class RightLoc extends RecyclerView.ViewHolder{
        public TextView show_locR,liked;
        public CircleImageView text_seen;

        public RightLoc(@NonNull View itemView) {
            super(itemView);
            liked = itemView.findViewById(R.id.liked);
            show_locR = itemView.findViewById(R.id.show_locationR);
            text_seen = itemView.findViewById(R.id.text_seen);
        }
    }


}
