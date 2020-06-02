package com.example.customviewdemo.scrollbarRecyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.example.customviewdemo.R;
import com.example.customviewdemo.scrollbarRecyclerview.RecyclerViewAdapter;
import com.example.customviewdemo.scrollbarRecyclerview.ScrollbarRecyclerView;

import java.util.ArrayList;

public class ScrollbarRecyclerDemo extends Activity {

    ScrollbarRecyclerView recyclerView;

    private ArrayList<String> mDatas;
    private String[]          mMovieStrings = {"星球大战系列", "异次元骇客（第十三层）", "超人", "终结者（1、2）", "12猴子", "黑客帝国系列", "移魂都市（黑暗城市）", "超时空接触", "千钧一发", "2001漫游太空", "肖申克的救赎", "教父", "美国往事", "天堂电影院", "无主之城", "活着", "阿甘正传", "勇敢的心", "楚门的世界", "音乐之声", "辛德勒的名单", "星球大战系列", "异次元骇客（第十三层）", "超人", "终结者（1、2）", "12猴子", "黑客帝国系列", "移魂都市（黑暗城市）"};


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
    }

    private ArrayList<String> getDummyItems() {
        ArrayList<String> items = new ArrayList<String>();
        for (int i = 0; i < mMovieStrings.length; i++) {
            items.add(mMovieStrings[i]);
        }
        return items;
    }
}
