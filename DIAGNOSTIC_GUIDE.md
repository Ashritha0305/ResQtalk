# 🆘 App Crashing - Diagnostic Guide

## ✅ What I Just Fixed

I improved `MainActivity.kt` with:
- ✅ Better error handling in onCreate
- ✅ Null-safety for GoogleMap object
- ✅ Try-catch blocks around critical operations
- ✅ Proper logging for debugging

---

## 🔍 How to Diagnose the Crash

When the app crashes, you'll see an error message in Android Studio's **Logcat** (bottom panel).

### Find the Error:
1. Open Android Studio
2. Click **View** → **Tool Windows** → **Logcat** (bottom)
3. Click the red **X** icon to filter for errors
4. Look for messages starting with: `E/` or `FATAL EXCEPTION`
5. Copy the error and share with me

### What to Look For:
```
FATAL EXCEPTION: main
Process: com.example.resqtalk
java.lang.RuntimeException: ...
```

---

## 🚀 Try Building and Running Again

### In Android Studio:
1. **File** → **Sync Now**
2. Wait for sync
3. **Run** → **Run 'app'**
4. Select your device/emulator

### If Still Crashing:
1. Check Logcat for error
2. Share the error with me
3. I'll fix it immediately!

---

## 🐛 Common Crash Causes

| Error | Cause | Solution |
|-------|-------|----------|
| **NullPointerException** | Something is null | Already fixed in code |
| **Map initialization failed** | Google Play Services issue | Restart emulator/device |
| **Permission denied** | Missing permission grant | Tap "Allow" when prompted |
| **API key invalid** | Wrong API key in manifest | Verify key in AndroidManifest.xml |
| **Fragment not found** | Layout issue | Already fixed layout |

---

## 📝 Steps to Debug

### Step 1: Clean Build
```powershell
cd C:\Users\Ashritha\AndroidStudioProjects\ResQtalk
./gradlew clean
```

### Step 2: Invalidate Cache
- File → Invalidate Caches → Invalidate and Restart

### Step 3: Rebuild
- Run app again

### Step 4: Check Logcat
- Share any red error messages with me

---

## ✨ Most Likely Solution

**Your issue is probably one of these:**

1. **API key not activated yet**
   - Solution: Wait 5 minutes after creating API key, then rebuild

2. **Google Play Services not installed on emulator**
   - Solution: Create emulator with "Google APIs" included
   - Or use a real device

3. **Permissions not granted**
   - Solution: App will ask for permissions, tap "Allow"

---

## 🎯 Quick Fix Checklist

- [ ] Synced Gradle after code changes
- [ ] API key added to AndroidManifest.xml
- [ ] Emulator has Google Play Services (or using real device)
- [ ] Google Maps SDK enabled in Cloud Console
- [ ] Checked Logcat for errors
- [ ] Restarted Android Studio
- [ ] Did clean build

---

## 💬 Next Steps

**Run the app and tell me:**
1. Does it crash immediately or after a few seconds?
2. What error appears in Logcat? (copy the full error)
3. Are you using an emulator or real phone?

Once I have this info, I can fix it!

---

**Status: Code improved with error handling ✅ | Ready to test**
