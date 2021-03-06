package in.avilaksh.vedioplayer;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class GridAdapter extends BaseAdapter {
    private List<VideoItem> itemList;

    private Context mContext;

    public GridAdapter(List<VideoItem> itemList, Context mContext) {
        this.itemList = itemList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View itemView, ViewGroup viewGroup) {
        if (itemView== null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            itemView = layoutInflater.inflate(R.layout.grid_row, null);
        }

        ImageView thumbnail;
        TextView title;
        TextView videoCount;
        thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        title = (TextView) itemView.findViewById(R.id.title);
        videoCount = (TextView) itemView.findViewById(R.id.vedioCount);
        try {
            VideoItem item = itemList.get(i);
            title.setText(Html.fromHtml(item.getDISPLAY_NAME()));
            videoCount.setText(Html.fromHtml(String.valueOf(item.getVIDEO_COUNT()) + " " + "Videos"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemView;
    }

    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
}
