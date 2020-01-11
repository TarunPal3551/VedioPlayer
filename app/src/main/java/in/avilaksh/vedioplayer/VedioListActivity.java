package in.avilaksh.vedioplayer;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

public class VedioListActivity extends AppCompatActivity implements VideoFileAdapter.OnItemClickListener, VideoFileAdapter.OnItemLongClickListener {
    RecyclerView vedioListRecylerViw;
    private ArrayList<VedioFileModel> videoItemByAlbum = new ArrayList<VedioFileModel>();
    private VideoFileAdapter adapter;
    TextView folder_Name_TextView;
    ImageView backButton;
    Dialog dialog;
    LinearLayout dialogButton_Info, dialogButton_delete, dialogbutton_share, dialogButton_cancel;
    TextView info_TitleTextview, info_SizeTextView, info_DurationTextView, info_ResolutionTextView, info_PathTextView;
    private Button dialogButtonOk;
    private ImageView openMenuButton;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio_list);
        dialog = new Dialog(VedioListActivity.this);
        folder_Name_TextView = (TextView) findViewById(R.id.folderNameTextView);
        backButton = (ImageView) findViewById(R.id.backButtonList);
        openMenuButton = (ImageView) findViewById(R.id.openMenu);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VedioListActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String vedio_path = bundle.getString("FolderPath");
        folder_Name_TextView.setText("" + vedio_path);
        vedioListRecylerViw = (RecyclerView) findViewById(R.id.recylerviewvediolist);
        vedioListRecylerViw.setHasFixedSize(true);
        vedioListRecylerViw.setLayoutManager(new LinearLayoutManager(this));
        getVideoByAlbum(vedio_path);
        openMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(VedioListActivity.this, openMenuButton);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_home, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.one) {
                            try {
                                Intent sendEmailIntent = new Intent();
                                sendEmailIntent.setAction(Intent.ACTION_SENDTO);
                                sendEmailIntent.setData(Uri.parse("mailto:"));
                                sendEmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"Email id"});
                                sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding Video Player");
                                startActivity(sendEmailIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (item.getItemId() == R.id.two) {
                            try {
                                Intent inviteIntent = new Intent();
                                inviteIntent.setAction(Intent.ACTION_SEND);
                                inviteIntent.putExtra(Intent.EXTRA_TEXT, "Hi! Download VideoPlayer to play videos in MultiWindow Mode and with High Quality" + "\n" + " https://play.google.com/store/apps/details?id=" + getPackageName());
                                inviteIntent.setType("text/plain");
                                Intent chooser = Intent.createChooser(inviteIntent, "Share App");
                                startActivity(chooser);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else if (item.getItemId() == R.id.three) {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=" + getPackageName())));
                            } catch (android.content.ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                            }

                        }
//                        Toast.makeText(
//                                MainActivity.this,
//                                "You Clicked : " + item.getTitle(),
//                                Toast.LENGTH_SHORT
//                        ).show();
                        return true;
                    }
                });

                popup.show();
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void getVideoByAlbum(String bucketName) {
        try {
            String orderBy = MediaStore.Images.Media.DATE_TAKEN;
//            String searchParams = null;
//            String bucket = bucketName;
//            searchParams = bucket;
            String selection = MediaStore.Video.Media.DATA + " like?";
            String[] selectionArgs = new String[]{"%" + bucketName + "%"};
            String[] PROJECTION_BUCKET = {MediaStore.Video.VideoColumns._ID,
                    MediaStore.Video.VideoColumns.DISPLAY_NAME,
                    MediaStore.Video.VideoColumns.DATE_TAKEN,
                    MediaStore.Video.VideoColumns.DATA,
                    MediaStore.Video.VideoColumns.DURATION,
                    MediaStore.Video.VideoColumns.TITLE, MediaStore.Video.VideoColumns.RESOLUTION, MediaStore.Video.VideoColumns.SIZE
            };

            Cursor mVideoCursor;
            mVideoCursor = this.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, PROJECTION_BUCKET,
                    selection, selectionArgs, orderBy, null);

            if (mVideoCursor != null) {
                if (mVideoCursor.moveToFirst()) {
                    String _id;
                    String date;
                    String data;
                    String displayName;
                    int duration;
                    String tittle;
                    int idColumn = mVideoCursor
                            .getColumnIndex(MediaStore.Video.Media._ID);
                    int bucketColumn = mVideoCursor
                            .getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
                    int size = mVideoCursor
                            .getColumnIndex(MediaStore.Video.Media.SIZE);
                    int resolution = mVideoCursor
                            .getColumnIndex(MediaStore.Video.Media.RESOLUTION);


                    int dateColumn = mVideoCursor
                            .getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);
                    int dataColumn = mVideoCursor.getColumnIndex(MediaStore.Video.Media.DATA);
                    int durationColumn = mVideoCursor.getColumnIndex(MediaStore.Video.Media.DURATION);
                    int tittleColumn = mVideoCursor.getColumnIndex(MediaStore.Video.Media.TITLE);
                    int sub = mVideoCursor.getColumnIndex(MediaStore.Video.Media._ID);

                    do {
                        data = mVideoCursor.getString(dataColumn);
                        displayName = mVideoCursor.getString(bucketColumn);
                        duration = mVideoCursor.getInt(durationColumn);
                        tittle = mVideoCursor.getString(tittleColumn);
                        VedioFileModel albumVideo = new VedioFileModel();
                        albumVideo.setmSubTitle(mVideoCursor.getString(sub));
                        albumVideo.setmDisplayName(displayName);
                        albumVideo.setmUrl_FilePath(data);
                        albumVideo.setmContentType("video");
                        albumVideo.setmTitle(tittle);
                        albumVideo.setmDuration(duration);
                        albumVideo.setDateTaken(mVideoCursor.getString(dataColumn));
                        long sizeInMb = mVideoCursor.getLong(size) / (1024 * 1024);
                        albumVideo.setSize(String.valueOf(sizeInMb));

                        albumVideo.setResolution(mVideoCursor.getString(resolution));
                        videoItemByAlbum.add(albumVideo);

                    } while (mVideoCursor.moveToNext());
// cursorData.addAll(MediaUtils.extractMediaList(mVideoCursor,
// MediaType.PHOTO));
// mediaAdapter.notifyDataSetChanged();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter = new VideoFileAdapter(videoItemByAlbum, VedioListActivity.this);
        vedioListRecylerViw.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);


    }


    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(VedioListActivity.this, VideoPlayerActivity.class);
        intent.putParcelableArrayListExtra("VideoPlaylist", videoItemByAlbum);
        intent.putExtra("SelectVideoFilePath", videoItemByAlbum.get(position).getmUrl_FilePath());
        intent.putExtra("SelectVideoposition", position);
        intent.putExtra("SelectVideoName", videoItemByAlbum.get(position).getmDisplayName());
        intent.putExtra("filepath", videoItemByAlbum.get(position).getmUrl_FilePath());
        startActivity(intent);

    }

    @Override
    public void onItemLongClick(View view, int position) {
        Toast.makeText(this, "Clicked Position  " + position, Toast.LENGTH_SHORT).show();
        customDialog(position);

    }

    private void deleteVideo(VedioFileModel vedioFileModel) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set up the projection (we only need the ID)
                String[] projection = {MediaStore.Images.Media._ID};

