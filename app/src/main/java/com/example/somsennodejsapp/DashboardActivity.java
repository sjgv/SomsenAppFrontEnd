package com.example.somsennodejsapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.somsennodejsapp.Retrofit.INodeJS;
import com.example.somsennodejsapp.Retrofit.RetrofitClient;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Retrofit stuff
    INodeJS loginAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    //Views and such
    TextView welcome_text, email_header;
    ListView listView;

    //QUANTITIES
    int numberOfResponses = 5;

    private DrawerLayout drawer;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Intialize API
        Retrofit retrofit = RetrofitClient.getInstance(); //Allows us to parse json
        loginAPI = retrofit.create(INodeJS.class);
        //Set toolbar as actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Hamburger Icon Menu
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Initialize Navigation View
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Initialize location
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //UserState.getInstance().location = location;
                UserState.getInstance().lat = location.getLatitude();
                UserState.getInstance().lon = location.getLongitude();
                Log.d("BxBxBxBxBxBxB", "In Location Changed");
                getNearestBiz(location);
                //Location test_location = new Location("Smiths");
                //test_location.setLatitude(40.347145);
                //test_location.setLongitude(-111.897532);
                //Log.d("ZaZaZaZaZaZaZaZa", Double.toString(UserState.getInstance().location.distanceTo(test_location)/1609.344));


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 7);
            }
            return;
        }

        //Location Parameters
        locationManager.requestLocationUpdates("gps", 0, 5, locationListener);
        //Log.d("XxBxBxBxBxBxB", Double.toString(UserState.getInstance().lat));


        //Initialize other fields
        welcome_text = (TextView)findViewById(R.id.welcome_text_name);
        email_header = (TextView) navigationView.getHeaderView(0).findViewById(R.id.header_email);

        email_header.setText(UserState.getInstance().email);
        welcome_text.setText("Some slogan!");

        /*
        testLst = (ListView)findViewById(R.id.biz_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, months);
        testLst.setAdapter(arrayAdapter);
        */


    }

    //Sends geo location to Back-End
    private void getNearestBiz(final Location location) {
        Log.d("AzAzAzAzAzAzAzAzA", Double.toString(UserState.getInstance().lat));
        compositeDisposable.add(loginAPI.getNearestBiz("Bearer " + UserState.getInstance().token,
                UserState.getInstance().lat, UserState.getInstance().lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        //Should only return 5 results, separate 'title' part
                        String[] titles = new String[numberOfResponses];
                        Double[] descriptions = new Double[numberOfResponses];

                        JSONArray response = new JSONArray(s);
                        Log.d("VVVVVVVVVVVV", response.get(0).toString());

                        //Create home location based on current lat, lon
                        Location startLocation = new Location("start");
                        startLocation.setLatitude(UserState.getInstance().lat);
                        startLocation.setLongitude(UserState.getInstance().lon);
                        //and calculate distance from long, lat.
                        for(int i = 0; i < response.length(); i++){
                            JSONObject obj = response.getJSONObject(i);
                            titles[i] = obj.getString("name");
                            //Create location out of result
                            Location thisLocation = new Location("this");
                            thisLocation.setLatitude(obj.getDouble("lat"));
                            thisLocation.setLongitude(obj.getDouble("lon"));

                            //Calculate distance between user and biz
                            double distanceTo = startLocation.distanceTo(thisLocation) * 0.00062137;
                            descriptions[i] = distanceTo;
                        }
                        //Sort the descriptions array (has to be custom in case the response has less than 5 biznesses
                        Arrays.sort(descriptions, new Comparator<Double>() {
                            @Override
                            public int compare(Double o1, Double o2) {
                                if(o1 == null && o2 == null)
                                    return 0; //equal
                                else if(o1 == null)
                                    return 1;
                                else if(o2 == null)
                                    return -1;
                                return o1.compareTo(o2);
                            }
                        });

                        listView = (ListView)findViewById(R.id.biz_list);
                        BizAdapter myAdapter = new BizAdapter(DashboardActivity.this,titles,descriptions);
                        listView.setAdapter(myAdapter);

                    }
                })
        );
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Clear the listview
        listView.setAdapter(null);
        //Choose fragment to display
        switch (item.getItemId()){
            case R.id.nav_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AccountFragment()).commit();
                break;
            case R.id.nav_personal:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PersonalFragment()).commit();
                break;
            case R.id.nav_friends:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FriendsFragment()).commit();
                break;
            case R.id.nav_public:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PublicFragment()).commit();
                break;
            case R.id.nav_logout:
                //Toast.makeText(this, "Syncing", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}