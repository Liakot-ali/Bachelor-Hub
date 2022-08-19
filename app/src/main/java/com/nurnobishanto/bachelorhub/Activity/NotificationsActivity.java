package com.nurnobishanto.bachelorhub.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nurnobishanto.bachelorhub.Adapter.NotificationAdapter;
import com.nurnobishanto.bachelorhub.Models.NotificationViewModel;
import com.nurnobishanto.bachelorhub.Models.User;
import com.nurnobishanto.bachelorhub.R;
import com.nurnobishanto.bachelorhub.Service.MyNetworkReceiver;
import com.nurnobishanto.bachelorhub.Session.SharedPrefManager;
import com.nurnobishanto.bachelorhub.utils.LocaleHelper;
import com.nurnobishanto.bachelorhub.utils.Utility;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {

    private MyNetworkReceiver mNetworkReceiver;
    private ProgressDialog mProgress = null;
    private RecyclerView mRecyclerView;
    private NotificationAdapter mAdapter;
    TextView emptyText;
    //private ArrayList<User> mArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        mNetworkReceiver = new MyNetworkReceiver(this);
        User mUser = SharedPrefManager.getInstance(NotificationsActivity.this).getUser();
        mProgress = Utility.showProgressDialog(NotificationsActivity.this, getResources().getString( R.string.progress), true);

        mRecyclerView = (RecyclerView) findViewById(R.id.notification_recycler_view);
        emptyText = (TextView) findViewById(R.id.notification_empty_Text);
        emptyText.setVisibility(View.GONE);

        //Receive the data and observe the data from view model
        NotificationViewModel mViewModel = ViewModelProviders.of(this).get(NotificationViewModel.class); //Initialize view model
        mViewModel.getNotifications(mUser.getUserAuthId()).observe(this, new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                if (users != null) {
                    //mArrayList.addAll(users);
                    initRecyclerView(users);
                    mAdapter.notifyDataSetChanged();
                    emptyText.setVisibility(View.GONE);
                    Utility.dismissProgressDialog(mProgress);
                } else {
                    Utility.dismissProgressDialog(mProgress);
                    emptyText.setVisibility(View.VISIBLE);
//                    Utility.alertDialog(NotificationsActivity.this, getResources().getString(R.string.msg_no_data));
                }
            }
        });
    }

    private void initRecyclerView(ArrayList<User> mArrayList) {
        mAdapter = new NotificationAdapter(this, mArrayList); //mUserViewModel.getUsers().getValue()
        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        //mAdapter.notifyDataSetChanged();
    }

    //===============================================| Language Change
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    //===============================================| onStart(), onPause(), onResume(), onStop()
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}