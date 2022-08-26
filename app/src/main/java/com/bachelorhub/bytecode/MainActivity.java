package com.bachelorhub.bytecode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.bachelorhub.bytecode.Activity.PostAdActivity;
import com.bachelorhub.bytecode.Activity.VerificationActivity;
import com.bachelorhub.bytecode.Fragments.HomeFragment;
import com.bachelorhub.bytecode.Fragments.MessagesFragment;
import com.bachelorhub.bytecode.Fragments.ProfileFragment;
import com.bachelorhub.bytecode.Models.Filter;
import com.bachelorhub.bytecode.Models.User;
import com.bachelorhub.bytecode.Service.MyLocationReceiver;
import com.bachelorhub.bytecode.Service.MyNetworkReceiver;
import com.bachelorhub.bytecode.Session.SharedPrefManager;
import com.bachelorhub.bytecode.utils.ConstantKey;
import com.bachelorhub.bytecode.utils.GpsUtility;
import com.bachelorhub.bytecode.utils.Network;
import com.bachelorhub.bytecode.utils.Utility;
import com.squareup.picasso.Picasso;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


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
    TextView nameTxt, emailTxt, verify, status;
    private DatabaseReference reference;
    private FirebaseUser fuser;
    private String userid, userRole = "User";


    private LocationManager locationManager;

    int PERMISSION_ID = 44;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        InitializeAll();

//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.nav_view);

//        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PostAdActivity.class));
            }
        });


        mNetworkReceiver = new MyNetworkReceiver(this);
        mLocationReceiver = new MyLocationReceiver(this);
        mUser = SharedPrefManager.getInstance(MainActivity.this).getUser();
//
        //-----------------------------------------------| GPS/Location
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkGpsEnabled(manager);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.nav_home:
//                        drawer.closeDrawer(GravityCompat.START);
//                        getSupportActionBar().setTitle("Home");
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
//                                new HomeFragment()).commit();
//                        break;
//                    case R.id.nav_editprofile:
//                        drawer.closeDrawer(GravityCompat.START);
//                        startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
//                        break;
//                    case R.id.nav_fav:
//                        drawer.closeDrawer(GravityCompat.START);
//                        getSupportActionBar().setTitle("Favorite");
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
//                                new FavoriteFragment()).commit();
//                        break;
//                    case R.id.nav_notification:
//                        drawer.closeDrawer(GravityCompat.START);
////                        getSupportActionBar().setTitle("Notifications");
////                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
////                                new NotificationsFragment()).commit();
//                        startActivity(new Intent(MainActivity.this, NotificationsActivity.class));
//                        break;
//                    case R.id.nav_verify:
//                        drawer.closeDrawer(GravityCompat.START);
//                        startActivity(new Intent(MainActivity.this, VerificationActivity.class));
//                        finish();
//                        break;
//                    case R.id.nav_about:
//                        drawer.closeDrawer(GravityCompat.START);
//                        getSupportActionBar().setTitle("About");
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
//                                new AboutFragment()).commit();
//                        break;
//                    case R.id.nav_logout:
//
//
//                        new AlertDialog.Builder(MainActivity.this)
//                                .setTitle(R.string.about_title)
//                                .setMessage(R.string.msg_sign_out)
//                                .setPositiveButton(R.string.sign_out, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int i) {
//                                        SharedPrefManager.getInstance(MainActivity.this).deleteCurrentSession();
//                                        FirebaseAuth.getInstance().signOut();
//                                        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //For login to clear this screen for that did not back this screen
//                                        startActivity(intent);
//                                        dialog.dismiss();
//                                    }
//                                })
//                                .setNegativeButton(R.string.msg_neg, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int i) {
//                                        dialog.dismiss();
//                                    }
//                                })
//                                .show();
//                        break;
//
//                }
//                return true;
//            }
//        });

