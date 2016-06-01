package com.example.kevin.mapapplication.ui.mainscreen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.model.MarkerManager;
import com.example.kevin.mapapplication.ui.mainscreen.tag.BlueTagInfoActivity;
import com.example.kevin.mapapplication.ui.mainscreen.tag.GreenTagInfoActivity;
import com.example.kevin.mapapplication.ui.mainscreen.tag.RedTagInfoActivity;
import com.example.kevin.mapapplication.ui.startup.StartUpActivity;
import com.example.kevin.mapapplication.ui.userinfo.HelpActivity;
import com.example.kevin.mapapplication.ui.userinfo.HistoryActivity;
import com.example.kevin.mapapplication.ui.userinfo.FriendsActivity;
import com.example.kevin.mapapplication.ui.userinfo.PromotionActivity;
import com.example.kevin.mapapplication.ui.userinfo.SettingsActivity;
import com.example.kevin.mapapplication.ui.userinfo.UserDetailActivity;
import com.example.kevin.mapapplication.ui.userinfo.WalletActivity;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;
import com.example.kevin.mapapplication.utils.DirectionManager;
import com.example.kevin.mapapplication.utils.LocationTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener,
        QueryFragment.OnFragmentInteractionListener, GoogleMap.InfoWindowAdapter, TaginfoDialog.OnAcceptClickedCallBack
        , TaskInformer.OnTaskCancelClicker {

    private GoogleMap mMap;
    private LatLng CurrentPosition;
    private float CurrentAccuracy;
    private LocationTracker mLocationTracker;
    private DirectionManager dm;
    private TaskInformer taskInformer;
    private TagButtonManager tagButtonManager;

    private Circle MyPositionCircle;
    private Marker MyPositionMarker;

    private static final float ZoomLevel = 18f;
    public static final int REQUEST_CODE = 233;
    private static final int TOUCH_USER_DETAIL = 1;
    private static final int TOUCH_QUERY_FOCUSED = 2;
    private static final int TOUCH_QUERY_UNFOCUSED = 3;
    private static final int TOUCH_SEARCH = 4;
    private boolean isMarkerClicker;

    private DrawerLayout drawer;

    private RelativeLayout action_bar_mask;
    private FloatingActionButton Fab;
    private long timebetweenclicks;
    private int fabclickcount;

    private SharedPreferences userinfo;
    private String m_username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mLocationTracker = new LocationTracker(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        InitUI();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode==REQUEST_CODE) {
            switch (resultCode) {
                case SettingsActivity.RESULT_CODE:
                    //Bundle bundle=data.getExtras();   ////////////To get extra data
                    //String str=bundle.getString("back");
                    //Toast.makeText(MapsActivity.this, str, Toast.LENGTH_LONG).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(300);
                                drawhandler.sendMessage(new Message());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case SearchResultActivity.RESULT_CODE:

                    break;
                case RedTagInfoActivity.RESULT_CODE:
                    Bundle bundle = data.getExtras();
                    String str = bundle.getString("Form");
                    if(str!=null && str.equals("Confirmed")) {
                        tagButtonManager.HideTagButton();
                        ChangeEverything();
                        TagAngChangePackage(BitmapDescriptorFactory.HUE_RED);
                    }
                    break;
                case GreenTagInfoActivity.RESULT_CODE:
                    bundle = data.getExtras();
                    str = bundle.getString("Form");
                    if(str!=null && str.equals("Confirmed")) {
                        tagButtonManager.HideTagButton();
                        ChangeEverything();
                        TagAngChangePackage(BitmapDescriptorFactory.HUE_GREEN);
                    }
                    break;
                case BlueTagInfoActivity.RESULT_CODE:
                    bundle = data.getExtras();
                    str = bundle.getString("Form");
                    if(str!=null && str.equals("Confirmed")) {
                        tagButtonManager.HideTagButton();
                        ChangeEverything();
                        TagAngChangePackage(BitmapDescriptorFactory.HUE_BLUE);
                    }
                    break;
                case UserDetailActivity.RESULT_CODE:
                    bundle = data.getExtras();
                    if(bundle.getBoolean("IsUsernameModified")) {
                        m_username = userinfo.getString("username", null);
                        NavigationView navigationview = (NavigationView) findViewById(R.id.nav_view);
                        View headerview = navigationview.getHeaderView(0);
                        TextView drawer_header_username = (TextView) headerview.findViewById(R.id.drawer_header_username);
                        drawer_header_username.setText(m_username);
                    }
                    break;
            }
        }
    }

    private void TagAngChangePackage(final float Color) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    Message msg = new Message();
                    msg.obj = Color;
                    showtaghandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Handler showtaghandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final Marker marker = AddEventTag((float) msg.obj);
            Fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_addtag));
                        marker.setDraggable(false);
                        MarkerManager.getInstance().ConfirmLatLng(marker.getId(), marker.getPosition().latitude, marker.getPosition().longitude);
                        SetEnabledActionBar(true);
                        SetInfoWindowListener();
                        action_bar_mask.setVisibility(View.GONE);
                        SetFabFunction();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void ChangeEverything () {
        DisableInfoWindowListener();
        Fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_check));
        DisableFabFunction();
        action_bar_mask.setVisibility(View.VISIBLE);
        SetEnabledActionBar(false);
    }

    private void SetEnabledActionBar(boolean enable) {
        EditText queryinput = (EditText) findViewById(R.id.queryinput);
        ImageButton search, detail;
        search = (ImageButton) findViewById(R.id.querystart);
        detail = (ImageButton) findViewById(R.id.user_detail);
        queryinput.setEnabled(enable);
        search.setEnabled(enable);
        detail.setEnabled(enable);
    }



    Handler drawhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            drawer.openDrawer(GravityCompat.START);
        }
    };

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Mapinit();

        //////////////TEST_ONLY//////////////////////////////////////////////////////////////////////////
        Marker markerG = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(CurrentPosition));
        Marker markerB = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .position(CurrentPosition));
        Marker markerR = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(CurrentPosition));

        try {
            markerB.setPosition(new LatLng((double) MarkerManager.getInstance().Get("m2").get("Lat"), (double)MarkerManager.getInstance().Get("m2").get("Lng")));
            markerG.setPosition(new LatLng((double) MarkerManager.getInstance().Get("m1").get("Lat"), (double)MarkerManager.getInstance().Get("m1").get("Lng")));
            markerR.setPosition(new LatLng((double) MarkerManager.getInstance().Get("m3").get("Lat"), (double)MarkerManager.getInstance().Get("m3").get("Lng")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////
    }

    @Override
    public View getInfoContents(Marker marker) {

        TagInfoWindow tagInfoWindow = new TagInfoWindow(MapsActivity.this);

        return tagInfoWindow.BuildTagContent(marker);
    }

    @Override
    public View getInfoWindow (Marker marker) {

        return null;
    }

    @Override
    public void onBackPressed() {

        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            MarkerManager.getInstance().Restore();
            super.onBackPressed();
        }
    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        drawer.closeDrawer(GravityCompat.START);

        switch (id) {
            case R.id.navigation_setting :
                final Intent intent_1 = new Intent(this, SettingsActivity.class);
                StartActivityWithDelay(intent_1);
                break;
            case R.id.navigation_help :
                final Intent intent_2 = new Intent(this, HelpActivity.class);
                StartActivityWithDelay(intent_2);
                break;
            case R.id.navigation_history :
                final Intent intent_3 = new Intent(this, HistoryActivity.class);
                StartActivityWithDelay(intent_3);
                break;
            case R.id.navigation_friends :
                final Intent intent_4 = new Intent(this, FriendsActivity.class);
                StartActivityWithDelay(intent_4);
                break;
            case R.id.navigation_promotion :
                final Intent intent_5 = new Intent(this, PromotionActivity.class);
                StartActivityWithDelay(intent_5);
                break;
            case R.id.navigation_wallet :
                final Intent intent_6 = new Intent(this, WalletActivity.class);
                StartActivityWithDelay(intent_6);
                break;
            case R.id.navigation_logout :
                deleteRegistrationFromServer();
                SharedPreferences userinfo = getSharedPreferences("User_info", MODE_PRIVATE);
                SharedPreferences.Editor editor = userinfo.edit();
                editor.putString("username", null);
                editor.putString("password", null);
                editor.putString("uid", null);
                editor.putString("token", null);
                editor.apply();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(150);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent intent_7 = new Intent(MapsActivity.this, StartUpActivity.class);
                        startActivity(intent_7);
                        finish();
                    }
                }).start();
                break;
        }
        return true;
    }

    private void deleteRegistrationFromServer() {
        ConnectionManager.getInstance().DeleteRegToken(userinfo.getString("uid", null), userinfo.getString("regToken", null), userinfo.getString("token", null), new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {

            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {

            }
        });
    }

    @Override
    public void onFragmentInteraction(int OperationCode, String Content) {
        switch (OperationCode) {
            case TOUCH_USER_DETAIL:
                ShowNavBar();
                break;
            case TOUCH_QUERY_FOCUSED:
                ListView listView = (ListView) findViewById(R.id.query_listview);
                Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fade_and_slide_down);
                listView.startAnimation(fadein);
                listView.setVisibility(View.VISIBLE);
                break;
            case TOUCH_QUERY_UNFOCUSED:
                listView = (ListView) findViewById(R.id.query_listview);
                Animation fadeout = AnimationUtils.loadAnimation(this, R.anim.fade_and_slide_up);
                listView.startAnimation(fadeout);
                listView.setVisibility(View.INVISIBLE);
                HideSoftInputandClearFocus();
                break;
            case TOUCH_SEARCH:
                if(!Content.equals("")) {
                    Intent intent = new Intent(this, SearchResultActivity.class);
                    intent.putExtra("Keywords", Content);
                    startActivityForResult(intent, REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.anim_empty);
                }
                else {
                    Toast.makeText(MapsActivity.this, "Search content cannot be empty", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    @Override
    public void DrawDirectionAndSet(JSONObject data) {
        try {
            dm = new DirectionManager(CurrentPosition, new LatLng((double)data.get("Lat"), (double)data.get("Lng")), mMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DisableInfoWindowListener();
        DisableFabFunction();

    }

    @Override
    public void ClearMapAndSetInfoWindow() {
        dm.ClearPolyline();
        SetInfoWindowListener();
        SetFabFunction();
    }

    private void StartActivityWithDelay (final Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(150);
                    startActivityForResult(intent, REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void DisableFabFunction() {
        Fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void SetFabFunction() {
        fabclickcount = 0;
        timebetweenclicks = 0;
        Fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timebetweenclicks == 0 || System.currentTimeMillis() - timebetweenclicks > 800) {
                    if (fabclickcount % 2 == 0) {
                        Fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_add_plusstate));
                        fabclickcount++;
                        tagButtonManager.ShowTagButton();
                    } else {
                        Fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_addtag));
                        fabclickcount++;
                        tagButtonManager.HideTagButton();
                    }
                }
                timebetweenclicks = System.currentTimeMillis();
            }
        });
    }

    private void InitUI() {
        NavigationView navigationview = (NavigationView) findViewById(R.id.nav_view);
        navigationview.setNavigationItemSelectedListener(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        Fab = (FloatingActionButton) findViewById(R.id.fab_addbutton);

        action_bar_mask = (RelativeLayout) findViewById(R.id.action_bar_mask);

        tagButtonManager = new TagButtonManager(this);

        tagButtonManager.SetButtonListener();

        SetFabFunction();

        taskInformer = new TaskInformer(this);

        taskInformer.SetTaskInformer();

        //////////////////GET_USER_INFO/////////////////////
        userinfo = getSharedPreferences("User_info", MODE_PRIVATE);
        m_username = userinfo.getString("username", null);

        if(m_username == null) {
            Toast.makeText(MapsActivity.this, "Error!", Toast.LENGTH_LONG).show();
        }
        ////////////////////////////////////////////////////

        View headerview = navigationview.getHeaderView(0);
        TextView drawer_header_username = (TextView) headerview.findViewById(R.id.drawer_header_username);
        drawer_header_username.setText(m_username);
        ImageButton user_detail_btn = (ImageButton) headerview.findViewById(R.id.drawer_header_detail_btn);

        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, UserDetailActivity.class);
                StartActivityWithDelay(intent);
            }
        });
        user_detail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, UserDetailActivity.class);
                StartActivityWithDelay(intent);
            }
        });

        final ArrayAdapter adapter =new ArrayAdapter<String>(this, R.layout.listview_item_search, new String[]{"StarCraft II", "Dota", "LOL", "Football", "Basketball", "Piano"});

        ListView listView = (ListView) findViewById(R.id.query_listview);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EditText queryinput = (EditText) findViewById(R.id.queryinput);
                queryinput.setText((String) adapter.getItem(position));
                HideSoftInputandClearFocus();
            }
        });
    }

    private void SetMyPositionMarker() {
        if(MyPositionCircle!=null && MyPositionMarker!=null) {
            MyPositionCircle.remove();
            MyPositionMarker.remove();
        }
        MyPositionCircle = mMap.addCircle(new CircleOptions().center(CurrentPosition)
                .radius(CurrentAccuracy)
                .fillColor(Color.argb(100, 91, 93, 255))
                .strokeColor(Color.argb(80, 0, 0, 170)));
        MyPositionMarker = mMap.addMarker(new MarkerOptions().position(CurrentPosition).title("Your Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
    }


    private void Mapinit() {
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setInfoWindowAdapter(this);

        CurrentPosition = mLocationTracker.getCurrentLatlng();
        CurrentAccuracy = mLocationTracker.getCurrentAccuracy();

        SetMyPositionMarker();

        LatLng TestScene = new LatLng(31.19326239, 121.59417715);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(TestScene));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(TestScene, ZoomLevel));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                HideSoftInputandClearFocus();
            }
        });
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                HideSoftInputandClearFocus();
            }
        });

        //////////////To enable update of the marker position
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }
        });

        SetInfoWindowListener();

        FloatingActionButton locatebutton = (FloatingActionButton) findViewById(R.id.fab_locationbutton);
        locatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentPosition = mLocationTracker.getCurrentLatlng();
                CurrentAccuracy = mLocationTracker.getCurrentAccuracy();
                SetMyPositionMarker();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(CurrentPosition, ZoomLevel));
            }
        });

        isMarkerClicker = false;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!isMarkerClicker) {
                    Toast.makeText(MapsActivity.this, "Tap on the Window to see more detail.", Toast.LENGTH_LONG).show();
                    isMarkerClicker = true;
                }
                return false;
            }
        });
    }

    private void SetInfoWindowListener() {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (!marker.getId().equals("m0")) {
                    try {
                        TaginfoDialog taginfoDialog = new TaginfoDialog(MapsActivity.this);
                        taginfoDialog.SetCallBack(MapsActivity.this);
                        taginfoDialog.BuildDialog(marker, taskInformer);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void DisableInfoWindowListener() {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
            }
        });
    }

    private void HideSoftInputandClearFocus() {
        EditText queryinput = (EditText) findViewById(R.id.queryinput);
        queryinput.clearFocus();
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(queryinput.getWindowToken(), 0);
    }

    private void ShowNavBar() {
        HideSoftInputandClearFocus();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }

    private Marker AddEventTag(float Color) {
        final LatLng target = mMap.getCameraPosition().target;
        final Marker marker = mMap.addMarker(new MarkerOptions().draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(Color))
                .position(target));
        PinDropAnimation(marker, target);
        return marker;
    }

    private void PinDropAnimation (final Marker marker,final  LatLng target) {
        final long duration = 900;
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();

        Point startPoint = proj.toScreenLocation(target);
        startPoint.y = 0;
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        marker.setPosition(startLatLng);

        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * target.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * target.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 10ms later.
                    handler.postDelayed(this, 10);
                } else {
                    // animation ended
                }
            }
        });
    }
}
