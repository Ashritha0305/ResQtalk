# ResQtalk - Setup & Implementation Guide

## 🚀 Project Overview

ResQtalk is an emergency SOS app with voice-triggered activation. The core feature allows users to trigger an emergency alert by speaking a custom trigger word, which automatically sends their location and an SOS message to pre-configured emergency contacts.

---

## 📋 Project Structure

```
ResQtalk/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/resqtalk/
│   │   │   │   ├── activity/          # UI Activities
│   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   ├── SendAlertActivity.kt
│   │   │   │   │   ├── ContactsActivity.kt
│   │   │   │   │   ├── VoiceActivationActivity.kt
│   │   │   │   │   └── SettingsActivity.kt
│   │   │   │   ├── data/              # Database Layer
│   │   │   │   │   ├── db/
│   │   │   │   │   │   └── ResQtalkDatabase.kt
│   │   │   │   │   ├── dao/
│   │   │   │   │   │   └── EmergencyContactDao.kt
│   │   │   │   │   └── entity/
│   │   │   │   │       └── EmergencyContact.kt
│   │   │   │   ├── service/           # Background Services
│   │   │   │   │   ├── VoiceListenerService.kt
│   │   │   │   │   └── LocationUpdateService.kt
│   │   │   │   ├── helper/            # Utility Classes
│   │   │   │   │   ├── SharedPrefsHelper.kt
│   │   │   │   │   ├── LocationHelper.kt
│   │   │   │   │   ├── SmsHelper.kt
│   │   │   │   │   └── VoiceTriggerHelper.kt
│   │   │   │   ├── adapter/           # RecyclerView Adapters
│   │   │   │   │   └── ContactAdapter.kt
│   │   │   │   └── broadcast/         # Broadcast Receivers
│   │   │   │       └── BootReceiver.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/            # XML Layouts
│   │   │   │   ├── drawable/          # Drawables
│   │   │   │   ├── values/            # Colors, Strings, Dimens
│   │   │   │   └── values-night/      # Dark Mode Colors (optional)
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
└── settings.gradle.kts
```

---

## 🔧 Setup Instructions

### Step 1: Sync Gradle Dependencies

After opening the project in Android Studio:

1. **Sync Gradle Files**
   - Click **File** → **Sync Now**
   - Wait for all dependencies to download
   - This includes:
     - Room Database
     - Google Play Services (Location, Maps)
     - Kotlin Coroutines
     - Material Design
     - AppCompat

### Step 2: Get Google Maps API Key

1. **Go to Google Cloud Console**
   - Visit: https://console.cloud.google.com/

