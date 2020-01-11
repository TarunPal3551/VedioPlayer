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

public class FileGridAdapter extends BaseAdapter {
    private List<VedioFileModel> itemList;

    private Context mContext;

    public FileGridAdapter(List<VedioFileModel> itemList, Context mContext) {
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
            itemView = layoutInflater.inflate(R.layout.videofilegriditem, null);
        }
        ImageView thumbnail;
        TextView title, duration;
        thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        title = (TextView) itemView.findViewById(R.id.title);
        duration = (TextView) itemView.findViewById(R.id.duration);
        try {
            VedioFileModel item = itemList.get(i);
            title.setText(Html.fromHtml(item.getmDisplayName()));
            duration.setText(stringForTime(item.getmDuration()));

            Uri uri = Uri.fromFile(new File(item.getmUrl_FilePath()));
            if (item.getmContentType().equalsIgnoreCase("video")) {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.loading);

                Glide.with(mContext)
                        .load(item.getmUrl_FilePath())
                        .apply(options)
                        .into(thumbnail);
            } else {
                itemList.remove(i);


            }




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
