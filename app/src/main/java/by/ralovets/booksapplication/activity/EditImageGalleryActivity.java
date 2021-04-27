package by.ralovets.booksapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import by.ralovets.booksapplication.model.Upload;
import by.ralovets.booksapplication.databinding.ActivityEditImageGalleryBinding;

public class EditImageGalleryActivity extends AppCompatActivity {

    private ActivityEditImageGalleryBinding binding;
    private Uri mImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    String imageName;
    String imagePostfix;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditImageGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mStorageRef = FirebaseStorage.getInstance().getReference("gallery");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("gallery");

        binding.loadImage.setOnClickListener(l -> {
            openFileChooser();
        });

        binding.uploadImage.setOnClickListener(l -> {
            uploadFile();
        });

        binding.showImages.setOnClickListener(l -> {
            Intent i = new Intent(EditImageGalleryActivity.this, GalleryActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });

        email = getIntent().getStringExtra("email");
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).rotate(90).into(binding.loadedImage);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {

        if (mImageUri != null) {
            imageName = email + "-" + System.currentTimeMillis() + "";
            imagePostfix = getFileExtension(mImageUri);
            String s =  imageName + "." + imagePostfix;
            StorageReference fileReference = mStorageRef.child(s);

            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            String downloadUrl = mStorageRef.getDownloadUrl().toString();
//                            Upload upload = new Upload(s, downloadUrl);
//                            String uploadId = mDatabaseRef.push().getKey();

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();

                            //Log.d(TAG, "onSuccess: firebase download url: " + downloadUrl.toString()); //use if testing...don't need this line.
                            Upload upload = new Upload(imageName.trim(), downloadUrl.toString());

                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditImageGalleryActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
                    });
        } else {
        }
    }
}