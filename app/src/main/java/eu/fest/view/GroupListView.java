package eu.fest.view;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import eu.fest.R;
import eu.fest.model.PerformersHeader;


@EViewGroup(R.layout.performers_list_base_item)
public class GroupListView extends RelativeLayout {

    @ViewById
    TextView performers_date;

    @ViewById
    TextView performers_day;

    public GroupListView(Context context) {
        super(context);
    }

    public void bind(PerformersHeader item){
        performers_date.setText(item.getDate());
        performers_day.setText(item.getDay());
    }
}
