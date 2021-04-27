package by.ralovets.booksapplication;

public class VideoUpload {

    private String mName;
    private String mVideoUploadUri;

    public VideoUpload() {
    }

    public VideoUpload(String mName, String mVideoUploadUri) {
        this.mName = mName;
        this.mVideoUploadUri = mVideoUploadUri;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmVideoUploadUri() {
        return mVideoUploadUri;
    }

    public void setmVideoUploadUri(String mVideoUploadUri) {
        this.mVideoUploadUri = mVideoUploadUri;
    }
}
