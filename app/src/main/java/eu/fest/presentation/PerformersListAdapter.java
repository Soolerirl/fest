package eu.fest.presentation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;

import eu.fest.model.PerformersHeader;
import eu.fest.model.databases.Performers;
import eu.fest.view.ChildListView;
import eu.fest.view.ChildListView_;
import eu.fest.view.GroupListView;
import eu.fest.view.GroupListView_;
import eu.fest.view.PerformersListItemView;
import eu.fest.view.PerformersListItemView_;
import it.sephiroth.android.library.widget.HListView;


public class PerformersListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<PerformersHeader> groups;

    public PerformersListAdapter(Context context, ArrayList<PerformersHeader> groups) {
        this.context = context;
        this.groups = groups;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<Performers> list = groups.get(groupPosition).getItems();
        return list.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<Performers> list = groups.get(groupPosition).getItems();
        return list.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupListView view;
        if (convertView == null) {
            view = GroupListView_.build(parent.getContext());
        } else {
            view = (GroupListView) convertView;
        }

        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);

        PerformersHeader item = (PerformersHeader) getGroup(groupPosition);
        view.bind(item);


        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        PerformersListItemView view;
        if (convertView == null) {
            view = PerformersListItemView_.build(parent.getContext());
        } else {
            view = (PerformersListItemView) convertView;
        }
        Performers item = (Performers) getChild(groupPosition, childPosition);
        view.bind(item);
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
