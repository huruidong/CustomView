package com.example.customviewdemo.scrollbarRecyclerview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.EdgeEffect;
import android.widget.LinearLayout;

import com.example.customviewdemo.R;
import com.example.customviewdemo.app.BaseActivity;
import com.example.customviewdemo.pinnerGroupTitle.PinnerItemDecoration;

import java.util.ArrayList;

public class ScrollbarRecyclerDemo extends BaseActivity {

    ScrollbarRecyclerView recyclerView;

    private ArrayList<String> mDatas;
    private String[]          mMovieStrings = {"星球大战系列", "异次元骇客（第十三层）", "超人", "终结者（1、2）", "12猴子", "黑客帝国系列", "移魂都市（黑暗城市）", "超时空接触", "千钧一发", "2001漫游太空", "肖申克的救赎", "教父", "美国往事", "天堂电影院", "无主之城", "活着", "阿甘正传", "勇敢的心", "楚门的世界", "音乐之声"/*, "辛德勒的名单", "星球大战系列", "异次元骇客（第十三层）", "超人", "终结者（1、2）", "12猴子", "黑客帝国系列", "移魂都市（黑暗城市）"*/};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrollbar_recyclerview_activity);

        getActionBar().setTitle("Scrollbar_Recyclerview");

        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDatas = getDummyItems();
        RecyclerViewAdapter listadapter = new RecyclerViewAdapter(null, this, mDatas);
        recyclerView.setAdapter(listadapter);
        recyclerView.setEdgeEffectFactory(new RecyclerView.EdgeEffectFactory() {
            @NonNull
            @Override
            protected EdgeEffect createEdgeEffect(@NonNull RecyclerView view, int direction) {
                EdgeEffect edgeEffect = new EdgeEffect(ScrollbarRecyclerDemo.this);
                edgeEffect.setColor(Color.parseColor("#ffffff"));
                edgeEffect.setSize(100, 500);
                return edgeEffect;
            }
        });
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
        layoutParams.height = getResources().getDisplayMetrics().widthPixels;
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        recyclerView.setLayoutParams(layoutParams);
//        recyclerView.setFadingEdgeLength(500);
//        recyclerView.setVerticalFadingEdgeEnabled(true);

//        recyclerView.addItemDecoration(new PinnerItemDecoration(ScrollbarRecyclerDemo.this,
//                                                                new PinnerItemDecoration.GroupCallBack() {
//                                                                       @Override
//                                                                       public int getGroupHeight() {
//                                                                           return 72;
//                                                                       }
//
//                                                                       @Override
//                                                                       public String getGroupText(int position) {
//                                                                           return PinnerItemDecoration.getFirstLetter(
//                                                                                   mDatas.get(
//                                                                                           position));
//                                                                       }
//                                                                   }));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new CustomItemTouchCallback(listadapter, R.id.hrd_slidelayout_content));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ScaleScrollbar scaleScrollbar = findViewById(R.id.scrollbar);
        scaleScrollbar.attachToRecyclerView(recyclerView);
    }

    private ArrayList<String> getDummyItems() {
        ArrayList<String> items = new ArrayList<String>();
        for (int i = 0; i < mMovieStrings.length; i++) {
            items.add(mMovieStrings[i]);
        }
        return items;
    }
}