//        View headView = navigationView.getHeaderView(0);
//        profile_image = headView.findViewById(R.id.profile_image);
//        nameTxt = headView.findViewById(R.id.name);
//        emailTxt = headView.findViewById(R.id.email);
//        verify = headView.findViewById(R.id.userVerify);
//        status = headView.findViewById(R.id.status);


    }

    private void InitializeAll() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Window window = MainActivity.this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.orange));

        floatingActionButton = findViewById(R.id.fab);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        assert fuser != null;
        userid = fuser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("tolet_users").child(userid);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        ///------Default fragment for first time-------
//        String name = getIntent().getStringExtra("name");
//        if (name != null) {
//            getSupportActionBar().setTitle(name);
//        } else {
//            getSupportActionBar().setTitle("Home");
//        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                new HomeFragment()).commit();
    }

    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
                            // Utility.moveToLocation(mMap, new LatLng(origin.latitude, origin.longitude));
                            if (Network.haveNetwork(MainActivity.this)) {
                                String mAddress = Utility.getAddress(MainActivity.this, origin);
                                SharedPrefManager.getInstance(MainActivity.this).saveCurrentLatLng(origin);
                                //Utility.alertDialog(MainActivity.this,mAddress+"\nLa: "+location.getLatitude()+"\nLo:"+location.getLongitude());
                            }


                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
                // Utility.moveToLocation(mMap, new LatLng(origin.latitude, origin.longitude));
                if (Network.haveNetwork(MainActivity.this)) {
                    String mAddress = Utility.getAddress(MainActivity.this, origin);
                    SharedPrefManager.getInstance(MainActivity.this).saveCurrentLatLng(origin);
                    //Utility.alertDialog(MainActivity.this,mAddress+"\nLat: "+location.getLatitude()+"\nLon :"+location.getLongitude());
                }

            }
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    private void getUserInformation() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();


                    if (map.get("userFullName") != null) {
                        nameTxt.setText(map.get("userFullName").toString());
                    }
                    if (map.get("userRole") != null) {
                        userRole = map.get("userRole").toString();
                    }
                    if (map.get("status") != null) {
                        status.setText(map.get("status").toString());
                    }
                    if (map.get("userEmail") != null) {
                        emailTxt.setText(map.get("userEmail").toString());

                    }
                    if (map.get("userImageUrl") != null) {
                        Picasso.get()
                                .load(map.get("userImageUrl").toString())
                                .placeholder(R.mipmap.ic_launcher)
                                .error(R.mipmap.ic_launcher)
                                .into(profile_image);
                    }
                    if (map.get("userVerify") != null) {
                        verify.setText(map.get("userVerify").toString());

                    } else {
                        startActivity(new Intent(MainActivity.this, VerificationActivity.class));
                        finish();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =

            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
                    switch (item.getItemId()) {
                        case R.id.nav_home:
//                            getSupportActionBar().setTitle("Home");

                            assert fragment != null;
                            if(!(fragment instanceof HomeFragment)){
//                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
//                                        new HomeFragment()).commit();
                                selectedFragment = new HomeFragment();
                            }
                            break;


                        case R.id.nav_search:
                            filterDialog();
                            // getSupportActionBar().setTitle("Search");
                            //  selectedFragment = new SearchFragment();
                            break;

                        case R.id.nav_message:
//                            getSupportActionBar().setTitle("Messages");
                            assert fragment != null;
                            if(!(fragment instanceof MessagesFragment)){
//                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
//                                        new HomeFragment()).commit();
                                selectedFragment = new MessagesFragment();
                            }
//                            selectedFragment = new MessagesFragment();
                            break;

                        case R.id.nav_profile:
//                            getSupportActionBar().hide();
//                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                            assert fragment != null;
                            if(!(fragment instanceof ProfileFragment)){
//                                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
//                                        new HomeFragment()).commit();
                                selectedFragment = new ProfileFragment();
                            }

                            break;
                    }
                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                                selectedFragment).commit();
                    }

                    return true;
                }
            };

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//
//            case R.id.action_search:
//                filterDialog();
//                // FeedFragment.searchInput.setVisibility(View.VISIBLE);
//                // startActivity(new Intent(this, FeedPostActivity.class));
//                return true;
//            case R.id.action_settings:
//                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
//                return true;
//            case R.id.action_remove_fav:
//                SharedPrefManager.getInstance(MainActivity.this).deleteFavoriteItems();
//                return true;
//            case R.id.action_notifications:
//                startActivity(new Intent(MainActivity.this, NotificationsActivity.class));
//                return true;
//            case R.id.action_showMap:
//                startActivity(new Intent(MainActivity.this, ShowMapsActivity.class));
//                return true;
////            case R.id.action_admin:
////                if (userRole.equals("Admin")) {
////                    startActivity(new Intent(MainActivity.this, AdminHomeActivity.class));
////                } else {
////                    //Utility.alertDialog(MainActivity.this,"You are not Admin!");
////                    startActivity(new Intent(MainActivity.this, AdminHomeActivity.class));
////                }
////                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.more_menu, menu);
//        //MenuItem item = menu.findItem(R.id.action_search);
////        SearchView searchView = (SearchView) item1.getActionView();
////        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
////            @Override
////            public boolean onQueryTextSubmit(String query) {
////
////                return false;
////            }
////
////            @Override
////            public boolean onQueryTextChange(String newText) {
////                FeedFragment.postAdapter.getFilter().filter(newText);
////                return false;
////            }
////        });
//
//        return true;
//    }

    @Override
    protected void onStart() {
        super.onStart();
//        getUserInformation();
    }

    //---For double back press to exit------
    long currentTime = System.currentTimeMillis();

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        assert fragment != null;
        if(!(fragment instanceof HomeFragment)){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                    new HomeFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else if (System.currentTimeMillis() - currentTime < 2000) {
            super.onBackPressed();
        } else {
            currentTime = System.currentTimeMillis();
            Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();
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
            checkPermissions();
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
        for (int i = 0; i < propertyBtn.length; i++) {
            propertyBtn[i] = (Button) view.findViewById(propertyBtn_id[i]);
            propertyBtn[i].setBackground(getResources().getDrawable(R.drawable.shape_button_border));
            propertyBtn[i].setOnClickListener(new ActionHandlerProperty());
        }
        propertyBtn_unfocused = propertyBtn[0];

        //---------------------------------------------| Renter Button
        for (int i = 0; i < renterBtn.length; i++) {
            renterBtn[i] = (Button) view.findViewById(renterBtn_id[i]);
            renterBtn[i].setBackground(getResources().getDrawable(R.drawable.shape_button_border));
            renterBtn[i].setOnClickListener(new ActionHandlerRenter());
        }
        renterBtn_unfocused = renterBtn[0];

        //---------------------------------------------| Bedrooms Button
        for (int i = 0; i < bedBtn.length; i++) {
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
                        passFilterValues(filterLocation.getSelectedItem().toString(), min.getText().toString(), max.getText().toString(), propertyType, renterType, "Any");
                        getSupportActionBar().setTitle("Search");
                    } else {
                        passFilterValues(filterLocation.getSelectedItem().toString(), min.getText().toString(), max.getText().toString(), propertyType, renterType, bedRooms);
                        getSupportActionBar().setTitle("Search");
                    }
                } else {
                    Utility.alertDialog(MainActivity.this, getResources().getString(R.string.msg_select_location));
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
    private void passFilterValues(String loc, String min, String max, String pro, String rent, String bed) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //intent.putExtra(ConstantKey.FILTER_KEY, loc+","+min+","+max+","+pro+","+rent+","+bed);
        intent.putExtra(ConstantKey.FILTER_KEY, new Filter(loc, min, max, pro, rent, bed));
        intent.putExtra("name", "Search");
        startActivity(intent);
        finish();
    }

    //====================================================| Property Button Action
    private class ActionHandlerProperty implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.propertyBtn0:
                    propertyType = propertyBtn[0].getText().toString();
                    setFocusProperty(propertyBtn_unfocused, propertyBtn[0]);
                    break;
                case R.id.propertyBtn1:
                    propertyType = propertyBtn[1].getText().toString();
                    setFocusProperty(propertyBtn_unfocused, propertyBtn[1]);
                    break;
                case R.id.propertyBtn2:
                    propertyType = propertyBtn[2].getText().toString();
                    setFocusProperty(propertyBtn_unfocused, propertyBtn[2]);
                    break;
                case R.id.propertyBtn3:
                    propertyType = propertyBtn[3].getText().toString();
                    setFocusProperty(propertyBtn_unfocused, propertyBtn[3]);
                    break;
                case R.id.propertyBtn4:
                    propertyType = propertyBtn[4].getText().toString();
                    setFocusProperty(propertyBtn_unfocused, propertyBtn[4]);
                    break;
                case R.id.propertyBtn5:
                    propertyType = propertyBtn[5].getText().toString();
                    setFocusProperty(propertyBtn_unfocused, propertyBtn[5]);
                    break;
            }
        }
    }

    private void setFocusProperty(Button propertyBtn_unfocused, Button propertyBtn_focus) {
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
            switch (view.getId()) {
                case R.id.renterBtn0:
                    renterType = renterBtn[0].getText().toString();
                    setFocusRenter(renterBtn_unfocused, renterBtn[0]);
                    break;
                case R.id.renterBtn1:
                    renterType = renterBtn[1].getText().toString();
                    setFocusRenter(renterBtn_unfocused, renterBtn[1]);
                    break;
                case R.id.renterBtn2:
                    renterType = renterBtn[2].getText().toString();
                    setFocusRenter(renterBtn_unfocused, renterBtn[2]);
                    break;
                case R.id.renterBtn3:
                    renterType = renterBtn[3].getText().toString();
                    setFocusRenter(renterBtn_unfocused, renterBtn[3]);
                    break;
                case R.id.renterBtn4:
                    renterType = renterBtn[4].getText().toString();
                    setFocusRenter(renterBtn_unfocused, renterBtn[4]);
                    break;
                case R.id.renterBtn5:
                    renterType = renterBtn[5].getText().toString();
                    setFocusRenter(renterBtn_unfocused, renterBtn[5]);
                    break;
            }
        }
    }

    private void setFocusRenter(Button btn_unfocused, Button btn_focus) {
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
            switch (view.getId()) {
                case R.id.bedBtn0:
                    bedRooms = bedBtn[0].getText().toString();
                    setFocusBed(bedBtn_unfocused, bedBtn[0]);
                    break;
                case R.id.bedBtn1:
                    bedRooms = bedBtn[1].getText().toString();
                    setFocusBed(bedBtn_unfocused, bedBtn[1]);
                    break;
                case R.id.bedBtn2:
                    bedRooms = bedBtn[2].getText().toString();
                    setFocusBed(bedBtn_unfocused, bedBtn[2]);
                    break;
                case R.id.bedBtn3:
                    bedRooms = bedBtn[3].getText().toString();
                    setFocusBed(bedBtn_unfocused, bedBtn[3]);
                    break;
                case R.id.bedBtn4:
                    bedRooms = bedBtn[4].getText().toString();
                    setFocusBed(bedBtn_unfocused, bedBtn[4]);
                    break;
            }
        }
    }

    private void setFocusBed(Button bedBtn_unfocused, Button bedBtn_focus) {
        bedBtn_unfocused.setTextColor(Color.rgb(49, 50, 51));
        bedBtn_unfocused.setBackground(getResources().getDrawable(R.drawable.shape_button_border));
        bedBtn_focus.setTextColor(Color.rgb(255, 255, 255));
        bedBtn_focus.setBackgroundColor(Color.parseColor("#000000"));
        this.bedBtn_unfocused = bedBtn_focus;
    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("tolet_users").child(fuser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        status("online");
    }


    @Override
    protected void onPause() {
        super.onPause();
//        DateFormat df = new SimpleDateFormat("h:mm a, EEE, d MMM yyyy");
//        String date = df.format(Calendar.getInstance().getTime());
//        status("Last Seen " + date);
    }

    //===============================================| Restart Activity

}