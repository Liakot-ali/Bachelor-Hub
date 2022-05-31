package com.nurnobishanto.bachelorhub.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nurnobishanto.bachelorhub.MainActivity;
import com.nurnobishanto.bachelorhub.R;
import com.nurnobishanto.bachelorhub.Service.MyNetworkReceiver;
import com.nurnobishanto.bachelorhub.utils.LocaleHelper;


public class SettingsFragment extends Fragment {
    private MyNetworkReceiver mNetworkReceiver;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mNetworkReceiver = new MyNetworkReceiver(getContext());

        //==========================================| Language change button
        Button bnBtn = (Button) view.findViewById(R.id.bangla_btn);
        bnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = LocaleHelper.setLocale(getContext(), "bn");
                Resources resources = context.getResources();
                reload();
            }
        });
        Button enBtn = (Button) view.findViewById(R.id.english_btn);
        enBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = LocaleHelper.setLocale(getContext(), "en");
                Resources resources = context.getResources();
                reload();
            }
        });
        return view;
    }


    public void reload() {
        Intent intent = getActivity().getIntent();
        getActivity().overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        getActivity().finish();
        getActivity().overridePendingTransition(0, 0);
        startActivity(intent);
    }



}