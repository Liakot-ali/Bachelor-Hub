package com.nurnobishanto.bachelorhub.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.nurnobishanto.bachelorhub.R;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }
    private int image[]={
            android.R.drawable.btn_star_big_on,
            R.drawable.ic_launcher_background,
            R.drawable.common_google_signin_btn_icon_light,
    };
    private String titles[]={
            "Hello 1",
            "Hello 2",
            "Hello 3",
    };
    private String desc[]={
            "Description 1",
            "Description 2",
            "Description 3",

    };

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.view_paper,container,false);
        ImageView imageView = view.findViewById(R.id.view_paper_img);
        TextView title = view.findViewById(R.id.view_paper_title);
        TextView dsc = view.findViewById(R.id.view_paper_dsc);

        imageView.setImageResource(image[position]);
        title.setText(titles[position]);
        dsc.setText(desc[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
