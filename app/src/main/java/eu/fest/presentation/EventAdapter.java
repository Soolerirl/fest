package eu.fest.presentation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import eu.fest.model.databases.Events;
import eu.fest.view.EventListItemView;
import eu.fest.view.EventListItemView_;


public class EventAdapter extends ArrayAdapter<Events> {

    public EventAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        EventListItemView view;
        if(convertView == null){
            view = EventListItemView_.build(parent.getContext());
        }else {
            view = (EventListItemView) convertView;
        }
        Events item = getItem(position);
        view.bind(item);
        return view;
    }
}
