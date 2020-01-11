package in.avilaksh.vedioplayer;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemClickListener {
    private Cursor cursor;
    /*
     * Column index for the Thumbnails Image IDs.
     */
    private int columnIndex;
    private static final String TAG = "RecyclerViewExample";


    private RecyclerView mRecyclerView;

    private VideoFolderAdapter adapter;
    String type = "";
    private ArrayList<VideoItem> vedioFolder = new ArrayList<VideoItem>();
    private ArrayList<VedioFileModel> videoItemByAlbum = new ArrayList<VedioFileModel>();
    int count = 0;
    RelativeLayout toolbar;
    ImageView openMenuButton;
    int backcount = 1;
    ImageView changeLayout;
    GridView gridView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    GridAdapter gridAdapter;
    ImageView sortImageView;
    String ORDER_BY="";


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        openMenuButton = (ImageView) findViewById(R.id.openMenu);
        toolbar = (RelativeLayout) findViewById(R.id.toptoolbar);
        sortImageView = (ImageView) findViewById(R.id.sortImageView);


        //ListView listView = (ListView) findViewById(R.id.listview);


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        gridView = (GridView) findViewById(R.id.gridViewFolder);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        openMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, openMenuButton);
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
        changeLayout = (ImageView) findViewById(R.id.changeView);
        changeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //true for recyclerView
                if (sharedPreferences.getBoolean("Layout", false)) {
                    editor.putBoolean("Layout", false);
                    editor.apply();
                    gridView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    changeLayout.setImageResource(R.drawable.ic_listview);
                } else {
                    editor.putBoolean("Layout", true);
                    editor.apply();
                    gridView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    changeLayout.setImageResource(R.drawable.ic_gridview);
                }

            }
        });
        if (sharedPreferences.getBoolean("Layout", false)) {

            gridView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            changeLayout.setImageResource(R.drawable.ic_gridview);
        } else {

            gridView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            changeLayout.setImageResource(R.drawable.ic_listview);
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String path = vedioFolder.get(i).getDISPLAY_NAME();
                Intent intent = new Intent(MainActivity.this, VedioListActivity.class);
                intent.putExtra("FolderPath", path);
                startActivity(intent);
            }
        });
        sortImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                // Set the alert dialog title
                builder.setTitle("Sort by");

                // Initializing an array of flowers
                final String[] flowers = new String[]{
                        "Date",
                        "Name A TO Z",
                        "Name Z TO A",
                        "Size"
                };
                // Item click listener
                builder.setSingleChoiceItems(
                        flowers, // Items list
                        -1, // Index of checked item (-1 = no selection)
                        (dialogInterface, i) -> {
                            // Get the alert dialog selected item's text
                            String selectedItem = Arrays.asList(flowers).get(i);

                            // Display the selected item's text on snack bar

                        });
                builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    // Just dismiss the alert dialog after selection
                    // Or do something now
                });
            }
        });
