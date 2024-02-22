package eu.fest.view;

import android.content.Context;
import android.widget.RelativeLayout;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import eu.fest.R;
import it.sephiroth.android.library.widget.HListView;

@EViewGroup(R.layout.performers_list_child_item)
public class ChildListView extends RelativeLayout {

    @ViewById
    HListView hList;

    public ChildListView(Context context) {
        super(context);
    }

    public void bind(HListView hList){
        this.hList = hList;
    }
}
