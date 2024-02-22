package eu.fest.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;

import eu.fest.FestivalPreviewActivity_;
import eu.fest.R;
import eu.fest.model.databases.Festival;
import eu.fest.presentation.DatabaseManager;
import eu.fest.service.WebServerService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@EViewGroup(R.layout.festival_list_item_card)
public class FestivalListItemView extends RelativeLayout {

    @Bean
    WebServerService webServerService;

    @Bean(DatabaseManager.class)
    DatabaseManager db;

    @ViewById
    TextView festival_name;

    @ViewById
    TextView like_text;

    @ViewById
    TextView like_all_text;

    @ViewById
    ImageView festival_logo;

    @ViewById
    CardView festival_logo_bg;

    @ViewById
    ImageButton like_button;

    public FestivalListItemView(Context context) {
        super(context);
    }

    @UiThread
    public void bind(final Festival data) {
        festival_name.setText(data.getFestival_name());

        if(data.getFestival_logo() == null || data.getFestival_logo().equals(null) || data.getFestival_logo() == "null" || data.getFestival_logo().equals("null")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                festival_logo.setBackground(getResources().getDrawable(R.drawable.male_avatar));
            }
        }else{
            if(data.getFestival_logo()!=null) {
                Picasso.with(getContext()).load(data.getFestival_logo().startsWith("http") ? data.getFestival_logo() : "http://" + data.getFestival_logo()).into(festival_logo);
            }
        }

        if(!data.is_favorite_festival()){
            like_text.setText(R.string.not_liked);
        }else if(data.is_favorite_festival()){
            like_text.setText(R.string.liked);
        }

        like_all_text.setText(data.getFavorite_festival()+"\n"+getResources().getString(R.string.likes));
        like_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!data.is_favorite_festival()) {
                    webServerService.addFavoriteFestival(data.getServerId() + "", new Callback<Festival>() {
                        @Override
                        public void success(Festival festival, Response response) {
                            data.setIs_favorite_festival(true);
                            int a = data.getFavorite_festival();
                            data.setFavorite_festival((a+1));
                            try {
                                db.getDatabaseHelper().getFestivalDataDao().update(data);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            like_all_text.setText(data.getFavorite_festival()+"\n"+getResources().getString(R.string.likes));
                            like_text.setText(R.string.liked);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("Festival", error.getMessage());
                        }
                    });
                } else if (data.is_favorite_festival()) {
                    webServerService.deleteFavoriteFestival(data.getServerId() + "", new Callback<Festival>() {
                        @Override
                        public void success(Festival festival, Response response) {
                            data.setIs_favorite_festival(false);
                            int a = data.getFavorite_festival();
                            data.setFavorite_festival((a-1));
                            try {
                                db.getDatabaseHelper().getFestivalDataDao().update(data);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            like_all_text.setText(data.getFavorite_festival()+"\n"+getResources().getString(R.string.likes));
                            like_text.setText(R.string.not_liked);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("Festival", error.getMessage());
                        }
                    });
                }
            }
        });
        festival_logo_bg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FestivalPreviewActivity_.intent(getContext()).festivalData(data).start();
            }
        });
    }
}
