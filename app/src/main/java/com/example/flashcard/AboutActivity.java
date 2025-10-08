package com.example.flashcard;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutActivity extends BaseActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        linkButton(R.id.HomeAboutImageView, MainActivity.class);

        TextView versionTextView = findViewById(R.id.versionTextView);

        try {
            PackageManager pm = getPackageManager();

            String packageName = getPackageName();

            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);

            String versionName = packageInfo.versionName;

            versionTextView.setText("Version " + versionName);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}