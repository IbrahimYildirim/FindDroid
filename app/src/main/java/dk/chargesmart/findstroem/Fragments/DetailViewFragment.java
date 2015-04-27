package dk.chargesmart.findstroem.Fragments;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import dk.chargesmart.findstroem.Model.Location;
import dk.chargesmart.findstroem.R;
import dk.chargesmart.findstroem.Views.AutoResizeTextView;

/**
 * Created by Ibrahim on 08/11/14.
 */
public class DetailViewFragment extends Fragment {

    AutoResizeTextView mNameLbl;
    AutoResizeTextView mAdressLbl;
    AutoResizeTextView mCityLbl;
    AutoResizeTextView mPhoneLbl;
    TextView mOpenLbl;
    ImageView mImgView;
    Button mShowWayBtn;
    Button mOpenWebBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.detail_fragment, container, false);

        mNameLbl = (AutoResizeTextView)view.findViewById(R.id.detail_name);
        mAdressLbl = (AutoResizeTextView)view.findViewById(R.id.detail_adress);
        mCityLbl = (AutoResizeTextView)view.findViewById(R.id.detail_city);
        mPhoneLbl = (AutoResizeTextView)view.findViewById(R.id.detail_phone);
        mOpenLbl = (TextView)view.findViewById(R.id.detail_open);
        mImgView = (ImageView)view.findViewById(R.id.detail_img);


        return view;
    }

    public void updateDetailView(Location l)
    {
        if (l.getName() != null){
            mNameLbl.setText(l.getName());
        }
        if (l.getAddress() != null){
            mAdressLbl.setText(l.getAddress());
        }
        if (l.getCity() != null){
            mCityLbl.setText(l.getZip() + " " + l.getCity());
        }

        if (l.getPhone() != null){
            mPhoneLbl.setText(l.getPhone());
        }
        else {
            mPhoneLbl.setText("");
        }

        if (l.getOpeningHours() != null){
            mOpenLbl.setText("Åben idag: " + l.getOpeningHours());
        }
        else {
            mOpenLbl.setText("");
        }

        if (l.getImgUrl() != null){
            mImgView.setImageBitmap(null);

            DisplayImageOptions imageOptions = (new DisplayImageOptions.Builder()).cacheInMemory(true).build();
            ImageLoader.getInstance().displayImage(l.getImgUrl(), mImgView, imageOptions, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f);
                    fadeIn.setDuration(300);
                    fadeIn.start();
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }
        else{
            mImgView.setImageBitmap(null);
        }
    }
}
