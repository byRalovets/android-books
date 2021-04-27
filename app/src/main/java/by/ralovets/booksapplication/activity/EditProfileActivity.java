package by.ralovets.booksapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import by.ralovets.booksapplication.component.LockableScrollView;
import by.ralovets.booksapplication.R;
import by.ralovets.booksapplication.model.Upload;
import by.ralovets.booksapplication.databinding.ActivityEditProfileBinding;

public class EditProfileActivity extends AppCompatActivity {

    ActivityEditProfileBinding binding;
    FirebaseFirestore db;
    private final static String TAG = "EditProfileActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private StorageReference mStorageRef;
    private DatabaseReference mDAtabaseRef;

    String imageName;
    String imagePostfix;
    String longitude;
    String latitude;
    EditText title;
    EditText username;
    EditText pages;
    EditText year;
    EditText description;
    EditText author;
    LockableScrollView lockableScrollView;

    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        lockableScrollView = binding.lockableScroll;

        binding.descriptionLabel2.setOnClickListener(l -> {
            lockableScrollView.setScrollingEnabled(false);
        });

        binding.dataPanel.setOnClickListener(l -> {
            lockableScrollView.setScrollingEnabled(true);
        });

        mStorageRef = FirebaseStorage.getInstance().getReference("avatars");
        mDAtabaseRef = FirebaseDatabase.getInstance().getReference("avatars");

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map_in_profile);

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        imageName = (String) document.getData().get("imageName");
                        imagePostfix = (String) document.getData().get("imagePostfix");
                        binding.bookTitle.setText((String) document.getData().get("title"));
                        binding.username.setText((String) document.getData().get("email"));
                        binding.pages.setText((String) document.getData().get("pages"));
                        binding.year.setText((String) document.getData().get("year"));
                        binding.description.setText((String) document.getData().get("description"));
                        binding.author.setText((String) document.getData().get("author"));
                        latitude = (String) document.getData().get("latitude");
                        longitude = (String) document.getData().get("longitude");

                        supportMapFragment.getMapAsync(googleMap -> {
                            if (latitude != null && longitude != null) {
                                MarkerOptions markerOptions = new MarkerOptions();

                                LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                                markerOptions.position(latLng);
                                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                                googleMap.clear();

                                EditProfileActivity.this.latitude = latLng.latitude + "";
                                EditProfileActivity.this.longitude = latLng.longitude + "";
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                                googleMap.addMarker(markerOptions);
                            }
                        });

                        if (document.getData().get("imageName") != null) {
                            StorageReference mRef = mStorageRef.child(imageName + "." + imagePostfix);
                            try {
                                final File localFile = File.createTempFile(imageName, imagePostfix);
                                mRef.getFile(localFile)
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Picasso.with(EditProfileActivity.this).load(localFile).into(binding.bookImage);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(Exception e) {
                                                Toast.makeText(EditProfileActivity.this, "Error", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        binding.imageGalleryButon.setOnClickListener(l -> {
            Intent intent = new Intent(EditProfileActivity.this, EditImageGalleryActivity.class);

            intent.putExtra("email", binding.username.getText().toString());

            startActivity(intent);
        });

        binding.videosButton.setOnClickListener(l -> {
            Intent intent = new Intent(EditProfileActivity.this, EditVideoActivity.class);

            intent.putExtra("email", binding.username.getText().toString());

            startActivity(intent);
        });

        binding.saveProfileButton.setOnClickListener(l -> {
            DocumentReference documentReference = db.collection("users")
                    .document(FirebaseAuth.getInstance().getUid());

            uploadFile();

            Map<String, Object> userDocument = new HashMap<>();
            userDocument.put("title", binding.bookTitle.getText().toString());
            userDocument.put("year", binding.year.getText().toString());
            userDocument.put("email", binding.username.getText().toString());
            userDocument.put("pages", binding.pages.getText().toString());
            userDocument.put("description", binding.description.getText().toString());
            userDocument.put("author", binding.author.getText().toString());
            userDocument.put("imageName", imageName);
            userDocument.put("imagePostfix", imagePostfix);
            if (latitude != null && longitude != null) {
                userDocument.put("latitude", latitude);
                userDocument.put("longitude", longitude);
            }
            startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
            documentReference.set(userDocument)
                    .addOnSuccessListener(s -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                    .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
            finish();
        });

        binding.bookImage.setOnClickListener(v -> {
            openFileChooser();
        });

        supportMapFragment.getMapAsync(googleMap -> {
            googleMap.setOnMapClickListener(latLng -> {
                        MarkerOptions markerOptions = new MarkerOptions();

                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                        googleMap.clear();

                        EditProfileActivity.this.latitude = latLng.latitude + "";
                        EditProfileActivity.this.longitude = latLng.longitude + "";
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                        googleMap.addMarker(markerOptions);
                    }
            );
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
        finish();
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

            Picasso.with(this).load(mImageUri).rotate(90).into(binding.bookImage);

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {

        if (mImageUri != null) {
            imageName = System.currentTimeMillis() + "";
            imagePostfix = getFileExtension(mImageUri);
            String s = imageName + "." + imagePostfix;
            StorageReference fileReference = mStorageRef.child(s);

            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadUrl = mStorageRef.getDownloadUrl().toString();
                            Upload upload = new Upload(s, downloadUrl);
                            String uploadId = mDAtabaseRef.push().getKey();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfileActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
                    });
        } else {
        }
    }
}