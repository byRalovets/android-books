package by.ralovets.booksapplication;

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

public class VideoGalleryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private VideoAdapter mAdapter;

    FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_gallery);

        mRecyclerView = findViewById(R.id.recycler_view_video);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("videos");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload member = postSnapshot.getValue(Upload.class);

                    if (member.getmName().startsWith(getIntent().getStringExtra("email"))) {
                        member.setmName(member.getmName() + ".mp4");
                        mUploads.add(member);
                    }
                }

                mAdapter = new VideoAdapter(VideoGalleryActivity.this, mUploads);

                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        
    }
}