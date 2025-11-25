# ResQtalk - Troubleshooting Guide

## 🔍 Comprehensive Troubleshooting

This guide helps solve common issues when building, running, or using ResQtalk.

---

## 🚨 Build & Compilation Issues

### ❌ Issue: "Cannot resolve symbol" errors

**Symptoms:**
- Red squiggly lines in code
- "Unresolved reference" in IDE
- Build fails with symbol not found

**Solutions:**

1. **Sync Gradle**
   ```bash
   # In Android Studio
   File → Sync Now
   
   # Or from terminal
   ./gradlew sync
   ```

2. **Invalidate Caches**
   ```
   File → Invalidate Caches → Invalidate and Restart
   ```

3. **Clean Build**
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

4. **Check Imports**
   - Verify all imports are correct in Kotlin files
   - Remove unused imports
   - Ensure kapt plugin is in build.gradle.kts

---

### ❌ Issue: "Could not find dependency"

**Symptoms:**
- Build fails with "Could not find"
- Dependency not downloading
- Gradle build fails

**Solutions:**

1. **Check Internet Connection**
   - Ensure stable internet connection

2. **Check build.gradle.kts**
   - Verify Room version: `2.6.1`
   - Verify Coroutines version: `1.7.3`
   - Verify Play Services version: `21.1.0`

3. **Add kapt Plugin**
   ```kotlin
   plugins {
       ...
       kotlin("kapt")
   }
   ```

4. **Clear Gradle Cache**
   ```bash
   rm -rf ~/.gradle/caches
   ./gradlew sync
   ```

---

### ❌ Issue: "ViewBinding not found"

**Symptoms:**
- Cannot access binding variables
- "Cannot resolve symbol binding" error

**Solutions:**

1. **Enable ViewBinding**
   ```kotlin
   buildFeatures {
       viewBinding = true
   }
   ```

2. **Rebuild Project**
   ```bash
   Build → Clean Project
   Build → Rebuild Project
   ```

---

### ❌ Issue: "kapt is not applied"

**Symptoms:**
- Room annotations not processed
- Database not compiling
- "error: Cannot find symbol"

**Solutions:**

1. **Add kapt Plugin**
   ```kotlin
   plugins {
       alias(libs.plugins.android.application)
       alias(libs.plugins.kotlin.android)
       alias(libs.plugins.kotlin.compose)
       kotlin("kapt")  // Add this
   }
   ```

2. **Fix Room Dependency**
   ```kotlin
   dependencies {
       val roomVersion = "2.6.1"
       implementation("androidx.room:room-runtime:$roomVersion")
       kapt("androidx.room:room-compiler:$roomVersion")  // Use kapt, not annotationProcessor
   }
   ```

---

## 📱 Runtime Issues

### ❌ Issue: App Crashes Immediately

**Symptoms:**
- App starts then crashes to home screen
- No error message visible
- Logcat shows "Process crashed"

**Solutions:**

1. **Check Logcat**
   ```bash
   adb logcat | grep ResQtalk
   adb logcat | grep FATAL
   ```

2. **Common Causes:**
   - **Missing API Key**: Add Google Maps API key to AndroidManifest.xml
   - **Missing Permission**: Ensure all permissions are in manifest
   - **Missing Service Registration**: Check all 5 activities and 2 services are in manifest
   - **Database Not Initialized**: Check ResQtalkDatabase singleton

3. **Fix Steps:**
   ```xml
   <!-- AndroidManifest.xml -->
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_ACTUAL_API_KEY_HERE" />
   ```

---

### ❌ Issue: "ClassNotFoundException"

**Symptoms:**
- Error: "Class not found: com.example.resqtalk.activity.MainActivity"
- App crashes on startup

**Solutions:**

1. **Check Android Manifest**
   - Verify activity names match file names exactly
   - Check package name is correct

   ```xml
   <activity
       android:name=".activity.MainActivity"
       android:exported="true"
       android:label="@string/app_name">
       <intent-filter>
           <action android:name="android.intent.action.MAIN" />
           <category android:name="android.intent.category.LAUNCHER" />
       </intent-filter>
   </activity>
   ```

2. **Rebuild APK**
   ```bash
   ./gradlew clean
   ./gradlew installDebug
   ```

---

### ❌ Issue: "AndroidRuntimeException: Unable to add window"

