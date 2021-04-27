package by.ralovets.booksapplication.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import by.ralovets.booksapplication.util.ImagesCache;
import by.ralovets.booksapplication.model.ImageModel;
import by.ralovets.booksapplication.R;

public class BooksGridAdapter extends BaseAdapter {

    Context context;
    Map<String, Object>[] items;
    LayoutInflater inflater;
    static Set<View> viewSet = new HashSet<>();

    public BooksGridAdapter(Context context, Map<String, Object>[] items) {
        viewSet.clear();
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

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.book_grid_item, null);

        if (viewSet.contains(view)) {
            return view;
        } else {
            viewSet.add(view);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.book_image);

        ImageModel imageModel = ImagesCache.userImages.get(items[i].get("email"));
        if (imageModel != null) {
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("avatars");
            StorageReference mRef = mStorageRef.child(imageModel.name + "." + imageModel.postfix);
            try {
                final File localFile = File.createTempFile(imageModel.name, imageModel.postfix);
                mRef.getFile(localFile)
                        .addOnSuccessListener(taskSnapshot -> Picasso.with(context).load(localFile).into(imageView));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        TextView titleView = (TextView) view.findViewById(R.id.book_title);
        TextView usernameView = (TextView) view.findViewById(R.id.book_username);
        TextView yearView = (TextView) view.findViewById(R.id.book_year);
        TextView pagesView = (TextView) view.findViewById(R.id.book_pages);

        String title = Objects.requireNonNullElse(items[i].get("title"), "N/A").toString();
        title = title.length() < 13 ? title : title.substring(0, 10).concat("...");

        String username = Objects.requireNonNullElse(items[i].get("email"), "N/A").toString();
        username = username.replaceAll("@.*", "");
        username = username.length() < 16 ? username : username.substring(0, 13).concat("...");

        String year = Objects.requireNonNullElse(items[i].get("year"), "N/A").toString();
        String pages = Objects.requireNonNullElse(items[i].get("pages"), "N/A").toString();

        titleView.setText(title);
        usernameView.setText(username);
        yearView.setText(year);
        pagesView.setText(pages);

        return view;
    }
}
