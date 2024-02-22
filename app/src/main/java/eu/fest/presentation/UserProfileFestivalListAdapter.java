package eu.fest.presentation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import eu.fest.model.databases.Festival;
import eu.fest.view.UserProfileFestivalListItemView;
import eu.fest.view.UserProfileFestivalListItemView_;


public class UserProfileFestivalListAdapter extends ArrayAdapter<Festival> {

    public UserProfileFestivalListAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserProfileFestivalListItemView view;
        if(convertView == null){
            view = UserProfileFestivalListItemView_.build(parent.getContext());
        }else {
            view = (UserProfileFestivalListItemView) convertView;
        }
        Festival item = getItem(position);
        view.bind(item);
        return view;
    }
}