//        if (isReadStoragePermissionGranted()){
//            getVideoAlbum();
//
//
//            type = "video";
//            Log.d(TAG, "onCreate: " + adapter.getItemCount());
//            int totalFoldersHave = vedioFolder.size();
//            Log.d(TAG, "onCreate: " + totalFoldersHave);
//        }
//        else {
//            isReadStoragePermissionGranted();
//        }

        isReadStoragePermissionGranted();


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public int getCount(int position) {
        String path = vedioFolder.get(position).getDISPLAY_NAME();
        int size = getVideoByAlbum(path).size();
        Log.d(TAG, "getCount: videocontains" + size);
        return size;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public List getVideoByAlbum(String bucketName) {
        try {
            String BUCKET_GROUP_BY =
                    "1) GROUP BY ${" + MediaStore.Video.VideoColumns.BUCKET_ID + "}, (${" + MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME + "}";
            String BUCKET_ORDER_BY = MediaStore.Video.VideoColumns.DATE_ADDED + " DESC";

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
                    MediaStore.Video.VideoColumns.TITLE,
                    MediaStore.Video.VideoColumns.DATE_ADDED};

            Cursor mVideoCursor;
            mVideoCursor = this.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, PROJECTION_BUCKET,
                    BUCKET_GROUP_BY, null, BUCKET_ORDER_BY, null);

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


                    int dateColumn = mVideoCursor
                            .getColumnIndex(MediaStore.Video.Media.DATE_ADDED);
                    int dataColumn = mVideoCursor.getColumnIndex(MediaStore.Video.Media.DATA);
                    int durationColumn = mVideoCursor.getColumnIndex(MediaStore.Video.Media.DURATION);
                    int tittleColumn = mVideoCursor.getColumnIndex(MediaStore.Video.Media.TITLE);
                    do {
                        date = mVideoCursor.getString(dateColumn);
                        Log.e(TAG, "getVideoByAlbum: Date" + date);
                        data = mVideoCursor.getString(dataColumn);
                        displayName = mVideoCursor.getString(bucketColumn);
                        duration = mVideoCursor.getInt(durationColumn);
                        tittle = mVideoCursor.getString(tittleColumn);


                        VedioFileModel albumVideo = new VedioFileModel();
                        albumVideo.setmDisplayName(displayName);
                        albumVideo.setmUrl_FilePath(data);
                        albumVideo.setmContentType("video");
                        albumVideo.setmTitle(tittle);
                        albumVideo.setmDuration(duration);
                        videoItemByAlbum.add(albumVideo);

                    } while (mVideoCursor.moveToNext());
// cursorData.addAll(MediaUtils.extractMediaList(mVideoCursor,
// MediaType.PHOTO));
// mediaAdapter.notifyDataSetChanged();

                }
                mVideoCursor.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        adapter = new VideoFileAdapter(videoItemByAlbum, VedioListActivity.this);
