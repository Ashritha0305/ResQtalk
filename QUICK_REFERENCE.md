# ResQtalk - Quick Reference Guide

## 🚀 Before Running the App

```
1. Sync Gradle
   File → Sync Now

2. Get Google Maps API Key
   • Go to console.cloud.google.com
   • Enable Maps SDK for Android
   • Create API key
   • Copy API key

3. Add API Key
   • Open: app/src/main/AndroidManifest.xml
   • Find: <meta-data android:name="com.google.android.geo.API_KEY"
   • Replace: YOUR_GOOGLE_MAPS_API_KEY with your actual key

4. Run App
   • Click Run → Run 'app'
   • Or: ./gradlew installDebug
```

---

## 📋 Main Files at a Glance

### Activities (User Interface)

| File | Purpose | Key Methods |
|------|---------|------------|
| MainActivity.kt | Home screen with map | triggerManualSOS(), onMapReady() |
| SendAlertActivity.kt | Send alert form | sendAlert(), loadEmergencyContacts() |
| ContactsActivity.kt | Manage contacts | addContact(), editContact(), deleteContact() |
| VoiceActivationActivity.kt | Voice settings | startVoiceListener(), stopVoiceListener() |
| SettingsActivity.kt | App config | saveSettings(), startLocationTracking() |

### Services (Background Tasks)

| File | Purpose | Runs As |
|------|---------|---------|
| VoiceListenerService.kt | Listen for trigger word | Foreground Service (microphone) |
| LocationUpdateService.kt | Track location | Foreground Service (location) |

### Data Layer

| File | Purpose | Tables |
|------|---------|--------|
| ResQtalkDatabase.kt | Room database | emergency_contacts |
| EmergencyContactDao.kt | Database queries | CRUD operations |
| EmergencyContact.kt | Contact model | Entity definition |

### Helpers (Utilities)

| File | Purpose | Key Methods |
|------|---------|------------|
| LocationHelper.kt | GPS operations | getCurrentLocation(), generateMapsLink() |
| SmsHelper.kt | Send SMS | sendSMS(), sendSOStoMultipleContacts() |
| VoiceTriggerHelper.kt | Voice detection | startListening(), stopListening() |
| SharedPrefsHelper.kt | Save settings | setTriggerWord(), getTriggerWord(), etc. |

---

## 🎨 Layouts

| File | Screen | Elements |
|------|--------|----------|
| activity_main.xml | Home | Map, SOS button, 4 action buttons |
| activity_send_alert.xml | Send Alert | Message box, location toggle, contact selector |
| activity_contacts.xml | Contacts | RecyclerView, add contact button |
| activity_voice_activation.xml | Voice | Enable toggle, trigger word input |
| activity_settings.xml | Settings | Message editor, toggles, privacy notice |
| item_contact.xml | Contact Item | Name, phone, edit/delete buttons |

---

## 🎤 How Voice Works

```
1. User enables Voice Activation
   → VoiceActivationActivity calls startVoiceListener()
   → VoiceListenerService starts
   
2. Service runs continuously in foreground
   → Shows persistent notification
   → Listens for partial speech results
   → Compares with trigger word (case-insensitive)
   
3. Trigger word detected
   → triggerSOS() method called
   → Fetches location (LocationHelper)
   → Sends SMS to emergency contacts (SmsHelper)
   → Shows notification to user
   
4. To disable
   → VoiceActivationActivity calls stopVoiceListener()
   → Service stops
   → No more notifications
```

---

## 📍 How Location Works

```
1. Permission Check
   → Request ACCESS_FINE_LOCATION at runtime
   
2. Get Location
   → LocationHelper.getCurrentLocation() (suspend function)
   → FusedLocationProviderClient makes request
   → Returns Location object with latitude/longitude
   
3. Generate Maps Link
   → LocationHelper.generateMapsLink(lat, lng)
   → Returns: https://maps.google.com/?q=lat,lng
   → Attached to SOS message
   
4. Background Tracking (Optional)
   → LocationUpdateService runs in foreground
   → Updates every 1 minute
   → Broadcasts location via Intent
```

---

## 💾 How Data Persists

