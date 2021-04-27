package by.ralovets.booksapplication.fragments.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import by.ralovets.booksapplication.ImageModel;
import by.ralovets.booksapplication.MainActivity;
import by.ralovets.booksapplication.ProfileActivity;
import by.ralovets.booksapplication.R;
import by.ralovets.booksapplication.databinding.FragmentMapBinding;
import by.ralovets.booksapplication.fragments.home.BooksGridAdapter;
import by.ralovets.booksapplication.fragments.home.HomeFragment;
import by.ralovets.booksapplication.fragments.home.ImagesNahui;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    List<Map<String, Object>> books = new ArrayList<>();
    String clickedMarker;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            supportMapFragment.getMapAsync(googleMap -> {

                                Map<String, QueryDocumentSnapshot> docs = new HashMap<>();

                                googleMap.setOnMapClickListener(l -> {
                                    clickedMarker = null;
                                });

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    MarkerOptions markerOptions = new MarkerOptions();

                                    String latitude = (String) document.getData().get("latitude");
                                    String longitude = (String) document.getData().get("longitude");

                                    if (latitude != null && longitude != null) {
                                        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                                        markerOptions.position(latLng);
                                        markerOptions.title((String) document.getData().get("email"));
                                        docs.put((String) document.getData().get("email"), document);

                                        googleMap.addMarker(markerOptions);
                                    }
                                }

                                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        String title = marker.getTitle();

                                        if (clickedMarker == null || !clickedMarker.equals(title)) {
                                            clickedMarker = title;
                                        } else {
                                            Intent intent = new Intent(getActivity(), ProfileActivity.class);

                                            intent.putExtra("image", docs.get(title).getData().get("image") != null ? Objects.requireNonNull(docs.get(title).getData().get("image")).toString() : "N/A");
                                            intent.putExtra("title", docs.get(title).getData().get("title") != null ? Objects.requireNonNull(docs.get(title).getData().get("title")).toString() : "N/A");
                                            intent.putExtra("username", docs.get(title).getData().get("email") != null ? Objects.requireNonNull(docs.get(title).getData().get("email")).toString() : "N/A");
                                            intent.putExtra("pages", docs.get(title).getData().get("pages") != null ? Objects.requireNonNull(docs.get(title).getData().get("pages")).toString() : "N/A");
                                            intent.putExtra("year", docs.get(title).getData().get("year") != null ? Objects.requireNonNull(docs.get(title).getData().get("year")).toString() : "N/A");
                                            intent.putExtra("description", docs.get(title).getData().get("description") != null ? Objects.requireNonNull(docs.get(title).getData().get("description")).toString() : "N/A");
                                            intent.putExtra("author", docs.get(title).getData().get("author") != null ? Objects.requireNonNull(docs.get(title).getData().get("author")).toString() : "N/A");

                                            clickedMarker = null;
                                            startActivity(intent);
                                        }

                                        return false;
                                    }
                                });
                            });
                        }
                    }
                });

        return v;
    }


}