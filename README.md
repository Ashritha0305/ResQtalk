# ResQtalk - Emergency SOS with Voice Activation

**ResQtalk** is an Android emergency response app that uses voice-activated triggers to send SOS alerts with live location to pre-configured emergency contacts.

## 🎯 Core Features

### ✅ Implemented Features

- **Voice-Triggered SOS** 🎤
  - Custom trigger word activation
  - Works even with app closed/minimized
  - Automatic location detection
  - Multi-contact SMS notifications

- **Emergency Alert System** 🚨
  - Manual SOS button on home screen
  - Customizable alert messages
  - Include location option
  - Multi-recipient SMS delivery

- **Contact Management** 👥
  - Add/edit/delete emergency contacts
  - Toggle on/off for alerts
  - Save permanently in database

- **Live Location Tracking** 📍
  - Real-time GPS location
  - Google Maps integration
  - Automatic location links
  - Background location updates

- **Background Services** ⚙️
  - Foreground voice listening service
  - Continuous location updates
  - Auto-start on device boot
  - Persistent settings

## 🛠️ Technology Stack

- **Language**: Kotlin
- **Database**: Room (SQLite)
- **Location**: Google Play Services (FusedLocationProviderClient)
- **Maps**: Google Maps SDK for Android
- **Voice**: Android Speech Recognizer
- **Messaging**: Android SMS Manager
- **Architecture**: MVVM-inspired with Services & Broadcast Receivers
- **Async**: Kotlin Coroutines

## 📁 Project Structure

```
ResQtalk/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/resqtalk/
│   │   │   ├── activity/         (5 Activities)
│   │   │   ├── service/          (2 Services)
│   │   │   ├── data/             (Room Database)
│   │   │   ├── helper/           (Utilities)
│   │   │   ├── adapter/          (RecyclerView)
│   │   │   └── broadcast/        (BootReceiver)
│   │   └── res/
│   │       ├── layout/           (5 XML Layouts)
│   │       ├── drawable/         (Shapes & Icons)
│   │       └── values/           (Colors, Strings)
│   └── build.gradle.kts
└── SETUP_GUIDE.md
```

## 🚀 Quick Start

### Prerequisites
- Android Studio (latest version)
- Android SDK 24 or higher
- Google Play Services
- Google Maps API Key (free)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ResQtalk
   ```

2. **Open in Android Studio**
   - File → Open → Select ResQtalk folder
   - Wait for Gradle sync

3. **Get Google Maps API Key**
   - Visit: https://console.cloud.google.com/
   - Create project, enable Maps SDK, create API key
   - Add key to `AndroidManifest.xml`

4. **Run the app**
   - Click **Run** → **Run 'app'**
   - Or use: `./gradlew installDebug`

## 📱 Screens Overview

### 1. Home Screen
- Full-screen Google Map with current location
- Large red SOS button
- Quick access buttons for all features
- Real-time location marker

### 2. Send Emergency Alert
- Custom message editor
- Toggle to include location
- Contact selector dropdown
- Send button

### 3. Emergency Contacts
- List of all saved contacts
- Add/Edit/Delete functionality
- Toggle emergency recipient status
- RecyclerView with inline controls

### 4. Voice Activation Settings
- Enable/disable background listening
- Set custom trigger word
- Test trigger word functionality
- Auto-save on service change

### 5. App Settings
- SOS message template editor
- Location tracking toggle
- Vibration control
- Privacy notice display

## 🎤 How Voice Activation Works

```
User says trigger word
         ↓
SpeechRecognizer captures audio
         ↓
VoiceTriggerHelper detects keyword
         ↓
VoiceListenerService triggered
         ↓
LocationHelper fetches GPS location
         ↓
Generate SOS message with Maps link
         ↓
SmsHelper sends to all emergency contacts
         ↓
