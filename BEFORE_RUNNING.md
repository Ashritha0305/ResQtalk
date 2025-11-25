# 🚀 Before Running ResQtalk in Android Studio

## ⚠️ IMPORTANT: 3 STEPS BEFORE YOU RUN

### Step 1: Get Google Maps API Key (REQUIRED) ⚙️

**Why?** Without this, the map won't load and the app will crash.

#### Get API Key
1. Go to: **https://console.cloud.google.com/**
2. Sign in with your Google account
3. Create a new project or select existing project
4. Search for **"Maps SDK for Android"** in the search bar
5. Click on it and press **ENABLE**
6. Go to **Credentials** (left sidebar)
7. Click **+ CREATE CREDENTIALS** → **API Key**
8. Copy the API key (looks like: `AIzaSy...`)

#### Add to AndroidManifest.xml
1. Open: `app/src/main/AndroidManifest.xml`
2. Find this line (around line 40):
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_GOOGLE_MAPS_API_KEY" />
```
3. Replace `YOUR_GOOGLE_MAPS_API_KEY` with your actual API key
4. Save the file

**Example:**
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="AIzaSyDxHHfJtzaLLXLdDJkjEwY-OPZ5YKjGdLE" />
```

---

### Step 2: Sync Gradle 📦

This downloads all dependencies (Room, Coroutines, Google Play Services, etc.)

#### Option A: Android Studio UI (Easiest)
1. At the top, click: **File** → **Sync Now**
2. Wait for "Gradle sync finished" message at bottom
3. ⏱️ Takes 2-5 minutes first time

#### Option B: Terminal Command
```powershell
# Open PowerShell in project root and run:
./gradlew sync
```

**What to expect:**
- ✅ "Gradle sync finished successfully"
- ✅ Gradle build tools downloaded
- ✅ All dependencies resolved
- ⏱️ First time: 2-5 minutes
- ⏱️ Subsequent: 30 seconds

**If sync fails:**
- Check internet connection
- Clear Gradle cache: `./gradlew --stop`
- Try again

---

### Step 3: Setup Android Emulator or Device 📱

Choose ONE option below:

#### Option A: Use Android Emulator (Recommended for First Run)

**Pre-requisite:** Android Studio installed with SDK Manager

##### Step-by-step:
1. **Open Device Manager**
   - Android Studio → Tools → Device Manager
   
2. **Create Virtual Device**
   - Click **+ Create Device**
   - Select **Pixel 4** (or any recent phone)
   - Click **Next**
   - Select **API 31+** (API 33 recommended)
   - Click **Next** → **Finish**
   - Wait for device creation (1-2 minutes)

3. **Start Emulator**
   - In Device Manager, click the **▶️ Play button**
   - Wait for "Android" home screen to appear (2-3 minutes)
   - You'll see a virtual phone on your screen

4. **Ready to Run!**
   - Leave emulator running
   - Go to Step 4 below

#### Option B: Use Physical Android Device

**Pre-requisites:**
- Android phone running Android 7.0+ (API 24+)
- USB cable
- Developer Mode enabled

##### Step-by-step:
1. **Enable Developer Mode**
   - Go to: Settings → About Phone
   - Tap "Build Number" 7 times
   - You'll see: "You are now a developer!"

2. **Enable USB Debugging**
   - Go to: Settings → Developer options
   - Turn ON: "USB Debugging"
   - Confirm the prompt on phone

3. **Connect Phone**
   - Plug phone into computer with USB cable
   - On phone, select "Allow" when prompted
   - On computer, trust the device

4. **Verify Connection**
   - Android Studio → Tools → Device Manager
   - Your phone should appear in the list
   - Status should be "Online"

5. **Ready to Run!**
   - Go to Step 4 below

---

## ✅ Step 4: Run the App

### Using Android Studio UI (Easiest)

1. **Select Your Device**
   - At the top toolbar, find the device dropdown
   - Select your emulator or physical device
   - Should show: `Pixel 4 API 33` or your device name

2. **Click Run Button**
   - Look for the **▶️ Green Play Button** (top right area)
   - Click it
   - Android Studio will start building
   - You'll see build progress in the "Build" tab at bottom

3. **What to Expect**
   - ⏱️ First build: 30-60 seconds
   - 📦 APK being built
   - 📱 App installing on device
   - 🎉 App launching

4. **App Launches!**
   - ResQtalk home screen appears
   - You see a Google Map
   - 4 buttons: Send Alert, Contacts, Voice, Settings
   - 1 large red SOS button at bottom

---

### Using Terminal Command

```powershell
# Run this in project root (PowerShell):
./gradlew installDebug
```

