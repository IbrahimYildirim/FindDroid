package dk.chargesmart.findstroem.Views;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Ibrahim on 05/11/14.
 */
public class MyPin implements ClusterItem {

    private final LatLng mPosition;

    public MyPin(double lat, double lon)
    {
        mPosition = new LatLng(lat, lon);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
