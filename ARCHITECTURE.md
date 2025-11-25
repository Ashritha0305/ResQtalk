# ResQtalk - Visual Architecture & Components

## 🏗️ Application Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     USER INTERFACE LAYER                    │
├─────────────────────────────────────────────────────────────┤
│  MainActivity  │  SendAlert  │  Contacts  │  Voice  │ Settings │
│   (Map + SOS)  │  Activity   │  Activity  │ Activity │  Activity │
└────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    BUSINESS LOGIC LAYER                     │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Helper Classes:                                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │LocationHelper│  │  SmsHelper   │  │ VoiceTrigger │    │
│  │  (GPS/Maps)  │  │ (SMS Logic)  │  │  Helper      │    │
│  └──────────────┘  └──────────────┘  └──────────────┘    │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         SharedPrefsHelper (Settings)                │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                  BACKGROUND SERVICES LAYER                  │
├─────────────────────────────────────────────────────────────┤
│  ┌────────────────────────────────────────────────────┐    │
│  │     VoiceListenerService                           │    │
│  │     ├─ Foreground Service (Microphone)             │    │
│  │     ├─ SpeechRecognizer (Voice Detection)          │    │
│  │     └─ Triggers SOS on Keyword Match               │    │
│  └────────────────────────────────────────────────────┘    │
│  ┌────────────────────────────────────────────────────┐    │
│  │     LocationUpdateService                          │    │
│  │     ├─ Foreground Service (Location)               │    │
│  │     ├─ FusedLocationProviderClient (GPS)           │    │
│  │     └─ Updates location every 1 minute             │    │
│  └────────────────────────────────────────────────────┘    │
│  ┌────────────────────────────────────────────────────┐    │
│  │     BootReceiver                                    │    │
│  │     ├─ Receives BOOT_COMPLETED                     │    │
│  │     ├─ Auto-starts services                        │    │
│  │     └─ Restores user preferences                   │    │
│  └────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    DATA LAYER                               │
├─────────────────────────────────────────────────────────────┤
│  Room Database                  SharedPreferences            │
│  ├─ EmergencyContact Entity      ├─ trigger_word            │
│  ├─ EmergencyContactDao          ├─ sos_message             │
│  ├─ ResQtalkDatabase             ├─ voice_enabled           │
│  └─ emergency_contacts table     ├─ tracking_enabled        │
│                                  └─ other_settings           │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 Voice Activation Flow

```
START
  ↓
User Speaks Trigger Word
  ↓
SpeechRecognizer captures audio
  ↓
VoiceTriggerHelper processes results
  ↓
Is trigger word in results?
  ├─ YES → Trigger SOS
  │         ↓
  │         LocationHelper.getCurrentLocation()
  │         ↓
  │         Generate Maps Link
  │         ↓
  │         SmsHelper.sendSOStoMultipleContacts()
  │         ↓
  │         Show Notification
  │         ↓
  │         Continue Listening
  │
  └─ NO → Continue Listening
  ↓
END (when disabled)
```

---

## 📱 Screen Navigation

```
┌──────────────────────────────┐
│      MAIN ACTIVITY           │
│   (Map + SOS Button)         │
│  ┌──────┬──────┬───────┬────┐│
│  │Send  │Contact│Voice │Set ││
│  │Alert │      │Actv. │tings│
│  └──┬───┴──┬───┴───┬──┴────┘│
└─────┼──────┼───────┼────────┘
      │      │       │
      ↓      ↓       ↓
   ┌──┴──┐ ┌─┴──┐  ┌─┴──┐
   │Send │ │   │  │Voice│
   │Alert│ │ C │  │Actv.│
   │Actv.│ │ O │  │     │
   │     │ │ N │  │     │
   │     │ │ T │  │     │
   │     │ │ A │  │     │
   │     │ │ C │  │     │
   │     │ │ T │  │     │
   │     │ │ S │  │     │
   │ (back)│  │  │ (back)
   └──┬──┘ └─┬──┘ └────┬─┘
      │      │ (+ add/edit/delete)
      └──────┴─────┬───┘
                   ↓
             ┌─────────┐
             │Settings │
             │ Activity│
             │ (back)  │
             └────┬────┘
                  ↓
            [Settings saved]
```

