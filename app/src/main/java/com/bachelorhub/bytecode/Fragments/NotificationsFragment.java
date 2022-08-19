package com.bachelorhub.bytecode.Fragments;

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

import com.bachelorhub.bytecode.Adapter.NotificationAdapter;
import com.bachelorhub.bytecode.Models.NotificationViewModel;
import com.bachelorhub.bytecode.Models.User;
import com.bachelorhub.bytecode.R;
import com.bachelorhub.bytecode.Service.MyNetworkReceiver;
import com.bachelorhub.bytecode.Session.SharedPrefManager;
import com.bachelorhub.bytecode.utils.Utility;

import java.util.ArrayList;


public class NotificationsFragment extends Fragment {

    private MyNetworkReceiver mNetworkReceiver;
    private ProgressDialog mProgress = null;
    private RecyclerView mRecyclerView;
    private NotificationAdapter mAdapter;

    //private ArrayList<User> mArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        mNetworkReceiver = new MyNetworkReceiver(getContext());
        User mUser = SharedPrefManager.getInstance(getContext()).getUser();
        mProgress = Utility.showProgressDialog(getActivity(), getResources().getString( R.string.progress), true);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.notification_recycler_view);



        //Receive the data and observe the data from view model
        NotificationViewModel mViewModel = ViewModelProviders.of(this).get(NotificationViewModel.class); //Initialize view model
        mViewModel.getNotifications(mUser.getUserAuthId()).observe(getActivity(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                if (users != null) {
                    //mArrayList.addAll(users);
                    initRecyclerView(users);
                    mAdapter.notifyDataSetChanged();
                    Utility.dismissProgressDialog(mProgress);
                } else {
                    Utility.dismissProgressDialog(mProgress);
                    Utility.alertDialog(getContext(), getResources().getString(R.string.msg_no_data));
                }
            }
        });
        return view;
    }

    private void initRecyclerView(ArrayList<User> mArrayList) {
        mAdapter = new NotificationAdapter(getContext(), mArrayList); //mUserViewModel.getUsers().getValue()
        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        //mAdapter.notifyDataSetChanged();
    }

}