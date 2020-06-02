package com.example.customviewdemo.scrollbarRecyclerview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.customviewdemo.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context           mContext;
    private ArrayList<String> list;
    private LayoutInflater    layoutInflater;
    private Class             targetClass;

    public RecyclerViewAdapter(Bundle savedInstanceState, Context mContext, ArrayList<String> list) {
        this.mContext = mContext;
        this.list = list;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MyViewHolder myViewHolder;
        myViewHolder = new MyViewHolder(
                layoutInflater.inflate(R.layout.item_recyclerview,
                                       viewGroup, false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
//        if (i == 0) {
//            myViewHolder.textlevel1.setText("window 平移动画-无延迟");
//        }
//        if (i == 1) {
//            myViewHolder.textlevel1.setText("window 平移动画-有延迟");
//        }
//        if (i == 2) {
//            myViewHolder.textlevel1.setText("window 缩放动画-无延迟");
//        }
//        if (i == 3) {
//            myViewHolder.textlevel1.setText("window 缩放动画-有延迟");
//        }
//        if (i == 4) {
//            myViewHolder.root.setBackgroundResource(R.drawable.item_click);
//            myViewHolder.textlevel1.setText("无涟漪-无延迟");
//        }
//        if (i == 5) {
//            myViewHolder.root.setBackgroundResource(R.drawable.item_click);
//            myViewHolder.textlevel1.setText("无涟漪-有延迟");
//        }
//        if (i == 6) {
//            myViewHolder.root.setBackgroundResource(R.drawable.item_click);
//            myViewHolder.textlevel1.setText("无涟漪-有延迟、延迟32");
//        }
//        if (i == 7) {
//            myViewHolder.root.setBackgroundResource(R.drawable.ss_base_ripple);
//            myViewHolder.textlevel1.setText("有涟漪-无延迟");
//        }
//        if (i == 8) {
//            myViewHolder.root.setBackgroundResource(R.drawable.ss_base_ripple);
//            myViewHolder.textlevel1.setText("有涟漪-有延迟");
//        }
//        myViewHolder.root.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (i == 0) {
//                    mContext.startActivity(new Intent(mContext, RecyclerViewDemo.class));
//                    ((Activity) mContext).overridePendingTransition(R.anim.activity_in,
//                                                                    R.anim.activity_out);
//                }
//                if (i == 1) {
//                    myViewHolder.itemView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mContext.startActivity(new Intent(mContext, RecyclerViewDemo.class));
//                            ((Activity) mContext).overridePendingTransition(R.anim.activity_in,
//                                                                            R.anim.activity_out);
//                        }
//                    }, 64);
//                } else if (i == 2) {
//                    mContext.startActivity(new Intent(mContext, RecyclerViewDemo.class));
//                    ((Activity) mContext).overridePendingTransition(R.anim.activity_in_scale,
//                                                                    R.anim.activity_out);
//                } else if (i == 3) {
//                    myViewHolder.itemView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mContext.startActivity(new Intent(mContext, RecyclerViewDemo.class));
//                            ((Activity) mContext).overridePendingTransition(
//                                    R.anim.activity_in_scale, R.anim.activity_out);
//                        }
//                    }, 64);
//                } else if (i == 5 || i== 8) {
//                    myViewHolder.itemView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mContext.startActivity(new Intent(mContext, RecyclerViewDemo.class));
//                        }
//                    }, 64);
//                }
//                else if (i == 6) {
//                    myViewHolder.itemView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mContext.startActivity(new Intent(mContext, RecyclerViewDemo.class));
//                        }
//                    }, 32);
//                }
//                else {
//                    ((Activity) mContext).startActivity(
//                            new Intent(mContext, RecyclerViewDemo.class));
//                }
//            }
//        });
        //        myViewHolder.root.setOnTouchListener(new View.OnTouchListener() {
        //            @Override
        //            public boolean onTouch(View v, MotionEvent event) {
        //                if (event.getAction() == MotionEvent.ACTION_UP) {
        //                    int[]  bb  = myViewHolder.root.getBackground().getState();
        //                    String bbb = "";
        //                    for (int i = 0; i < bb.length; i++) {
        //                        bbb = bbb + "," + bb[i];
        //                    }
        //                    Log.d("huruidong", "at ssui at com ---> onTouch() bbb: \n" + bbb);
        //                    int[] status = {
        ////                            16842908, // state_focused
        //                            16842909, // state_window_focused
        //                            16842910, // state_enabled
        ////                            16842919, // state_pressed
        //                            16843547, // state_accelerated
        //                            1052726   // state_hovered
        //                    };
        //                    myViewHolder.root.getBackground().setState(status);
        //                    ((RippleDrawable) myViewHolder.root.getBackground()).jumpToCurrentState();
        //                }
        //                int[]  aa  = myViewHolder.root.getDrawableState();
        //                String aaa = "";
        //                for (int i = 0; i < aa.length; i++) {
        //                    aaa = aaa + "," + aa[i];
        //                }
        //                int[]  bb  = myViewHolder.root.getBackground().getState();
        //                String bbb = "";
        //                for (int i = 0; i < bb.length; i++) {
        //                    bbb = bbb + "," + bb[i];
        //                }
        //                Log.d("huruidong",
        //                      "at ssui at com ---> recycler-onTouch() key:" + "\n" + event.getAction() + "\n" + aaa +"\n" + bbb);
        //                return false;
        //            }
        //        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView    headicon;
        public TextView     textlevel1;
        public ImageView    tailicon;
        public LinearLayout root;

        public MyViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            headicon = itemView.findViewById(R.id.headicon);
            textlevel1 = itemView.findViewById(R.id.textlevel1);
            tailicon = itemView.findViewById(R.id.tailicon);
        }
    }
}
