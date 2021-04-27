package by.ralovets.booksapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import by.ralovets.booksapplication.model.ImageModel;
import by.ralovets.booksapplication.databinding.ActivityProfileBinding;
import by.ralovets.booksapplication.util.ImagesNahui;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    FirebaseFirestore db;
    private final static String TAG = "ProfileActivity";

    String imagePath;
//    EditText title;
//    EditText username;
//    EditText pages;
//    EditText year;
//    EditText description;
//    EditText author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle arguments = getIntent().getExtras();

        if (!arguments.isEmpty()) {
            imagePath = arguments.getString("image");
            binding.bookTitle.setText(arguments.getString("title"));
            binding.username.setText(arguments.getString("username"));
            binding.pages.setText(arguments.getString("pages"));
            binding.year.setText(arguments.getString("year"));
            binding.description.setText(arguments.getString("description"));
            binding.author.setText(arguments.getString("author"));

            ImageModel imageModel = ImagesNahui.userImages.get(arguments.getString("username"));
            if (imageModel != null) {
                StorageReference mStorageRef;
                mStorageRef = FirebaseStorage.getInstance().getReference("avatars");
                FirebaseFirestore db;
                db = FirebaseFirestore.getInstance();
                StorageReference mRef = mStorageRef.child(imageModel.name + "." + imageModel.postfix);
                try {
                    final File localFile = File.createTempFile(imageModel.name, imageModel.postfix);
                    mRef.getFile(localFile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Picasso.with(ProfileActivity.this).load(localFile).into(binding.bookImage);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        binding.viewGallery.setOnClickListener(l -> {
            Intent i = new Intent(ProfileActivity.this, GalleryActivity.class);
            i.putExtra("email", arguments.getString("username"));
            startActivity(i);
        });

        binding.video.setOnClickListener(l -> {
            Intent i = new Intent(ProfileActivity.this, VideoGalleryActivity.class);
            i.putExtra("email", arguments.getString("username"));
            startActivity(i);
        });
    }
}