2. **Create a Project** (if you don't have one)
   - Click on the project dropdown at the top
   - Click **NEW PROJECT**
   - Enter project name: "ResQtalk"
   - Click **CREATE**

3. **Enable Google Maps Android API**
   - Go to **APIs & Services** → **Library**
   - Search for "Maps SDK for Android"
   - Click on it and press **ENABLE**

4. **Create an API Key**
   - Go to **APIs & Services** → **Credentials**
   - Click **+ CREATE CREDENTIALS** → **API Key**
   - Copy your API key

5. **Add API Key to AndroidManifest.xml**

   Open `app/src/main/AndroidManifest.xml` and find this line:

   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_GOOGLE_MAPS_API_KEY" />
   ```

   Replace `YOUR_GOOGLE_MAPS_API_KEY` with your actual API key.

### Step 3: Install the App

```bash
# Build and run the app
./gradlew installDebug

# Or use Android Studio:
# Run → Run 'app'
```

---

## 📱 App Features

### 1. **Home Screen (MainActivity)**
- Full-screen Google Map with user's current location
- Large red SOS button in the center
- Quick action buttons:
  - Send Alert
  - Contacts
  - Voice Activation
  - Settings
- Real-time location marker on map

**Permissions required:**
- `ACCESS_FINE_LOCATION`
- `ACCESS_COARSE_LOCATION`

### 2. **Send Emergency Alert Screen (SendAlertActivity)**
- Customizable SOS message text box
- Toggle to include live location
- Dropdown to select emergency contact
- Send button with visual feedback

**Permissions required:**
- `SEND_SMS`
- `ACCESS_FINE_LOCATION` (if including location)

### 3. **Emergency Contacts Screen (ContactsActivity)**
- RecyclerView displaying all saved contacts
- Add new contact button (manual entry)
- Edit/Delete options for each contact
- Toggle switch to enable/disable contact as emergency recipient

**Database operations:**
- Insert, Update, Delete, Select contacts from Room DB

### 4. **Voice Activation Screen (VoiceActivationActivity)**
- Toggle to enable/disable background voice listening
- Text field to set custom trigger word (default: "Help")
- Test trigger word button
- Save settings button

**Functionality:**
- Starts `VoiceListenerService` when enabled
- Stops service when disabled
- Saves settings to `SharedPreferences`

### 5. **Settings Screen (SettingsActivity)**
- SOS message template editor
- Toggle continuous live tracking
- Vibration toggle
- Privacy notice
- Save button to persist settings

---

## 🎤 Voice Activation System

### How It Works

1. **VoiceListenerService**
   - Runs as a foreground service
   - Uses Android's `SpeechRecognizer` for voice detection
   - Listens continuously for the trigger word
   - Automatically restarts on error

2. **Trigger Detection Flow**
   - When user speaks, partial results are captured
   - Each partial result is checked against the trigger word
   - When trigger word is detected:
     - Service fetches current location
     - Generates SOS message with location link
     - Sends SMS to all emergency contacts
     - Plays notification sound

3. **BootReceiver**
   - Starts `VoiceListenerService` automatically when phone boots
   - Starts `LocationUpdateService` if continuous tracking is enabled
   - Reads from `SharedPreferences` to determine which services to start

### Permissions for Voice

```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.INTERNET" />
```

---

## 📍 Location System

### LocationHelper
- Uses `FusedLocationProviderClient` for accurate location
- Gets high-accuracy location (PRIORITY_HIGH_ACCURACY)
- Generates Google Maps links: `https://maps.google.com/?q=lat,lng`
- Handles permission errors gracefully

### LocationUpdateService
- Foreground service for continuous location updates
- Updates every 1 minute (configurable in code)
- Broadcasts location updates for emergency contacts to track

### Permissions for Location

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

---

## 💾 Data Storage

### Room Database

**EmergencyContact Entity:**
```kotlin
@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val phone: String,
    val isEmergencyContact: Boolean = true,
    val isTrackingEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
```

**Database Operations:**
- Insert contact
- Update contact
- Delete contact
- Get all contacts
- Get emergency contacts only

### SharedPreferences

Stored in `resqtalk_prefs`:
- `trigger_word` - Custom voice trigger word
- `sos_message` - Custom SOS message template
- `voice_enabled` - Voice activation toggle
- `tracking_enabled` - Location tracking toggle
- `alert_tone` - Alert sound URI
- `vibration_enabled` - Vibration toggle

---

## 📞 SMS System

### SmsHelper Class

```kotlin
fun sendSMS(phoneNumber: String, message: String): Boolean
fun sendSOStoMultipleContacts(phoneNumbers: List<String>, message: String): Int
```

**Features:**
- Divides long messages into multiple parts
- Sends SMS using `SmsManager`
- Handles exceptions gracefully
- Returns count of successfully sent messages

**Permissions:**
```xml
<uses-permission android:name="android.permission.SEND_SMS" />
```

---

## 🔐 Security & Permissions

### Manifest Permissions

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.SEND_SMS" />
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### Runtime Permissions (Handled in Code)

The app requests permissions at runtime for:
- Location access (API 23+)
- SMS sending (API 23+)
- Audio recording (API 23+)
- Notifications (API 31+)

---

## 🎨 UI Styling

### Color Scheme

| Color | Hex Code | Usage |
|-------|----------|-------|
| Red Main | #E53935 | SOS button, Primary action buttons |
| Red Light | #FFCDD2 | Secondary buttons, highlights |
| Red Light BG | #FFEBEE | Light backgrounds, info boxes |
| White | #FFFFFF | Text, backgrounds |
| Black | #000000 | Primary text |

### Components

- **Rounded EditText**: 12dp radius with red border
- **Rounded Background**: 8dp radius with light red fill
- **Buttons**: Red color with white text, 60dp height for main actions
- **Maps**: Full-screen with current location marker

---

## 🚀 Building & Running

### Debug Build

```bash
./gradlew buildDebug
```

### Release Build

```bash
./gradlew buildRelease
```

### Run on Emulator

```bash
./gradlew installDebug
adb shell am start -n com.example.resqtalk/.activity.MainActivity
```

### Run on Physical Device

1. Connect device via USB
2. Enable USB Debugging on device
3. Click **Run** → **Run 'app'** in Android Studio

---

## 🧪 Testing Voice Activation

### Test Steps

1. **Add Emergency Contacts**
   - Open app → Contacts button
   - Click "Add Contact"
   - Enter name and phone number
   - Save

2. **Enable Voice Activation**
   - Open app → Voice Activation button
   - Enter custom trigger word (e.g., "Help")
   - Toggle "Enable Background Voice Listening"
   - Save

3. **Test Trigger**
   - Click "Test Trigger Word" button
   - Speak the trigger word
   - App should detect it and show notification

4. **Manual SOS**
   - From home screen, click large red SOS button
   - SMS should be sent to all emergency contacts with location

---

## 📝 Important Notes

### For Testing

- Use Android Emulator with GPS support enabled
- Use mock SMS app or actual SMS service
- Ensure Google Play Services are installed on device
- Configure mock location for testing

### Firebase Cloud Messaging (Optional for Phase 2)

To add real-time tracking notifications:
1. Set up Firebase project
2. Add FCM dependency to `build.gradle.kts`
3. Implement `FirebaseMessagingService`
4. Send notifications from backend

### Common Issues & Fixes

| Issue | Fix |
|-------|-----|
| Map not showing | Add Google Maps API key to manifest |
| Voice not detecting | Ensure microphone permission granted, restart service |
| SMS not sending | Check SMS permission granted, valid phone number format |
| Location null | Ensure location permission granted, GPS enabled |
| Crashes on boot | Check service permissions in manifest |

---

## 📞 Contact Sync (Optional Feature)

To import contacts from phone (future enhancement):

```kotlin
val projection = arrayOf(ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER)
val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, null)
// Parse and save to database
```

Requires permission:
```xml
<uses-permission android:name="android.permission.READ_CONTACTS" />
```

---

## 🔄 Update Trigger Word

The app allows changing the trigger word anytime:
1. Go to Voice Activation
2. Change the text field
3. Click Save
4. Service restarts with new trigger word
5. New trigger word takes effect immediately

---

## 📈 Next Steps (Phase 2)

- [ ] Real-time location sharing with emergency contacts
- [ ] Geofencing for safe zones (home, office, etc.)
- [ ] Backend server for centralized location tracking
- [ ] Emergency contact app to view user location on map
- [ ] Firebase Cloud Messaging for push notifications
- [ ] Emergency service integration (911, police)
- [ ] Panic button alert to nearby users
- [ ] SOS history and analytics

---

## ✅ Checklist Before Release

- [ ] Google Maps API key configured
- [ ] All permissions added to manifest
- [ ] Database migrations tested
- [ ] Voice recognition tested on device
- [ ] SMS sending tested with real phone
- [ ] Location services tested with GPS
- [ ] Boot receiver tested after restart
- [ ] Privacy policy reviewed
- [ ] App signed with release keystore
- [ ] Tested on multiple Android versions (API 24+)

---

## 📞 Support

For issues or questions, check the logs:
```bash
adb logcat | grep ResQtalk
```

All classes use `Log.d()` and `Log.e()` for debugging.

---

**Happy coding! Stay safe! 🚨**
