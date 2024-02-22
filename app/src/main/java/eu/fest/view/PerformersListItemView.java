package eu.fest.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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

import eu.fest.R;
import eu.fest.model.databases.Performers;
import eu.fest.presentation.DatabaseManager;
import eu.fest.service.WebServerService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@EViewGroup(R.layout.performers_list_item)
public class PerformersListItemView extends RelativeLayout {

    @Bean
    WebServerService webServerService;

    @Bean(DatabaseManager.class)
    DatabaseManager db;

    @ViewById
    ImageView performers_img;

    @ViewById
    TextView performers_name;

    @ViewById
    TextView performers_location;

    @ViewById
    TextView performers_time;

    @ViewById
    TextView performers_genre;

    @ViewById
    TextView go_on_text;

    @ViewById
    LinearLayout go_on_btn;

    public PerformersListItemView(Context context) {
        super(context);
    }

    @UiThread
    public void bind(final Performers data) {
        if(data.getPerformer_logo()==null && data.getPerformer_logo()=="null" && data.getPerformer_logo().equals("null") && data.getPerformer_logo().equals(null)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                performers_img.setBackground(getResources().getDrawable(R.drawable.male_avatar));
            }
        }else{
            if(data.getPerformer_logo()!=null && data.getPerformer_logo()!="null" && !data.getPerformer_logo().equals("null") && !data.getPerformer_logo().equals(null)) {
                Picasso.with(getContext()).load(data.getPerformer_logo().startsWith("http") ? data.getPerformer_logo() : "http://" + data.getPerformer_logo()).into(performers_img);
            }
        }

        performers_name.setText(data.getPerformer_name());
        performers_genre.setText(data.getPerformer_genre());
        performers_location.setText(data.getLocation_name());
        go_on_text.setText(data.getCount_going_festival_event_event()+"");

        go_on_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.isgoing()){
                    webServerService.deleteGoingPerformers(data, new Callback<Performers>() {
                        @Override
                        public void success(Performers performers, Response response) {
                            Log.d("Success", "asd");
                            try {
                                data.setIsgoing(false);
                                db.getDatabaseHelper().getPerformersDataDao().update(data);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("Error", error.getMessage());
                        }
                    });
                }else{
                    webServerService.addGoingPerformers(data, new Callback<Performers>() {
                        @Override
                        public void success(Performers performers, Response response) {
                            Log.d("Success", "asd");
                            try {
                                data.setIsgoing(true);
                                db.getDatabaseHelper().getPerformersDataDao().update(data);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("Error", error.getMessage());
                        }
                    });
                }
            }
        });
    }
}
