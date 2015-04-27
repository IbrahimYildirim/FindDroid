package dk.chargesmart.findstroem.Utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Ibrahim on 03/11/14.
 */
public class FindStroemHttpClient {

    private static final String BASE_URL = "http://www.findstroem.dk/";
    private static AsyncHttpClient client = new AsyncHttpClient();



    public static void getLocations(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
