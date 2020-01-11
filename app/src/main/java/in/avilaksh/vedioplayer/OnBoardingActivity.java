package in.avilaksh.vedioplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OnBoardingActivity extends AppCompatActivity {
    RelativeLayout getStarted;
    ViewPager onBoardingViewPager;
    Context mContext;
    OnBoardingViewPagerAdapter onBoardingViewPagerAdapter;
    public LinearLayout dot_layout;
    public ImageView[] dots;
    TextView nextButton;
    Button skipButton;
    int current_Page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);


        onBoardingViewPager = (ViewPager) findViewById(R.id.viewpager);
        dot_layout = (LinearLayout) findViewById(R.id.dotslayoutonboarding);
        skipButton = (Button) findViewById(R.id.buttonSkip);
        nextButton = (TextView) findViewById(R.id.textViewNext);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OnBoardingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        String[] title = new String[]{"POPUP Play", "Manage videos", "Video formats", "Play and Share"};
        String[] description = new String[]{"You can play video in Picture-In-Picture Mode,Just tap on single button.",
                "You can Delete and Info about Videos.",
                "Supporting multiple format of videos and play videos of any format like Mp4,3gp,Mkv etc.",
                "Play videos along with share video to your friends."};
        int[] images = new int[]{R.drawable.onboarding1,
                R.drawable.onboarding2,
                R.drawable.video_file, R.drawable.share};
        mContext = OnBoardingActivity.this;
        onBoardingViewPagerAdapter = new OnBoardingViewPagerAdapter(mContext, title, description, images);
        onBoardingViewPager.setAdapter(onBoardingViewPagerAdapter);
        createdots(onBoardingViewPager.getCurrentItem());
        onBoardingViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                for (int j = 0; j < 4; j++) {

                    dots[j].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.default_dots));
                }
                dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dots));

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (current_Page == onBoardingViewPagerAdapter.getCount() - 1) {
                    Intent intent = new Intent(OnBoardingActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                current_Page = current_Page + 1;
                onBoardingViewPager.setCurrentItem(current_Page, true);
//                int position = onBoardingViewPager.getCurrentItem();
//                if (position == 0) {
//                    onBoardingViewPager.setCurrentItem(1);
//                } else if (position == 1) {
//                    onBoardingViewPager.setCurrentItem(2);
//
//                } else if (position == 2) {
//                    onBoardingViewPager.setCurrentItem(3);
//                } else if (position == 3) {
//                    onBoardingViewPager.setCurrentItem(0);
//                }
            }
        });

    }


    public void createdots(int current_position) {
        if (dot_layout != null) {
            dot_layout.removeAllViews();
        }
        dots = new ImageView[4];
        for (int i = 0; i < 4; i++) {
            dots[i] = new ImageView(this);
            if (i == current_position) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dots));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_dots));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(3, 0, 3, 0);
            dot_layout.addView(dots[i], params);


        }

    }
}
