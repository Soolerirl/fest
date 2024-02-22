package eu.fest.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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

import eu.fest.EventPreviewActivity_;
import eu.fest.R;
import eu.fest.model.databases.Events;
import eu.fest.presentation.DatabaseManager;
import eu.fest.service.WebServerService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@EViewGroup(R.layout.event_list_item_card)
public class EventListItemView extends RelativeLayout {

    @Bean
    WebServerService webServerService;

    @Bean(DatabaseManager.class)
    DatabaseManager db;

    @ViewById
    TextView go_on_text;

    @ViewById
    TextView follow_text;

    @ViewById
    TextView event_time;

    @ViewById
    TextView event_location;

    @ViewById
    ImageButton go_on_btn;

    @ViewById
    ImageButton follow_btn;

    @ViewById
    CardView event_logo_bg;

    @ViewById
    ImageView event_logo;

    TextView text;

    private Dialog mDialog;

    public EventListItemView(Context context) {
        super(context);
    }

    @UiThread
    public void bind(final Events data) {
        go_on_text.setText(String.valueOf(data.getGoing_festival_event()));
        follow_text.setText(String.valueOf(data.getWatch_festival_event()));
        event_location.setText(data.getLocation_country() + ", " + data.getLocation_city());
        event_time.setText(data.getStart_date());


        if(data.getFestival_event_logo() == null || data.getFestival_event_logo().equals(null) || data.getFestival_event_logo() == "null" || data.getFestival_event_logo().equals("null")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                event_logo.setBackground(getResources().getDrawable(R.drawable.male_avatar));
            }
        }else{
            if(data.getFestival_event_logo()!=null) {
                Picasso.with(getContext()).load(data.getFestival_event_logo().startsWith("http") ? data.getFestival_event_logo() : "http://" + data.getFestival_event_logo()).into(event_logo);
            }
        }

        go_on_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.is_going_festival_event()){
                    webServerService.deleteGoingFestivalEvent(data.getFestivalId()+"", data.getServerId()+"", new Callback<Events>() {
                        @Override
                        public void success(Events events, Response response) {
                            data.setIs_going_festival_event(false);
                            int a = data.getGoing_festival_event();
                            data.setGoing_festival_event((a-1));
                            try {
                                db.getDatabaseHelper().getEventsDataDao().update(data);
                                go_on_text.setText(data.getGoing_festival_event()+"");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("Error", error.getMessage());
                            openDialog(error.getMessage());
                        }
                    });
                }else{
                    webServerService.addGoingFestivalEvent(data.getFestivalId()+"", data.getServerId()+"", new Callback<Events>() {
                        @Override
                        public void success(Events events, Response response) {
                            Log.d("Succes", " ");
                            data.setIs_going_festival_event(true);
                            int a = data.getGoing_festival_event();
                            data.setGoing_festival_event((a+1));
                            try {
                                db.getDatabaseHelper().getEventsDataDao().update(data);
                                go_on_text.setText(data.getGoing_festival_event()+"");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("Error", error.getMessage());
                            openDialog(error.getMessage());
                        }
                    });
                }
            }
        });

        follow_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.is_watch_festival_event()){
                    webServerService.deleteWatchFestivalEvent(data.getFestivalId() + "", data.getServerId() + "", new Callback<Events>() {
                        @Override
                        public void success(Events events, Response response) {
                            Log.d("Succes", " ");
                            data.setIs_watch_festival_event(false);
                            int a = data.getWatch_festival_event();
                            data.setWatch_festival_event((a-1));
                            try {
                                db.getDatabaseHelper().getEventsDataDao().update(data);
                                follow_text.setText(data.getWatch_festival_event()+"");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("Error", error.getMessage());
                            openDialog(error.getMessage());
                        }
                    });
                }else{
                    webServerService.addWatchFestivalEvent(data.getFestivalId() + "", data.getServerId() + "", new Callback<Events>() {
                        @Override
                        public void success(Events events, Response response) {
                            Log.d("Succes", " ");
                            data.setIs_watch_festival_event(true);
                            int a = data.getWatch_festival_event();
                            data.setWatch_festival_event((a+1));
                            try {
                                db.getDatabaseHelper().getEventsDataDao().update(data);
                                follow_text.setText(data.getWatch_festival_event()+"");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d("Error", error.getMessage());
                            openDialog(error.getMessage());
                        }
                    });
                }
            }
        });

        event_logo_bg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EventPreviewActivity_.intent(getContext()).eventData(data).start();
            }
        });

    }


    public void openDialog(String content){
        mDialog = new Dialog(getContext());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mDialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setContentView(R.layout.message_dialog);
        mDialog.setCancelable(true);
        text = (TextView) mDialog.findViewById(R.id.text);
        text.setText(content);
        Button dialogButton = (Button) mDialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }
}
