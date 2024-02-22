package eu.fest.presentation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import eu.fest.model.databases.Performers;
import eu.fest.view.PerformersListItemView;
import eu.fest.view.PerformersListItemView_;

public class PerformersAdapterV2 extends ArrayAdapter<Performers> {

    public PerformersAdapterV2(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        PerformersListItemView view;
        if(convertView == null){
            view = PerformersListItemView_.build(parent.getContext());
        }else {
            view = (PerformersListItemView) convertView;
        }
        Performers item = getItem(position);
        view.bind(item);
        return view;
    }
}
