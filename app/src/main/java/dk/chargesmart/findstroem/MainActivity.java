package dk.chargesmart.findstroem;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dk.chargesmart.findstroem.Fragments.DetailViewFragment;
import dk.chargesmart.findstroem.Fragments.FilterFragment;
import dk.chargesmart.findstroem.Fragments.InfoFragment;
import dk.chargesmart.findstroem.Fragments.MySupportMapFragment;
import dk.chargesmart.findstroem.Fragments.SettingsFragment;
import dk.chargesmart.findstroem.Model.Location;
import dk.chargesmart.findstroem.Utils.JellyBounceInterpolator;
import dk.chargesmart.findstroem.Views.MyPin;
import dk.chargesmart.findstroem.Views.TouchableWrapper;


public class MainActivity extends FragmentActivity implements TouchableWrapper.UpdateMapAfterUserInterection {

    private static String PREFS_LAST_REQUEST_DAY = "last_request_day";

    GoogleMap mMap;
    ClusterManager<MyPin> mClusterManager;
    ArrayList<Location> mLocations;
    ArrayList<FilterItem> mCategories;
    Location mSelectedLocation;
    Fragment selectedSetting;
    DetailViewFragment detailFragment;

    boolean showDetail;
    boolean menuHidden;
    boolean isAnimating;
    boolean firstMenuClick;
    boolean receivedData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpMap();

        menuHidden = true;
        showDetail = false;
        firstMenuClick = true; //not clicked menu yet
        receivedData = false;

        detailFragment = (DetailViewFragment)getSupportFragmentManager().findFragmentById(R.id.detail_fragment);

        //Setup the img loader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int lastRequestDay = prefs.getInt(PREFS_LAST_REQUEST_DAY, -1);

