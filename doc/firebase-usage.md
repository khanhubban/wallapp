## Firebase Usage

This project makes extensive use of Firebase services, including:
* Authentication
* Firestore
* Remote Config
* Storage
* Crashlytics

Typically, the app won't run unless properly configured with Firebase. However, for the open source release, numerous modifications were made to remove Firebase dependencies.

If you want to fork this project for your own use, you will need to set up your own Firebase project and configure it accordingly.

## Configuring a new project with Firebase

1. Create a new Firebase project in the [Firebase Console](https://console.firebase.google.com/).
2. Configure the project for iOS and Android:
   * For iOS, register your app with your app's bundle identifier. Download and configure the `GoogleService-Info.plist` file.
   * For Android, register your app with your app's package name. Download the `google-services.json` file and place it in the `app/android` directory.
3. Enable the services listed above.
4. Manually update `GoogleSignInFactory.kt`'s `requestIdToken` with your Android client ID. Ideally `R.string.default_web_client_id` would be used to avoid hardcoding, but this value is inaccessible from `GoogleSignInFactory.kt`. 
5. Manually update `Info-Prod.plist` and `Info-Dev.plist`'s `CFBundleURLSchemes` definition with your `REVERSED_CLIENT_ID`.
6. Find and replace all instances of `"panels-oss"` across the project with the name of your Firebase project.

Note: If you discover any steps that are missing or out of date, please consider submitting a pull request to improve this documentation.

## Firebase Systems

### Authentication

WallApp uses Firebase Authentication to manage user sign-in and sign-up. The app supports:
* Anonymous sign-in
* Google sign-in
* Apple sign-in (iOS only)

Anonymous user support is required to access API data which is hosted in Firebase Storage. See `./shared/data/account*`.

### Firestore
Firestore is used to store user data, such as email, favorites, etc. See `./shared/data/account*`.

The Cloud Firestore Database Rules used in the project are as follows:
```
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      function currentUser() {
        return request.auth.uid == userId;
      }
      function userIdValid() {
        return request.resource.data.userId == userId;
      }
      function epochCreatedValid() {
        return request.resource.data.epochCreated is number;
      }
      function epochLastUpdatedValid() {
        return request.resource.data.epochLastUpdated is number;
      }
      function epochLastSeenValid() {
        return request.resource.data.epochLastSeen is number;
      }
      function isAnonymousValid() {
        return request.resource.data.isAnonymous is bool;
      }
      function favoriteIdsValid() {
        return request.resource.data.favoriteIds == null || 
          request.resource.data.favoriteIds is list;
      }
      function currentWallpaperIdsValid() {
        return request.resource.data.currentWallpaperIds == null || 
          request.resource.data.currentWallpaperIds is map;
      }
      function dataVersionValid() {
        return request.resource.data.dataVersion == null ||
          request.resource.data.dataVersion is number;
      }
      function followingIdsValid() {
        return request.resource.data.followingIds == null ||
          request.resource.data.followingIds is list;
      }
      function purchaseRecordsValid() {
        return request.resource.data.purchaseRecords == null ||
          request.resource.data.purchaseRecords is list;
      }
      function deviceInfoValid() {
        return request.resource.data.deviceInfo == null ||
            request.resource.data.deviceInfo is string;
      }
      function currencyValid() {
        return request.resource.data.currency == null ||
            request.resource.data.currency is string;
      }
      function newsletterValid() {
        return request.resource.data.newsletter == null ||
            request.resource.data.newsletter is bool;
      }
      function flagsValid() {
        return request.resource.data.flags == null ||
            request.resource.data.flags is list;
      }
      function wallpaperDownloadEventsValid() {
        return request.resource.data.wallpaperDownloadEvents == null ||
            request.resource.data.wallpaperDownloadEvents is list;
      }
      function wallpaperDownloadEvents2Valid() {
        return request.resource.data.wallpaperDownloadEvents2 == null ||
            request.resource.data.wallpaperDownloadEvents2 is list;
      }
      function receiveNotificationsValid() {
        return request.resource.data.receiveNotifications == null ||
            request.resource.data.receiveNotifications is bool;
      }
      function accountDeletedValid() {
        return request.resource.data.accountDeleted == null ||
            request.resource.data.accountDeleted is bool;
      }
      function loginTypeValid() {
        return request.resource.data.loginType is string &&
          request.resource.data["loginType"] in [
            "firebase/anonymous",
            "firebase/google",
            "firebase/apple",
            "none"
          ];
      }
      function emailValid() {
        return request.resource.data.email == null || (request.resource.data.email is string &&
          request.resource.data.email.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$"));
      }

      function createDataIsValid() {
        let requiredFields = [
          'userId', 
          'epochCreated', 
          'epochLastUpdated',
          'epochLastSeen', 
          'isAnonymous', 
          'loginType'
        ];
        let optionalFields = [
          'email',
          'favoriteIds',
          'currentWallpaperIds',
          'dataVersion',
          'followingIds',
          'purchaseRecords',
          'deviceInfo',
          'currency',
          'newsletter',
          'flags',
          'wallpaperDownloadEvents',
          'wallpaperDownloadEvents2',
          'receiveNotifications',
          'accountDeleted'
        ];
        let allowedFields = requiredFields.concat(optionalFields);
        let incomingFields = request.resource.data.keys();
        return incomingFields.hasOnly(allowedFields) &&
          incomingFields.hasAll(requiredFields) &&
          userIdValid() &&
          epochCreatedValid() &&
          epochLastUpdatedValid() &&
          epochLastSeenValid() &&
          isAnonymousValid() &&
          loginTypeValid() &&
          nonMandatoryFieldsAreValid();
      }
      function nonMandatoryFieldsAreValid() {
        let incomingFields = request.resource.data.keys();
        return (!incomingFields.hasAny(['email']) || emailValid()) &&
          (!incomingFields.hasAny(['favoriteIds']) || favoriteIdsValid()) &&
          (!incomingFields.hasAny(['currentWallpaperIds']) || currentWallpaperIdsValid()) &&
          (!incomingFields.hasAny(['dataVersion']) || dataVersionValid()) &&
          (!incomingFields.hasAny(['followingIds']) || followingIdsValid()) &&
          (!incomingFields.hasAny(['purchaseRecords']) || purchaseRecordsValid()) &&
          (!incomingFields.hasAny(['deviceInfo']) || deviceInfoValid()) &&
          (!incomingFields.hasAny(['currency']) || currencyValid()) &&
          (!incomingFields.hasAny(['newsletter']) || newsletterValid()) &&
          (!incomingFields.hasAny(['flags']) || flagsValid()) &&
          (!incomingFields.hasAny(['wallpaperDownloadEvents']) || wallpaperDownloadEventsValid()) && 
          (!incomingFields.hasAny(['wallpaperDownloadEvents2']) || wallpaperDownloadEvents2Valid()) &&
          (!incomingFields.hasAny(['receiveNotifications']) || receiveNotificationsValid()) &&
          (!incomingFields.hasAny(['accountDeleted']) || accountDeletedValid());
      }

      function updateDataIsValid() {
        let fixedFields = ['userId', 'epochCreated'];
        let allowedFields = fixedFields.concat([
          'epochLastUpdated',
            'epochLastSeen',
            'isAnonymous',
            'loginType',
            'email',
            'favoriteIds',
            'currentWallpaperIds',
            'dataVersion',
            'followingIds',
            'purchaseRecords',
            'deviceInfo',
            'currency',
            'newsletter',
            'flags',
            'wallpaperDownloadEvents',
            'wallpaperDownloadEvents2',
            'receiveNotifications',
            'accountDeleted'
        ]);
        let incomingFields = request.resource.data.keys();
        // Check if all incoming fields are the ones that are allowed and nothing else
        return incomingFields.hasOnly(allowedFields) &&
          // Check if other fields are not being updated
          fixedFieldsAreNotBeingUpdated() &&
          requiredFieldsArePresentAndValid() &&
          // Check if fields are valid
          updatedFieldsAreValid(incomingFields) &&
          isAnonymousUpdateValid(incomingFields) &&
          loginTypeUpdateValid(incomingFields);
      }
      function requiredFieldsArePresentAndValid() {
        let requiredFields = [
          'userId', 
          'epochCreated', 
          'epochLastUpdated',
          'epochLastSeen', 
          'isAnonymous', 
          'loginType'
        ];
        return request.resource.data.keys().hasAll(requiredFields) &&
          userIdValid() &&
          epochCreatedValid() &&
          epochLastUpdatedValid() &&
          epochLastSeenValid() &&
          isAnonymousValid() &&
          loginTypeValid();
      }
      
      function fixedFieldsAreNotBeingUpdated() {
        return request.resource.data.userId == resource.data.userId &&
          request.resource.data.epochCreated == resource.data.epochCreated;
      }

      function updatedFieldsAreValid(incomingFields) {
        return (!incomingFields.hasAny(['epochLastUpdated']) || epochLastUpdatedValid()) &&
          (!incomingFields.hasAny(['epochLastSeen']) || epochLastSeenValid()) &&
          (!incomingFields.hasAny(['isAnonymous']) || isAnonymousValid()) &&
          (!incomingFields.hasAny(['loginType']) || loginTypeValid()) &&
          (!incomingFields.hasAny(['email']) || emailValid()) &&
          (!incomingFields.hasAny(['favoriteIds']) || favoriteIdsValid()) &&
          (!incomingFields.hasAny(['currentWallpaperIds']) || currentWallpaperIdsValid()) &&
          (!incomingFields.hasAny(['dataVersion']) || dataVersionValid()) &&
          (!incomingFields.hasAny(['followingIds']) || followingIdsValid()) &&
          (!incomingFields.hasAny(['purchaseRecords']) || purchaseRecordsValid()) &&
          (!incomingFields.hasAny(['deviceInfo']) || deviceInfoValid()) &&
          (!incomingFields.hasAny(['currency']) || currencyValid()) &&
          (!incomingFields.hasAny(['newsletter']) || newsletterValid()) &&
          (!incomingFields.hasAny(['flags']) || flagsValid()) &&
          (!incomingFields.hasAny(['wallpaperDownloadEvents']) || wallpaperDownloadEventsValid()) &&
          (!incomingFields.hasAny(['wallpaperDownloadEvents2']) || wallpaperDownloadEvents2Valid()) &&
          (!incomingFields.hasAny(['receiveNotifications']) || receiveNotificationsValid()) &&
          (!incomingFields.hasAny(['accountDeleted']) || accountDeletedValid());
      }
      // Anonymous users can't become non-anonymous
      function isAnonymousUpdateValid(incomingFields) {
        return (!incomingFields.hasAny(['isAnonymous']) || resource.data.isAnonymous == request.resource.data.isAnonymous ||
            (resource.data.isAnonymous == true && request.resource.data.isAnonymous == false));
      }
      // Login type can't be changed from non-anonymous to anonymous
      function loginTypeUpdateValid(incomingFields) {
        return (!incomingFields.hasAny(['loginType']) || resource.data.loginType == request.resource.data.loginType ||
            (request.resource.data.loginType == "none") ||
            (resource.data.loginType == "firebase/anonymous" && request.resource.data.loginType == "firebase/google") ||
            (resource.data.loginType == "firebase/anonymous" && request.resource.data.loginType == "firebase/apple"));
      }

      allow read: if currentUser();
      allow create: if currentUser() &&
        createDataIsValid();
      allow update: if currentUser() &&
        updateDataIsValid();
    }
  }
}
```

### Remote Config
Remote Config is used to manage feature flags and other configuration settings remotely. See `./shared/data/remoteconfig*`.

### Storage
Firebase Storage is used to host the API data. The project is configured such that an account authenticated via Firebase Authentication is required to read data. This can be an anonymous account.
See `FirebaseStorageDownloader` and its implementations.  

The Cloud Storage Security Rules used in the project are as follows:
```
rules_version = '2';

service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read: if request.auth != null;
    }
  }
}
```
