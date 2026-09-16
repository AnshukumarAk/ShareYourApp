# Share Your App

A lightweight Android app to **view, search, and share any installed app's APK** through the system share sheet — send apps to friends over WhatsApp, Bluetooth, Nearby Share, email, and more. Clean Material design, no ads, no tracking.

---

## ✨ Features

- 📱 **Lists all installed apps** (user apps + updated system apps) with icon, name, size, install date, and package name
- 🔍 **Instant search** by app name or package name
- 📤 **One-tap share** — extracts the app's `base.apk` and shares it via the Android share sheet
- 🔤 **Sorted alphabetically** for easy browsing
- ⚡ **Smooth & responsive** — app scanning and APK copying run on background threads (no UI freeze / ANR, even for large apps)
- 🎨 **Material Design** — cards, app count header, loading indicator
- 🔒 **Private** — everything runs on-device; no internet permission, no data collection

---

## 📸 Screenshots

> _Add screenshots here_ (`screenshots/home.png`, `screenshots/search.png`)

| Home | Share |
|------|-------|
| _app list_ | _system share sheet_ |

---

## 🛠️ Tech Stack

- **Language:** Java
- **UI:** Android Views, RecyclerView, Material Components, CardView
- **APIs:** `PackageManager` (app discovery), `FileProvider` (secure APK sharing), `Intent.ACTION_SEND`
- **Min SDK:** 24 (Android 7.0) · **Target SDK:** 35 (Android 15)

---

## 🔐 Permissions

| Permission | Why it's needed |
|-----------|-----------------|
| `QUERY_ALL_PACKAGES` | To list the apps installed on the device |

The app requests **no** internet, location, or storage permissions. APKs are copied to the app's own private directory and shared through a secure `FileProvider` URI.

---

## ⚙️ How Sharing Works

1. You tap **Share** on an app.
2. The app copies that app's `base.apk` into its private files directory (`getExternalFilesDir`) on a background thread.
3. A secure content URI is generated via `FileProvider` and passed to the Android share sheet.
4. You pick any app (WhatsApp, Bluetooth, Nearby Share, Drive…) to send the APK.

---

## 🚀 Build & Run

```bash
git clone https://github.com/<your-username>/ShareYourApp.git
```

1. Open the project in **Android Studio**.
2. Let Gradle sync.
3. Connect a device (or emulator) and press **Run ▶**.

---

## 📄 Notes

- Only apps whose APK file is accessible are shown.
- `QUERY_ALL_PACKAGES` is a sensitive permission on Google Play; if you publish there, you must declare a valid use case (an app-sharing tool qualifies).

---

## 📝 License

Released under the **MIT License** — see [LICENSE](LICENSE).