        if (!receivedData || !isSameDay(lastRequestDay))
        {
            downloadItems();
        }
    }

    private boolean isSameDay(int lastRequest)
    {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);

        if (lastRequest == currentDay)
            return true;
        else
            return false;
    }

    public void onBackPressed() {
        if (showDetail) {
            View detailView = findViewById(R.id.detail_container);
            fadeOut(detailView, 300, true);
            showDetail = false;
        }
        else if (!menuHidden){
            RelativeLayout b = (RelativeLayout)findViewById(R.id.home_btn);
            b.performClick();
        }
        else {
            super.onBackPressed();
        }

    }

    private void downloadItems() {

        Location.getLocations(new Location.LocationListener() {

            @Override
            public void onSuccess(List<Location> locationList) {
                System.out.println("Got " + locationList.size() + " items");

                if (locationList != null)
                    mLocations = new ArrayList<Location>(locationList);

                setUpCategories();

                receivedData = true;

                //Save data in sharedPrefs
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();

                Calendar calendar = Calendar.getInstance();
                int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
                editor.putInt(PREFS_LAST_REQUEST_DAY, currentDay);
                editor.commit();
            }

            @Override
            public void onFailure(Throwable error) {
                System.out.println("Didn't get items!");
                Toast.makeText(getApplicationContext(), "Error in getting places, please restart app!", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void setUpCategories()
    {
        mCategories = new ArrayList<FilterItem>();
        ArrayList<String> categories = new ArrayList<String>();

        categories.add("Alle Kategorier");

        for (Location l : mLocations){
            if (!categories.contains(l.getCategory())){
                categories.add(l.getCategory());
            }
        }


        if (categories.contains("Andet"))
        {
            categories.remove("Andet");
            categories.add("Andet");
        }

        for (String s : categories)
        {
            FilterItem item;
            if (s.equals("Alle Kategorier"))
                item = new FilterItem(s, true);
            else
                item = new FilterItem(s, false);

            mCategories.add(item);
        }

        FilterFragment.mCategories = mCategories;

        setUpClusterer();

    }

    private void setUpMap() {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap = ((MySupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }


    private void setUpClusterer() {

        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(56.159687, 10.357533), 7));

        // Initialize the manager with the context and the map.
        mClusterManager = new ClusterManager<MyPin>(this, mMap);

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyPin>() {
            @Override
            public boolean onClusterItemClick(final MyPin myPin) {

                if (!menuHidden){
                    RelativeLayout b = (RelativeLayout)findViewById(R.id.home_btn);
                    b.performClick();
                }

                LatLng position = new LatLng(myPin.getPosition().latitude + 0.0008f, myPin.getPosition().longitude);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 17), new GoogleMap.CancelableCallback() {
                    public void onFinish() {

                        mSelectedLocation = null;

                        for (Location l : mLocations){
                            if (Double.parseDouble(l.getLatitude()) == myPin.getPosition().latitude &&
                                    Double.parseDouble(l.getLongitude()) == myPin.getPosition().longitude)
                            {
                                mSelectedLocation = l;
                                break;
                            }
                        }

                        if (mSelectedLocation != null){
                            detailFragment.updateDetailView(mSelectedLocation);
                        }
                        else {
                            detailFragment.updateDetailView(null);
                        }

                        View detailView = findViewById(R.id.detail_container);
                        scaleViewUp(detailView);
                        showDetail = true;
                    }

                    public void onCancel() {
                    }
                });

                return true;
            }
        });

        mClusterManager.setRenderer(new ClusterRenderer());

        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        updateItemsOnMap(mCategories);
    }

    public void updateItemsOnMap(ArrayList<FilterItem> filters) {

        double lat;
        double lng;

        mMap.clear();
        mClusterManager.clearItems();

        ArrayList<String> selectedCategories = new ArrayList<String>();
        for (FilterItem item : filters)
        {
            if (item.isChecked())
                selectedCategories.add(item.getName());
        }

        for (int i = 0; i < mLocations.size(); i++)
        {
            Location l = mLocations.get(i);

            if (selectedCategories.contains("Alle Kategorier") || selectedCategories.contains(l.getCategory())) {
                lat = Double.parseDouble(l.getLatitude());
                lng = Double.parseDouble(l.getLongitude());

                MyPin pin = new MyPin(lat, lng);
                mClusterManager.addItem(pin);
            }
        }
        mClusterManager.cluster();
    }

    public void shareClicked(View v)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        intent.putExtra(Intent.EXTRA_SUBJECT, "FindStroem");
        intent.putExtra(Intent.EXTRA_TEXT, "Tjek appen FindStroem fra ChargeSmart ud! http://market.android.com/details?id=dk.chargesmart.findstroem");

        Intent chooser = Intent.createChooser(intent, "Fortæl en ven om FindStroem");
        startActivity(chooser);
    }

    public void reviewClicked(View v)
    {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void zoomInClicked(View v) {

        if (mMap.isMyLocationEnabled()){
            android.location.Location l = mMap.getMyLocation();

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(l.getLatitude(), l.getLongitude()), 15f));

            if(showDetail){
                View detail = findViewById(R.id.detail_container);
                fadeOut(detail, 500, true);
            }
            if (!menuHidden){
                RelativeLayout b = (RelativeLayout)findViewById(R.id.home_btn);
                b.performClick();
            }
        }
    }

    public void zoomOutClicked(View v){

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(56.159687, 10.357533), 6.5f));

        if(showDetail){
            View detail = findViewById(R.id.detail_container);
            fadeOut(detail, 500, true);
        }
        if (!menuHidden){
            RelativeLayout b = (RelativeLayout)findViewById(R.id.home_btn);
            b.performClick();
        }
    }

    public void filterClicked(View v){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        int contId = R.id.settings_container;

        if (selectedSetting == null)
        {
            selectedSetting = new FilterFragment();
            ft.add(R.id.settings_container, selectedSetting, "filter").commit();
            scaleViewUp(findViewById(R.id.settings_container));
        }
        else if(selectedSetting instanceof FilterFragment){
            fadeOut(findViewById(contId), 300, true);
            selectedSetting = null;
        }
        else{
//            ft.remove(selectedSetting);
            selectedSetting = new FilterFragment();
            ft.replace(contId, selectedSetting);
            fadeOutSendIn(selectedSetting);
        }
    }

    public void infoClicked(View v){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        int contId = R.id.settings_container;

        if (selectedSetting == null){
            selectedSetting = new InfoFragment();
            ft.add(contId, selectedSetting).commit();
            scaleViewUp(findViewById(R.id.settings_container));
        }
        else if(selectedSetting instanceof InfoFragment){
            fadeOut(findViewById(contId), 300, true);
            selectedSetting = null;
        }
        else{
            selectedSetting = new InfoFragment();
            ft.replace(contId, selectedSetting);
            fadeOutSendIn(selectedSetting);
        }

    }

    public void settingsClicked(View v){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        int contId = R.id.settings_container;

        if (selectedSetting == null){
            selectedSetting = new SettingsFragment();
            ft.add(contId, selectedSetting).commit();
            scaleViewUp(findViewById(R.id.settings_container));
        }
        else if(selectedSetting instanceof SettingsFragment){
            fadeOut(findViewById(contId), 300, true);
            selectedSetting = null;
        }
        else{
            ft.remove(selectedSetting);
            selectedSetting = new SettingsFragment();
            ft.add(contId, selectedSetting);
            fadeOutSendIn(selectedSetting);
        }
    }

    public void showDirectionClicked(View v)
    {
        if(mSelectedLocation != null){
            android.location.Location l = mMap.getMyLocation();
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + l.getLatitude() + "," + l.getLongitude()
                            + "&daddr=" + mSelectedLocation.getLatitude() + "," + mSelectedLocation.getLongitude()));
            startActivity(intent);
        }
    }

    public void openWebPageClicked(View v){
        if (mSelectedLocation != null){
            if (mSelectedLocation.getWebUrl() != null || !mSelectedLocation.getWebUrl().equals(""))
            {
                String url = mSelectedLocation.getWebUrl();

                if (!url.startsWith("http://") && !url.startsWith("https://"))
                        url = "http://" + url;

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
            else
            {
                Toast.makeText(this, "Denne lokation har ikke en hjemmeside", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void menuClicked(final View v) {

        if (isAnimating)
            return;

        if (showDetail){
            View detail = findViewById(R.id.detail_container);
            fadeOut(detail, 500, true);
        }

        final ViewGroup buttons = (ViewGroup) findViewById(R.id.buttons);
        float btnCenterX = v.getX() + v.getWidth()/3;
        float btnCenterY = v.getY() + v.getHeight();

        if (menuHidden) {
            int delay = 0;
            for (int i = 0; i < buttons.getChildCount(); i++) {
                View button = buttons.getChildAt(i);
                button.setVisibility(View.VISIBLE);

                ObjectAnimator translateY;
                ObjectAnimator translateX;

                if (firstMenuClick){
                    float diffX;
                    float diffY;

                    if (btnCenterX > button.getX() || btnCenterX < button.getX()) {
                        diffX = btnCenterX - button.getX();
                        diffY = btnCenterY - button.getY();
                    } else {
                        diffX = 0.0f;
                        diffY = btnCenterY;
                    }

                    translateY = ObjectAnimator.ofFloat(button, "translationY", diffY, 0.0f);
                    translateX = ObjectAnimator.ofFloat(button, "translationX", diffX, 0.0f);

                    if(i == buttons.getChildCount() - 1)
                        firstMenuClick = false;
                }
                else
                {
                    translateY = ObjectAnimator.ofFloat(button, "translationY", 0.0f);
                    translateX = ObjectAnimator.ofFloat(button, "translationX", 0.0f);
                }

                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(button, "alpha", 0.0f, 1.0f);
                ObjectAnimator fadeMenu = ObjectAnimator.ofFloat(v, "alpha", 1.0f);

                AnimatorSet shootOut = new AnimatorSet();
                shootOut.play(translateX).with(translateY).with(fadeIn).with(fadeMenu);
                shootOut.setStartDelay(delay);
                shootOut.setDuration(1000);
                shootOut.setInterpolator(new JellyBounceInterpolator());
                shootOut.addListener(new Animator.AnimatorListener() {
                    public void onAnimationStart(Animator animator) {
                    }

                    public void onAnimationEnd(Animator animator) {
                        isAnimating = false;
                    }

                    public void onAnimationCancel(Animator animator) {
                    }

                    public void onAnimationRepeat(Animator animator) {
                    }
                });
                shootOut.start();
                isAnimating = true;

                delay += 50;
            }
            menuHidden = false;
        }
        else {

            for (int i = 0; i < buttons.getChildCount(); i++) {
                final View button = buttons.getChildAt(i);
                float diffX;
                float diffY;

                if (btnCenterX > button.getX() || btnCenterX < button.getX()) {
                    diffX = btnCenterX - button.getX();
                    diffY = btnCenterY - button.getY();
                } else {
                    diffX = 0.0f;
                    diffY = btnCenterY;
                }

                ObjectAnimator translateY = ObjectAnimator.ofFloat(button, "translationY", diffY);
                ObjectAnimator translateX = ObjectAnimator.ofFloat(button, "translationX", diffX);
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(button, "alpha", 0.0f);
                ObjectAnimator fadeMenu = ObjectAnimator.ofFloat(v, "alpha", 0.7f);

                AnimatorSet shootIn = new AnimatorSet();
                shootIn.play(translateX).with(translateY).with(fadeOut).with(fadeMenu);
                shootIn.setDuration(300);
                shootIn.addListener(new Animator.AnimatorListener() {
                    public void onAnimationStart(Animator animator) {
                    }

                    public void onAnimationEnd(Animator animator) {
                        isAnimating = false;
                        button.setVisibility(View.INVISIBLE);
                    }

                    public void onAnimationCancel(Animator animator) {
                    }

                    public void onAnimationRepeat(Animator animator) {
                    }
                });
                shootIn.start();
                isAnimating = true;
            }
            menuHidden = true;
        }
        v.bringToFront();

        if (selectedSetting != null)
        {
            View view = findViewById(R.id.settings_container);
            fadeOut(view, 300, true);
        }
    }

    private void scaleViewUp(View v) {
        v.setVisibility(View.VISIBLE);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 0.3f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 0.3f, 1.0f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", 0.0f, 1.0f);

        scaleX.setDuration(1000);
        scaleY.setDuration(1000);
        fadeIn.setDuration(200);

        scaleX.setInterpolator(new JellyBounceInterpolator());
        scaleY.setInterpolator(new JellyBounceInterpolator());

        AnimatorSet set = new AnimatorSet();
        set.play(scaleX).with(scaleY).with(fadeIn);
        set.start();

    }

    public void fadeOut(final View v, int duration, final boolean setHidden) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(v, "alpha", 0.0f);
        fadeOut.setDuration(duration);
        fadeOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (setHidden)
                    v.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        fadeOut.start();
    }

    public void fadeOutSendIn(final Fragment fadeInView)
    {
        final View view = findViewById(R.id.settings_container);
        view.setVisibility(View.VISIBLE);

        ObjectAnimator translateY = ObjectAnimator.ofFloat(view, "scaleY", 0.2f);
        ObjectAnimator translateX = ObjectAnimator.ofFloat(view, "scaleX", 0.2f);
        ObjectAnimator fade = ObjectAnimator.ofFloat(view, "alpha", 0.0f);

        AnimatorSet fadeOut = new AnimatorSet();
        fadeOut.play(translateX).with(translateY).with(fade);
        fadeOut.setDuration(200);
        fadeOut.addListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                selectedSetting = fadeInView;
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.settings_container, selectedSetting).commit();

                scaleViewUp(view);
            }

            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }
        });
        fadeOut.start();
    }

    @Override
    public void onUpdateMapAfterUserInterection() {
//        System.out.println("Map Changed");
        if (showDetail) {
            View detail = findViewById(R.id.detail_container);
            fadeOut(detail, 500, true);
            showDetail = false;
        }
        else if (!menuHidden){

            RelativeLayout b = (RelativeLayout)findViewById(R.id.home_btn);
            b.performClick();
        }
    }

    private class ClusterRenderer extends DefaultClusterRenderer<MyPin> {

        public ClusterRenderer() {
            super(getApplicationContext(), mMap, mClusterManager);

        }

        @Override
        protected void onClusterItemRendered(MyPin clusterItem, Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);

            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
            //here you have access to the marker itself
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 3;
        }
    }
}