//        vedioListRecylerViw.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//        adapter.setClickListener(this);
        return videoItemByAlbum;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public List getVideoAlbum(String sortOrder) {
        String[] PROJECTION_BUCKET_1 = {"DISTINCT " +
                MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Video.VideoColumns.BUCKET_ID,
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.DATE_ADDED};

        String BUCKET_GROUP_BY =
                "1) GROUP BY 1,(2";

        String BUCKET_ORDER_BY = MediaStore.Video.VideoColumns.DATE_ADDED + " DESC";
        if (sortOrder.equals("DATE")){
            BUCKET_ORDER_BY = MediaStore.Video.VideoColumns.DATE_ADDED + " DESC";
        }
        else if(sortOrder.equals("NAME A To Z")){
            BUCKET_ORDER_BY = MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME + " DESC";

        }
        else if(sortOrder.equals("NAME Z TO A"))
        {
            BUCKET_ORDER_BY = MediaStore.Video.VideoColumns.DATE_ADDED + " DESC";

        }

        Uri video = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        Cursor cur = this.getContentResolver().query(video, PROJECTION_BUCKET_1,
                BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);

        if (cur != null)
            if (cur.moveToFirst()) {
                String bucket;
                String date;
                String data;
                long bucketId;
                int count;

                int bucketColumn = cur
                        .getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME);

                int dateColumn = cur
                        .getColumnIndex(MediaStore.Video.VideoColumns.DATE_ADDED);
                int dataColumn = cur.getColumnIndex(MediaStore.Video.VideoColumns.DATA);


                int bucketIdColumn = cur
                        .getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_ID);
                // int bucketCountcolumn=cur.getColumnIndex(MediaStore.Video.VideoColumns.SIZE);

                do {
// Get the field valuesA

                    bucket = cur.getString(bucketColumn);
                    date = cur.getString(dateColumn);
                    Log.e(TAG, "getVideoByAlbum: Date" + date);
                    data = cur.getString(dataColumn);
                    bucketId = cur.getInt(bucketIdColumn);

                    if (bucket != null && bucket.length() > 0) {


/* videoItem.set_ID(cursor.getString(0));
videoItem.setARTIST(cursor.getString(1));
videoItem.setTITLE(cursor.getString(2));
videoItem.setDATA(cursor.getString(3));
videoItem.setDISPLAY_NAME(cursor.getString(4));
videoItem.setDURATION(cursor.getString(5));
*/
                        VideoItem videoItem = new VideoItem();
                        videoItem.set_ID(cur.getString(0));
                        videoItem.setDISPLAY_NAME(bucket);
                        videoItem.setDATA(data);
                        videoItem.setFILETYPE("folder");
                        videoItem.setBUCKET_ID(bucketId);
                        videoItem.setDATE_TAKEN(date);

//++++++++++++++++++++below code till the next line  is for finding the number of videos of a folder++++++++++++++++++
                        int sub_vid_count = 0;
                        try {
                            String orderBy = MediaStore.Images.Media.DATE_TAKEN;

//            String searchParams = null;
//            String bucket = bucketName;
//            searchParams = bucket;
                            String selection = MediaStore.Video.Media.DATA + " like?";
                            String[] selectionArgs = new String[]{"%" + bucket + "%"};
                            String[] PROJECTION_BUCKET_2 = {MediaStore.Video.VideoColumns._ID,
                                    MediaStore.Video.VideoColumns.DISPLAY_NAME,
                                    MediaStore.Video.VideoColumns.DATE_TAKEN,
                                    MediaStore.Video.VideoColumns.DATA,
                                    MediaStore.Video.VideoColumns.DURATION,
                                    MediaStore.Video.VideoColumns.TITLE};

                            Cursor mVideoCursor;
                            mVideoCursor = this.getContentResolver().query(
                                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, PROJECTION_BUCKET_2,
                                    selection, selectionArgs, orderBy, null);

                            if (mVideoCursor != null) {
                                if (mVideoCursor.moveToFirst()) {
                                    String _id;
                                    String date_2;
                                    String data_2;
                                    String displayName;
                                    int duration;
                                    String tittle;
                                    int idColumn = mVideoCursor
                                            .getColumnIndex(MediaStore.Video.Media._ID);
                                    int bucketColumn_2 = mVideoCursor
                                            .getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);


                                    int dateColumn_2 = mVideoCursor
                                            .getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);
                                    int dataColumn_2 = mVideoCursor.getColumnIndex(MediaStore.Video.Media.DATA);
                                    int durationColumn = mVideoCursor.getColumnIndex(MediaStore.Video.Media.DURATION);
                                    int tittleColumn = mVideoCursor.getColumnIndex(MediaStore.Video.Media.TITLE);
                                    do {
                                        date_2 = mVideoCursor.getString(dateColumn_2);
                                        data_2 = mVideoCursor.getString(dataColumn_2);
                                        displayName = mVideoCursor.getString(bucketColumn_2);
                                        duration = mVideoCursor.getInt(durationColumn);
                                        tittle = mVideoCursor.getString(tittleColumn);


                                        VedioFileModel albumVideo = new VedioFileModel();
                                        albumVideo.setmDisplayName(displayName);
                                        albumVideo.setmUrl_FilePath(data_2);
                                        albumVideo.setmContentType("video");
                                        albumVideo.setmTitle(tittle);
                                        albumVideo.setmDuration(duration);
                                        videoItemByAlbum.add(albumVideo);

                                        sub_vid_count++;
                                    } while (mVideoCursor.moveToNext());
// cursorData.addAll(MediaUtils.extractMediaList(mVideoCursor,
// MediaType.PHOTO));
// mediaAdapter.notifyDataSetChanged();

                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //-----------------------------------------------------------------------
                        videoItem.setVIDEO_COUNT(sub_vid_count);

                        vedioFolder.add(videoItem);

                    }


                } while (cur.moveToNext());
            }
        if (cur != null && !cur.isClosed()) {
            cur.close();
        }
        adapter = new VideoFolderAdapter(vedioFolder, MainActivity.this);
        gridAdapter = new GridAdapter(vedioFolder, MainActivity.this);
        mRecyclerView.setAdapter(adapter);
        gridView.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        adapter.setClickListener(this);


        return vedioFolder;
    }


