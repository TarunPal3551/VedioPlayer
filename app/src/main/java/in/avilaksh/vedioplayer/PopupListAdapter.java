package in.avilaksh.vedioplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class PopupListAdapter extends BaseAdapter {
    private Context context;
    private List<PopupItem> programList = new ArrayList<>(150);

    public PopupListAdapter(Context context, List<PopupItem> programList) {
        this.context = context;
        this.programList = programList;
    }

    @Override
    public int getCount() {
        return programList.size();
    }

    @Override
    public PopupItem getItem(int position) {
        return programList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final PopupItem popupItem = programList.get(position);
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_popup_program, parent, false);
            viewHolder.llProgram = (LinearLayout) convertView.findViewById(R.id.llPopProgram);
            viewHolder.tvProgram = (TextView) convertView.findViewById(R.id.tvPopProgram);
            convertView.setTag(viewHolder);
        }
        viewHolder.tvProgram.setText(popupItem.getName());
        viewHolder.llProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if ("1".equals(App.programType) || "3".equals(App.programType)) {
//                    Uri videoUri = Uri.parse(popupItem.getPath());
//                    //controller.rePlay(videoUri);
//                } else if ("2".equals(App.programType)) {
////                    String[] info = {"2", popupItem.getTimeStart(), popupItem.getTimeEnd(), popupItem.getP()};
////                   // String uri = ProgramUrlUtils.getProgramPathFromInfo(info);
////                    Uri videoUri = Uri.parse(uri);
////                    controller.rePlay(videoUri);
//                } else if ("5".equals(App.programType)) {
//
//                    // controller.rePlay(popupItem.getXdVideo());
//                }
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        public TextView tvProgram;
        public LinearLayout llProgram;
    }
}
