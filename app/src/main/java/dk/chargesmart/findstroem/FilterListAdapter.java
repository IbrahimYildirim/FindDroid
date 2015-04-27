package dk.chargesmart.findstroem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Ibrahim on 10/11/14.
 */
public class FilterListAdapter extends ArrayAdapter<FilterItem>
{
    private Context context;

    public FilterListAdapter(Context context) {
        super(context, 0);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        FilterItem item = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.filter_item, parent, false);
        }

        TextView text = (TextView)convertView.findViewById(R.id.filter_name);
        ImageView img = (ImageView)convertView.findViewById(R.id.img_filter_check);

        text.setText(item.getName());
        if (item.isChecked()){
            img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.box_checked));
        }
        else{
            img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.box_unchecked));
        }

        return convertView;
    }
}
