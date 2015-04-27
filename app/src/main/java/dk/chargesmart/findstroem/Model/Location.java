package dk.chargesmart.findstroem.Model;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dk.chargesmart.findstroem.Utils.FindStroemHttpClient;

/**
 * Created by Ibrahim on 03/11/14.
 */
public class Location {

    private String latitude;
    private String longitude;
    private String name;
    private String address;
    private String city;
    private String zip;
    private String category;
    private String webUrl;
    private String phone;
    private String imgUrl;
    private String openingHours;
    public Location()
    {

    }

    public static void getLocations(final LocationListener listener)
    {
        FindStroemHttpClient.getLocations("service.php", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error,
                                  JSONObject errorResponse) {
                System.out.println("Failed to load!");
                listener.onFailure(error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                try {
                    if (response != null) {
                        ArrayList<Location> list = new ArrayList<Location>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = null;
                            Location l = new Location();

                            jsonObject = (JSONObject) response.get(i);

                            if (jsonObject.has("Name"))
                                l.setName(jsonObject.getString("Name"));
                            if (jsonObject.has("Adress"))
                                l.setAddress(jsonObject.getString("Adress"));
                            if (jsonObject.has("Zip"))
                                l.setZip(jsonObject.getString("Zip"));
                            if (jsonObject.has("City"))
                                l.setCity(jsonObject.getString("City"));
                            if (jsonObject.has("Web"))
                                l.setWebUrl(jsonObject.getString("Web"));
                            if (jsonObject.has("Category"))
                                l.setCategory(jsonObject.getString("Category"));
                            if (jsonObject.has("Latitude"))
                                l.setLatitude(jsonObject.getString("Latitude"));
                            if (jsonObject.has("Longtitude"))
                                l.setLongitude(jsonObject.getString("Longtitude"));
                            if (jsonObject.has("image_url"))
                                l.setImgUrl(jsonObject.getString("image_url"));
                            if (jsonObject.has("open"))
                                l.setOpeningHours(jsonObject.getString("open"));
                            if (jsonObject.has("Phone"))
                                l.setPhone(jsonObject.getString("Phone"));

                            list.add(l);
                        }

                        listener.onSuccess(list);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public interface LocationListener
    {
        public void onSuccess(List<Location> locationListList);
        public void onFailure(Throwable error);
    }
}