---

## 💾 Data Model

```
┌─────────────────────────────────┐
│    EmergencyContact (Room Entity)│
├─────────────────────────────────┤
│ @Entity("emergency_contacts")   │
│                                 │
│ id: Int (PK)                    │
│ name: String                    │
│ phone: String                   │
│ isEmergencyContact: Boolean     │
│ isTrackingEnabled: Boolean      │
│ createdAt: Long                 │
└─────────────────────────────────┘
         ↓ (CRUD via)
┌─────────────────────────────────┐
│ EmergencyContactDao             │
├─────────────────────────────────┤
│ insertContact()                 │
│ updateContact()                 │
│ deleteContact()                 │
│ getAllContacts()                │
│ getEmergencyContacts()          │
│ getContactById()                │
└─────────────────────────────────┘
         ↓ (managed by)
┌─────────────────────────────────┐
│ ResQtalkDatabase                │
│ (Room Database Singleton)       │
└─────────────────────────────────┘
         ↓ (stored in)
         ↓
   [SQLite Database]
   emergency_contacts table
```

---

## 🔐 Permission Flow

```
App Installation
       ↓
AndroidManifest.xml declares:
├─ 13 dangerous/special permissions
├─ Services with foreground types
├─ Activities with launcher intent
└─ Broadcast receivers with boot action
       ↓
App First Launch (MainActivity)
       ↓
requestPermissions() called
├─ Request LOCATION (2 permissions)
├─ Request SMS
├─ Request AUDIO
└─ Request NOTIFICATIONS
       ↓
User sees permission dialogs
       ↓
Permissions Granted?
├─ YES → Features enabled
└─ NO → Graceful degradation
```

---

## 🎤 Voice Detection State Machine

```
┌─────────┐
│ STOPPED │
└────┬────┘
     │ startVoiceListener()
     ↓
┌───────────────────────────┐
│ INITIALIZING RECOGNIZER   │
└────┬────────────────────┬─┘
     │ Success            │ Error
     ↓                    ↓
┌──────────────────┐  ┌─────────┐
│ LISTENING        │  │ STOPPED │
│ (Foreground Svc) │  │ (retry) │
└────┬─────────────┘  └────┬────┘
     │ Speech detected     │
     ↓                     │
┌────────────────┐        │
│ PROCESSING     │        │
├────────────────┤        │
│ Keyword Match? │        │
└─┬────────┬─────┘        │
  │ YES    │ NO           │
  │        └──────────────┘
  │                │
  ↓                ↓
┌─────────┐   ┌──────────────┐
│ TRIGGER │   │ CONTINUE     │
│ SOS     │   │ LISTENING    │
└────┬────┘   └────┬─────────┘
     │             │
     └─────┬───────┘
           ↓
┌─────────────────────────┐
│ Continue Listening or   │
│ Stop on User Request    │
└────┬────────────────────┘
     │ stopVoiceListener()
     ↓
┌─────────┐
│ STOPPED │
└─────────┘
```

---

## 📍 Location Service Lifecycle

```
App Start
   ↓
CHECK: Is tracking enabled?
   ├─ YES → START LocationUpdateService
   │         ↓
   │      Create Foreground Notification
   │         ↓
   │      Request Location Updates
   │         ↓
   │      LocationCallback registered
   │         ↓
   │      Background updates (every 1 min)
   │         ↓
   │      Broadcast location updates
   │         ↓
   │      Run indefinitely until disabled
   │
   └─ NO → Service not started
           ↓
           User enables in Settings
           ↓
           startLocationTracking() called
           ↓
           Service starts
```

---

## 🚀 Service Startup Flow

```
DEVICE BOOTS
   ↓
System sends BOOT_COMPLETED
   ↓
BootReceiver.onReceive()
   ↓
Read from SharedPreferences
├─ voice_enabled?
│  └─ YES → Start VoiceListenerService
└─ tracking_enabled?
   └─ YES → Start LocationUpdateService
   ↓
Both services show foreground notifications
   ↓
User sees "ResQtalk is running"
   ↓
Services listen for trigger + location updates
```

---

## 📞 SOS Sending Flow

