package in.avilaksh.vedioplayer;


import java.io.Serializable;

public class PopupItem implements Serializable {

    private String p;

    private String path;
    private String name;
    private String timeStart;
    private String timeEnd;
    private VideoItem xdVideo;


    public PopupItem(String path, String name) {
        this.path = path;
        this.name = name;
    }


    public PopupItem(String name, VideoItem xdVideo) {
        this.name = name;
        this.xdVideo = xdVideo;
    }


    public PopupItem(String p, String name, String timeStart, String timeEnd) {
        this.p = p;
        this.name = name;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }


}