Then open the app manually on your device.

---

## 🎯 First Run Checklist

- [ ] Google Maps API key obtained
- [ ] API key added to AndroidManifest.xml
- [ ] Gradle synced successfully
- [ ] Emulator running OR Phone connected
- [ ] Device appears in Android Studio Device Manager
- [ ] Ready to click Run ▶️

---

## 📱 Testing After Launch

Once app opens:

### Test 1: Check Basic UI
- [ ] Home screen shows Google Map
- [ ] 4 navigation buttons visible (Send Alert, Contacts, Voice, Settings)
- [ ] Large red SOS button at bottom
- [ ] Map shows current location marker

### Test 2: Grant Permissions
- [ ] App may ask for permissions
- [ ] Grant: Location, Microphone, SMS, Notifications
- [ ] Tap "Allow" for each permission

### Test 3: Add Emergency Contact
1. Tap **Contacts** button
2. Tap **Add Contact** button
3. Enter name: `Test Contact`
4. Enter phone: `+1234567890` (or your phone number)
5. Tap **Save**
6. Contact appears in list
7. Toggle the switch to mark as "Emergency Contact"

### Test 4: Customize Settings
1. Tap **Settings** button
2. Edit SOS message (or keep default)
3. Toggle "Enable Location Tracking"
4. Tap **Save**

### Test 5: Configure Voice
1. Tap **Voice Activation** button
2. Edit trigger word (or keep "Help")
3. Click **Test** button and speak "Help"
4. Should show: "Trigger detected!"
5. Toggle **Enable Voice Listener** to turn it ON

### Test 6: Manual SOS
1. Go back to home screen
2. Tap large red **SOS** button
3. You should see: "Alert sent to 1 contact"
4. Check your phone - you should receive an SMS with:
   - Your custom SOS message
   - Google Maps link to your location

---

## ⚠️ Common Issues & Quick Fixes

### "Failed to sync"
**Solution:**
```powershell
./gradlew --stop
./gradlew sync
```

### "Build failed: Cannot resolve symbol 'Room'"
**Solution:**
- Go to File → Invalidate Caches → Invalidate and Restart
- Sync Gradle again

### "API key error" / "Map not showing"
**Solution:**
- Check API key in AndroidManifest.xml
- Make sure Google Maps SDK is ENABLED in Cloud Console
- Wait 5 minutes for key to activate
- Rebuild app

### "Device not appearing in Device Manager"
**Solution (Physical Phone):**
- Reconnect USB cable
- Ensure USB Debugging is ON
- Try: `adb devices` in terminal
- Should see your device listed

**Solution (Emulator):**
- Restart Android Studio
- In Device Manager, click Play button to start emulator
- Wait for home screen

### "Permission denied" errors
**Solution:**
- When app asks for permission, tap "Allow"
- If already denied, go to: Settings → Apps → ResQtalk → Permissions
- Enable all permissions
- Restart app

### "Location not working"
**Solution:**
- On emulator: Settings → Location → Turn ON
- On phone: Settings → Location → Turn ON
- Grant app location permission

### "App keeps crashing"
**Solution:**
- Check logcat (bottom of Android Studio)
- Look for red error messages
- Search error in TROUBLESHOOTING.md
- Rebuild and clear cache: File → Invalidate Caches

---

## 📞 Debug Commands

### Check Connected Devices
```powershell
adb devices
```
Should list connected devices and emulator

### View Real-time Logs
```powershell
adb logcat | grep "ResQtalk"
```

### Clear App Data
```powershell
adb shell pm clear com.example.resqtalk
```

### Uninstall App
```powershell
adb uninstall com.example.resqtalk
```

---

## ✨ You're Ready!

Follow these steps in order:
1. ✅ Get API key
2. ✅ Add to AndroidManifest.xml
3. ✅ Sync Gradle
4. ✅ Setup emulator or device
5. ✅ Click Run ▶️

**That's it! The app will build and launch!**

---

## 📚 Need More Help?

- **Setup issues?** → See SETUP_GUIDE.md
- **Build problems?** → See TROUBLESHOOTING.md
- **Architecture?** → See ARCHITECTURE.md
- **Quick questions?** → See QUICK_REFERENCE.md

---

## 🚀 Ready? Let's Go!

**Next Action:**
1. Go to Google Cloud Console
2. Get your API key
3. Add it to AndroidManifest.xml
4. Sync Gradle
5. Click Run!

**Estimated time: 15 minutes** ⏱️

---

**Last Updated:** November 18, 2025
**Status:** Ready for Launch ✅

Good luck! 🎉
