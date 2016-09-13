package com.example.kevin.mapapplication.ui.mainscreen;

import android.animation.TimeInterpolator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.model.MarkerManager;
import com.example.kevin.mapapplication.ui.mainscreen.tag.BlueTagInfoActivity;
import com.example.kevin.mapapplication.ui.mainscreen.tag.GreenTagInfoActivity;
import com.example.kevin.mapapplication.ui.mainscreen.tag.RedTagInfoActivity;
import com.example.kevin.mapapplication.ui.mainscreen.tag.TagInfoActivity;
import com.example.kevin.mapapplication.ui.startup.StartUpActivity;
import com.example.kevin.mapapplication.ui.userinfo.HelpActivity;
import com.example.kevin.mapapplication.ui.userinfo.FriendsActivity;
import com.example.kevin.mapapplication.ui.userinfo.NotificationsActivity;
import com.example.kevin.mapapplication.ui.userinfo.OrdersActivity;
import com.example.kevin.mapapplication.ui.userinfo.PromotionActivity;
import com.example.kevin.mapapplication.ui.userinfo.SettingsActivity;
import com.example.kevin.mapapplication.ui.userinfo.TemplatesActivity;
import com.example.kevin.mapapplication.ui.userinfo.TransactionsActivity;
import com.example.kevin.mapapplication.ui.userinfo.UserDetailActivity;
import com.example.kevin.mapapplication.ui.userinfo.WalletActivity;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;
import com.example.kevin.mapapplication.utils.DirectionManager;
import com.example.kevin.mapapplication.utils.LocationTracker;
import com.example.kevin.mapapplication.utils.MapWrapperLayout;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener,
        QueryFragment.OnFragmentInteractionListener, GoogleMap.InfoWindowAdapter, DirectionManager.onDirectionShownCallBack
        , DirectionInformer.OnTaskCancelClicker {

    private GoogleMap mMap;
    private LatLng CurrentPosition;
    private float CurrentAccuracy;
    private LocationTracker mLocationTracker;
    private DirectionManager dm;
    private TagButtonManager tagButtonManager;
    private MapWrapperLayout mapWrapperLayout;
    private TagInfoWindow tagInfoWindow;
    private View InfoWindow_show;
    private ListView QueryListView;
    private GoogleMap.InfoWindowAdapter adapter_showOne;

    private DirectionInformer directionInformer;
    private TaskInformer taskInformer;
    ProgressBar loading;

    private Circle MyPositionCircle;
    private Marker MyPositionMarker;

    private volatile String app_state;

    private static final float ZoomLevel = 18f;
    public static final int REQUEST_CODE = 233;
    public static final int RESULT_CODE_CANCEL = 240;
    public static final int RESULT_CODE_OK = 241;
    private static final int TOUCH_USER_DETAIL = 1;
    private static final int TEXT_QUERY_INPUT = 2;
    private static final int TOUCH_QUERY_UNFOCUSED = 3;
    private static final int TOUCH_SEARCH = 4;
    private boolean show_isMarkerClicker;
    private boolean isFirstCreate = true;
    private Marker FocusedMarker;
    private String FocusedMarkerPlaceName;

    private Polyline DirectionLine;

    private DrawerLayout drawer;
    private RelativeLayout action_bar_mask;
    private FloatingActionButton Fab;
    private long timebetweenclicks;
    private int fabclickcount;

    private Set<String> orderTitleList;
    private JSONArray onGoingOrders;

    private SharedPreferences userinfo;
    private String m_username;
    private Bundle m_bundle;
    private ceaselessMethods ceaselessMethods;

    class ceaselessMethods {

        private final Handler handler = new Handler();

        Runnable locationUpdate = new Runnable() {
            @Override
            public void run() {
                CurrentPosition = mLocationTracker.getCurrentLatlng();
                CurrentAccuracy = mLocationTracker.getCurrentAccuracy();
                moveMyPositionMarker();
                handler.postDelayed(this, 1000);
            }
        };

        public void startCeaselessMethods() {
            handler.post(locationUpdate);
        }

        public void stopCeaselessMethods() {
            handler.removeCallbacks(locationUpdate);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mLocationTracker = new LocationTracker(this);
        ceaselessMethods = new ceaselessMethods();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        m_bundle = getIntent().getExtras();
        if(m_bundle!=null &&(m_bundle.getString("state").equals("modify")||m_bundle.getString("state").equals("showOne"))) {
            InitParams();
        }
        else {
            InitParams();
            InitUI();
        }
    }

    @Override
    protected void onResume() {
        mLocationTracker.setLocationListener();
        ceaselessMethods.startCeaselessMethods();
        if(!isFirstCreate) {
            restoreMapAndsetMarkers();
            if(directionInformer != null)
                directionInformer.HideDirectionInformer();
            if(taskInformer != null)
                taskInformer.HideTaskInformer();
        }
        updateUsernameAndBalance();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mLocationTracker.removeLocationListener();
        ceaselessMethods.stopCeaselessMethods();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE) {
            switch (resultCode) {
                case SettingsActivity.RESULT_CODE:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            drawer.openDrawer(GravityCompat.START);
                        }
                    }, 300);
                    break;
                case TagInfoActivity.RESULT_CODE:
                    Bundle bundle = data.getExtras();
                    bundle.putString("state", "show");
                    setState(bundle);
                    break;
            }
        }
    }

    private void updateUsernameAndBalance() {
        loading.setVisibility(View.VISIBLE);
        AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                NavigationView navigationview = (NavigationView) findViewById(R.id.nav_view);
                View headerview = navigationview.getHeaderView(0);
                TextView drawer_header_balance = (TextView) headerview.findViewById(R.id.drawer_header_balance);
                drawer_header_balance.setText(Integer.toString(res.optInt("balance")) + " Points ");
                TextView drawer_header_username = (TextView) headerview.findViewById(R.id.drawer_header_username);
                drawer_header_username.setText(res.optString("username"));
                SharedPreferences.Editor editor = userinfo.edit();
                m_username = res.optString("username");
                editor.putString("username", m_username);
                editor.apply();
            }
            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(MapsActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        };
        ConnectionManager.getInstance().GetUserInfo(userinfo.getString("uid", null), userinfo.getString("token", null), handler);
    }

    public void setState(Bundle bundle) {
        String state = bundle.getString("state");
        boolean isMarkerDraggable = true;
        String Custom_Place = null;
        assert state != null;
        switch (state) {
            case "show":
                app_state = "show";
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                tagButtonManager.hideTagButton();
                Fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_addtag));
                tagButtonManager.setButtonListener(true);
                modifyScreen(true);
                mMap.setInfoWindowAdapter(this);
                break;
            case "add":
                app_state = "add";
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                directionInformer.HideDirectionInformer();
                mMap.setInfoWindowAdapter(MapsActivity.this);
                float color = bundle.getFloat("color");
                addMarkerAndRelatedListener(color, true, false, true, true, null, 0, 0);
                Fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_check));
                setInfoWindowWithPlaceDetail(false, null,  null, null);
                modifyScreen(false);
                break;
            case "showOne":
                app_state = "showOne";
                Fab.setVisibility(View.GONE);
                isMarkerDraggable = false;
                Custom_Place = bundle.getString("place");
                TextView Header = (TextView) findViewById(R.id.mask_header);
                Header.setText("Position");
            case "modify":
                if(state.equals("modify"))
                    app_state = "modify";
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                modifyScreen(false);
                setInfoWindowWithPlaceDetail(false, null, null, null);
                Double mod_latitude = bundle.getDouble("latitude", 200), mod_longitude = bundle.getDouble("longitude", 200);
                boolean hasAnimation = false;
                if (mod_latitude == 200) {
                    mod_latitude = CurrentPosition.latitude;
                    mod_longitude = CurrentPosition.longitude;
                    hasAnimation = true;
                }
                addMarkerAndRelatedListener(bundle.getFloat("color"), hasAnimation, true, false, isMarkerDraggable, Custom_Place, mod_latitude, mod_longitude);
                tagButtonManager.hideTagButton();
                Fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_check));
                ImageButton back = (ImageButton) findViewById(R.id.btn_back);
                back.setVisibility(View.VISIBLE);
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(RESULT_CODE_CANCEL);
                        onBackPressed();
                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    }
                });
                Fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mdf_intent = new Intent();
                        mdf_intent.putExtra("place", FocusedMarkerPlaceName);
                        mdf_intent.putExtra("latitude", FocusedMarker.getPosition().latitude);
                        mdf_intent.putExtra("longitude", FocusedMarker.getPosition().longitude);
                        setResult(RESULT_CODE_OK, mdf_intent);
                        onBackPressed();
                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    }
                });
                break;
        }
    }

    private void getPlaceName() {
        loading.setVisibility(View.VISIBLE);
        AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                String premise_name = "", district_name = "";
                JSONArray results = res.optJSONArray("results");
                if(results!=null) {
                    JSONObject place_info = results.optJSONObject(0);
                    JSONArray address_component = place_info.optJSONArray("address_components");
                    for (int i = 0; i < address_component.length(); i++) {
                        JSONObject comp = address_component.optJSONObject(i);
                        String name = comp.optString("long_name");
                        JSONArray types = comp.optJSONArray("types");
                        for (int j = 0; j < types.length(); j++) {
                            if (types.optString(j).equals("premise")) {
                                premise_name = name;
                            } else if (types.optString(j).equals("street_number")) {
                                premise_name = name + " ";
                            } else if (types.optString(j).equals("route")) {
                                premise_name += name;
                            }
                            if (types.optString(j).equals("sublocality")) {
                                district_name = name;
                            } else if (types.optString(j).equals("locality")) {
                                district_name += ", " + name;
                            }
                        }
                    }
                    FocusedMarkerPlaceName = premise_name;
                    setInfoWindowWithPlaceDetail(true, null, premise_name, district_name);

                    /////////Manual Refresh.....//////////
                    if (FocusedMarker.isInfoWindowShown()) {
                        FocusedMarker.hideInfoWindow();
                        FocusedMarker.showInfoWindow();
                    }
                }
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(MapsActivity.this, "Cannot connect to Google Geocoding service", Toast.LENGTH_SHORT).show();
            }
        };

        ConnectionManager.getInstance().GetGeocodingPlace(FocusedMarker.getPosition(), handler);
    }


    private void setInfoWindowWithPlaceDetail(final boolean enable, final String Custom_Place, final String premise_name, final String district_name) {

        GoogleMap.InfoWindowAdapter adapter = new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                if(enable) {
                    if (marker.equals(FocusedMarker)) {
                        LayoutInflater inflater = LayoutInflater.from(MapsActivity.this);
                        View v;
                        if(Custom_Place == null) {
                            v = inflater.inflate(R.layout.infowindow_marker_onadd, null);
                            final TextView placename = (TextView) v.findViewById(R.id.markerinfowindow_placename);
                            final TextView districtname = (TextView) v.findViewById(R.id.markerinfowindow_district);
                            if (premise_name != null) {
                                placename.setText(premise_name);
                                districtname.setText(district_name);
                            }
                        }
                        else {
                            v = inflater.inflate(R.layout.infowindow_marker_onadd_with_direction, null);
                            final TextView placename = (TextView) v.findViewById(R.id.markerinfowindow_placename);
                            placename.setText(Custom_Place);
                            ImageButton direction = (ImageButton) v.findViewById(R.id.markerinfowindow_dir_btn);
                            direction.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent event) {
                                    switch (event.getActionMasked()) {
                                        case MotionEvent.ACTION_UP:
                                            FocusedMarker.hideInfoWindow();
                                            loading.setVisibility(View.VISIBLE);
                                            DrawDirectionAndSet(FocusedMarker.getPosition());
                                            break;
                                    }
                                    return true;
                                }
                            });
                            mapWrapperLayout.setMarkerWithInfoWindow(FocusedMarker, v);
                        }
                        return v;
                    }
                }
                return null;
            }
        };
        mMap.setInfoWindowAdapter(adapter);

        if(app_state.equals("showOne")) {
            adapter_showOne = adapter;
        }
    }

    private void modifyScreen(boolean enable) {
        setQueryBar(enable);
        setInfoWindowListener(enable);
        setFabFunction(enable);
        if(enable)
            action_bar_mask.setVisibility(View.GONE);
        else action_bar_mask.setVisibility(View.VISIBLE);
    }

    private void addMarkerAndRelatedListener(final float Color, final boolean hasAnimation, final boolean hasDefaultPos, final boolean enableFab, final boolean isMarkerDraggable, final String Custom_Place, final double latitude, final double longitude) {
        int Anim_Delay = 500;
        if(!hasAnimation) {
            Anim_Delay = 0;
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {
                final Marker marker = addEventTag(Color, hasAnimation, hasDefaultPos, isMarkerDraggable, latitude, longitude);
                FocusedMarker = marker;

                setInfoWindowWithPlaceDetail(true, Custom_Place, null, null);

                if(hasAnimation) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FocusedMarker.showInfoWindow();
                        }
                    }, 10);
                }
                else {
                    FocusedMarker.showInfoWindow();
                }

                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                        setInfoWindowWithPlaceDetail(false, null, null, null);
                        marker.hideInfoWindow();
                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        setInfoWindowWithPlaceDetail(true, null, null, null);
                        marker.showInfoWindow();
                        getPlaceName();
                    }
                });
                if(Custom_Place == null) {
                    getPlaceName();
                }
                if(enableFab) {
                    Fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            marker.setDraggable(false);
                            marker.hideInfoWindow();
                            if (Color == BitmapDescriptorFactory.HUE_BLUE) {
                                final Intent intent = new Intent(MapsActivity.this, BlueTagInfoActivity.class);
                                intent.putExtra("place", FocusedMarkerPlaceName);
                                intent.putExtra("class", "order");
                                intent.putExtra("latitude", marker.getPosition().latitude);
                                intent.putExtra("longitude", marker.getPosition().longitude);
                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MapsActivity.this
                                        , findViewById(R.id.btn_blueevent), "bluetag");
                                startActivityForResult(intent, MapsActivity.REQUEST_CODE, options.toBundle());
                            } else if (Color == BitmapDescriptorFactory.HUE_RED) {
                                final Intent intent = new Intent(MapsActivity.this, RedTagInfoActivity.class);
                                intent.putExtra("place", FocusedMarkerPlaceName);
                                intent.putExtra("class", "order");
                                intent.putExtra("latitude", marker.getPosition().latitude);
                                intent.putExtra("longitude", marker.getPosition().longitude);
                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MapsActivity.this
                                        , findViewById(R.id.btn_redevent), "redtag");
                                startActivityForResult(intent, MapsActivity.REQUEST_CODE, options.toBundle());
                            } else if (Color == BitmapDescriptorFactory.HUE_GREEN) {
                                final Intent intent = new Intent(MapsActivity.this, GreenTagInfoActivity.class);
                                intent.putExtra("place", FocusedMarkerPlaceName);
                                intent.putExtra("class", "order");
                                intent.putExtra("latitude", marker.getPosition().latitude);
                                intent.putExtra("longitude", marker.getPosition().longitude);
                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MapsActivity.this
                                        , findViewById(R.id.btn_greenevent), "greentag");
                                startActivityForResult(intent, MapsActivity.REQUEST_CODE, options.toBundle());
                            }
                        }
                    });
                }
            }
        }, Anim_Delay);
    }

    private Marker addEventTag(float Color, final boolean hasAnimation, boolean hasDefaultPos, boolean isMarkerDraggable, double latitude, double longitude) {
        LatLng target = mMap.getCameraPosition().target;
        if(hasDefaultPos) {
            target = new LatLng(latitude, longitude);
        }
        final Marker marker = mMap.addMarker(new MarkerOptions().draggable(isMarkerDraggable)
                .icon(BitmapDescriptorFactory.defaultMarker(Color))
                .position(target));
        if(hasAnimation) {
            PinDropAnimation(marker, target);
        }
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
                if (t < 1.0) {
                    handler.postDelayed(this, 10);
                    marker.setPosition(new LatLng(lat, lng));
                }
            }
        });
    }

    private void PinJumpAnimation(final Marker marker, final LatLng target) {

        final TimeInterpolator timeInterpolator = new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return (float)Math.abs(Math.sin(input * 2 * 3.1415926));
            }
        };
        final long duration = 900;
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(target);
        startPoint.y = startPoint.y - 50;
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        marker.setPosition(startLatLng);

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float state = (float) elapsed / duration;
                float t = timeInterpolator.getInterpolation(state);
                double lng = target.longitude + t * (startLatLng.longitude - target.longitude) ;
                double lat = target.latitude + t * (startLatLng.latitude - target.latitude);
                if (state < 1.0) {
                    handler.postDelayed(this, 10);
                    marker.setPosition(new LatLng(lat, lng));
                }
            }
        });
    }

    private void setQueryBar(boolean enable) {
        EditText queryinput = (EditText) findViewById(R.id.queryinput);
        ImageButton search, detail;
        search = (ImageButton) findViewById(R.id.querystart);
        detail = (ImageButton) findViewById(R.id.user_detail);
        queryinput.setEnabled(enable);
        search.setEnabled(enable);
        detail.setEnabled(enable);
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

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
        isFirstCreate = false;
        mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.mapwrapperlayout);
        mapWrapperLayout.init(mMap, getPixelsFromDp(this, -10));

        m_bundle = getIntent().getExtras();
        if(m_bundle!=null && (m_bundle.getString("state").equals("modify")||m_bundle.getString("state").equals("showOne"))) {
            Mapinit_location();
            setMyPositionMarker();

            /////////////////Init Direction in ShowOne Mode////////////////
            if(m_bundle.getString("state").equals("showOne")) {
                directionInformer = new DirectionInformer(this, mMap);
                directionInformer.SetDirectionInformer();
            }

            Double mod_latitude = m_bundle.getDouble("latitude", 200), mod_longitude = m_bundle.getDouble("longitude", 200);
            if (mod_latitude == 200) {
                mod_latitude = CurrentPosition.latitude;
                mod_longitude = CurrentPosition.longitude;
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mod_latitude, mod_longitude), ZoomLevel));
            mMap.getUiSettings().setMapToolbarEnabled(false);

            app_state = "modify";
            setState(m_bundle);
        }
        else {
            Mapinit();
        }
    }

    public float getZoomLevel(float distance) {
        float zoomLevel;
        double scale = distance / 500;
        zoomLevel = (float) (16 - Math.log(scale) / Math.log(2));
        return zoomLevel;
    }

    @Override
    public void onDirectionShown(LatLng origin, LatLng destination, PolylineOptions lineOptions) {
        if(lineOptions!=null) {
            DirectionLine = mMap.addPolyline(lineOptions);
            loading.setVisibility(View.INVISIBLE);
            directionInformer.ShowDirectionInformer();
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });
            double distance = SphericalUtil.computeDistanceBetween(origin, destination);
            LatLng Center = new LatLng((origin.latitude + destination.latitude) / 2, (origin.longitude + destination.longitude) / 2);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Center, getZoomLevel((float) distance)));
        }
        else {
            loading.setVisibility(View.INVISIBLE);
            Toast.makeText(MapsActivity.this, "Direction error.", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onDirectionFailed() {
        loading.setVisibility(View.INVISIBLE);
        Toast.makeText(MapsActivity.this, "Cannot connect to Google Direction Service.", Toast.LENGTH_LONG).show();
    }

    @Override
    public View getInfoContents(Marker marker) {

        if(marker.getId().equals(MarkerManager.getInstance().getLocationMarkerid())) {
            return null;
        }

        tagInfoWindow.setTagContent(InfoWindow_show, marker, mMap);
        mapWrapperLayout.setMarkerWithInfoWindow(marker, InfoWindow_show);
        return InfoWindow_show;
    }

    @Override
    public View getInfoWindow (Marker marker) {
        return null;
    }

    @Override
    public void onBackPressed() {
        if(app_state.equals("add")) {
            Bundle bundle = new Bundle();
            bundle.putString("state", "show");
            setState(bundle);
            FocusedMarker.remove();
        }
        else if(app_state.equals("show")){
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
        else if(app_state.equals("modify") || app_state.equals("showOne")){
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        drawer.closeDrawer(GravityCompat.START);

        switch (id) {
            case R.id.navigation_notifications:
                final Intent intent_7 = new Intent(this, NotificationsActivity.class);
                startActivityWithDelay(intent_7);
                break;
//            case R.id.navigation_setting :
//                final Intent intent_1 = new Intent(this, SettingsActivity.class);
//                startActivityWithDelay(intent_1);
//                break;
//            case R.id.navigation_help :
//                final Intent intent_2 = new Intent(this, HelpActivity.class);
//                startActivityWithDelay(intent_2);
//                break;
            case R.id.navigation_history :
                final Intent intent_3 = new Intent(this, OrdersActivity.class);
                startActivityWithDelay(intent_3);
                break;
            case R.id.navigation_friends :
                final Intent intent_4 = new Intent(this, FriendsActivity.class);
                startActivityWithDelay(intent_4);
                break;
            case R.id.navigation_promotion :
                final Intent intent_5 = new Intent(this, PromotionActivity.class);
                startActivityWithDelay(intent_5);
                break;
            case R.id.navigation_wallet :
                final Intent intent_6 = new Intent(this, TransactionsActivity.class);
                startActivityWithDelay(intent_6);
                break;
            case R.id.navigation_templates :
                final Intent intent_8 = new Intent(this, TemplatesActivity.class);
                startActivityWithDelay(intent_8);
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent_7 = new Intent(MapsActivity.this, StartUpActivity.class);
                        startActivity(intent_7);
                        finish();
                    }
                }, 150);
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
            case TOUCH_QUERY_UNFOCUSED:
                HideSoftInputandClearFocus();
                break;
            case TOUCH_SEARCH:
                if(!Content.equals("")) {
                    Intent intent = new Intent(this, SearchResultActivity.class);
                    intent.putExtra("keywords", Content);
                    startActivityForResult(intent, REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.anim_empty);
                }
                else {
                    Toast.makeText(MapsActivity.this, "Search content cannot be empty", Toast.LENGTH_SHORT).show();
                }
                break;
            case TEXT_QUERY_INPUT:
                List<String> search_result = new ArrayList<>();
                for(String title : orderTitleList) {
                    if(title.contains(Content)) {
                        search_result.add(title);
                    }
                }
                ArrayAdapter adapter =new ArrayAdapter<>(MapsActivity.this, R.layout.listview_item_search, search_result.toArray());
                QueryListView.setAdapter(adapter);
                break;

        }
    }

    public void DrawDirectionAndSet(LatLng MarkerPosition) {
        dm = new DirectionManager(CurrentPosition, MarkerPosition);
        dm.setOnDirectionShowCallBack(this);
        setInfoWindowListener(false);
    }

    @Override
    public void ClearMapAndSetInfoWindow() {
        if(DirectionLine!=null) {
            DirectionLine.remove();
        }
        if(app_state.equals("show")) {
            mMap.setInfoWindowAdapter(this);
            setInfoWindowListener(true);
        }
        else if(app_state.equals("showOne")) {
            mMap.setInfoWindowAdapter(adapter_showOne);
        }
    }

    private void restoreMapAndsetMarkers() {
        if(app_state.equals("show")) {
            Mapinit_location();
            mMap.clear();
            setMyPositionMarker();
            setDefaultMarkers();
            mMap.setInfoWindowAdapter(this);
        }
    }

    private void setDefaultMarkers() {
        loading.setVisibility(View.VISIBLE);
        final Set<String> acceptedorderid = new HashSet<>();
        AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                onGoingOrders = res.optJSONArray("orders");
                for(int i = 0;i < onGoingOrders.length();i++) {
                    JSONObject order = onGoingOrders.optJSONObject(i);
                    acceptedorderid.add(order.optString("oid"));
                    setDefaultMarkerProperty(order, true);
                }
                MarkersFilter(acceptedorderid);

                if(onGoingOrders.length() != 0) {
                    taskInformer = new TaskInformer(MapsActivity.this, mMap, onGoingOrders);
                    taskInformer.SetTaskInformer();
                    taskInformer.ShowTaskInformer();
                }
                else if(taskInformer != null) {
                    taskInformer.CloseTaskInformer();
                }
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(MapsActivity.this, error, Toast.LENGTH_LONG).show();
            }
        };

        ConnectionManager.getInstance().GetOrderList("", "waiting|accepted|canceling", "", userinfo.getString("uid", null), false ,userinfo.getString("token", null), handler);
    }

    private void setDefaultMarkerProperty(JSONObject order, boolean hasAnimation) throws JSONException {

        final Marker marker = mMap.addMarker(new MarkerOptions().position(CurrentPosition));
        MarkerManager.getInstance().Put(marker.getId(), order);
        final LatLng position = new LatLng(order.optJSONObject("coordinate").optDouble("latitude"), order.optJSONObject("coordinate").optDouble("longitude"));

        switch (order.optString("type")) {
            case "request":
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                break;
            case "offer":
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                break;
            case "prompt":
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                break;
        }

        marker.setPosition(position);
        if(hasAnimation) {
            final Handler animate_handler = new Handler();
            animate_handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Handler jumphandler = new Handler();
                    jumphandler.post(new Runnable() {
                        @Override
                        public void run() {
                            PinJumpAnimation(marker, position);
                            animate_handler.postDelayed(this, 1700);
                        }
                    });
                }
            }, 2000);
        }
    }

    private void MarkersFilter(final Set<String> acceptedoid) {
        loading.setVisibility(View.VISIBLE);
        /////////////////////////////////GetDefaultOrdersList////////////////////////////////////////
        AsyncJSONHttpResponseHandler handler = new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                orderTitleList = new HashSet<>();
                JSONArray orders_list = res.getJSONArray("orders");
                int markers_num = orders_list.length();
                for(int i = 0;i < markers_num;i ++) {
                    JSONObject order = orders_list.getJSONObject(i);
                    orderTitleList.add(order.optString("title"));
                    if(!acceptedoid.contains(order.optString("oid"))) {
                        setDefaultMarkerProperty( order, false);
                    }
                }
                ArrayAdapter adapter =new ArrayAdapter<>(MapsActivity.this, R.layout.listview_item_search, orderTitleList.toArray());
                QueryListView.setAdapter(adapter);

            }
            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(MapsActivity.this, error, Toast.LENGTH_LONG).show();
            }
        };
        ConnectionManager.getInstance().GetDefaultOrdersList(userinfo.getString("token", null), handler);
    }

    private void startActivityWithDelay(final Intent intent) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivityForResult(intent, REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        }, 150);
    }

    private void setFabFunction(final boolean enable) {
        fabclickcount = 0;
        timebetweenclicks = 0;
        Fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enable) {
                    if (timebetweenclicks == 0 || System.currentTimeMillis() - timebetweenclicks > 800) {
                        if (fabclickcount % 2 == 0) {
                            Fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_add_plusstate));
                            fabclickcount++;
                            tagButtonManager.showTagButton();
                        } else {
                            Fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_addtag));
                            fabclickcount++;
                            tagButtonManager.hideTagButton();
                        }
                    }
                    timebetweenclicks = System.currentTimeMillis();
                }
            }
        });
    }

    private void InitParams() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        Fab = (FloatingActionButton) findViewById(R.id.fab_addbutton);

        action_bar_mask = (RelativeLayout) findViewById(R.id.action_bar_mask);

        loading = (ProgressBar) findViewById(R.id.main_loading);

        tagButtonManager = new TagButtonManager(this);
        tagButtonManager.setButtonListener(true);

        FocusedMarker = null;
        FocusedMarkerPlaceName = "";
        app_state = "show";

        //////////////////GET_USER_INFO/////////////////////
        userinfo = getSharedPreferences("User_info", MODE_PRIVATE);
        m_username = userinfo.getString("username", null);

        if(m_username == null) {
            Toast.makeText(MapsActivity.this, "Error!", Toast.LENGTH_LONG).show();
        }
        ////////////////////////////////////////////////////
    }

    private void InitUI() {
        NavigationView navigationview = (NavigationView) findViewById(R.id.nav_view);
        navigationview.setNavigationItemSelectedListener(this);

        setFabFunction(true);

        View headerview = navigationview.getHeaderView(0);
        TextView drawer_header_username = (TextView) headerview.findViewById(R.id.drawer_header_username);
        TextView drawer_header_balance = (TextView) headerview.findViewById(R.id.drawer_header_balance);
        drawer_header_username.setText(m_username);
        drawer_header_balance.setText(userinfo.getString("balance", null));
        ImageButton user_detail_btn = (ImageButton) headerview.findViewById(R.id.drawer_header_detail_btn);

        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, UserDetailActivity.class);
                startActivityWithDelay(intent);
            }
        });
        user_detail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, UserDetailActivity.class);
                startActivityWithDelay(intent);
            }
        });

        QueryListView = (ListView) findViewById(R.id.query_listview);

        QueryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EditText queryinput = (EditText) findViewById(R.id.queryinput);
                queryinput.setText((String) QueryListView.getAdapter().getItem(position));
                HideSoftInputandClearFocus();
            }
        });
    }

    private void moveMyPositionMarker() {
        if(MyPositionCircle!=null && MyPositionMarker!=null) {
            MyPositionCircle.setCenter(CurrentPosition);
            MyPositionCircle.setRadius(CurrentAccuracy);
            MyPositionMarker.setPosition(CurrentPosition);
        }
    }

    private void setMyPositionMarker() {
        /*if(MyPositionCircle!=null && MyPositionMarker!=null) {
            MyPositionCircle.remove();
            MyPositionMarker.remove();
        }*/
        MyPositionCircle = mMap.addCircle(new CircleOptions().center(CurrentPosition)
                .radius(CurrentAccuracy)
                .fillColor(Color.argb(30, 36, 132, 237))
                .strokeWidth(1.5f)
                .strokeColor(Color.argb(130, 36, 132, 246)));
        MyPositionMarker = mMap.addMarker(new MarkerOptions().position(CurrentPosition)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        MarkerManager.getInstance().setLocationMarkerid(MyPositionMarker.getId());
    }

    private void Mapinit_location() {
        CurrentPosition = mLocationTracker.getCurrentLatlng();
        CurrentAccuracy = mLocationTracker.getCurrentAccuracy();

        FloatingActionButton locatebutton = (FloatingActionButton) findViewById(R.id.fab_locationbutton);
        locatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentPosition = mLocationTracker.getCurrentLatlng();
                CurrentAccuracy = mLocationTracker.getCurrentAccuracy();
                moveMyPositionMarker();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(CurrentPosition, ZoomLevel));
            }
        });
    }

    private void Mapinit() {
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setInfoWindowAdapter(this);

        Mapinit_location();

        setMyPositionMarker();

        directionInformer = new DirectionInformer(this, mMap);
        directionInformer.SetDirectionInformer();

        tagInfoWindow  = new TagInfoWindow(MapsActivity.this);
        InfoWindow_show = tagInfoWindow.BuildTagContent();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(CurrentPosition));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(CurrentPosition, ZoomLevel));
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

        setInfoWindowListener(true);

        setDefaultMarkers();

        show_isMarkerClicker = false;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!marker.getId().equals(MarkerManager.getInstance().getLocationMarkerid())) {
                    if (app_state.equals("show") && !show_isMarkerClicker) {
                        Toast.makeText(MapsActivity.this, "Tap on the Bubble to see more detail.", Toast.LENGTH_LONG).show();
                        show_isMarkerClicker = true;
                    }
                }
                return false;
            }
        });
    }

    private void setInfoWindowListener(final boolean enable) {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(enable) {
                    if (!marker.getId().equals(MarkerManager.getInstance().getLocationMarkerid())) {
                        try {
                            TaginfoDialog taginfoDialog = new TaginfoDialog(MapsActivity.this);
                            taginfoDialog.BuildDialog(marker);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
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

}
