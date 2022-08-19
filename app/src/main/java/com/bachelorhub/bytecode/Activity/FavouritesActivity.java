package com.bachelorhub.bytecode.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bachelorhub.bytecode.Adapter.PostsAdapter;
import com.bachelorhub.bytecode.Models.PostAd;
import com.bachelorhub.bytecode.Models.PostAdViewModel;
import com.bachelorhub.bytecode.R;
import com.bachelorhub.bytecode.Service.MyNetworkReceiver;
import com.bachelorhub.bytecode.Session.SharedPrefManager;
import com.bachelorhub.bytecode.utils.Utility;

import java.util.ArrayList;

public class FavouritesActivity extends AppCompatActivity {

    private MyNetworkReceiver mNetworkReceiver;
    private ProgressDialog mProgress = null;
    private RecyclerView mRecyclerView;
    private PostsAdapter mAdapter;
    private ArrayList<PostAd> mArrayList = new ArrayList<>();

    TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        emptyText = findViewById(R.id.favouriteEmptyText);
        emptyText.setVisibility(View.GONE);

        mNetworkReceiver = new MyNetworkReceiver(FavouritesActivity.this);
        mProgress = Utility.showProgressDialog(FavouritesActivity.this, getResources().getString(R.string.progress), true);

        //SharedPrefManager.getInstance(FavoriteActivity.this).deleteFavoriteItems();
        ArrayList<String> arrayList = SharedPrefManager.getInstance(FavouritesActivity.this).getFavoriteItems();
        mRecyclerView = (RecyclerView) findViewById(R.id.favorite_recycler_view);

        //Receive the data and observe the data from view model
        PostAdViewModel mPostAdViewModel = ViewModelProviders.of(this).get(PostAdViewModel.class); //Initialize view model

        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                mPostAdViewModel.getPostAdById(arrayList.get(i)).observe(FavouritesActivity.this, new Observer<PostAd>() {
                    @Override
                    public void onChanged(PostAd model) {
                        if (model != null) {
                            mArrayList.add(model);
                            mAdapter.notifyDataSetChanged();
                            Utility.dismissProgressDialog(mProgress);
                            emptyText.setVisibility(View.GONE);
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
    }

    private void alertMessage() {
        Utility.dismissProgressDialog(mProgress);
        emptyText.setVisibility(View.VISIBLE);
//        Utility.alertDialog(this, getResources().getString(R.string.msg_no_data));
    }

    private void initRecyclerView() {
        mAdapter = new PostsAdapter(FavouritesActivity.this, mArrayList); //mUserViewModel.getUsers().getValue()
        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(FavouritesActivity.this));
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}