package com.example.shareyourapp;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AppAdapter adapter;
    private ProgressBar progressBar;
    private TextView subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        SearchView searchView = findViewById(R.id.search_view);
        RecyclerView recyclerView = findViewById(R.id.app_list);
        progressBar = findViewById(R.id.progress);
        subtitle = findViewById(R.id.subtitle);

        styleSearchView(searchView);

        adapter = new AppAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { adapter.filter(query); return true; }
            @Override public boolean onQueryTextChange(String newText) { adapter.filter(newText); return true; }
        });

        loadApps();
    }

    /** Make the SearchView white with dark text (default plate is dark on M3). */
    private void styleSearchView(SearchView searchView) {
        searchView.setBackgroundColor(Color.WHITE);
        View plate = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        if (plate != null) plate.setBackgroundColor(Color.TRANSPARENT);

        EditText et = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (et != null) {
            et.setTextColor(Color.parseColor("#1A2733"));
            et.setHintTextColor(Color.parseColor("#8A97A0"));
        }
        ImageView icon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        if (icon != null) icon.setColorFilter(Color.parseColor("#607684"));
    }

    /** Scan installed apps on a background thread (keeps the UI responsive). */
    private void loadApps() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            PackageManager pm = getPackageManager();
            List<ApplicationInfo> all = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

            List<AppModel> models = new ArrayList<>();
            for (ApplicationInfo app : all) {
                boolean isUser = (app.flags & ApplicationInfo.FLAG_SYSTEM) == 0
                        || (app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
                if (!isUser) continue;

                File apk = new File(app.sourceDir);
                if (!apk.exists()) continue;

                models.add(new AppModel(
                        app.loadLabel(pm).toString(),
                        app.packageName,
                        app.sourceDir,
                        app.loadIcon(pm),
                        Formatter.formatShortFileSize(this, apk.length()),
                        fmt.format(new Date(apk.lastModified()))
                ));
            }

            Collections.sort(models, (a, b) -> a.name.compareToIgnoreCase(b.name));

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                adapter.setData(models);
                subtitle.setText(models.size() + " apps");
            });
        }).start();
    }
}
