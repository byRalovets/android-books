package by.ralovets.booksapplication.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import by.ralovets.booksapplication.activity.LoginActivity;
import by.ralovets.booksapplication.activity.MainActivity;
import by.ralovets.booksapplication.databinding.FragmentSettingsBinding;
import by.ralovets.booksapplication.util.LocaleHelper;

public class SettingsFragment extends Fragment {

    private static String lang = "en";
    private FragmentSettingsBinding binding;
    Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        lang = sharedPreferences.getString("LANG", "en");

        if (lang.equals("ru")) {
            binding.languageSwitch.setChecked(true);
        }

        binding.logoutButton.setOnClickListener(l -> {
            FirebaseAuth.getInstance().signOut();
            Intent loginscreen = new Intent(requireActivity(), LoginActivity.class);
            requireActivity().finish();
            loginscreen.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(loginscreen);
        });

        binding.languageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lang = "ru";
                } else {
                    lang = "en";
                }
                editor.putString("LANG", lang);
                editor.apply();
                setAppLocale(requireActivity(), lang);
                requireActivity().recreate();
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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