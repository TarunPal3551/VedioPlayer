package in.avilaksh.vedioplayer;

import android.os.Parcel;
import android.os.Parcelable;

public class VedioFileModel implements Parcelable {
    private String mTitle;
    private String mSubTitle;
    private String mUrl_FilePath;
    private String mContentType;
    private int mDuration;
    private String mDisplayName;
    private String size;
    private String resolution;
    private String dateTaken;


    public VedioFileModel(Parcel in) {
        mTitle = in.readString();
        mSubTitle = in.readString();
        mUrl_FilePath = in.readString();
        mContentType = in.readString();
        mDuration = in.readInt();
        mDisplayName = in.readString();
        size = in.readString();
        resolution = in.readString();
        dateTaken = in.readString();
    }


    public static final Creator<VedioFileModel> CREATOR = new Creator<VedioFileModel>() {
        @Override
        public VedioFileModel createFromParcel(Parcel in) {
            return new VedioFileModel(in);
        }

        @Override
        public VedioFileModel[] newArray(int size) {
            return new VedioFileModel[size];
        }
    };

    public VedioFileModel() {

    }

    public String getmDisplayName() {
        return mDisplayName;
    }

    public void setmDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }

    public VedioFileModel(String mTitle, String mSubTitle, String mUrl_FilePath, String mContentType, int mDuration, String mDisplayName, String size, String resolution, String dateTaken) {
        this.mTitle = mTitle;
        this.mSubTitle = mSubTitle;
        this.mUrl_FilePath = mUrl_FilePath;
        this.mContentType = mContentType;
        this.mDuration = mDuration;
        this.mDisplayName = mDisplayName;
        this.size = size;
        this.resolution = resolution;
        this.dateTaken = dateTaken;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmSubTitle() {
        return mSubTitle;
    }

    public void setmSubTitle(String mSubTitle) {
        this.mSubTitle = mSubTitle;
    }

    public String getmUrl_FilePath() {
        return mUrl_FilePath;
    }

    public void setmUrl_FilePath(String mUrl_FilePath) {
        this.mUrl_FilePath = mUrl_FilePath;
    }

    public String getmContentType() {
        return mContentType;
    }

    public void setmContentType(String mContentType) {
        this.mContentType = mContentType;
    }

    public int getmDuration() {
        return mDuration;
    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public static Creator<VedioFileModel> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }
//

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDisplayName);
        dest.writeString(mSubTitle);
        dest.writeString(mUrl_FilePath);
        dest.writeString(mContentType);
        dest.writeInt(mDuration);
        dest.writeString(mTitle);
        dest.writeString(size);
        dest.writeString(resolution);
        dest.writeString(dateTaken);

    }
}