**Symptoms:**
- Crash related to views or dialogs
- Error in logcat about window manager

**Solutions:**

1. **Check Dialog Context**
   - Ensure dialogs use correct context (Activity, not null)

2. **Check Layout Inflation**
   - Verify all layout files are valid XML
   - No syntax errors in layout files

---

## 🎤 Voice Recognition Issues

### ❌ Issue: "Speech recognition unavailable"

**Symptoms:**
- Voice activation doesn't work
- No response when speaking
- Service crashes silently

**Solutions:**

1. **Check Google Play Services**
   - Emulator: Install on API 24+ with Google Play
   - Device: Ensure Google Play Services installed

2. **Check Permission**
   ```bash
   adb shell pm grant com.example.resqtalk android.permission.RECORD_AUDIO
   ```

3. **Check Logcat**
   ```bash
   adb logcat | grep VoiceTrigger
   adb logcat | grep SpeechRecognizer
   ```

4. **Test Voice Recognition**
   - Go to Voice Activation screen
   - Enable toggle
   - Speak clearly
   - Check logcat for "Recognized: [text]"

---

### ❌ Issue: Trigger word not detected

**Symptoms:**
- Voice heard but not recognized as trigger
- App listens but doesn't trigger SOS

**Solutions:**

1. **Check Trigger Word**
   ```kotlin
   // In Voice Activation Activity
   val triggerWord = binding.etTriggerWord.text.toString().lowercase()
   Log.d("VoiceTrigger", "Looking for: $triggerWord")
   ```

2. **Verify Matching**
   - Ensure spoken word matches configured word
   - Check logcat for recognized text
   - Both are converted to lowercase for comparison

3. **Restart Service**
   - Disable and re-enable voice activation
   - Service will restart with updated trigger word

4. **Check Partial Results**
   - VoiceTriggerHelper checks both full and partial results
   - Spoken word must contain trigger word substring

---

### ❌ Issue: Service crashes after speaking

**Symptoms:**
- SpeechRecognizer crashes
- Service restarts repeatedly
- High CPU usage

**Solutions:**

1. **Check Error Handling**
   ```kotlin
   override fun onError(error: Int) {
       Log.e("VoiceTrigger", "Error: $error")
       stopListening()
       // Then restart
   }
   ```

2. **Restart Logic**
   - Service automatically restarts on error
   - Check Logcat for error codes (0-14)

3. **Kill and Restart Service**
   ```bash
   adb shell am startservice com.example.resqtalk/.service.VoiceListenerService
   ```

---

## 📍 Location Issues

### ❌ Issue: "FusedLocationProviderClient not available"

**Symptoms:**
- Location is always null
- Maps show blank screen
- Location permission shows as denied

**Solutions:**

1. **Check Google Play Services**
   ```bash
   adb shell pm list packages | grep play
   ```

2. **Grant Permission**
   ```bash
   adb shell pm grant com.example.resqtalk android.permission.ACCESS_FINE_LOCATION
   adb shell pm grant com.example.resqtalk android.permission.ACCESS_COARSE_LOCATION
   ```

3. **Restart App**
   ```bash
   adb shell am force-stop com.example.resqtalk
   adb shell am start -n com.example.resqtalk/.activity.MainActivity
   ```

---

### ❌ Issue: "Unable to start activity: Google Play services out of date"

**Symptoms:**
- App crashes when accessing location/maps
- Error: "Google Play services out of date"

**Solutions:**

1. **Update Google Play Services**
   - On device/emulator, go to Play Store
   - Search for "Google Play Services"
   - Update to latest version

2. **Check Emulator Version**
   - Use emulator with API 24+ and Google Play installed
   - Not available on API levels without Play Services

---

### ❌ Issue: Mock location not working

**Symptoms:**
- Emulator: Set mock location but app shows null
- Device: Can't change location

**Solutions:**

1. **Enable Mock Location (Device)**
   ```
   Settings → Developer Options → Select Mock Location App
   (Choose "Maps" or another location provider)
   ```

2. **Set Mock Location (Emulator)**
   ```bash
   adb shell am geo fix 40.7128 -74.0060
   ```

3. **Verify in Logcat**
   ```bash
   adb logcat | grep Location
   ```

---

### ❌ Issue: Maps showing "Google Maps Platform rejected"

