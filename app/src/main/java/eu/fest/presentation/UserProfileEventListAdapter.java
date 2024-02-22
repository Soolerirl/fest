package eu.fest.presentation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import eu.fest.model.databases.Events;
import eu.fest.view.UserProfileEventListItemView;
import eu.fest.view.UserProfileEventListItemView_;

public class UserProfileEventListAdapter extends ArrayAdapter<Events> {

    public UserProfileEventListAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserProfileEventListItemView view;
        if(convertView == null){
            view = UserProfileEventListItemView_.build(parent.getContext());
        }else {
            view = (UserProfileEventListItemView) convertView;
        }
        Events item = getItem(position);
        view.bind(item);
        return view;
    }
}
