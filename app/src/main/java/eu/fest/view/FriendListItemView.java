package eu.fest.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.fest.R;
import eu.fest.UserProfileActivity_;
import eu.fest.model.FriendItem;

@EViewGroup(R.layout.friend_item)
public class FriendListItemView extends RelativeLayout {

    @ViewById
    TextView friend_firstname;

    @ViewById
    TextView friend_lastname;

    @ViewById
    CircleImageView friend_image;

    @ViewById
    LinearLayout profile_container;

    public FriendListItemView(Context context) {
        super(context);
    }

    @UiThread
    public void bind(final FriendItem item){
        friend_firstname.setText(item.getFirstName());
        friend_lastname.setText(item.getLastName());
        if(item.getProfileImage() == null || item.getProfileImage().equals(null) || item.getProfileImage() == "null" || item.getProfileImage().equals("null")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                friend_image.setBackground(getResources().getDrawable(R.drawable.male_avatar));
            }
        }else{
            if(item.getProfileImage()!=null) {
                Picasso.with(getContext()).load(item.getProfileImage().startsWith("http") ? item.getProfileImage() : "http://" + item.getProfileImage()).into(friend_image);
            }
        }

        profile_container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileActivity_.intent(getContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK).user_id(item.getUser_id()).start();
            }
        });
    }
}
