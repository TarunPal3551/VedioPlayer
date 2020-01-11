package in.avilaksh.vedioplayer;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class VideoFolderAdapter extends RecyclerView.Adapter<VideoFolderAdapter.ViewHolder> {
    private List<VideoItem> itemList;

    private Context mContext;
    private ItemClickListener clickListener;


    public VideoFolderAdapter(List<VideoItem> itemList, Context mContext) {
        this.itemList = itemList;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        try {
            VideoItem item = itemList.get(i);
            viewHolder.title.setText(Html.fromHtml(item.getDISPLAY_NAME()));
            viewHolder.videoCount.setText(Html.fromHtml(String.valueOf(item.getVIDEO_COUNT()) + " " + "Videos"));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView thumbnail;
        TextView title;
        TextView videoCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            title = (TextView) itemView.findViewById(R.id.title);
            videoCount = (TextView) itemView.findViewById(R.id.vedioCount);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onItemClick(v, getAdapterPosition());

        }

        @Override
        public boolean onLongClick(View v) {
            if (clickListener != null) {
                clickListener.onItemLongClick(v, getAdapterPosition());
                return true;
            }
            return false;
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
