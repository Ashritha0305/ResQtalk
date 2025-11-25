# Quick SMS Testing Guide

## 🎯 The Fix
**Problem:** App wasn't requesting SMS permission  
**Solution:** Added runtime permission check before sending SMS  
**Status:** ✅ Fixed and rebuilt

---

## ⚡ Quick Test (5 Minutes)

### 1. Reinstall App
```
Uninstall old version
Install new APK from Android Studio
```

### 2. Add Contact (if needed)
```
ResQtalk → Contacts Tab
+ Add new contact
Name: John
Phone: +1234567890  (use a real number)
Tap Save
```

### 3. Send Test SMS
```
ResQtalk → Send Alert Button
Enter message: "Test message"
Tap "Send Alert"
⬇️ IMPORTANT: Grant SMS permission when dialog appears
✅ Toast shows "Alert sent!"
✅ Check SMS app to verify message sent
```

---

## 📊 Expected Results

| Action | Before Fix | After Fix |
|--------|-----------|-----------|
| Click "Send Alert" | ❌ SMS fails silently | ✅ Permission dialog |
| Permission Granted | N/A | ✅ SMS sends |
| Check SMS App | ❌ No message | ✅ Message found |

---

## ⚠️ Common Issues & Fixes

| Issue | Solution |
|-------|----------|
| "Permission dialog not showing" | App may need reinstall |
| "Alert sent but SMS not received" | Check recipient number format |
| "No contacts available" | Add contact in Contacts tab first |
| "Allow button greyed out" | Restart phone and try again |

---

## 📝 Your Checklist

- [ ] Uninstall old app
- [ ] Install new APK
- [ ] Add emergency contact
- [ ] Test SMS sending
- [ ] Grant permission when asked
- [ ] Verify SMS received
- [ ] Report back! ✅

---

## 🆘 If Still Not Working

Check 3 things:
1. Permission granted? (Settings → ResQtalk → Permissions → SEND_SMS)
2. Contact has valid phone number?
3. Stock SMS app works? (test with Messages app)

If all 3 yes but SMS still fails → provide Logcat error from Android Studio

---

**Build Status:** ✅ BUILD SUCCESSFUL  
**Ready to Test:** ✅ YES
