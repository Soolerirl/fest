package eu.fest.presentation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import eu.fest.model.databases.Festival;
import eu.fest.view.FestivalListItemView;
import eu.fest.view.FestivalListItemView_;

public class FestivalAdapter extends ArrayAdapter<Festival> {

    public FestivalAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        FestivalListItemView view;
        if(convertView == null){
            view = FestivalListItemView_.build(parent.getContext());
        }else {
            view = (FestivalListItemView) convertView;
        }
        Festival item = getItem(position);
        view.bind(item);
        return view;
    }
}