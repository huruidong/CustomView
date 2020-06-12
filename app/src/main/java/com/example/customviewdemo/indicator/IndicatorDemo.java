package com.example.customviewdemo.indicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.customviewdemo.R;

import java.util.ArrayList;
import java.util.List;

public class IndicatorDemo extends AppCompatActivity {

    ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indicatior_demo_main);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(AaFragmentDemo.getInstance("Title-1"));
        fragments.add(AaFragmentDemo.getInstance("Title-2"));
        fragments.add(AaFragmentDemo.getInstance("Title-3"));
        fragments.add(AaFragmentDemo.getInstance("Title-4"));
        fragments.add(AaFragmentDemo.getInstance("Title-5"));
        fragments.add(AaFragmentDemo.getInstance("Title-6"));
        fragments.add(AaFragmentDemo.getInstance("Title-7"));
        fragments.add(AaFragmentDemo.getInstance("Title-8"));
        viewpager = findViewById(R.id.viewpager);
        AaFragmentAdapter adapter = new AaFragmentAdapter(getSupportFragmentManager(), fragments, new String[]{"Title-1", "Title-2", "Title-3", "Title-4"});
        viewpager.setAdapter(adapter);
        ((CustomIndicator)findViewById(R.id.CustomIndicator1)).bindViewPager(viewpager);
    }

}
