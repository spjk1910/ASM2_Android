# Assignment 2 - Android Development  

**Student ID:** s4027648  
**Student Name:** Doan Phan Thuy Trang  
**Course:** COSC2657 - Android Development  
**Assignment:** Assignment 2  

---

## Functionality of the App  

### 1. User Authentication  
- Login with username/email and password.  
- Sign up for new users (Donors and Site Managers).  
- Reset password via email verification.  

### 2. User Roles  
- Unique home screens for Donors, Site Managers, and Super Users.  
- Role-based access control.  

### 3. Permissions  
- Request permissions for Internet, Location, Network State, Notifications, and Storage.  

### 4. User Interface  
- Full-screen immersive mode.  
- Loading screens during transitions.  

### 5. Location Services  
- Display maps using Google Maps SDK.  
- Geocode data using the Geocoding API.  
- Search and view details of places via the Places API.  

### 6. User Data Management  
- Store user data in Firebase Firestore.  
- Store user avatars in Firebase Storage.  

### 7. PDF Saving  
- Generate and save PDFs using the PdfDocument API.  
- Store PDFs in device storage.  

### 8. Main Functionalities  
#### Donors  
- Locate nearby blood donation drives (within 5km).  
- Filter drives by blood type and start date.  
- Register for events and view registered events.  
- Cancel the registered events.  
- Find route from current location to the donation sites.  
- Receive notifications for event updates.  
- Edit personal profile (info and avatar).  

#### Blood Donation Site Managers  
- Set up new blood donation sites.  
- Download donor info as PDFs.  
- Modify site information.  
- Register for events and edit profile.  

#### Super Users  
- View all donation sites.  
- Generate and download site reports (PDFs).  
- Edit personal profile.  

---

## Technologies Used  

1. **Java**  
   - Primary language for app development.  

2. **Firebase Firestore**  
   - Stores user and app data.  

3. **Firebase Storage**  
   - Stores user avatars.  

4. **Google Maps SDK for Android**  
   - Displays maps and location data.  

5. **Google Geocoding API**  
   - Converts coordinates to addresses.  

6. **Google Places API**  
   - Provides location search and details.  

7. **Broadcast Receiver**  
   - Detects internet connectivity during login.  

8. **Android Studio**  
   - IDE for development and debugging.  

9. **Gradle**  
   - Manages dependencies and builds.  

10. **SharedPreferences**  
    - Stores user preferences and session data.  

11. **ActivityCompat and ContextCompat**  
    - Provides backward compatibility for resources and permissions.  

12. **PdfDocument API**  
    - Generates and saves PDFs.  

---

## Drawbacks  

1. **Limited Error Handling**  
   - Basic error handling; may crash on null objects.  

2. **Dependency on Internet**  
   - Requires internet for Firebase and Google Cloud services.  
   - Broadcast Receiver only checks connectivity during login.  

3. **Role Management Constraints**  
   - Hardcoded roles, limiting future extensions.  

---

## Known Bugs and Limitations  

1. **Duplicate Event Names**  
   - Event IDs are created using the format `siteManagerUsername + eventName`.  
   - If the same user creates events with duplicate names, the app may break (similarly for register collections).  
   - Needs handling in future updates.  

2. **Custom Info Sheet**  
   - Initially intended to place `CustomInfoWindowAdapter` in the Controller package.  
   - Program error occurred due to missing file; had to move it to the Model package.  

3. **User Avatar Management**  
   - If a user changes their avatar or deletes their account, the old avatar remains in cloud storage.  
   - Needs a cleanup mechanism for orphaned files.  

4. **Phone Number Validation**  
   - Currently, no validation for phone numbers. Users can enter letters.  
   - Plan to implement proper validation in future updates.  

5. **Total Blood Amount Calculation**  
   - Intended to calculate the total blood amount based on individual donor contributions.  
   - Modifying the value is complex for Site Managers due to the required updates.  
   - Plan to simplify and implement this feature later.  

6. **Notifications**  
   - Planned to use Firebase Cloud Messaging (FCM) for push notifications.  
   - Due to limited time, this feature was not implemented or tested.  
   - May add in a future update.  

---

## GitHub Repository Link  

[ASM2_Android Repository](https://github.com/spjk1910/ASM2_Android.git)  