Notification plays on device
```

## 💾 Data Storage

### Room Database
- **Table**: emergency_contacts
- **Columns**: id, name, phone, isEmergencyContact, isTrackingEnabled, createdAt
- **Operations**: CRUD (Create, Read, Update, Delete)

### SharedPreferences
```
resqtalk_prefs:
├── trigger_word (default: "Help")
├── sos_message (customizable)
├── voice_enabled (true/false)
├── tracking_enabled (true/false)
├── alert_tone (URI)
└── vibration_enabled (true/false)
```

## 🔐 Permissions

### Dangerous Permissions (Runtime)
```
ACCESS_FINE_LOCATION
ACCESS_COARSE_LOCATION
SEND_SMS
RECORD_AUDIO
POST_NOTIFICATIONS
```

### Normal Permissions
```
INTERNET
ACCESS_NETWORK_STATE
READ_CONTACTS
READ_PHONE_STATE
VIBRATE
FOREGROUND_SERVICE
RECEIVE_BOOT_COMPLETED
```

## 📞 Key Classes

### Activities
- `MainActivity.kt` - Home screen with map
- `SendAlertActivity.kt` - Send alert interface
- `ContactsActivity.kt` - Contact management
- `VoiceActivationActivity.kt` - Voice settings
- `SettingsActivity.kt` - App configuration

### Services
- `VoiceListenerService.kt` - Background voice detection
- `LocationUpdateService.kt` - Continuous location tracking

### Helpers
- `LocationHelper.kt` - GPS & Maps link generation
- `SmsHelper.kt` - SMS sending logic
- `VoiceTriggerHelper.kt` - Speech recognition
- `SharedPrefsHelper.kt` - Settings persistence

### Data Layer
- `EmergencyContact.kt` - Entity
- `EmergencyContactDao.kt` - Database access
- `ResQtalkDatabase.kt` - Database instance

## 🧪 Testing

### Manual Testing Checklist
- [ ] Add emergency contact
- [ ] Enable voice activation
- [ ] Speak trigger word
- [ ] Verify SMS sent
- [ ] Check location in message
- [ ] Test manual SOS button
- [ ] Verify app works when minimized
- [ ] Check persistence after reboot

### Debug Commands
```bash
# View logs
adb logcat | grep ResQtalk

# Send mock location
adb shell am geo fix 40.7128 -74.0060

# Test SMS
adb shell service call isms 7 i 1 s com.example.resqtalk
```

## 🐛 Troubleshooting

### Map Not Showing
- Ensure Google Maps API key is added to AndroidManifest.xml
- Verify API key has Maps SDK enabled
- Check internet permission is granted

### Voice Not Detecting
- Ensure microphone permission is granted
- Restart app or toggle voice activation
- Verify trigger word matches exactly
- Check phone has speech recognition available

### SMS Not Sending
- Verify SEND_SMS permission granted
- Use valid phone number format
- Check device has SMS capability
- Ensure not blocked by firewall

### Location is Null
- Enable GPS on device
- Grant location permission
- Use emulator with GPS support or mock location
- Ensure Google Play Services installed

## 📈 Features in Development (Phase 2)

- [ ] Real-time location sharing
- [ ] Geofencing for safe zones
- [ ] Backend server integration
- [ ] Emergency contact app for receiving alerts
- [ ] Firebase Cloud Messaging
- [ ] Emergency service integration
- [ ] Panic button for nearby users
- [ ] Analytics and history

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Create a feature branch
2. Make your changes
3. Test thoroughly
4. Submit a pull request

## 📜 License

This project is licensed under the MIT License.

## 🙏 Acknowledgments

- Google Play Services for location and maps
- Android Speech Recognition API
- Room Database by Google
- Material Design guidelines

## 💡 Tips

- **Best Practice**: Always test with real phone numbers
- **Performance**: Foreground services consume battery; adjust intervals if needed
- **Testing**: Use emulator with GPS support or mock location
- **Privacy**: Ensure users explicitly enable tracking
- **Accessibility**: Test with accessibility services enabled

## 📞 Support

For issues:
contact: ashrithap2005@gmail.com
---

**Stay Safe! 🚨 ResQtalk - Always There in Emergencies**
