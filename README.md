# PermissionsAndLocationPicker
This project demonstrates how to:
- Request runtime permissions for accessing the user's location.
- Retrieve the user's current location using the `FusedLocationProviderClient`.

---

## Features
- **Runtime Permissions**: Dynamically request and handle permissions at runtime.
- **Location Retrieval**: Use the `FusedLocationProviderClient` for precise location data.
- **Kotlin Language**: Built with Kotlin, leveraging its modern and concise syntax.

---

## Prerequisites
- Android Studio **Chipmunk** or later.
- Kotlin **1.5** or later.
- Google Play Services installed on the target device.
- Minimum SDK **21** (Android 5.0 Lollipop).

---

## Permissions Required
To access the user's location, the following permissions are required:
1. **ACCESS_FINE_LOCATION**: For precise location data.
2. **ACCESS_COARSE_LOCATION**: For approximate location data.

Add these permissions in the `AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

 