### In Room Database
```kotlin
// Add contact
val contact = EmergencyContact(name = "Mom", phone = "1234567890")
dao.insertContact(contact)

// Get all contacts
val contacts = dao.getAllContacts()

// Update contact
val updated = contact.copy(isEmergencyContact = true)
dao.updateContact(updated)

// Delete contact
dao.deleteContact(contact)
```

### In SharedPreferences
```kotlin
// Save trigger word
prefs.setTriggerWord("Help")

// Load trigger word
val word = prefs.getTriggerWord() // "Help"

// Save SOS message
prefs.setSOSMessage("I need help urgently!")

// Toggle voice
prefs.setVoiceEnabled(true)
```

---

## 📞 How SMS Works

```
1. Get emergency contacts from database
   contacts = dao.getEmergencyContacts()
   
2. Create SOS message
   message = "HELP! I need assistance!\n" +
             "Location: https://maps.google.com/?q=40.7128,-74.0060"
   
3. Send to each contact
   smsHelper.sendSOStoMultipleContacts(phoneNumbers, message)
   
4. Result
   → SMS sent via Android SMS Manager
   → Returns count of successful sends
   → Notification shown to user
```

---

## ⚙️ Permission Handling

### Dangerous Permissions (Require Runtime Request)

```kotlin
// Location
ACCESS_FINE_LOCATION
ACCESS_COARSE_LOCATION

// SMS
SEND_SMS

// Audio
RECORD_AUDIO

// Notifications
POST_NOTIFICATIONS
```

### How They're Requested

```
MainActivity → requestPermissions()
├── Check if permission granted
├── If not, use registerForActivityResult
└── If granted, enable feature

VoiceActivationActivity → saveSettings()
├── Get trigger word
├── Save to prefs
└── Restart service with new trigger word
```

---

## 🔄 App Lifecycle

### First Launch
```
MainActivity.onCreate()
├── Initialize helpers
├── Request all permissions
├── Get location
└── Show map
```

### When Voice Enabled
```
VoiceActivationActivity.saveSettings()
├── Save trigger word to prefs
├── Start VoiceListenerService
└── Show persistent notification
```

### When Device Boots
```
BootReceiver.onReceive()
├── Check if voice was enabled
├── Check if tracking was enabled
├── Start services automatically
└── User sees foreground notifications
```

### When Trigger Word Spoken
```
VoiceListenerService.triggerSOS()
├── Get current location
├── Get emergency contacts
├── Create SOS message
├── Send SMS to all contacts
├── Show notification
└── Continue listening
```

---

## 🐛 Common Issues & Fixes

### Map Not Showing
```
✗ ERROR: Blank white screen
✓ FIX: Add Google Maps API key to AndroidManifest.xml

✗ ERROR: API key invalid
✓ FIX: Verify API key on console.cloud.google.com

✗ ERROR: Permission denied
✓ FIX: Grant location permission when prompted
```

### Voice Not Detecting
```
✗ ERROR: No response when speaking
✓ FIX: Ensure microphone permission granted

✗ ERROR: Trigger word not detected
✓ FIX: Speak slowly and clearly, check trigger word matches

✗ ERROR: Service crashes
✓ FIX: Check logcat (adb logcat | grep ResQtalk)
```

### SMS Not Sending
```
✗ ERROR: No SMS received
✓ FIX: Verify SEND_SMS permission granted

✗ ERROR: Invalid phone number
✓ FIX: Use format like "+1234567890" or "1234567890"

✗ ERROR: Emulator limitation
✓ FIX: Test on physical device with SMS capability
```

### Location Null
```
✗ ERROR: Location is null in message
✓ FIX: Enable GPS on device

✗ ERROR: No permission for location
✓ FIX: Grant ACCESS_FINE_LOCATION permission

✗ ERROR: Google Play Services not installed
✓ FIX: Install on emulator or device
```

---

## 🧪 Debug Commands

### View Logs
```bash
adb logcat | grep ResQtalk
adb logcat | grep VoiceListenerService
adb logcat | grep LocationUpdateService
```

### Send Mock Location (Emulator)
```bash
adb shell am geo fix 40.7128 -74.0060
```

### Check Permissions
```bash
adb shell pm list permissions
adb shell pm grant com.example.resqtalk android.permission.ACCESS_FINE_LOCATION
```

