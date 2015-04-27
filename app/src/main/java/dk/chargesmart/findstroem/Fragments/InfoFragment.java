package dk.chargesmart.findstroem.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dk.chargesmart.findstroem.R;

/**
 * Created by Ibrahim on 08/11/14.
 */
public class InfoFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.info_fragment, container, false);

        return view;
    }
}
