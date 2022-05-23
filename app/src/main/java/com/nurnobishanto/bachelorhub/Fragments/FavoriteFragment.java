package com.nurnobishanto.bachelorhub.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.nurnobishanto.bachelorhub.Adapter.PostsAdapter;
import com.nurnobishanto.bachelorhub.Models.PostAd;
import com.nurnobishanto.bachelorhub.Models.PostAdViewModel;
import com.nurnobishanto.bachelorhub.R;
import com.nurnobishanto.bachelorhub.Service.MyNetworkReceiver;
import com.nurnobishanto.bachelorhub.Session.SharedPrefManager;
import com.nurnobishanto.bachelorhub.utils.Utility;

import java.util.ArrayList;


public class FavoriteFragment extends Fragment {
    private MyNetworkReceiver mNetworkReceiver;
    private ProgressDialog mProgress = null;
    private RecyclerView mRecyclerView;
    private PostsAdapter mAdapter;
    private ArrayList<PostAd> mArrayList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        mNetworkReceiver = new MyNetworkReceiver(getContext());
        mProgress = Utility.showProgressDialog(getActivity(), getResources().getString( R.string.progress), true);

        //SharedPrefManager.getInstance(FavoriteActivity.this).deleteFavoriteItems();
        ArrayList<String> arrayList = SharedPrefManager.getInstance(getContext()).getFavoriteItems();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.favorite_recycler_view);

        //Receive the data and observe the data from view model
        PostAdViewModel mPostAdViewModel = ViewModelProviders.of(this).get(PostAdViewModel.class); //Initialize view model

        if (arrayList != null) {
            for (int i=0; i<arrayList.size(); i++) {
                mPostAdViewModel.getPostAdById(arrayList.get(i)).observe(getActivity(), new Observer<PostAd>() {
                    @Override
                    public void onChanged(PostAd model) {
                        if (model != null) {
                            mArrayList.add(model);
                            mAdapter.notifyDataSetChanged();
                            Utility.dismissProgressDialog(mProgress);
                        } else {
                            alertMessage();
                        }
                    }
                });
            }
        } else {
            alertMessage();
        }

        initRecyclerView();
        return view;
    }

    private void alertMessage() {
        Utility.dismissProgressDialog(mProgress);
        Utility.alertDialog(getContext(), getResources().getString(R.string.msg_no_data));
    }
    private void initRecyclerView() {
        mAdapter = new PostsAdapter(getContext(), mArrayList); //mUserViewModel.getUsers().getValue()
        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        //mAdapter.notifyDataSetChanged();
    }

}