// Match on the file path
                String selection = MediaStore.Images.Media.DATA + " = ?";
                String[] selectionArgs = new String[]{vedioFileModel.getmUrl_FilePath()};

                // Query for the ID of the media matching the file path
                Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = getContentResolver();
                Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
                if (c.moveToFirst()) {
                    // We found the ID. Deleting the item via the content provider will also remove the file
                    long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                    Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    contentResolver.delete(deleteUri, null, null);
                    dialog.dismiss();

                } else {
                    dialog.dismiss();
                    // File not found in media store DB
                }
                c.close();
            }


        }, 3000);


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

    public void showInfoDialog(int position) {
        dialog.setContentView(R.layout.video_info_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        info_TitleTextview = (TextView) dialog.findViewById(R.id.tvTitle);
        info_DurationTextView = (TextView) dialog.findViewById(R.id.tvDuration);
        info_PathTextView = (TextView) dialog.findViewById(R.id.tvPath);
        info_ResolutionTextView = (TextView) dialog.findViewById(R.id.tvResolution);
        info_SizeTextView = (TextView) dialog.findViewById(R.id.tvSize);
        dialogButtonOk = (Button) dialog.findViewById(R.id.btnOK);
        info_TitleTextview.setText("" + videoItemByAlbum.get(position).getmTitle());
        info_SizeTextView.setText("" + videoItemByAlbum.get(position).getSize() + " MB");
        info_ResolutionTextView.setText("" + videoItemByAlbum.get(position).getResolution());
        info_PathTextView.setText("" + videoItemByAlbum.get(position).getmUrl_FilePath());
        info_DurationTextView.setText("" + stringForTime(videoItemByAlbum.get(position).getmDuration()));
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }


    public void customDialog(int position) {
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.longpressdialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialogButton_Info = (LinearLayout) dialog.findViewById(R.id.button1);
        dialogButton_delete = (LinearLayout) dialog.findViewById(R.id.button2);
        dialogbutton_share = (LinearLayout) dialog.findViewById(R.id.button3);
        dialogButton_cancel = (LinearLayout) dialog.findViewById(R.id.button4);
        dialogButton_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialogbutton_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("video/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(VedioListActivity.this,
                        getApplicationContext().getPackageName() + ".my.package.name.provider", new File(videoItemByAlbum.get(position).getmUrl_FilePath())));
                ComponentName cn = shareIntent.resolveActivity(getPackageManager());
                if (cn != null) {
                    startActivity(Intent.createChooser(shareIntent, "Share Via"));
                    dialog.dismiss();
                } else {
                    dialog.dismiss();

                    Toast.makeText(VedioListActivity.this, "Video not found", Toast.LENGTH_SHORT).show();
                }


            }
        });
        dialogButton_Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog(position);

            }
        });
        dialogButton_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWriteStoragePermissionGranted()) {
                    ContentResolver contentResolver = getContentResolver();
                    contentResolver.delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Video.Media._ID + "=" + videoItemByAlbum.get(position).getmSubTitle(), null);

                    videoItemByAlbum.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(VedioListActivity.this, "Video Deleted", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();

                }
            }
        });


        dialog.show();
    }

    private static final String TAG = "VedioListActivity";

    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted2");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted2");
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                    //downloadPdfFile();

                } else {
                    //progress.dismiss();
                }
                break;
        }
    }


}
