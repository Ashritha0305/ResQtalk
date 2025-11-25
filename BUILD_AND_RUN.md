# 🎯 How to Build & Run in Android Studio (NOT Terminal)

## ⚠️ Important
Building via terminal requires Java setup. **Android Studio handles this automatically!**

---

## ✅ Use Android Studio Instead (MUCH EASIER)

### Step 1: Open Android Studio
- Double-click Android Studio icon on your desktop
- Wait for it to load your project

### Step 2: Sync Gradle
1. At the top, click: **File**
2. Click: **Sync Now**
3. Wait for message at bottom: `"Gradle sync finished successfully"`
4. ⏱️ This may take 2-5 minutes

### Step 3: Build the App
1. Click: **Build** → **Make Project**
2. Wait for build to complete
3. You'll see: `"Build: Build Successful"` at bottom

### Step 4: Run the App
1. Click: **Run** → **Run 'app'**
2. Select your device/emulator from the list
3. Click **OK**
4. App will build and launch! 🚀

---

## 📱 If No Device Appears

### Option A: Use Emulator
1. Click: **Tools** → **Device Manager**
2. Click **+ Create Device**
3. Select **Pixel 4** (or any phone)
4. Click **Next** → Select **API 33+** → **Finish**
5. Click the **▶️ Play button** to start emulator
6. Wait 2-3 minutes for it to boot
7. Then click **Run 'app'** again

### Option B: Use Real Phone
1. Enable USB Debugging on phone (Settings → Developer options)
2. Connect phone with USB cable
3. Tap "Allow" on phone when prompted
4. Device appears in Android Studio
5. Click **Run 'app'**

---

## 🎉 Expected Result

When you click Run:
1. ✅ Android Studio shows "Building Variant"
2. ✅ APK is created
3. ✅ App installs on device
4. ✅ ResQtalk launches with map screen!

---

## 🆘 If Build Fails

### In Android Studio:
1. Look at the **Build Output** tab at bottom
2. Look for RED text showing the error
3. Common fixes:
   - **Sync Not Complete**: File → Sync Now again
   - **API Key Error**: Check AndroidManifest.xml
   - **Map Fragment Error**: Already fixed!

---

## ❌ Don't Use Terminal

The terminal approach requires:
- ❌ Java setup (complex)
- ❌ Environment variables
- ❌ Manual configurations

**Android Studio does all this automatically!** ✅

---

## 🚀 Quick Summary

| Action | Where |
|--------|-------|
| Build | Android Studio → Build → Make Project |
| Run | Android Studio → Run → Run 'app' |
| Sync | Android Studio → File → Sync Now |
| Emulator | Android Studio → Tools → Device Manager |

---

## 📝 Step-by-Step (Super Simple)

1. Open **Android Studio**
2. Wait for project to load
3. Click **File** → **Sync Now** (wait)
4. Click **Run** → **Run 'app'** (wait)
5. Select device
6. Click **OK**
7. **✅ App launches!**

---

**Done! This is the official way to build Android apps.** 🎉

---

# 📦 APK Location

Your APK is located at:
```
C:\Users\Ashritha\AndroidStudioProjects\ResQtalk\app\build\outputs\apk\debug\app-debug.apk
```

