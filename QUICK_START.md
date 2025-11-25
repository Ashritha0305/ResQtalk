# ⚡ Quick Start: Running ResQtalk (5 Minutes)

## 📋 Absolute Minimum Steps

### ✅ Step 1: Get Google Maps API Key (3 min)

```
1. Go to: https://console.cloud.google.com/
2. Create project or select existing
3. Search: "Maps SDK for Android"
4. Click ENABLE
5. Go to Credentials → + CREATE CREDENTIALS → API Key
6. Copy your key (looks like: AIzaSy...)
```

### ✅ Step 2: Add API Key to Manifest (30 sec)

Open: `app/src/main/AndroidManifest.xml`

Find this line (around line 88):
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_GOOGLE_MAPS_API_KEY" />
```

Replace `YOUR_GOOGLE_MAPS_API_KEY` with your actual key.

### ✅ Step 3: Sync Gradle (2 min)

In Android Studio:
- Click: **File** → **Sync Now**
- Wait for: "Gradle sync finished"

### ✅ Step 4: Run App (1 min)

1. At top toolbar, select device (emulator or phone)
2. Click **▶️ Run** button (green play icon)
3. App builds and launches!

---

## 🎯 That's It!

**Total time: 5-10 minutes**

The app will:
- ✅ Build automatically
- ✅ Install on your device/emulator
- ✅ Launch with home screen
- ✅ Show Google Map
- ✅ Ask for permissions (tap Allow)

---

## 📱 Device Options

### Option A: Android Emulator (Easiest)
- Tools → Device Manager
- Create device or use existing
- Click ▶️ to start
- Leave running while developing

### Option B: Physical Phone
- Enable: Settings → Developer Mode → USB Debugging
- Connect USB cable
- Trust the device
- Android Studio sees it in Device Manager

---

## 🧪 First Test After Launch

1. **Contacts** → Add Test Contact → Save
2. **Settings** → (Optional) Edit SOS message → Save
3. **Voice** → (Optional) Edit trigger word → Test
4. **SOS Button** → Tap red button at bottom
5. Check your SMS - should get alert with location link!

---

## 🆘 If Something Doesn't Work

| Problem | Solution |
|---------|----------|
| "Gradle sync failed" | `./gradlew --stop` then sync again |
| "API key error" | Check key is correct in AndroidManifest.xml |
| "Map not showing" | Wait 5 min after enabling API, then rebuild |
| "Device not found" | Restart Android Studio or emulator |
| "App crashes" | Check logcat for error (bottom of Android Studio) |
| "Permission denied" | Tap "Allow" when app asks for permission |

---

## ✨ Key Files

| File | Purpose |
|------|---------|
| `app/src/main/AndroidManifest.xml` | Add your API key here |
| `build.gradle.kts` | Already has all dependencies |
| `README.md` | Full project overview |
| `TROUBLESHOOTING.md` | Detailed problem solutions |

---

## 🚀 Ready?

1. ✅ Get API key
2. ✅ Add to AndroidManifest.xml
3. ✅ Sync Gradle
4. ✅ Click Run!

**Let's build ResQtalk! 🎉**

---

**For detailed help, see:** `BEFORE_RUNNING.md`
