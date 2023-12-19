package adapters;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.mobiletouch.sharehub.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import models.MediaList;
import utility.AppUtils;

public class ImageSliderAdapter extends PagerAdapter {
    private final ArrayList<MediaList> adImages;
    Context mContext;
    private MediaList mItem;
    private LayoutInflater mLayoutInflater;

    public ImageSliderAdapter(Context context, ArrayList<MediaList> adImages) {
        this.mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.adImages = adImages;
    }

    @Override
    public int getCount() {
        return adImages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // TODO Auto-generated method stub
        return view == ((LinearLayout) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
        mItem = adImages.get(position);
        try {
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            if (AppUtils.isSet(mItem.getFullImage() )) {
                Picasso.with(mContext)
                        .load(mItem.getFullImage())   //
                        .placeholder(R.drawable.ic_placeholder) // optional
                        .error(R.drawable.ic_placeholder)         // optional
                        .into(imageView);
            }

            //ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.pb_img);
            /*ImageUtils.displayImageFromUrl(mContext, images.get(position).getImage(), imageView, null, new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@android.support.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            });*/
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ((ViewPager) container).addView(itemView);
        return itemView;
    }


/*
    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        mItem = adImages.get(i);
        ImageView mImageView = new ImageView(mContext);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //mImageView.setImageResource(sliderImagesId[i]);
        //String url = Urls.BaseUrlUploadImage;
        if (AppUtils.isSet(mItem.getFullImage() )) {
            Picasso.with(mContext)
                    .load(mItem.getFullImage())   //
                    .placeholder(R.drawable.ic_placeholder) // optional
                    .error(R.drawable.ic_placeholder)         // optional
                    .into(mImageView);
        }

        */
/*mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*
*/
/*Intent intent = new Intent(mContext, ImagePreviewActivity.class);
                intent.putParcelableArrayListExtra("adsImage", (ArrayList<? extends Parcelable>) adImages);
                mContext.startActivity(intent);*//*
*/
/*
            }
        });*//*


            ((ViewPager) container).addView(mImageView);
        return mImageView;
    }
*/

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        ((ViewPager) container).removeView((LinearLayout) obj);
    }
}