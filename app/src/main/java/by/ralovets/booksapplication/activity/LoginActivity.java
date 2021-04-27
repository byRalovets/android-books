package by.ralovets.booksapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import static java.util.Objects.nonNull;

import by.ralovets.booksapplication.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    EditText email;
    EditText password;
    Button loginButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        String lang = sharedPreferences.getString("LANG", "en");
        setAppLocale(this, lang);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        email = binding.email;
        password = binding.password;
        loginButton = binding.loginButton;
        progressBar = binding.progressBar;

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        binding.loginButton.setOnClickListener(v -> {
            String email = LoginActivity.this.email.getText().toString();
            String password = LoginActivity.this.password.getText().toString();

            if (TextUtils.isEmpty(email)) {
                LoginActivity.this.email.setError("Email is required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                LoginActivity.this.password.setError("Password is required.");
                return;
            }

            if (password.length() < 6) {
                LoginActivity.this.password.setError("Password must be >=6 characters.");
                return;
            }

            LoginActivity.this.progressBar.setVisibility(View.VISIBLE);

            signIn(binding.email.getText().toString(), binding.password.getText().toString());
        });

        binding.dontHaveAccountLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (nonNull(currentUser)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail: success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(LoginActivity.this, user.toString(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Log.w(TAG, "signInWithEmail: failure", task.getException());
                        if (task.getException() != null) {
                            Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
    }

    private void setAppLocale(Activity activity, String localeCode){

        Locale locale = new Locale(localeCode);
        locale.setDefault(locale);

        Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

/*        Resources resources = ChangeLanguageActivity.this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);*/

/*        Locale locale = new Locale(localeCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        ChangeLanguageActivity.this.getApplicationContext().getResources().updateConfiguration(config, null);*/
    }
}