package dk.chargesmart.findstroem.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import dk.chargesmart.findstroem.FilterItem;
import dk.chargesmart.findstroem.MainActivity;
import dk.chargesmart.findstroem.R;
import dk.chargesmart.findstroem.FilterListAdapter;

/**
 * Created by Ibrahim on 08/11/14.
 */
public class FilterFragment extends Fragment {

    public static ArrayList<FilterItem> mCategories;

    MainActivity mainActivity;
    ListView mListView;
    FilterListAdapter mListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.filter_fragment, container, false);

        mainActivity = (MainActivity)getActivity();
        mListAdapter = new FilterListAdapter(getActivity());
        mListView = (ListView)view.findViewById(R.id.filter_list);
        mListAdapter.addAll(mCategories);
        mListView.setAdapter(mListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                FilterItem item = mCategories.get(position);

                if(item.getName().equals("Alle Kategorier"))
                {
                    for (int i = 0; i < mCategories.size(); i++) {
                        mCategories.get(i).setChecked(false);
                    }
                    mCategories.get(0).setChecked(true);
                }
                else
                {
                    if(mCategories.get(0).isChecked()){
                        mCategories.get(0).setChecked(false);
                    }

                    if(!mCategories.get(position).isChecked()){
                        mCategories.get(position).setChecked(true);
                    }
                    else{
                        mCategories.get(position).setChecked(false);
                    }

                    int counter = 0;
                    for(int i = 1; i < mCategories.size(); i++){
                        if(mCategories.get(i).isChecked()){
                            counter++;
                        }
                    }
                    if(counter == 0){
                        mCategories.get(0).setChecked(true);
                    }

                }

                mListAdapter.notifyDataSetChanged();
                mainActivity.updateItemsOnMap(mCategories);
            }
        });

        return view;
    }
}
