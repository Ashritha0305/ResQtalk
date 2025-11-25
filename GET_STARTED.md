# 🎯 I Don't Know Where to Start? START HERE!

## 📍 You Are Here
✅ ResQtalk app is **100% built**
✅ All code files are **created**
✅ You just need to **run it**

---

## 🚀 STEP 1: Get Google Maps API Key
**Time: 3 minutes**

### What's an API Key?
It's like a password that lets your app use Google Maps. You MUST have this.

### How to Get It:

**Step 1A: Open Google Cloud Console**
- Go to: **https://console.cloud.google.com/**
- Sign in with your Google account
- Click your profile → Create new account if needed

**Step 1B: Create a Project**
- At top, click the project dropdown
- Click **NEW PROJECT**
- Name it: `ResQtalk`
- Click **CREATE**
- Wait 1-2 minutes

**Step 1C: Enable Maps**
- In the search bar at top, type: `Maps SDK for Android`
- Click on it
- Click **ENABLE**
- Wait 30 seconds

**Step 1D: Create API Key**
- On left sidebar, click **Credentials**
- Click **+ CREATE CREDENTIALS**
- Select **API Key**
- A popup shows your key
- Click the copy icon (📋)
- **Save it somewhere** (you'll need it in Step 2)

Example key looks like:
```
AIzaSyDxHHfJtzaLLXLdDJkjEwY-OPZ5YKjGdLE
```

✅ **You now have your API key!**

---

## 🎯 STEP 2: Add API Key to Your Project
**Time: 1 minute**

### Where to Add It?

**Step 2A: Open File in Android Studio**
1. Open Android Studio
2. On left sidebar, find: `app` → `src` → `main` → `AndroidManifest.xml`
3. Double-click to open it

**Step 2B: Find the API Key Line**
Look for this line (around line 88):
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_GOOGLE_MAPS_API_KEY" />
```

**Step 2C: Replace with Your Key**
- Replace: `YOUR_GOOGLE_MAPS_API_KEY`
- With: Your actual key (from Step 1D)

**Before:**
```xml
android:value="YOUR_GOOGLE_MAPS_API_KEY"
```

**After:**
```xml
android:value="AIzaSyDxHHfJtzaLLXLdDJkjEwY-OPZ5YKjGdLE"
```

**Step 2D: Save**
- Press: **Ctrl + S**

✅ **API key added!**

---

## ⚙️ STEP 3: Sync Gradle (Download Dependencies)
**Time: 2-5 minutes**

This downloads all the libraries the app needs.

### How to Sync:

**Option A: Using Android Studio (Easy)**
1. At the top, click: **File**
2. Click: **Sync Now**
3. Wait for message at bottom: `"Gradle sync finished successfully"`
4. ✅ Done!

**Option B: Using Terminal**
1. Open PowerShell
2. Go to your project folder:
   ```powershell
   cd C:\Users\Ashritha\AndroidStudioProjects\ResQtalk
   ```
3. Run:
   ```powershell
   ./gradlew sync
   ```
4. Wait for: `"BUILD SUCCESSFUL"`

---

## 📱 STEP 4: Setup Your Device/Emulator
**Time: 5 minutes**

You need to test the app somewhere. Choose ONE option:

### Option A: Use Android Emulator (Virtual Phone)

**Step 4A: Open Device Manager**
- In Android Studio, click: **Tools** → **Device Manager**

**Step 4B: Create Virtual Phone**
- Click **+ Create Device**
- Select **Pixel 4** (or any phone)
- Click **Next**
- Select **API 33** or higher
- Click **Finish**
- Wait for creation

**Step 4C: Start Emulator**
- In Device Manager, find your device
- Click the **▶️ Play button** next to it
- Wait 2-3 minutes for virtual phone to start
- You'll see an Android home screen
- ✅ Leave it running!

### Option B: Use Your Real Phone

**Step 4B: Enable Developer Mode**
1. On your phone, go to: **Settings**
2. Go to: **About Phone**
3. Find: **Build Number**
4. Tap **Build Number** exactly **7 times**
5. You'll see: "You are now a developer!"

**Step 4C: Enable USB Debugging**
1. Go back to **Settings**
2. Open: **Developer options** (new menu)
3. Turn ON: **USB Debugging**
4. A dialog appears, tap **OK**

**Step 4D: Connect Phone**
1. Plug phone into computer with USB cable
2. On phone, a dialog asks: "Allow USB debugging?"
3. Tap **Allow**
4. ✅ Phone is connected!

---

## ▶️ STEP 5: RUN THE APP!
**Time: 1-2 minutes**

### How to Run:

**Step 5A: Select Device**
- At the top of Android Studio, find the device dropdown
- It might say: `"No devices selected"` or `"Emulator"`
- Click it and select your device/emulator

**Step 5B: Click Run**
- Look for a green **▶️ Play button** at the top right
- Click it
- Android Studio starts building

**Step 5C: Wait for App to Launch**
- You'll see build progress at the bottom
- App compiles and installs on your device
- **ResQtalk** launches!
- You see the home screen with a map
- ✅ Success!

---

## 🎉 STEP 6: Test the App
**Time: 5 minutes**

Now that it's running, let's make sure it works!

### Test 1: Grant Permissions
- App might ask for permissions (Location, Microphone, SMS, etc.)
- Tap **Allow** for each one

### Test 2: Add a Contact
1. Tap **Contacts** button (bottom navigation)
2. Tap **Add Contact** button
3. Enter:
   - Name: `Test Contact`
   - Phone: Your phone number (or `+1234567890`)
4. Tap **Save**
5. ✅ Contact appears in list

### Test 3: Send Emergency Alert
1. Go back to home screen (tap the map)
2. Tap the large red **SOS** button
3. App should show: "Alert sent!"
4. ✅ You should get an SMS with your location link!

### Test 4: (Optional) Test Voice
1. Tap **Voice Activation** button
2. Tap **Test** button
3. Speak: "Help" (or your trigger word)
4. Should show: "Trigger detected!"
5. ✅ Voice works!

---

## ✅ CHECKLIST: Everything Done?

- [ ] Got Google Maps API key from https://console.cloud.google.com/
- [ ] Added API key to AndroidManifest.xml
- [ ] Synced Gradle (File → Sync Now)
- [ ] Setup emulator or connected phone
- [ ] Clicked Run ▶️ button
- [ ] App launched successfully
- [ ] Granted permissions
- [ ] Added test contact
- [ ] Sent emergency alert
- [ ] Got SMS with location

---

## 🆘 Something Went Wrong?

### "Gradle sync failed"
**Solution:**
```powershell
./gradlew --stop
./gradlew sync
```

### "Map not showing / API key error"
**Solution:**
- Double-check your API key in AndroidManifest.xml is correct
- Make sure "Maps SDK for Android" is ENABLED in Google Cloud Console
- Wait 5 minutes and rebuild

### "Device not found"
**Solution:**
- Restart Android Studio
- For emulator: Click Play button again
- For phone: Unplug and replug USB cable

### "App keeps crashing"
**Solution:**
1. Check the errors at bottom of Android Studio (Logcat)
2. Look in `TROUBLESHOOTING.md` file
3. Try: File → Invalidate Caches → Restart

### "Permission denied errors"
**Solution:**
- When app asks, tap **Allow**
- Or: Settings → Apps → ResQtalk → Permissions → Enable all

---

## 📚 Need More Help?

| Question | Read This |
|----------|-----------|
| I'm stuck on a specific step | BEFORE_RUNNING.md |
| My build is failing | TROUBLESHOOTING.md |
| I want to understand the code | ARCHITECTURE.md |
| I want a quick overview | README.md |

---

## 🎯 The 5-Minute Version

**TL;DR - Just do this:**

1. Get API key from: https://console.cloud.google.com/
2. Add to: `app/src/main/AndroidManifest.xml` (line 88)
3. Sync: File → Sync Now
4. Run: Click ▶️ button
5. Done! 🎉

---

## 🚀 You've Got This!

**Questions?**
- Check TROUBLESHOOTING.md
- Check BEFORE_RUNNING.md
- All files are in your project folder

**Ready to start?**
→ Go get that API key! 🔑

---

**Good luck! 🍀 You're going to build something awesome! 💪**