```
SOS Triggered (Voice or Manual Button)
   ↓
LocationHelper.getCurrentLocation()
   ├─ Requests location from FusedLocationClient
   ├─ Returns LatLng or null
   └─ Generates maps link
   ↓
Get SOS Message from SharedPrefs
   ↓
Get Emergency Contacts from Database
   └─ Query: emergency_contacts WHERE isEmergencyContact=1
   ↓
Create Final Message
   ├─ Base message
   ├─ + Maps link (if location available)
   └─ Result: "Help! [location link]"
   ↓
SmsHelper.sendSOStoMultipleContacts()
   ├─ For each contact phone number:
   │  ├─ Divide message into parts
   │  └─ Send via SmsManager
   └─ Return count of sent
   ↓
Show Notification
   └─ "SOS sent to X contacts!"
   ↓
Continue service (if voice)
```

---

## 🎨 UI Component Hierarchy

```
MainActivity
├─ FrameLayout (Map Container)
│  └─ MapFragment
│     └─ GoogleMap
├─ BottomAppBar
│  ├─ Button (Send Alert)
│  ├─ Button (Contacts)
│  ├─ Button (Voice)
│  └─ Button (Settings)
└─ FloatingActionButton (SOS)
   └─ 120x120 red button

SendAlertActivity
├─ EditText (Message)
├─ Switch (Include Location)
├─ Spinner (Contact Selector)
└─ Button (Send)

ContactsActivity
├─ RecyclerView
│  └─ ContactAdapter
│     └─ ContactItem
│        ├─ TextView (Name)
│        ├─ TextView (Phone)
│        ├─ Switch (Emergency)
│        ├─ Button (Edit)
│        └─ Button (Delete)
└─ Button (Add)

VoiceActivationActivity
├─ Switch (Enable)
├─ EditText (Trigger Word)
├─ Button (Test)
└─ Button (Save)

SettingsActivity
├─ EditText (SOS Message)
├─ Switch (Tracking)
├─ Switch (Vibration)
├─ TextView (Privacy Notice)
└─ Button (Save)
```

---

## 🔗 Class Dependency Graph

```
MainActivity
├─ LocationHelper
├─ SmsHelper
├─ SharedPrefsHelper
├─ ResQtalkDatabase
└─ VoiceListenerService

SendAlertActivity
├─ LocationHelper
├─ SmsHelper
├─ SharedPrefsHelper
└─ ResQtalkDatabase

ContactsActivity
├─ ResQtalkDatabase
└─ ContactAdapter

VoiceActivationActivity
├─ SharedPrefsHelper
└─ VoiceListenerService

SettingsActivity
├─ SharedPrefsHelper
└─ LocationUpdateService

VoiceListenerService
├─ VoiceTriggerHelper
├─ LocationHelper
├─ SmsHelper
├─ SharedPrefsHelper
└─ ResQtalkDatabase

LocationUpdateService
├─ SharedPrefsHelper
└─ FusedLocationProviderClient

BootReceiver
├─ SharedPrefsHelper
├─ VoiceListenerService
└─ LocationUpdateService

ContactAdapter
└─ EmergencyContact

ResQtalkDatabase
├─ EmergencyContact
└─ EmergencyContactDao
```

---

## 📊 Data Flow Diagram

```
User Input
├─ Speak (Voice)
├─ Click SOS Button
├─ Fill Forms
└─ Change Settings
   ↓
   Processing Layer
   ├─ VoiceTriggerHelper (keyword detection)
   ├─ LocationHelper (GPS fetch)
   ├─ SmsHelper (message send)
   └─ SharedPrefsHelper (read/write settings)
   ↓
   Storage Layer
   ├─ Room Database (contacts)
   └─ SharedPreferences (settings)
   ↓
   External Services
   ├─ FusedLocationClient (location)
   ├─ SmsManager (SMS)
   ├─ SpeechRecognizer (voice)
   └─ Google Maps (visualization)
   ↓
   Output
   ├─ Map display
   ├─ SMS notifications
   ├─ Toast/Snackbar feedback
   └─ Notification alerts
```

---

## 🎯 Module Interaction

