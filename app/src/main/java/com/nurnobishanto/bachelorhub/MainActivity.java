package com.nurnobishanto.bachelorhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nurnobishanto.bachelorhub.Activity.AuthActivity;
import com.nurnobishanto.bachelorhub.Activity.EditProfileActivity;
import com.nurnobishanto.bachelorhub.Activity.PostAdActivity;
import com.nurnobishanto.bachelorhub.Additional.AboutFragment;
import com.nurnobishanto.bachelorhub.Fragments.HomeFragment;
import com.nurnobishanto.bachelorhub.Fragments.MessagesFragment;
import com.nurnobishanto.bachelorhub.Fragments.ProfileFragment;
import com.nurnobishanto.bachelorhub.Fragments.SearchFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    ActionBarDrawerToggle toggle;
    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;
    FloatingActionButton floatingActionButton;
    CircleImageView profile_image;
    TextView nameTxt,emailTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PostAdActivity.class));
            }
        });

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                new HomeFragment()).commit();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        drawer.closeDrawer(GravityCompat.START);
                        getSupportActionBar().setTitle("Home");
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                                new HomeFragment()).commit();
                        break;
                    case R.id.nav_editprofile:
                        startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
                        break;
                    case R.id.nav_about:
                        drawer.closeDrawer(GravityCompat.START);
                        getSupportActionBar().setTitle("About");
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                                new AboutFragment()).commit();
                        break;
                    case  R.id.nav_logout:
                        logout();
                        break;

                }
                return true;
            }
        });

        SharedPreferences preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);


        View headView = navigationView.getHeaderView(0);
        profile_image = headView.findViewById(R.id.profile_image);
        nameTxt = headView.findViewById(R.id.name);
        emailTxt = headView.findViewById(R.id.email);
    }

    private void getUserInformation() {


    }
    private void logout() {
        SharedPreferences sharedPreference=getSharedPreferences("Users",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.remove("isLogged");
        editor.remove("UserId");
        editor.remove("UserEmail");
        editor.apply();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        startActivity(intent);
        Toast.makeText(this,"Logout",Toast.LENGTH_SHORT).show();
        //deleteCache(SettingActivity.this);
        // clearAppData();
        finish();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =

            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {


                        case R.id.nav_home:
                            getSupportActionBar().setTitle("Home");
                            selectedFragment = new HomeFragment();
                            break;


                        case R.id.nav_search:
                            getSupportActionBar().setTitle("Search");
                            selectedFragment = new SearchFragment();
                            break;

                        case R.id.nav_message:
                            getSupportActionBar().setTitle("Messages");
                            selectedFragment = new MessagesFragment();
                            break;

                        case R.id.nav_profile:
                            getSupportActionBar().setTitle("Profile");
                            selectedFragment = new ProfileFragment();

                            break;


                    }
                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                                selectedFragment).commit();
                    }

                    return true;
                }
            };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.action_search:
                // FeedFragment.searchInput.setVisibility(View.VISIBLE);
               // startActivity(new Intent(this, FeedPostActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.more_menu, menu);
        //MenuItem item = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) item1.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                FeedFragment.postAdapter.getFilter().filter(newText);
//                return false;
//            }
//        });

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUserInformation();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        getSupportActionBar().setTitle("Home");
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                new HomeFragment()).commit();
        if (doubleBackToExitPressedOnce) {

            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}