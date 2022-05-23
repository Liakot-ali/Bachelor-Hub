package com.nurnobishanto.bachelorhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nurnobishanto.bachelorhub.Activity.AuthActivity;
import com.nurnobishanto.bachelorhub.Activity.EditProfileActivity;
import com.nurnobishanto.bachelorhub.Activity.PhoneActivity;
import com.nurnobishanto.bachelorhub.Activity.PostAdActivity;
import com.nurnobishanto.bachelorhub.Additional.AboutFragment;
import com.nurnobishanto.bachelorhub.Fragments.FavoriteFragment;
import com.nurnobishanto.bachelorhub.Fragments.HomeFragment;
import com.nurnobishanto.bachelorhub.Fragments.MessagesFragment;
import com.nurnobishanto.bachelorhub.Fragments.ProfileFragment;
import com.nurnobishanto.bachelorhub.Fragments.SearchFragment;
import com.nurnobishanto.bachelorhub.Models.Filter;
import com.nurnobishanto.bachelorhub.Models.User;
import com.nurnobishanto.bachelorhub.Service.MyLocationReceiver;
import com.nurnobishanto.bachelorhub.Service.MyNetworkReceiver;
import com.nurnobishanto.bachelorhub.Session.SharedPrefManager;
import com.nurnobishanto.bachelorhub.utils.ConstantKey;
import com.nurnobishanto.bachelorhub.utils.GpsUtility;
import com.nurnobishanto.bachelorhub.utils.Network;
import com.nurnobishanto.bachelorhub.utils.Utility;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    public static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 99;
    private int REQUEST_CHECK_SETTINGS = 0;
    private FusedLocationProviderClient mFusedLocationClient;

    //==========================================| Filter
    private Spinner filterLocation;
    private RangeSeekBar filterPriceRange;

    private Button[] propertyBtn = new Button[6];
    private Button propertyBtn_unfocused;
    private int[] propertyBtn_id = {R.id.propertyBtn0, R.id.propertyBtn1, R.id.propertyBtn2, R.id.propertyBtn3, R.id.propertyBtn4, R.id.propertyBtn5};

    private Button[] renterBtn = new Button[6];
    private Button renterBtn_unfocused;
    private int[] renterBtn_id = {R.id.renterBtn0, R.id.renterBtn1, R.id.renterBtn2, R.id.renterBtn3, R.id.renterBtn4, R.id.renterBtn5};

    private Button[] bedBtn = new Button[5];
    private Button bedBtn_unfocused;
    private int[] bedBtn_id = {R.id.bedBtn0, R.id.bedBtn1, R.id.bedBtn2, R.id.bedBtn3, R.id.bedBtn4};

    private Button filterBtn;
    private String propertyType;
    private String renterType;
    private String bedRooms;

  //  private ArrayList<PostAd> mArrayList = new ArrayList<>();
    private MyNetworkReceiver mNetworkReceiver;
    private MyLocationReceiver mLocationReceiver;
    private LocationManager manager;
    private User mUser = null;

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

        mNetworkReceiver = new MyNetworkReceiver(this);
        mLocationReceiver = new MyLocationReceiver(this);
        mUser = SharedPrefManager.getInstance(MainActivity.this).getUser();

        //-----------------------------------------------| GPS/Location
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkGpsEnabled(manager);

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
                    case R.id.nav_fav:
                        drawer.closeDrawer(GravityCompat.START);
                        getSupportActionBar().setTitle("Favorite");
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                                new FavoriteFragment()).commit();
                        break;
                    case R.id.nav_about:
                        drawer.closeDrawer(GravityCompat.START);
                        getSupportActionBar().setTitle("About");
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                                new AboutFragment()).commit();
                        break;
                    case  R.id.nav_logout:
                       // logout();
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.about_title)
                                .setMessage(R.string.msg_sign_out)
                                .setPositiveButton(R.string.sign_out, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        SharedPrefManager.getInstance(MainActivity.this).deleteCurrentSession();
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(MainActivity.this, PhoneActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //For login to clear this screen for that did not back this screen
                                        startActivity(intent);
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(R.string.msg_neg, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
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
                filterDialog();
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

    //====================================| Explain why the app needs the request permissions
    //https://developers.google.com/maps/documentation/android-sdk/location
    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION); //if there is no permission allowed then, display permission request dialog
        } else {
          //  mMap.setMyLocationEnabled(true);
           // Utility.changeCurrentLocationIcon(mapFragment);
            getDeviceLocation();
        }
    }

    //====================================| Handle the permissions request response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //For allow button
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // mMap.setMyLocationEnabled(true);
                    // Utility.changeCurrentLocationIcon(mapFragment);
                    getDeviceLocation();
                }
            } else {
                //For denied button
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                requestPermissions();
            }
        }
    }

    //===============================================| Get Device Location/LatLng
    private void getDeviceLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng origin = new LatLng(location.getLatitude(),location.getLongitude());
                       // Utility.moveToLocation(mMap, new LatLng(origin.latitude, origin.longitude));
                        if (Network.haveNetwork(MainActivity.this)) {
                            String mAddress = Utility.getAddress(MainActivity.this, origin);
                            SharedPrefManager.getInstance(MainActivity.this).saveCurrentLatLng(origin);
                        }
                    }
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    //===============================================| GPS/Location
    private void checkGpsEnabled(LocationManager manager) {
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && GpsUtility.hasGPSDevice(this)) {
            GpsUtility.displayLocationSettingsRequest(new GpsUtility.GpsOnListenerCallBack() {
                @Override
                public void gpsResultCode(int resultCode) {
                    REQUEST_CHECK_SETTINGS = resultCode;
                }
            }, this);
        } else {
           // checkPermissions();
        }
    }

    //====================================================| BroadcastReceiver
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getExtras().getString("title");
            if (title.equals("Accepted")) {
                //
            }
        }
    };

    //====================================================| Filter Custom AlertDialog
    protected void filterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_post_filter, null);
        builder.setView(view);
        builder.setCancelable(true);
        builder.create();

        final AlertDialog dialog = builder.show();

        this.filterLocation = (Spinner) view.findViewById(R.id.filter_location);
        this.filterPriceRange = (RangeSeekBar) view.findViewById(R.id.filter_price_range);
        final EditText min = view.findViewById(R.id.range_min);
        final EditText max = view.findViewById(R.id.range_max);
        filterPriceRange.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                min.setText(minValue.toString());
                max.setText(maxValue.toString());
            }
        });

        //---------------------------------------------| Property Button
        for(int i = 0; i < propertyBtn.length; i++){
            propertyBtn[i] = (Button) view.findViewById(propertyBtn_id[i]);
            propertyBtn[i].setBackground(getResources().getDrawable(R.drawable.shape_button_border));
            propertyBtn[i].setOnClickListener(new ActionHandlerProperty());
        }
        propertyBtn_unfocused = propertyBtn[0];

        //---------------------------------------------| Renter Button
        for(int i = 0; i < renterBtn.length; i++){
            renterBtn[i] = (Button) view.findViewById(renterBtn_id[i]);
            renterBtn[i].setBackground(getResources().getDrawable(R.drawable.shape_button_border));
            renterBtn[i].setOnClickListener(new ActionHandlerRenter());
        }
        renterBtn_unfocused = renterBtn[0];

        //---------------------------------------------| Bedrooms Button
        for(int i = 0; i < bedBtn.length; i++){
            bedBtn[i] = (Button) view.findViewById(bedBtn_id[i]);
            bedBtn[i].setBackground(getResources().getDrawable(R.drawable.shape_button_border));
            bedBtn[i].setOnClickListener(new ActionHandlerBedrooms());
        }
        bedBtn_unfocused = bedBtn[0];

        //---------------------------------------------| Filter Button
        filterBtn = (Button) view.findViewById(R.id.filter_btn);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!filterLocation.getSelectedItem().toString().contains("Select Location")) {
                    if (bedRooms != null && bedRooms.equals("4+")) {
                        passFilterValues(filterLocation.getSelectedItem().toString(),min.getText().toString(),max.getText().toString(),propertyType,renterType,"Any");
                    } else {
                        passFilterValues(filterLocation.getSelectedItem().toString(),min.getText().toString(),max.getText().toString(),propertyType,renterType,bedRooms);
                    }
                } else {
                    Utility.alertDialog(MainActivity.this, getResources().getString( R.string.msg_select_location));
                }
            }
        });

        ((TextView) view.findViewById(R.id.filter_dialog_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        ((TextView) view.findViewById(R.id.filter_dialog_reset)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Reset", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //====================================================| Filter values pass into intent
    private void passFilterValues(String loc,String min,String max,String pro,String rent,String bed) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //intent.putExtra(ConstantKey.FILTER_KEY, loc+","+min+","+max+","+pro+","+rent+","+bed);
        intent.putExtra(ConstantKey.FILTER_KEY, new Filter(loc, min, max, pro, rent, bed));
        startActivity(intent);
    }
    //====================================================| Property Button Action
    private class ActionHandlerProperty implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.propertyBtn0 :
                    propertyType = propertyBtn[0].getText().toString();
                    setFocusProperty(propertyBtn_unfocused, propertyBtn[0]);
                    break;
                case R.id.propertyBtn1 :
                    propertyType = propertyBtn[1].getText().toString();
                    setFocusProperty(propertyBtn_unfocused, propertyBtn[1]);
                    break;
                case R.id.propertyBtn2 :
                    propertyType = propertyBtn[2].getText().toString();
                    setFocusProperty(propertyBtn_unfocused, propertyBtn[2]);
                    break;
                case R.id.propertyBtn3 :
                    propertyType = propertyBtn[3].getText().toString();
                    setFocusProperty(propertyBtn_unfocused, propertyBtn[3]);
                    break;
                case R.id.propertyBtn4 :
                    propertyType = propertyBtn[4].getText().toString();
                    setFocusProperty(propertyBtn_unfocused, propertyBtn[4]);
                    break;
                case R.id.propertyBtn5 :
                    propertyType = propertyBtn[5].getText().toString();
                    setFocusProperty(propertyBtn_unfocused, propertyBtn[5]);
                    break;
            }
        }
    }
    private void setFocusProperty(Button propertyBtn_unfocused, Button propertyBtn_focus){
        propertyBtn_unfocused.setTextColor(Color.rgb(49, 50, 51));
        propertyBtn_unfocused.setBackground(getResources().getDrawable(R.drawable.shape_button_border));
        propertyBtn_focus.setTextColor(Color.rgb(255, 255, 255));
        propertyBtn_focus.setBackgroundColor(Color.parseColor("#000000"));
        this.propertyBtn_unfocused = propertyBtn_focus;
    }

    //====================================================| Renter Button Action
    private class ActionHandlerRenter implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.renterBtn0 :
                    renterType = renterBtn[0].getText().toString();
                    setFocusRenter(renterBtn_unfocused, renterBtn[0]);
                    break;
                case R.id.renterBtn1 :
                    renterType = renterBtn[1].getText().toString();
                    setFocusRenter(renterBtn_unfocused, renterBtn[1]);
                    break;
                case R.id.renterBtn2 :
                    renterType = renterBtn[2].getText().toString();
                    setFocusRenter(renterBtn_unfocused, renterBtn[2]);
                    break;
                case R.id.renterBtn3 :
                    renterType = renterBtn[3].getText().toString();
                    setFocusRenter(renterBtn_unfocused, renterBtn[3]);
                    break;
                case R.id.renterBtn4 :
                    renterType = renterBtn[4].getText().toString();
                    setFocusRenter(renterBtn_unfocused, renterBtn[4]);
                    break;
                case R.id.renterBtn5 :
                    renterType = renterBtn[5].getText().toString();
                    setFocusRenter(renterBtn_unfocused, renterBtn[5]);
                    break;
            }
        }
    }

    private void setFocusRenter(Button btn_unfocused, Button btn_focus){
        btn_unfocused.setTextColor(Color.rgb(49, 50, 51));
        btn_unfocused.setBackground(getResources().getDrawable(R.drawable.shape_button_border));
        btn_focus.setTextColor(Color.rgb(255, 255, 255));
        btn_focus.setBackgroundColor(Color.parseColor("#000000"));
        this.renterBtn_unfocused = btn_focus;
    }

    //====================================================| Bedrooms Button Action
    private class ActionHandlerBedrooms implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.bedBtn0 :
                    bedRooms = bedBtn[0].getText().toString();
                    setFocusBed(bedBtn_unfocused, bedBtn[0]);
                    break;
                case R.id.bedBtn1 :
                    bedRooms = bedBtn[1].getText().toString();
                    setFocusBed(bedBtn_unfocused, bedBtn[1]);
                    break;
                case R.id.bedBtn2 :
                    bedRooms = bedBtn[2].getText().toString();
                    setFocusBed(bedBtn_unfocused, bedBtn[2]);
                    break;
                case R.id.bedBtn3 :
                    bedRooms = bedBtn[3].getText().toString();
                    setFocusBed(bedBtn_unfocused, bedBtn[3]);
                    break;
                case R.id.bedBtn4 :
                    bedRooms = bedBtn[4].getText().toString();
                    setFocusBed(bedBtn_unfocused, bedBtn[4]);
                    break;
            }
        }
    }

    private void setFocusBed(Button bedBtn_unfocused, Button bedBtn_focus){
        bedBtn_unfocused.setTextColor(Color.rgb(49, 50, 51));
        bedBtn_unfocused.setBackground(getResources().getDrawable(R.drawable.shape_button_border));
        bedBtn_focus.setTextColor(Color.rgb(255, 255, 255));
        bedBtn_focus.setBackgroundColor(Color.parseColor("#000000"));
        this.bedBtn_unfocused = bedBtn_focus;
    }


}