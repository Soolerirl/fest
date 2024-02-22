package eu.fest.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import eu.fest.EventPreviewActivity_;
import eu.fest.R;
import eu.fest.model.databases.Events;

@EViewGroup(R.layout.user_profile_event_item)
public class UserProfileEventListItemView extends RelativeLayout {

    @ViewById
    ImageView event_logo;

    @ViewById
    TextView event_name;

    @ViewById
    CardView event_logo_bg;


    public UserProfileEventListItemView(Context context) {
        super(context);
    }

    public void bind(final Events item){

        if(item.getFestival_event_logo() == null || item.getFestival_event_logo().equals(null) || item.getFestival_event_logo() == "null" || item.getFestival_event_logo().equals("null")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                event_logo.setBackground(getResources().getDrawable(R.drawable.male_avatar));
            }
        }else{
            if(item.getFestival_event_logo()!=null) {
                Picasso.with(getContext()).load(item.getFestival_event_logo().startsWith("http") ? item.getFestival_event_logo() : "http://" + item.getFestival_event_logo()).into(event_logo);
            }
        }

        event_name.setText(item.getFestival_event_name());

        event_logo_bg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EventPreviewActivity_.intent(getContext()).eventData(item).start();
            }
        });
    }
}