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
            R.mipmap.ic_launcher,
            R.mipmap.ic_deal,
            R.mipmap.ic_communication,
    };
    private String titles[]={
            "Welcome to The \nBachelor Hub",
            "Rent Your Property",
            "Capture and Upload Your Property"
    };
    private String desc[]={
            "Here you can find a property rent solution. We help you to make deal with directly to the owner/tenant easily. We don't charge the owner",
            "We have more tenants community.\n You can rent your property easily by a single post.",
            "Property photos are more effective for getting your tenant easily.",

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
