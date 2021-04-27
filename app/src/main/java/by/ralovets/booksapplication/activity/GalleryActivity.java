package by.ralovets.booksapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import by.ralovets.booksapplication.adapter.ImageAdapter;
import by.ralovets.booksapplication.R;
import by.ralovets.booksapplication.model.Upload;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;

    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("gallery");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);

                    if (upload.getmName().startsWith(getIntent().getStringExtra("email"))) {
                        upload.setmName(upload.getmName() + ".jpg");
                        mUploads.add(upload);
                    }
                }

                mAdapter = new ImageAdapter(GalleryActivity.this, mUploads);

                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}