```
┌─────────────────────────────────────────────────────────┐
│              USER INTERFACE LAYER                       │
│    Activities handle user input and display output      │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────┴──────────────────────────────────┐
│         SERVICE & HELPER LAYER                         │
│   Process logic, fetch data, coordinate actions        │
├──────────┬──────────┬───────────┬──────────┬──────────┐
│Location  │   SMS    │  Voice    │Settings  │Database  │
│Helper    │ Helper   │Trigger    │ Storage  │Access    │
│          │          │ Helper    │          │          │
└──────────┴──────────┴───────────┴──────────┴──────────┘
           │
┌──────────┴──────────────────────────────────────────────┐
│          BACKGROUND SERVICES LAYER                     │
│   Run continuously for voice/location updates          │
├──────────────────────┬────────────────────────────────┐
│ VoiceListener        │ LocationUpdate                 │
│ Service              │ Service                        │
│                      │                                │
│ • Foreground Notif   │ • Foreground Notif             │
│ • SpeechRecognizer   │ • Location Updates             │
│ • Trigger Detection  │ • Broadcasts                   │
└──────────────────────┴────────────────────────────────┘
           │
┌──────────┴──────────────────────────────────────────────┐
│              SYSTEM LAYER                              │
│   Android OS, GPS, SMS, Google Services               │
├──────┬──────┬──────┬──────┬──────┬────────────────────┐
│ GPS  │ SMS  │Voice │Network│Maps │ Database (SQLite)  │
└──────┴──────┴──────┴──────┴──────┴────────────────────┘
```

---

## 🔄 State Persistence

```
App State (In Memory)
├─ Current Location
├─ UI State
├─ Service Status
└─ Network Connection Status
   ↓ Saved to
┌──────────────────────────────┐
│   SharedPreferences          │
│   ("resqtalk_prefs")         │
│   ├─ trigger_word            │
│   ├─ sos_message             │
│   ├─ voice_enabled           │
│   ├─ tracking_enabled        │
│   ├─ vibration_enabled       │
│   └─ alert_tone              │
└──────────────────────────────┘
   ↓ Saved to
┌──────────────────────────────┐
│   Room Database (SQLite)     │
│   ├─ EmergencyContact        │
│   │  ├─ ID                   │
│   │  ├─ Name                 │
│   │  ├─ Phone                │
│   │  ├─ Emergency Status     │
│   │  ├─ Tracking Status      │
│   │  └─ Created Date         │
│   └─ emergency_contacts table│
└──────────────────────────────┘

Device Restart
   ↓
BootReceiver triggered
   ↓
Read from SharedPreferences
   ↓
Restore settings
   ↓
Start services as needed
   ↓
App returns to previous state
```

---

## 🎯 Test Scenarios Flow

```
Scenario 1: Basic Setup
  Add Contact → Enable Voice → Save Settings → ✅

Scenario 2: Manual SOS
  Open App → Click SOS → Verify SMS → Check Location → ✅

Scenario 3: Voice Activation
  Enable → Minimize App → Speak Trigger → Verify SOS → ✅

Scenario 4: Background Service
  Enable → Close App → Wait → Speak Trigger → Verify SOS → ✅

Scenario 5: Device Boot
  Enable Services → Restart Phone → Wait → Verify Active → ✅

Scenario 6: Contact Management
  Add → Edit → Delete → Verify DB Update → ✅

Scenario 7: Location Tracking
  Enable Tracking → Wait → Check Updates → Verify Broadcasts → ✅
```

---

## 📈 App Lifecycle

```
Installation
   ↓
First Run
   ├─ Request Permissions
   ├─ Initialize Database
   ├─ Load Default Settings
   └─ Show Home Screen
   ↓
Normal Operation
   ├─ User adds contacts
   ├─ User customizes settings
   ├─ User enables voice/tracking
   └─ Services run in background
   ↓
Device Reboot
   ├─ BootReceiver triggered
   ├─ Read SharedPreferences
   ├─ Auto-start services
   └─ Resume operation
   ↓
Continuous
   ├─ Listen for voice
   ├─ Update location
   ├─ Persist data
   └─ Respond to SOS trigger
```

---

**Complete Visual Architecture Map Generated!** ✅

This document provides a visual understanding of:
- Application architecture
- Data flow
- Service lifecycle
- Screen navigation
- Component interaction
- State management
- Test scenarios

---

**Last Updated**: November 18, 2025
