package by.ralovets.booksapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import by.ralovets.booksapplication.databinding.ActivityRegistrationBinding;

import static java.util.Objects.nonNull;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";
    private ActivityRegistrationBinding binding;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    EditText title;
    EditText year;
    EditText email;
    EditText password;
    Button signupButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        title = binding.title;
        year = binding.year;
        email = binding.email;
        password = binding.password;
        signupButton = binding.signupButton;
        progressBar = binding.signupProgressbar;

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            finish();
        }

        signupButton.setOnClickListener(l -> {
            String email = RegistrationActivity.this.email.getText().toString();
            String password = RegistrationActivity.this.password.getText().toString();
            String title = RegistrationActivity.this.title.getText().toString();
            String year = RegistrationActivity.this.year.getText().toString();

            if (TextUtils.isEmpty(email)) {
                RegistrationActivity.this.email.setError("Email is required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                RegistrationActivity.this.password.setError("Password is required.");
                return;
            }

            if (password.length() < 6) {
                RegistrationActivity.this.password.setError("Password must be >=6 characters.");
                return;
            }

            RegistrationActivity.this.progressBar.setVisibility(View.VISIBLE);

            Log.d(TAG, "createAccount: " + email);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail: success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                DocumentReference documentReference = db.collection("users")
                                        .document(user.getUid());

                                Map<String, Object> userDocument = new HashMap<>();
                                userDocument.put("title", title);
                                userDocument.put("year", year);
                                userDocument.put("email", email);

                                documentReference.set(userDocument)
                                        .addOnSuccessListener(l -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                                        .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));

                                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Log.w(TAG, "createUserWithEmail: error", task.getException());
                                if (task.getException() != null) {
                                    Toast.makeText(RegistrationActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        });

        binding.alreadyHaveAccountLink.setOnClickListener(l -> {
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (nonNull(currentUser)) {
            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            finish();
        }
    }
}