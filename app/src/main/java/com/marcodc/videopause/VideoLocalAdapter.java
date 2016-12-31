package com.marcodc.videopause;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class VideoLocalAdapter extends ArrayAdapter<String> {
    private final HashMap<String, Bitmap> cacheBitmap;
    private Context mContext;
   private List<String> paths;
    private ArrayList<Bitmap> listaImmagini = new ArrayList<>();

    static class ViewHolder {
        // value for the name of the file
        public TextView text;
        // video file thumbnail
        public ImageView image;
    }
    public VideoLocalAdapter(Context c, List<String> paths) {
        super(c, R.layout.layout_gridview_video_local, paths);
        mContext = c;
        this.paths = paths;
        cacheBitmap = new HashMap<String, Bitmap>(paths.size());
        initCacheBitmap();
    }

    private void initCacheBitmap() {
        for(String string : paths)
            cacheBitmap.put(string, ThumbnailUtils.createVideoThumbnail(string, MediaStore.Video.Thumbnails.MINI_KIND));

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.layout_gridview_video_local, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.grid_item_label);
            viewHolder.image = (ImageView) rowView.findViewById(R.id.grid_item_image);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        // Video file name
        String s = paths.get(position);
        MediaPlayer mp = MediaPlayer.create(mContext, Uri.parse(paths.get(position)));

        int duration = mp.getDuration();
        mp.release();

        String text = String.format("%02d:%02d", duration/60000, (duration/1000%60 - (duration/60000)));
        holder.text.setText(text);
        //holder.text.setText(s);


        holder.image.setImageBitmap(cacheBitmap.get(s));

        return rowView;
    }











/*



    public void updateResults(ArrayList<String> list) {
        paths = (List<String>) list.clone();
        for(String pathTmp : paths){
            listaImmagini.add(ThumbnailUtils.createVideoThumbnail(pathTmp, MediaStore.Images.Thumbnails.MINI_KIND ));
        }
        //Triggers the list update
        notifyDataSetChanged();
    }

    public int getCount() {
        return paths.size();
    }

    public Object getItem(int position) {
        return paths.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    */
/*android:id="@+id/grid_item_image"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_marginRight="10px"
            >
    </ImageView>

    <TextView
    android:id="@+id/grid_item_label"*//*


    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {
            gridView = new View(mContext);
            //+-gridView = new View(mContext);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.layout_gridview_video_local, null);
            // set value into textview
            TextView textView = (TextView) gridView
                    .findViewById(R.id.grid_item_label);


            // set image based on selected text
             ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.grid_item_image);
            imageView.setImageResource(R.drawable.zenfone2);;
           // Glide.with(mContext).placeholder(R.drawable.zenfone2).into(imageView);
           */
/* MediaPlayer mp = MediaPlayer.create(mContext, Uri.parse(paths.get(position)));

            int duration = mp.getDuration();
            mp.release();*//*

*/
/*convert millis to appropriate time*//*

           */
/* String text = String.format("%02d:%02d", duration/60000, (duration/1000%60 - (duration/60000)));
            textView.setText(text);*//*

           */
/* Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {*//*

            imageView.setImageBitmap(listaImmagini.get(position));
            MediaPlayer mp = MediaPlayer.create(mContext, Uri.parse(paths.get(position)));

                        int duration = mp.getDuration();
                        mp.release();
                        String text = String.format("%02d:%02d", duration/60000, (duration/1000%60 - (duration/60000)));
                        textView.setText(text);
                  */
/*  } catch (Exception ex ){

                    }
                }
            }, 100);*//*

        } else {
            gridView = (View) convertView;


        }



        return gridView;
    }
*/

/*
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2,2,2,2);
        } else {
            imageView = (ImageView) convertView;
        }
        Glide.with(mContext).load(paths.get(position)) .into(imageView);
        MediaPlayer mp = MediaPlayer.create(mContext, Uri.parse(paths.get(position)));
        int duration = mp.getDuration();
        mp.release();
*//*convert millis to appropriate time*//*
        String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
        return imageView;
    }*/
}