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

import eu.fest.FestivalPreviewActivity_;
import eu.fest.R;
import eu.fest.model.databases.Festival;

@EViewGroup(R.layout.user_profile_festival_item)
public class UserProfileFestivalListItemView extends RelativeLayout {

    @ViewById
    ImageView event_logo;

    @ViewById
    TextView event_name;

    @ViewById
    CardView event_logo_bg;


    public UserProfileFestivalListItemView(Context context) {
        super(context);
    }

    public void bind(final Festival item){

        if(item.getFestival_logo() == null || item.getFestival_logo().equals(null) || item.getFestival_logo() == "null" || item.getFestival_logo().equals("null")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                event_logo.setBackground(getResources().getDrawable(R.drawable.male_avatar));
            }
        }else{
            if(item.getFestival_logo()!=null) {
                Picasso.with(getContext()).load(item.getFestival_logo().startsWith("http") ? item.getFestival_logo() : "http://" + item.getFestival_logo()).into(event_logo);
            }
        }

        event_name.setText(item.getFestival_name());

        event_logo_bg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FestivalPreviewActivity_.intent(getContext()).festivalData(item).start();
            }
        });
    }
}
