package com.example.shareyourapp;

import android.graphics.drawable.Drawable;

/**
 * Pre-computed app info so the adapter never touches PackageManager on the UI
 * thread (loadLabel/loadIcon are expensive and caused scroll jank).
 */
public class AppModel {
    public final String name;
    public final String packageName;
    public final String sourceDir;
    public final Drawable icon;
    public final String sizeLabel;      // e.g. "45 MB"
    public final String installDate;    // e.g. "12 Apr 2025"

    public AppModel(String name, String packageName, String sourceDir,
                    Drawable icon, String sizeLabel, String installDate) {
        this.name = name;
        this.packageName = packageName;
        this.sourceDir = sourceDir;
        this.icon = icon;
        this.sizeLabel = sizeLabel;
        this.installDate = installDate;
    }
}
