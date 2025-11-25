# ResQtalk - SMS Troubleshooting Guide

## Issue: SMS Not Sending

The app has been updated to properly request **SEND_SMS runtime permission** before attempting to send alerts.

---

## What Was Fixed

✅ Added runtime permission check for `SEND_SMS`
✅ Permission request dialog now appears before sending SMS
✅ User must grant permission in the dialog

---

## How to Test SMS Sending

### Step 1: Reinstall the App
1. Uninstall the old version from your phone
2. Download the new APK from Android Studio
3. Open the app

### Step 2: First Time Using "Send Alert"
1. Go to **SendAlertActivity** (button from main screen)
2. Enter a message
3. Click **"Send Alert"**
4. **A permission dialog will appear** asking to allow SMS
5. **Tap "Allow"** or **"Allow Only While Using the App"**
6. The SMS will be sent to the first contact in your list

### Step 3: Verify SMS Was Sent
- Check your SMS app on the phone
- Look for the message you sent
- **Check the recipient's phone** to confirm they received it

---

## Requirements for SMS to Work

### 1. **SMS Permission Granted**
   - ✅ App requests it at runtime
   - ✅ You must tap "Allow"
   - ⚠️ If you tap "Deny", SMS won't send

### 2. **Contacts Saved**
   - Go to **Contacts** in the app
   - Add at least one emergency contact with phone number
   - Tap **Save Contact**

### 3. **Valid Phone Numbers**
   - Phone numbers must be in format: `+1234567890` or `1234567890`
   - Must include country code or area code
   - Must be a real, valid phone number

### 4. **Internet Connection**
   - WiFi or mobile data required
   - Necessary for location (if enabled)

### 5. **SMS Service Active**
   - Phone must have SMS service active
   - Some countries restrict SMS sending
   - Check your carrier settings

---

## Troubleshooting Steps

### "Permission Required" Message Appears
**Problem:** SMS permission not granted
**Solution:** 
1. Go to Android Settings
2. Settings → Apps → ResQtalk → Permissions
3. Enable "Send SMS"
4. Restart the app

### "No Contacts Available" Message
**Problem:** No emergency contacts saved
**Solution:**
1. Open ResQtalk
2. Go to **Contacts** tab
3. Enter name and phone number
4. Tap **Save Contact**
5. Try sending alert again

### SMS Sent Toast Shows, But Message Not Received
**Problem:** SMS may be pending or failed silently
**Solution:**
1. Check SMS app on your phone
2. Look in **Sent Messages** folder
3. Verify recipient phone number is correct
4. Try sending to a different number
5. Check carrier SMS balance (some prepaid plans need credits)

### "Invalid Resource ID" Error in Logs
**Problem:** Theme or layout issue (not SMS related)
**Solution:**
- This is a warning, SMS should still work
- Contact developer if SMS still fails

---

## Testing With Android Emulator

If using Android emulator:

### Option 1: Using Fake SMS App
1. Install "Fake SMS" from Play Store on emulator
2. Configure it to receive test SMS
3. Send SMS from your ResQtalk app
4. Check if Fake SMS app receives it

### Option 2: Using Real Phone
1. Transfer APK to real phone
2. Install APK manually
3. Test with real contacts and SMS service
4. This is most reliable way to test

---

## Advanced: Enable SMS Debugging

### To See Detailed SMS Logs:
1. Connect phone via USB
2. Open Android Studio
3. Go to **Logcat** tab
4. Search for: `SmsHelper`
5. Try sending SMS
6. Check the detailed error message

### Common Log Messages:
```
✅ SMS Sent Successfully
E/SmsHelper: Error sending SMS - Permission denied
E/SmsHelper: Error sending SMS - No SMS service
```

---

## Manual SMS Send Test (Without App)

1. Open stock **Messages/SMS app**
2. Create new message
3. Enter phone number of a contact
4. Type your test message
5. Send it
6. If this works, your phone's SMS service is fine
7. Then the issue is app-specific (check permissions)

---

## Code Changes Made

**File:** `SendAlertActivity.kt`

**Before:**
- No runtime permission check for SEND_SMS
- Directly called `smsHelper.sendSMS()`
- Failed silently on Android 6.0+

**After:**
- ✅ Checks `SEND_SMS` permission at runtime
- ✅ Requests permission via dialog if needed
- ✅ Only sends SMS after permission granted
- ✅ Shows error if permission denied

---

## Next Steps

1. **Rebuild the app** (done ✅)
2. **Reinstall on your phone**
3. **Test SMS sending** using steps above
4. **Grant SEND_SMS permission** when prompted
5. **Report results** (success/failure)

---

## If SMS Still Not Working

Please provide:
1. **Logcat error message** (from Android Studio)
2. **Phone model and Android version**
3. **Carrier information** (Airtel, Vodafone, etc.)
4. **Whether manual SMS test worked** (see section above)
5. **Whether permission was granted**

Then we can debug further! 🔧
