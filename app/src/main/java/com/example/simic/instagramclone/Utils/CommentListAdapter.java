package com.example.simic.instagramclone.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.simic.instagramclone.Models.Comment;
import com.example.simic.instagramclone.R;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Simic on 26.4.2018..
 */

public class CommentListAdapter extends ArrayAdapter<Comment> {
    private static final String TAG = "CommentListAdapter";

    private LayoutInflater mInflater;
    private int layoutResource;
    private Context mContext;

    public CommentListAdapter(@NonNull Context context, int resource, @NonNull Comment[] objects) {
        super(context, resource, objects);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null){
            convertView = mInflater.inflate(layoutResource,parent,false);
            holder = new ViewHolder();

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        //set the comment
        holder.comment.setText(getItem(position).getComment());

        //set the timestamp difference
        String timestampDifference = getTimestampDifference(getItem(position));
        if (!timestampDifference.equals("0")){
            holder.timestamp.setText(timestampDifference + " d");
        }else {
            holder.timestamp.setText("today");
        }

        //set the username
        holder.username.setText(getItem(position).getUser_id());

        return convertView;
    }

    public static class ViewHolder{

        @BindView(R.id.comment_username) TextView username;
        @BindView(R.id.comment) TextView comment;
        @BindView(R.id.comment_time_posted) TextView timestamp;
        @BindView(R.id.comment_reply) TextView reply;
        @BindView(R.id.comment_likes) TextView likes;
        @BindView(R.id.comment_profile_image) CircleImageView profileImage;
        @BindView(R.id.comment_like) ImageView like;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }

        public ViewHolder() {

        }
    }

    //region GetTimestampDifference
    private String getTimestampDifference(Comment comment){
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss'Z'", Locale.ITALIAN);
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Zagreb"));

        Date today = (Date) c.getTime();
        sdf.format(today);
        Date timestamp;

        final String photoTimestamp = comment.getDate_created();

        try {

            timestamp = (Date) sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24 ));


        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException " + e.getMessage() );

        }
        return difference;
    }
    //endregion

}
