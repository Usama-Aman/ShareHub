package adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobiletouch.sharehub.EventCommentsActivity;
import com.mobiletouch.sharehub.EventVideoCommentsActivity;
import com.mobiletouch.sharehub.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import models.EventDetailsResponseData;
import utility.AppUtils;
import utility.RoundRectCornerImageView;


/**
 * Created by M.Mubashir on 5/17/2018.
 */

public class OverviewMediaAdapter extends RecyclerView.Adapter<OverviewMediaAdapter.MyViewHolder> {

    private ArrayList<EventDetailsResponseData.Media> List;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RoundRectCornerImageView mImage;


        public MyViewHolder(View view) {
            super(view);
            mImage = view.findViewById(R.id.iv_media);


        }
    }


    public OverviewMediaAdapter(Context context, ArrayList<EventDetailsResponseData.Media> eventList) {
        this.List = eventList;
        this.mContext = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_media_view_gallery, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final EventDetailsResponseData.Media event = List.get(position);
        if (event.getEmediaType().equalsIgnoreCase("image")) {
            if (AppUtils.isSet(event.getFullImage())) {
                Picasso.with(mContext)
                        .load(event.getFullImage())   //
                        .placeholder(R.drawable.ic_placeholder) // optional
                        .error(R.drawable.ic_placeholder)         // optional
                        .into(holder.mImage);
            }
        } else {
            if (AppUtils.isSet(event.getFullImage())) {
                try {
                    getThumbnail(holder.mImage, event.getFullImage());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        holder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event.getEmediaType().equalsIgnoreCase("image")) {
                    Intent intent = new Intent(mContext, EventCommentsActivity.class);
                    intent.putExtra("url", event.getFullImage());
                    intent.putExtra("event_id", event.getEventId());
                    intent.putExtra("media_id", event.getEmediaId());
                    intent.putExtra("islike", event.getIs_liked());
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, EventVideoCommentsActivity.class);
                    intent.putExtra("url", event.getFullImage());
                    intent.putExtra("event_id", event.getEventId());
                    intent.putExtra("media_id", event.getEmediaId());
                    intent.putExtra("islike", event.getIs_liked());
                    mContext.startActivity(intent);
                }

            }
        });

    }

    public void getThumbnail(final RoundRectCornerImageView imageView, final String url) {
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap = null;
                String videoPath = url;

                MediaMetadataRetriever mediaMetadataRetriever = null;
                try {
                    mediaMetadataRetriever = new MediaMetadataRetriever();
                    if (Build.VERSION.SDK_INT >= 14) { // no headers included
                        mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
                    } else
                        mediaMetadataRetriever.setDataSource(videoPath);
                    bitmap = mediaMetadataRetriever.getFrameAtTime();
                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    if (mediaMetadataRetriever != null)
                        mediaMetadataRetriever.release();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }

    @Override
    public int getItemCount() {
        return List.size();
    }


}

