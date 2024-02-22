package eu.fest.presentation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import eu.fest.model.FriendItem;
import eu.fest.view.FriendListItemView;
import eu.fest.view.FriendListItemView_;

public class FriendAdapter extends ArrayAdapter<FriendItem> {

    public FriendAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        FriendListItemView view;
        if(convertView == null){
            view = FriendListItemView_.build(parent.getContext());
        }else {
            view = (FriendListItemView) convertView;
        }
        FriendItem item = getItem(position);
        view.bind(item);
        return view;
    }
}
