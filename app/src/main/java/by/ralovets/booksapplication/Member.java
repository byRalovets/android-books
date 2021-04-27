package by.ralovets.booksapplication;

public class Member {

    private String videoName;
    private String videoUri;

    public Member(String videoName, String videoUri) {
        this.videoName = videoName;
        this.videoUri = videoUri;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }
}
