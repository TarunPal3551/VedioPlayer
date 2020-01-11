package in.avilaksh.vedioplayer;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class VideoFileAdapter extends RecyclerView.Adapter<VideoFileAdapter.ViewHolder> {
    private List<VedioFileModel> itemList;

    private Context mContext;


    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }


    public VideoFileAdapter(List<VedioFileModel> itemList, Context mContext) {
        this.itemList = itemList;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.videofilelistitem, null);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        try {
            VedioFileModel item = itemList.get(i);
            viewHolder.title.setText(Html.fromHtml(item.getmDisplayName()));
            viewHolder.duration.setText(stringForTime(item.getmDuration()));

            Uri uri = Uri.fromFile(new File(item.getmUrl_FilePath()));
            if (item.getmContentType().equalsIgnoreCase("video")) {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.loading);

                Glide.with(mContext)
                        .load(item.getmUrl_FilePath())
                        .apply(options)
                        .into(viewHolder.thumbnail);
            } else {
                itemList.remove(i);


            }

            if (mOnItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(viewHolder.itemView, i);
                    }
                });
            }

            if (mOnItemLongClickListener != null) {
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = viewHolder.getLayoutPosition();
                        mOnItemLongClickListener.onItemLongClick(viewHolder.itemView, i);
                        return true;
                    }
                });
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


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

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title, duration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            title = (TextView) itemView.findViewById(R.id.title);
            duration = (TextView) itemView.findViewById(R.id.duration);


        }

    }


//    private List<MediaFileInfo> itemList;
//
//    private Context mContext;
//
//    public VideoFolderAdapter(Context context, List<MediaFileInfo> itemList) {
//        this.itemList = itemList;
//        this.mContext = context;
//    }
//
//    @Override
//    public MediaListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);
//        MediaListRowHolder mh = new MediaListRowHolder(v);
//
//        return mh;
//    }
//
//    @Override
//    public void onBindViewHolder(MediaListRowHolder mediaListRowHolder, int i) {
//        try{
//            MediaFileInfo item = itemList.get(i);
//            mediaListRowHolder.title.setText(Html.fromHtml(item.getFileName()));
//            Uri uri = Uri.fromFile(new File(item.getFilePath()));
//            if(item.getFileType().equalsIgnoreCase("video")) {
//                Bitmap bmThumbnail = ThumbnailUtils.
//                        extractThumbnail(ThumbnailUtils.createVideoThumbnail(item.getFilePath(),
//                                MediaStore.Video.Thumbnails.MINI_KIND), 80, 50);
//                if(bmThumbnail != null) {
//                    mediaListRowHolder.thumbnail.setImageBitmap(bmThumbnail);
//                }
//            }
//            else {
//                Picasso.with(mContext).load(uri)
//                        .centerCrop()
//                        .resize(80, 50)
//                        .into(mediaListRowHolder.thumbnail);
//
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return (null != itemList ? itemList.size() : 0);
//    }
}
