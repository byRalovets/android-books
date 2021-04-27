package by.ralovets.booksapplication.fragments.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import by.ralovets.booksapplication.EditProfileActivity;
import by.ralovets.booksapplication.ImageModel;
import by.ralovets.booksapplication.R;

public class BooksGridAdapter extends BaseAdapter {

    Context context;
    Map<String, Object>[] items;
    LayoutInflater inflater;
    Set<View> viewSet = new HashSet<>();

    public BooksGridAdapter(Context context, Map<String, Object>[] items) {
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.book_grid_item, null);

        if (viewSet.contains(view)) {
            return view;
        } else {
            viewSet.add(view);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.book_image);

        ImageModel imageModel = ImagesNahui.userImages.get(items[i].get("email"));
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
                                Picasso.with(context).load(localFile).into(imageView);
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

        TextView titleView = (TextView) view.findViewById(R.id.book_title);
        TextView usernameView = (TextView) view.findViewById(R.id.book_username);
        TextView yearView = (TextView) view.findViewById(R.id.book_year);
        TextView pagesView = (TextView) view.findViewById(R.id.book_pages);

        String title = items[i].get("title") == null ? "" : Objects.requireNonNull(items[i].get("title")).toString();
        title = title.length() < 13 ? title : title.substring(0, 10).concat("...");

        String username = items[i].get("email") == null ? "" : Objects.requireNonNull(items[i].get("email")).toString();
        username = username.replaceAll("@.*", "");
        username = username.length() < 16 ? username : username.substring(0, 13).concat("...");

        String year = items[i].get("year") != null ? Objects.requireNonNull(items[i].get("year")).toString() : "N/A year";

        String pages = items[i].get("pages") != null ? Objects.requireNonNull(items[i].get("pages")).toString() : "N/A pages";

        titleView.setText(title);
        usernameView.setText(username);
        yearView.setText(year);
        pagesView.setText(pages);

        return view;
    }
}
