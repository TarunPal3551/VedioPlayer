package in.avilaksh.vedioplayer;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OnBoardingViewPagerAdapter extends PagerAdapter {
    Context mContext;
    String[] textTitle;
    String[] textDescription;
    int[] images;
    LayoutInflater inflater;

    public OnBoardingViewPagerAdapter(Context mContext, String[] textTitle, String[] textDescription, int[] images) {
        this.mContext = mContext;
        this.textTitle = textTitle;
        this.textDescription = textDescription;
        this.images = images;
    }

    @Override
    public int getCount() {
        return textTitle.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == ((RelativeLayout) o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        TextView textTittleTextView;
        TextView textDescriptionTextView;
        ImageView imageView;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpageritem, container, false);
        textDescriptionTextView = (TextView) itemView.findViewById(R.id.textViewdescription);
        textTittleTextView = (TextView) itemView.findViewById(R.id.textViewtitle);
        imageView = (ImageView) itemView.findViewById(R.id.imageViewOnboarding);
        textDescriptionTextView.setText(textDescription[position]);
        textTittleTextView.setText(textTitle[position]);
        imageView.setImageDrawable(ContextCompat.getDrawable(mContext.getApplicationContext(), images[position]));
        ((ViewPager) container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}
