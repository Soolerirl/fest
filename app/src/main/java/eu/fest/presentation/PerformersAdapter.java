package eu.fest.presentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

import eu.fest.R;
import eu.fest.model.databases.Performers;


public class PerformersAdapter extends BaseAdapter {

    private ArrayList<Performers> performers;
    private LayoutInflater inflater;
    private Context context;

    public PerformersAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.performers = new ArrayList<>();
        this.context = context;
    }

    public void replaceWith(Collection<Performers> newPerformers) {
        this.performers.clear();
        this.performers.addAll(newPerformers);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return performers.size();
    }

    @Override
    public Performers getItem(int position) {
        return performers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflateIfRequired(view, position, parent);
        bind(getItem(position), view);
        return view;
    }

    private void bind(Performers performers, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if(performers.getPerformer_logo() == null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.performers_img.setBackground(context.getDrawable(R.drawable.male_avatar));
                }
            }
        }else{
            if(performers.getPerformer_logo()!=null) {
                Picasso.with(context).load(performers.getPerformer_logo().startsWith("http") ? performers.getPerformer_logo() : "http://" + performers.getPerformer_logo()).into(holder.performers_img);
            }
        }

        holder.performers_name.setText(performers.getPerformer_name());
        holder.performers_location.setText(performers.getLocation_name());
        holder.performers_time.setText(performers.getStart_time());
        holder.performers_genre.setText(performers.getPerformer_genre());
        holder.go_on_text.setText(performers.getCount_going_festival_event_event()+"");
        holder.go_on_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.performers_list_item, null);
            view.setTag(new ViewHolder(view));
        }
        return view;
    }

    static class ViewHolder {
        final ImageView performers_img;
        final TextView performers_name;
        final TextView performers_location;
        final TextView performers_time;
        final TextView performers_genre;
        final LinearLayout go_on_btn;
        final TextView go_on_text;

        ViewHolder(View view) {
            performers_img = (ImageView) view.findViewWithTag("performers_img");
            performers_name = (TextView) view.findViewWithTag("performers_name");
            performers_location = (TextView) view.findViewWithTag("performers_location");
            performers_time = (TextView) view.findViewWithTag("performers_time");
            performers_genre = (TextView) view.findViewWithTag("performers_genre");
            go_on_btn = (LinearLayout) view.findViewWithTag("go_on_btn");
            go_on_text = (TextView) view.findViewWithTag("go_on_text");
        }
    }
}