### Check Installed Services
```bash
adb shell dumpsys package com.example.resqtalk
```

---

## 📱 Testing Checklist

### Basic Flow
- [ ] Add emergency contact
- [ ] Enable voice activation
- [ ] Speak trigger word
- [ ] Check SMS received
- [ ] Verify location in message

### Manual SOS
- [ ] Open app
- [ ] Click SOS button
- [ ] Select contact from dropdown
- [ ] Verify SMS received

### Background Test
- [ ] Enable voice
- [ ] Press home button (app minimized)
- [ ] Speak trigger word
- [ ] Verify SOS triggered

### Boot Test
- [ ] Enable voice
- [ ] Restart phone
- [ ] Wait for app to auto-start
- [ ] Verify notification appears

### Contact Management
- [ ] Add contact
- [ ] Edit contact
- [ ] Delete contact
- [ ] Verify database updates

---

## 💡 Pro Tips

1. **Testing with Emulator**
   - Use mock location in emulator settings
   - Use SMS simulator or mock SMS broadcast

2. **Testing with Device**
   - Use real phone number for SMS
   - Enable Developer Options
   - Keep USB Debugging enabled

3. **Voice Testing**
   - Speak clearly and slowly
   - Avoid background noise
   - Test multiple times for accuracy

4. **Location Testing**
   - Allow 2-3 seconds for GPS to lock
   - Outdoors gives better accuracy
   - Mock location works in emulator

5. **Database Testing**
   - Open Device File Explorer in Android Studio
   - Navigate to /data/data/com.example.resqtalk/databases/
   - Download resqtalk_database for inspection

---

## 🔗 Important Links

| Resource | URL |
|----------|-----|
| Google Cloud Console | https://console.cloud.google.com/ |
| Android Developer Docs | https://developer.android.com/ |
| Google Play Services | https://developers.google.com/android/guides/overview |
| Room Database Docs | https://developer.android.com/training/data-storage/room |
| SMS Manager Docs | https://developer.android.com/reference/android/telephony/SmsManager |
| Speech Recognizer Docs | https://developer.android.com/reference/android/speech/SpeechRecognizer |

---

## 📖 Reading Order

1. **README.md** - Project overview (5 min read)
2. **SETUP_GUIDE.md** - Detailed setup (10 min read)
3. **This file** - Quick reference (5 min read)
4. **Source code** - Review architecture (30 min read)
5. **AndroidManifest.xml** - Permissions and components (10 min read)

---

## 🎯 Next Steps

### Immediate
- [ ] Sync Gradle
- [ ] Add Google Maps API key
- [ ] Build and run app
- [ ] Test on device/emulator

### Short Term
- [ ] Add more emergency contacts
- [ ] Customize trigger word
- [ ] Test all screens
- [ ] Verify SMS delivery

### Long Term (Phase 2)
- [ ] Add backend server
- [ ] Real-time location sharing
- [ ] Emergency contact app
- [ ] Geofencing
- [ ] Firebase integration

---

## 🆘 Need Help?

1. **Check Logcat**
   ```bash
   adb logcat | grep ResQtalk
   ```

2. **Review AndroidManifest.xml**
   - Verify all permissions
   - Check all activities/services registered

3. **Verify Gradle Build**
   - Make sure all dependencies resolved
   - Check build.gradle.kts for correct versions

4. **Test Permissions**
   - Grant manually in Settings → Apps → ResQtalk → Permissions

5. **Check Google Maps API**
   - Verify API key is valid
   - Verify Maps SDK is enabled

---

## 📞 Key Contact Points

### Voice Activation Entry Point
- **File**: VoiceActivationActivity.kt
- **Method**: `startVoiceListener()`
- **Service**: VoiceListenerService

### Emergency Alert Entry Point
- **File**: MainActivity.kt or SendAlertActivity.kt
- **Method**: `triggerManualSOS()` or `sendAlert()`
- **Helper**: SmsHelper

### Contact Management Entry Point
- **File**: ContactsActivity.kt
- **Database**: EmergencyContactDao
- **Adapter**: ContactAdapter

---

**Last Updated**: November 18, 2025
**Version**: 1.0
**Status**: Production Ready ✅
