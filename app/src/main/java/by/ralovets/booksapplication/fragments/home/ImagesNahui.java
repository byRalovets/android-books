package by.ralovets.booksapplication.fragments.home;

import android.content.Context;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import by.ralovets.booksapplication.ImageModel;

public class ImagesNahui {
    public static Map<String, ImageModel> userImages = new HashMap<>();
}
