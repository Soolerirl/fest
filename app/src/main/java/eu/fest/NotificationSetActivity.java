package eu.fest;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import eu.fest.service.WebServerService;

@EActivity(R.layout.activity_notification)
public class NotificationSetActivity extends BaseActivity{

    @App
    ServiceBusApplication app;

    @Bean
    WebServerService webServerService;

    @ViewById
    Switch global_switch;

    @ViewById
    Switch vibration_switch;

    @ViewById
    Switch notification_sound_switch;

    @ViewById
    Switch led_light_switch;

    @ViewById
    Switch friend_request_switch;

    @ViewById
    Switch confirmation_switch;

    @ViewById
    Switch potential_new_friend_switch;

    @ViewById
    Switch crash_request_switch;

    @ViewById
    Switch friends_goon;

    @ViewById
    Switch add_location;

    @ViewById
    Switch event_update_switch;

    @ViewById
    Switch festival_recommendation;

    @ViewById
    Switch event_recommendation;

    @ViewById
    Switch performers_recommendation;

    @ViewById
    Switch change_event;

    @ViewById
    Switch change_performers;

    @ViewById
    TextView send_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    void setupUi(){
        if(app.getSettings().getSendCrashReports()==1){
            crash_request_switch.setChecked(true);
        }else{
            crash_request_switch.setChecked(false);
        }

        if(app.getSettings().getVibration()==1){
            vibration_switch.setChecked(true);
        }else{
            vibration_switch.setChecked(false);
        }

        if(app.getSettings().getNotificationSound()==1){
            notification_sound_switch.setChecked(true);
        }else{
            notification_sound_switch.setChecked(false);
        }

        if(app.getSettings().getLedLights()==1){
            led_light_switch.setChecked(true);
        }else{
            led_light_switch.setChecked(false);
        }


        if(app.getSettings().getGlobalNotification()==1){
            global_switch.setChecked(true);
        }else{
            global_switch.setChecked(false);
        }

        if(app.getSettings().getRecommendFriend()==1){
            potential_new_friend_switch.setChecked(true);
        }else{
            potential_new_friend_switch.setChecked(false);
        }


        /*if(app.getSettings().ge()==1){
            event_update_switch.setChecked(true);
        }else{
            event_update_switch.setChecked(false);
        }      */

        if(app.getSettings().getFriendRequest()==1){
            friend_request_switch.setChecked(true);
        }else{
            friend_request_switch.setChecked(false);
        }


        if(app.getSettings().getAcceptFriendRequest()==1){
            confirmation_switch.setChecked(true);
        }else{
            confirmation_switch.setChecked(false);
        }


        if(app.getSettings().getFriendGoingFestivalEvent()==1){
            friends_goon.setChecked(true);
        }else{
            friends_goon.setChecked(false);
        }

        if(app.getSettings().getAddLocation()==1){
            add_location.setChecked(true);
        }else{
            add_location.setChecked(false);
        }

        if(app.getSettings().getRecommendFestival()==1){
            festival_recommendation.setChecked(true);
        }else{
            festival_recommendation.setChecked(false);
        }

        if(app.getSettings().getRecommendFestivalEvent()==1){
            event_recommendation.setChecked(true);
        }else{
            event_recommendation.setChecked(false);
        }

        if(app.getSettings().getRecommendFestivalEventEvent()==1){
            performers_recommendation.setChecked(true);
        }else{
            performers_recommendation.setChecked(false);
        }

        if(app.getSettings().getChangeFestivalEvent()==1){
            change_event.setChecked(true);
        }else{
            change_event.setChecked(false);
        }

        if(app.getSettings().getChangeFestivalEventEvent()==1){
            change_performers.setChecked(true);
        }else{
            change_performers.setChecked(false);
        }

        send_time.setText(app.getSettings().getNotificationBeforeEventTime());

        send_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePickerShow((TextView) v);
            }
        });

        global_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                crash_request_switch.setChecked(isChecked);
                vibration_switch.setChecked(isChecked);
                notification_sound_switch.setChecked(isChecked);
                potential_new_friend_switch.setChecked(isChecked);
                led_light_switch.setChecked(isChecked);
                event_update_switch.setChecked(isChecked);
                friend_request_switch.setChecked(isChecked);
                confirmation_switch.setChecked(isChecked);
                friends_goon.setChecked(isChecked);
                add_location.setChecked(isChecked);
                festival_recommendation.setChecked(isChecked);
                event_recommendation.setChecked(isChecked);
                performers_recommendation.setChecked(isChecked);
                change_event.setChecked(isChecked);
                change_performers.setChecked(isChecked);
                if(isChecked){
                    setAll(1);
                }else{
                    setAll(0);
                }
            }
        });

        change_performers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                if(isChecked){
                    app.getSettings().setChangeFestivalEventEvent(1);
                }else{
                    app.getSettings().setChangeFestivalEventEvent(0);
                }
            }
        });

        change_event.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                if(isChecked){
                    app.getSettings().setChangeFestivalEvent(1);
                }else{
                    app.getSettings().setChangeFestivalEvent(0);
                }
            }
        });

        performers_recommendation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                if(isChecked){
                    app.getSettings().setRecommendFestivalEventEvent(1);
                }else{
                    app.getSettings().setRecommendFestivalEventEvent(0);
                }
            }
        });

        event_recommendation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                if(isChecked){
                    app.getSettings().setRecommendFestivalEvent(1);
                }else{
                    app.getSettings().setRecommendFestivalEvent(0);
                }
            }
        });

        festival_recommendation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                if(isChecked){
                    app.getSettings().setRecommendFestival(1);
                }else{
                    app.getSettings().setRecommendFestival(0);
                }
            }
        });

        add_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                if(isChecked){
                    app.getSettings().setAddLocation(1);
                }else{
                    app.getSettings().setAddLocation(0);
                }
            }
        });

        friends_goon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                if(isChecked){
                    app.getSettings().setFriendGoingFestivalEvent(1);
                }else{
                    app.getSettings().setFriendGoingFestivalEvent(0);
                }
            }
        });

        confirmation_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                if(isChecked){
                    app.getSettings().setAcceptFriendRequest(1);
                }else{
                    app.getSettings().setAcceptFriendRequest(0);
                }
            }
        });

        friend_request_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                if(isChecked){
                    app.getSettings().setFriendRequest(1);
                }else{
                    app.getSettings().setFriendRequest(0);
                }
            }
        });

        event_update_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                if(isChecked){
                   // app.getSettings().setChangeFestivalEvent(1);
                }else{
                   // app.getSettings().setSendCrashReports(0);
                }
            }
        });

        led_light_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                if(isChecked){
                    app.getSettings().setLedLights(1);
                }else{
                    app.getSettings().setLedLights(0);
                }
            }
        });

        potential_new_friend_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                if(isChecked){
                    app.getSettings().setRecommendFriend(1);
                }else{
                    app.getSettings().setRecommendFriend(0);
                }
            }
        });

        notification_sound_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                if(isChecked){
                    app.getSettings().setNotificationSound(1);
                }else{
                    app.getSettings().setNotificationSound(0);
                }
            }
        });

        vibration_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                if(isChecked){
                    app.getSettings().setVibration(1);
                }else{
                    app.getSettings().setVibration(0);
                }
            }
        });

        crash_request_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                if(isChecked){
                    app.getSettings().setSendCrashReports(1);
                }else{
                    app.getSettings().setSendCrashReports(0);
                }
            }
        });

    }

    void dateTimePickerShow(final TextView textView){
        Dialog dialogs = new Dialog(NotificationSetActivity.this);
        dialogs.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
        dialogs.setContentView(R.layout.timepicker);
        dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button set = (Button) dialogs.findViewById(R.id.date_time_set);
        final TimePicker timePicker = (TimePicker) dialogs.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        final Dialog finalDialogs = dialogs;
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textView.setText(timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute());
                app.getSettings().setNotificationBeforeEventTime(timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute()+":00");
                finalDialogs.dismiss();
            }
        });
        dialogs.getWindow().setLayout(android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT);
        dialogs.show();
    }

    void setAll(int checked){
        app.getSettings().setGlobalNotification(checked);
        app.getSettings().setSendCrashReports(checked);
        app.getSettings().setVibration(checked);
        app.getSettings().setNotificationSound(checked);
        app.getSettings().setLedLights(checked);
        app.getSettings().setFriendRequest(checked);
        app.getSettings().setAcceptFriendRequest(checked);
        app.getSettings().setFriendGoingFestivalEvent(checked);
        app.getSettings().setAddLocation(checked);
        app.getSettings().setRecommendFriend(checked);
        app.getSettings().setRecommendFestival(checked);
        app.getSettings().setRecommendFestivalEvent(checked);
        app.getSettings().setRecommendFestivalEventEvent(checked);
        app.getSettings().setChangeFestivalEvent(checked);
        app.getSettings().setChangeFestivalEventEvent(checked);
    }
}