**Symptoms:**
- Map displays error message
- Can see gray area but no map

**Solutions:**

1. **Verify API Key**
   - Check console.cloud.google.com
   - Verify key is valid and not revoked
   - Check API key restrictions (should allow all)

2. **Add to AndroidManifest.xml**
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="AIzaSyDxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" />
   ```

3. **Rebuild App**
   ```bash
   ./gradlew clean
   ./gradlew installDebug
   ```

4. **Regenerate Key**
   - Go to console.cloud.google.com
   - Delete old key
   - Create new key
   - Update in manifest

---

## 📞 SMS Issues

### ❌ Issue: "Permission denied for SMS"

**Symptoms:**
- SOS doesn't send
- Toast shows "Permission denied"
- SMS not received

**Solutions:**

1. **Grant Permission**
   ```bash
   adb shell pm grant com.example.resqtalk android.permission.SEND_SMS
   ```

2. **Check Manifest**
   ```xml
   <uses-permission android:name="android.permission.SEND_SMS" />
   ```

3. **Run Permission Dialog**
   - First launch of app should request permission
   - Grant when prompted
   - Or manually grant in Settings → Apps → ResQtalk → Permissions

---

### ❌ Issue: "SMS not sending"

**Symptoms:**
- No error but SMS never arrives
- Emulator limitation

**Solutions:**

1. **On Emulator**
   - SMS not actually sent (emulator limitation)
   - Use logcat to verify code path:
     ```bash
     adb logcat | grep SmsHelper
     ```

2. **On Device**
   - Verify phone number format: "+1234567890" or "1234567890"
   - Verify contact actually has emergency contact saved
   - Check SMS capability (some devices restricted)
   - Try SMS from built-in Messages app first

3. **Check Logcat**
   ```bash
   adb logcat | grep "sending SMS\|SMS sent\|Error"
   ```

---

### ❌ Issue: "Invalid phone number"

**Symptoms:**
- SMS fails silently
- No crash but no SMS

**Solutions:**

1. **Validate Phone Number Format**
   ```kotlin
   // Valid formats:
   // "1234567890"
   // "+11234567890"
   // "001234567890"
   
   // Invalid formats:
   // "(123) 456-7890"  // With formatting
   // "1-234-567-8900"  // With dashes
   ```

2. **Add Validation in Code**
   ```kotlin
   fun isValidPhoneNumber(phone: String): Boolean {
       return phone.matches(Regex("^[0-9+]{10,15}$"))
   }
   ```

---

## 💾 Database Issues

### ❌ Issue: "Cannot create database"

**Symptoms:**
- App crashes when accessing contacts
- Error: "Unable to open database"
- Database locked

**Solutions:**

1. **Check Storage Permissions**
   ```xml
   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
   ```

2. **Clear App Data**
   ```bash
   adb shell pm clear com.example.resqtalk
   ```

3. **Rebuild Database**
   ```bash
   rm -rf ~/.gradle
   ./gradlew clean
   ./gradlew installDebug
   ```

---

### ❌ Issue: "Database table doesn't exist"

**Symptoms:**
- Crash when querying contacts
- Error: "no such table: emergency_contacts"

**Solutions:**

1. **Verify Room Annotation**
   ```kotlin
   @Entity(tableName = "emergency_contacts")
   data class EmergencyContact(...)
   ```

2. **Verify DAO**
   ```kotlin
   @Dao
   interface EmergencyContactDao {
       @Query("SELECT * FROM emergency_contacts")
       suspend fun getAllContacts(): List<EmergencyContact>
   }
   ```

3. **Rebuild Project**
   ```bash
   Build → Clean Project
   Build → Rebuild Project
   ```

---

### ❌ Issue: "Database migration failed"

**Symptoms:**
- App crashes upgrading app
- Error: "Migration didn't properly handle"

**Solutions:**

1. **Uninstall Previous Version**
   ```bash
   adb uninstall com.example.resqtalk
   adb shell pm clear com.example.resqtalk
   ```

2. **Clear Cache**
   ```bash
   rm -rf build/
   ./gradlew clean
   ```

3. **Reinstall**
   ```bash
   ./gradlew installDebug
   ```

---

## 🔐 Permission Issues

### ❌ Issue: "Permission not granted at runtime"

**Symptoms:**
- Features don't work
- Permission denied errors
- Settings → Apps → ResQtalk → Permissions shows denied

**Solutions:**

1. **Grant Manually**
   ```
   Settings → Apps → ResQtalk → Permissions
   → Grant Location, Microphone, SMS, Notifications
   ```

2. **Grant via ADB**
   ```bash
   adb shell pm grant com.example.resqtalk android.permission.ACCESS_FINE_LOCATION
   adb shell pm grant com.example.resqtalk android.permission.RECORD_AUDIO
   adb shell pm grant com.example.resqtalk android.permission.SEND_SMS
   adb shell pm grant com.example.resqtalk android.permission.POST_NOTIFICATIONS
   ```

3. **Check Manifest**
   - Verify all needed permissions are declared

---

### ❌ Issue: "Foreground service not started"

**Symptoms:**
- Voice listening doesn't work
- No persistent notification
- Service crashes

**Solutions:**

1. **Check Foreground Service Declaration**
   ```xml
   <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
   
   <service
       android:name=".service.VoiceListenerService"
       android:foregroundServiceType="microphone" />
   ```

2. **Check Notification Channel**
   ```kotlin
   private fun createNotificationChannel() {
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           val channel = NotificationChannel(
               CHANNEL_ID,
               "ResQtalk Voice Listener",
               NotificationManager.IMPORTANCE_LOW
           )
           notificationManager.createNotificationChannel(channel)
       }
   }
   ```

3. **Start Service Properly**
   ```kotlin
   val intent = Intent(this, VoiceListenerService::class.java)
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
       startForegroundService(intent)
   } else {
       startService(intent)
   }
   ```

---

## 🚀 Launch Issues

### ❌ Issue: "App not launching after install"

**Symptoms:**
- Installation succeeds but app won't start
- Error on clicking app icon
- Launcher error

**Solutions:**

1. **Check Activity Export**
   ```xml
   <activity
       android:name=".activity.MainActivity"
       android:exported="true"
   />
   ```

2. **Check Launcher Intent Filter**
   ```xml
   <intent-filter>
       <action android:name="android.intent.action.MAIN" />
       <category android:name="android.intent.category.LAUNCHER" />
   </intent-filter>
   ```

3. **Reinstall**
   ```bash
   adb uninstall com.example.resqtalk
   ./gradlew installDebug
   ```

---

### ❌ Issue: "Multiple launcher icons"

**Symptoms:**
- App appears twice in launcher
- Duplicate activities

**Solutions:**

1. **Check Activities**
   - Only one activity should have LAUNCHER intent filter
   - Remove if accidentally added to others

2. **Verify Manifest**
   ```xml
   <!-- Only MainActivity should have this -->
   <activity
       android:name=".activity.MainActivity"
       android:exported="true">
       <intent-filter>
           <action android:name="android.intent.action.MAIN" />
           <category android:name="android.intent.category.LAUNCHER" />
       </intent-filter>
   </activity>
   ```

---

## 🎨 UI/Layout Issues

### ❌ Issue: "Map View not inflating"

**Symptoms:**
- Map fragment shows blank
- Can't see Google Map
- Fragment not displaying

**Solutions:**

1. **Check Fragment ID**
   ```xml
   <!-- activity_main.xml -->
   <com.google.android.gms.maps.MapView
       android:id="@+id/map"
       android:layout_width="match_parent"
       android:layout_height="match_parent" />
   ```

2. **Check Fragment Manager**
   ```kotlin
   val mapFragment = supportFragmentManager
       .findFragmentById(R.id.map) as? SupportMapFragment
   mapFragment?.getMapAsync(this)
   ```

3. **Verify Import**
   ```kotlin
   import com.google.android.gms.maps.SupportMapFragment
   ```

---

### ❌ Issue: "Buttons not responding to clicks"

**Symptoms:**
- Click button but nothing happens
- No navigation or action triggered
- OnClickListener not called

**Solutions:**

1. **Verify ClickListener Registration**
   ```kotlin
   binding.btnSOS.setOnClickListener {
       triggerManualSOS()
   }
   ```

2. **Check Button ID in Layout**
   ```xml
   <Button
       android:id="@+id/btn_sos"
       android:layout_width="120dp"
       android:layout_height="120dp" />
   ```

3. **Verify View Binding**
   - Ensure view binding is enabled
   - Rebuild project if names changed

---

## 🔄 Coroutine Issues

### ❌ Issue: "lifecycleScope not available"

**Symptoms:**
- Error: "Unresolved reference: lifecycleScope"
- Code won't compile

**Solutions:**

1. **Add Lifecycle Dependency**
   ```kotlin
   implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
   ```

2. **Extend AppCompatActivity**
   ```kotlin
   class MainActivity : AppCompatActivity() {
       // lifecycleScope now available
   }
   ```

3. **Sync Gradle**
   ```bash
   ./gradlew sync
   ```

---

### ❌ Issue: "Suspend function called from non-suspend"

**Symptoms:**
- Error: "Suspend function can only be called from suspend context"
- Compilation fails

**Solutions:**

1. **Use lifecycleScope.launch**
   ```kotlin
   lifecycleScope.launch {
       val location = locationHelper.getCurrentLocation()  // suspend function
   }
   ```

2. **Make Function Suspend**
   ```kotlin
   private suspend fun myFunction() {
       val location = locationHelper.getCurrentLocation()
   }
   ```

---

## 📊 Performance Issues

### ❌ Issue: "App is slow or freezes"

**Symptoms:**
- UI lag when clicking buttons
- Voice detection delays
- Location updates slow

**Solutions:**

1. **Check Background Tasks**
   - Ensure suspendCancellableCoroutine used correctly
   - Avoid blocking main thread

2. **Reduce Update Frequency**
   ```kotlin
   // LocationUpdateService
   val locationRequest = LocationRequest.Builder(
       Priority.PRIORITY_HIGH_ACCURACY,
       60000  // Increase to 120000 to reduce frequency
   )
   ```

3. **Profile with Android Studio**
   - Profiler → CPU
   - Check for high CPU usage
   - Look for main thread blocking

---

### ❌ Issue: "High battery drain"

**Symptoms:**
- Battery drains quickly
- Voice listening is on

**Solutions:**

1. **Disable Continuous Tracking**
   - Only enable when needed
   - Reduce location update frequency

2. **Adjust Service**
   ```kotlin
   val locationRequest = LocationRequest.Builder(
       Priority.PRIORITY_HIGH_ACCURACY,
       300000  // 5 minutes instead of 1 minute
   )
   ```

---

## 🧪 Testing Issues

### ❌ Issue: "Can't test on emulator"

**Symptoms:**
- SMS doesn't work on emulator
- Location not available
- Microphone not working

**Solutions:**

1. **Use Physical Device**
   - Emulator limitations for SMS and microphone
   - Device will work better for testing

2. **Mock Services on Emulator**
   - Use mock location (Settings → Developer Options)
   - Check logs instead of real SMS
   - Use built-in voice recognition

3. **Enable Necessary Emulator Features**
   - Use Android Emulator with Google Play
   - Ensure API 24+ with Play Services
   - Enable Microphone in emulator settings

---

## 📋 Diagnostic Checklist

Use this checklist when troubleshooting:

- [ ] **Gradle Synced?** File → Sync Now
- [ ] **API Key Added?** Check AndroidManifest.xml meta-data
- [ ] **Permissions Added?** Check all 13 permissions in manifest
- [ ] **Services Registered?** Check VoiceListenerService and LocationUpdateService
- [ ] **Activities Registered?** Check all 5 activities in manifest
- [ ] **Logcat Checked?** adb logcat | grep ResQtalk
- [ ] **App Reinstalled?** adb uninstall && ./gradlew installDebug
- [ ] **Permissions Granted?** Settings → Apps → ResQtalk → Permissions
- [ ] **Google Play Services?** Verify installed on device/emulator
- [ ] **Network Available?** Check internet connection for maps

---

## 💬 Getting Help

### Before Asking for Help

1. Check all errors in logcat
2. Run through this troubleshooting guide
3. Try clean rebuild
4. Test on different device/emulator
5. Verify all manual setup steps

### When Asking for Help

Include:
- Exact error message from logcat
- Steps to reproduce
- Device/Emulator details (API version, OS)
- AndroidManifest.xml snippet
- Relevant code section

---

**Last Updated**: November 18, 2025
**Version**: 1.0
**Status**: Comprehensive Guide Complete ✅