//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//    public List getVideoAlbum() {
//        String[] PROJECTION_BUCKET = {"DISTINCT " +
//                MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
//                MediaStore.Video.VideoColumns.BUCKET_ID,
//                MediaStore.Video.VideoColumns.DATA,
//                MediaStore.Images.ImageColumns.DATE_TAKEN};
//
//        String BUCKET_ORDER_BY = "MAX(datetaken)DESC";
//        String BUCKET_GROUP_BY = "1)GROUP BY 1,(2";
//
//
//        Uri video = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//
//        Cursor cur = this.getContentResolver().query(video, PROJECTION_BUCKET,
//                BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);
//
//        if (cur != null)
//            if (cur.moveToFirst()) {
//                String bucket;
//                String date;
//                String data;
//                long bucketId;
//                int count;
//
//                int bucketColumn = cur
//                        .getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
//
//                int dateColumn = cur
//                        .getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
//                int dataColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);
//
//
//                int bucketIdColumn = cur
//                        .getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
//                // int bucketCountcolumn=cur.getColumnIndex(MediaStore.Video.VideoColumns.SIZE);
//
//                do {
//// Get the field valuesA
//
//                    bucket = cur.getString(bucketColumn);
//                    date = cur.getString(dateColumn);
//                    data = cur.getString(dataColumn);
//                    bucketId = cur.getInt(bucketIdColumn);
//
//                    if (bucket != null && bucket.length() > 0) {
//
//
///* videoItem.set_ID(cursor.getString(0));
//videoItem.setARTIST(cursor.getString(1));
//videoItem.setTITLE(cursor.getString(2));
//videoItem.setDATA(cursor.getString(3));
//videoItem.setDISPLAY_NAME(cursor.getString(4));
//videoItem.setDURATION(cursor.getString(5));
//*/
//                        VideoItem videoItem = new VideoItem();
//                        videoItem.set_ID(cur.getString(0));
//                        videoItem.setDISPLAY_NAME(bucket);
//                        videoItem.setDATA(data);
//                        videoItem.setFILETYPE("folder");
//                        videoItem.setBUCKET_ID(bucketId);
//                        videoItem.setDATE_TAKEN(date);
//                        sub_vid_count++;
//                        //videoItem.setVIDEO_COUNT(getVideoByAlbum(bucket).size());
//                        videoItem.setVIDEO_COUNT(sub_vid_count);
//
//                        vedioFolder.add(videoItem);
//
//                    }
//
//
//                } while (cur.moveToNext());
//            }
//        if (cur != null && !cur.isClosed()) {
//            cur.close();
//        }
//        adapter = new VideoFolderAdapter(vedioFolder, MainActivity.this);
//        mRecyclerView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//        adapter.setClickListener(this);
//
//
//        return vedioFolder;
//    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemClick(View view, int position) {
        String path = vedioFolder.get(position).getDISPLAY_NAME();
        Intent intent = new Intent(MainActivity.this, VedioListActivity.class);
        intent.putExtra("FolderPath", path);
        startActivity(intent);


    }

    @Override
    public void onItemLongClick(View view, int position) {

    }


    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted1");
                getVideoAlbum();


                type = "video";
                Log.d(TAG, "onCreate: " + adapter.getItemCount());
                int totalFoldersHave = vedioFolder.size();
                Log.d(TAG, "onCreate: " + totalFoldersHave);

                return true;
            } else {

                Log.v(TAG, "Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted1");
            return true;
        }
    }

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

            case 3:
                Log.d(TAG, "External storage1");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                    //SharePdfFile();
                    getVideoAlbum();


                    type = "video";
                    Log.d(TAG, "onCreate: " + adapter.getItemCount());
                    int totalFoldersHave = vedioFolder.size();
                    Log.d(TAG, "onCreate: " + totalFoldersHave);
                } else {
                    //progress.dismiss();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

        if (backcount == 1) {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            backcount = 2;
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_exit)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to close this activity?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();

        }

    }
}
