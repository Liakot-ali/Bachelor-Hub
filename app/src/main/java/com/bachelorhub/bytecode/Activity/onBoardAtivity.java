package com.bachelorhub.bytecode.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bachelorhub.bytecode.Adapter.ViewPagerAdapter;
import com.bachelorhub.bytecode.R;
import com.bachelorhub.bytecode.Session.SharedPrefManager;

public class onBoardAtivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private ViewPager viewPager;
    private Button skip, next;
    private ViewPagerAdapter adapter;
    private LinearLayout dotLayout;
    private TextView[] dots;
    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            if (position == 0) {
                skip.setVisibility(View.VISIBLE);
                skip.setEnabled(true);
                next.setText("Next");

            } else if (position == 1) {
                skip.setVisibility(View.GONE);
                skip.setEnabled(false);
                next.setText("Next");
            } else {
                skip.setVisibility(View.GONE);
                skip.setEnabled(false);
                next.setText("Finish");

            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board_ativity);

        viewPager = findViewById(R.id.view_paper);
        skip = findViewById(R.id.skip);
        next = findViewById(R.id.next);
        dotLayout = findViewById(R.id.dots);
        adapter = new ViewPagerAdapter(this);
        addDots(0);
        viewPager.addOnPageChangeListener(listener);
        viewPager.setAdapter(adapter);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (next.getText().equals("Next")) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                } else {
                    SharedPrefManager.getInstance(onBoardAtivity.this).setUserIsFirstTime(false);
                    startActivity(new Intent(onBoardAtivity.this, AuthActivity.class));
                    finish();
                }
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 2);
            }
        });
    }

    @SuppressLint({"ResourceAsColor", "ResourceType"})
    private void addDots(int position) {
        dotLayout.removeAllViews();
        dots = new TextView[3];

        for (int i = 0; i < dots.length; i++) {

            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.blue));
            dotLayout.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.black));
        }
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {

            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


}