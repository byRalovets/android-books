package by.ralovets.booksapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import by.ralovets.booksapplication.R;
import by.ralovets.booksapplication.model.Upload;

public class EditVideoActivity extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;

    private Button chooseBtn;
    private Button uploadBtn;
    private VideoView videoView;
    private Uri videoUri;
    private MediaController mediaController;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private String videoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        chooseBtn = findViewById(R.id.choose_video);
        uploadBtn = findViewById(R.id.load_video);
        videoView = findViewById(R.id.videoView);

        mediaController = new MediaController(this);

        mStorageRef = FirebaseStorage.getInstance().getReference("videos");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("videos");

        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.start();

        chooseBtn.setOnClickListener(l -> {
            chooseVideo();
        });

        uploadBtn.setOnClickListener(l -> {
            uploadVideo();
        });

    }

    private void chooseVideo() {
        Intent i = new Intent();
        i.setType("video/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadVideo() {

        if (videoUri != null) {
            videoName = getIntent().getStringExtra("email") + "-" + System.currentTimeMillis();
            String videoPostfix = getFileExtension(videoUri);
            String s = videoName + "." + videoPostfix;
            StorageReference fileReference = mStorageRef.child(s);

            fileReference.putFile(videoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Upload member = new Upload(videoName, taskSnapshot.getUploadSessionUri().toString());
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();

                            //Log.d(TAG, "onSuccess: firebase download url: " + downloadUrl.toString()); //use if testing...don't need this line.
                            Upload upload = new Upload(videoName.trim(), downloadUrl.toString());

                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(e -> {
//                        Toast.makeText(EditProfileActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
                    });
        } else {
        }
    }
}