package by.ralovets.booksapplication.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import by.ralovets.booksapplication.ImageModel;
import by.ralovets.booksapplication.ProfileActivity;
import by.ralovets.booksapplication.R;
import by.ralovets.booksapplication.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    GridView gridView;
    List<Map<String, Object>> books = new ArrayList<>();
    FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {

                            Map<String, ImageModel> userImages = new HashMap<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                books.add(document.getData());

                                ImageModel imageModel = new ImageModel();
                                imageModel.name = (String) document.get("imageName");
                                imageModel.postfix = document.getData().get("imagePostfix") + "";

                                if (imageModel.name != null) {
                                    userImages.put((String) document.get("email"), imageModel);
                                }
                            }

                            ImagesNahui.userImages = userImages;

                            gridView = binding.booksGridView;
                            BooksGridAdapter customAdapter = new BooksGridAdapter(getActivity().getApplicationContext(), books.toArray(new Map[0]));
                            gridView.setAdapter(customAdapter);

                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    Intent intent = new Intent(HomeFragment.this.getActivity(), ProfileActivity.class);

                                    intent.putExtra("image", books.get(position).get("image") != null ? Objects.requireNonNull(books.get(position).get("image")).toString() : "N/A");
                                    intent.putExtra("title", books.get(position).get("title") != null ? Objects.requireNonNull(books.get(position).get("title")).toString() : "N/A");
                                    intent.putExtra("username", books.get(position).get("email") != null ? Objects.requireNonNull(books.get(position).get("email")).toString() : "N/A");
                                    intent.putExtra("pages", books.get(position).get("pages") != null ? Objects.requireNonNull(books.get(position).get("pages")).toString() : "N/A");
                                    intent.putExtra("year", books.get(position).get("year") != null ? Objects.requireNonNull(books.get(position).get("year")).toString() : "N/A");
                                    intent.putExtra("description", books.get(position).get("description") != null ? Objects.requireNonNull(books.get(position).get("description")).toString() : "N/A");
                                    intent.putExtra("author", books.get(position).get("author") != null ? Objects.requireNonNull(books.get(position).get("author")).toString() : "N/A");

                                    startActivity(intent);
                                }
                            });
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}