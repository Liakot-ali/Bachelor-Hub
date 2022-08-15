package com.nurnobishanto.bachelorhub.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.nurnobishanto.bachelorhub.Activity.NotificationsActivity;
import com.nurnobishanto.bachelorhub.Adapter.PostsAdapter;
import com.nurnobishanto.bachelorhub.MainActivity;
import com.nurnobishanto.bachelorhub.Models.Filter;
import com.nurnobishanto.bachelorhub.Models.PostAd;
import com.nurnobishanto.bachelorhub.Models.PostAdViewModel;
import com.nurnobishanto.bachelorhub.R;
import com.nurnobishanto.bachelorhub.Service.MyNetworkReceiver;
import com.nurnobishanto.bachelorhub.utils.ConstantKey;
import com.nurnobishanto.bachelorhub.utils.LocaleHelper;
import com.nurnobishanto.bachelorhub.utils.Utility;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private static final String TAG = "PostsListActivity";
    private MyNetworkReceiver mNetworkReceiver;
    private ProgressDialog mProgress = null;
    private RecyclerView mRecyclerView;
    private PostsAdapter mAdapter;
    private ImageView notification;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mNetworkReceiver = new MyNetworkReceiver(getContext());
        mProgress = Utility.showProgressDialog(getActivity(), getResources().getString( R.string.progress), true);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        notification = (ImageView) view.findViewById(R.id.homeNotification);

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NotificationsActivity.class));
            }
        });

        //Receive the data and observe the data from view model
        PostAdViewModel mPostAdViewModel = ViewModelProviders.of(this).get(PostAdViewModel.class); //Initialize view model

        if (getActivity().getIntent().getExtras() != null) {
            Filter model = (Filter) getActivity().getIntent().getSerializableExtra(ConstantKey.FILTER_KEY);
            String name = getActivity().getIntent().getStringExtra("name");

            mPostAdViewModel.getPostAdByFilter(model).observe(getActivity(), new Observer<ArrayList<PostAd>>() {
                @Override
                public void onChanged(ArrayList<PostAd> postAds) {
                    getAllPostData(postAds);
                }
            });
        } else {
            mPostAdViewModel.getAllPostAd().observe(getActivity(), new Observer<ArrayList<PostAd>>() {
                @Override
                public void onChanged(ArrayList<PostAd> postAds) {
                    getAllPostData(postAds);
                }
            });
        }
        return view;
    }
    private void getAllPostData(ArrayList<PostAd> postAds) {
        if (postAds != null) {
            Log.d(TAG, new Gson().toJson(postAds));
            //mArrayList.addAll(postAds);
            initRecyclerView(postAds);
            mAdapter.notifyDataSetChanged();
            Utility.dismissProgressDialog(mProgress);
        } else {
            Utility.dismissProgressDialog(mProgress);
            Utility.alertDialog(getContext(), getResources().getString(R.string.msg_no_data));
        }
    }

    private void initRecyclerView(ArrayList<PostAd> mArrayList) {
        mAdapter = new PostsAdapter(getContext(), mArrayList); //mUserViewModel.getUsers().getValue()
        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        //mAdapter.notifyDataSetChanged();
    }



}