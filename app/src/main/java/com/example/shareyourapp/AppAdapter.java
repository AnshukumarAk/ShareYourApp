package com.example.shareyourapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private final Context context;
    private List<AppModel> fullList = new ArrayList<>();
    private List<AppModel> filteredList = new ArrayList<>();

    public AppAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<AppModel> apps) {
        this.fullList = new ArrayList<>(apps);
        this.filteredList = new ArrayList<>(apps);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        String q = query == null ? "" : query.toLowerCase().trim();
        filteredList = new ArrayList<>();
        for (AppModel app : fullList) {
            if (app.name.toLowerCase().contains(q) || app.packageName.toLowerCase().contains(q)) {
                filteredList.add(app);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppModel app = filteredList.get(position);
        holder.appIcon.setImageDrawable(app.icon);
        holder.appName.setText(app.name);
        holder.appSize.setText(app.sizeLabel + "  •  " + app.installDate);
        holder.appPath.setText(app.packageName);
        holder.shareBtn.setOnClickListener(v -> shareApk(app));
    }

    /** Copy base.apk to a shareable location on a background thread, then share. */
    private void shareApk(AppModel app) {
        Toast.makeText(context, "Preparing " + app.name + "…", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            try {
                File src = new File(app.sourceDir);
                File dest = new File(context.getExternalFilesDir(null), safeName(app) + ".apk");

                try (InputStream in = new FileInputStream(src);
                     OutputStream out = new FileOutputStream(dest)) {
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
                }

                Uri uri = FileProvider.getUriForFile(
                        context, context.getPackageName() + ".provider", dest);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/vnd.android.package-archive");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.putExtra(Intent.EXTRA_SUBJECT, app.name);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                runOnUi(() -> context.startActivity(
                        Intent.createChooser(intent, "Share " + app.name).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUi(() -> Toast.makeText(context, "Failed to share APK", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private String safeName(AppModel app) {
        return app.name.replaceAll("[^a-zA-Z0-9._-]", "_") + "_" + app.packageName;
    }

    private void runOnUi(Runnable r) {
        if (context instanceof Activity) ((Activity) context).runOnUiThread(r);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName, appSize, appPath;
        MaterialButton shareBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            appSize = itemView.findViewById(R.id.app_size);
            appPath = itemView.findViewById(R.id.app_path);
            shareBtn = itemView.findViewById(R.id.share_button);
        }
    }
}
