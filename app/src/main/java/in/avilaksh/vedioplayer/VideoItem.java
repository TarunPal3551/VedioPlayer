package in.avilaksh.vedioplayer;

public class VideoItem {
    String _ID;
    String DATA;
    String DISPLAY_NAME;
    String FILETYPE;
    long BUCKET_ID;
    String DATE_TAKEN;
    int VIDEO_COUNT;

    public int getVIDEO_COUNT() {
        return VIDEO_COUNT;
    }

    public void setVIDEO_COUNT(int VIDEO_COUNT) {
        this.VIDEO_COUNT = VIDEO_COUNT;
    }

    public long getBUCKET_ID() {
        return BUCKET_ID;
    }

    public void setBUCKET_ID(long BUCKET_ID) {
        this.BUCKET_ID = BUCKET_ID;
    }

    public String getDATE_TAKEN() {
        return DATE_TAKEN;
    }

    public void setDATE_TAKEN(String DATE_TAKEN) {
        this.DATE_TAKEN = DATE_TAKEN;
    }

    public VideoItem() {
    }


    public VideoItem(String _ID, String DATA, String DISPLAY_NAME, String FILETYPE, long BUCKET_ID, String DATE_TAKEN, int VIDEO_COUNT) {
        this._ID = _ID;
        this.DATA = DATA;
        this.DISPLAY_NAME = DISPLAY_NAME;
        this.FILETYPE = FILETYPE;
        this.BUCKET_ID = BUCKET_ID;
        this.DATE_TAKEN = DATE_TAKEN;
        this.VIDEO_COUNT = VIDEO_COUNT;
    }

    public String getFILETYPE() {
        return FILETYPE;
    }

    public void setFILETYPE(String FILETYPE) {
        this.FILETYPE = FILETYPE;
    }


    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getDATA() {
        return DATA;
    }

    public void setDATA(String DATA) {
        this.DATA = DATA;
    }

    public String getDISPLAY_NAME() {
        return DISPLAY_NAME;
    }

    public void setDISPLAY_NAME(String DISPLAY_NAME) {
        this.DISPLAY_NAME = DISPLAY_NAME;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoItem)) return false;

        VideoItem videoItem = (VideoItem) o;

        if (getBUCKET_ID() != videoItem.getBUCKET_ID()) return false;
        if (get_ID() != null ? !get_ID().equals(videoItem.get_ID()) : videoItem.get_ID() != null)
            return false;
        if (getDATA() != null ? !getDATA().equals(videoItem.getDATA()) : videoItem.getDATA() != null)
            return false;
        if (getDISPLAY_NAME() != null ? !getDISPLAY_NAME().equals(videoItem.getDISPLAY_NAME()) : videoItem.getDISPLAY_NAME() != null)
            return false;
        if (getFILETYPE() != null ? !getFILETYPE().equals(videoItem.getFILETYPE()) : videoItem.getFILETYPE() != null)
            return false;
        return getDATE_TAKEN() != null ? getDATE_TAKEN().equals(videoItem.getDATE_TAKEN()) : videoItem.getDATE_TAKEN() == null;
    }

    @Override
    public int hashCode() {
        int result = get_ID() != null ? get_ID().hashCode() : 0;
        result = 31 * result + (getDATA() != null ? getDATA().hashCode() : 0);
        result = 31 * result + (getDISPLAY_NAME() != null ? getDISPLAY_NAME().hashCode() : 0);
        result = 31 * result + (getFILETYPE() != null ? getFILETYPE().hashCode() : 0);
        result = 31 * result + (int) (getBUCKET_ID() ^ (getBUCKET_ID() >>> 32));
        result = 31 * result + (getDATE_TAKEN() != null ? getDATE_TAKEN().hashCode() : 0);
        return result;
    }
}
