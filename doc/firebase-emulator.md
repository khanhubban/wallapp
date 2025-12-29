Instructions for running the Firebase emulator

# Configure Firebase Emulator

## Install and configure the Firebase CLI

### Install Firebase CLI

Next, install the Firebase CLI globally using `npm`:

1. Run the following command:
    `npm install -g firebase-tools`

2. Verify the installation:
    `firebase --version`

### Log in to Firebase

1. Run the login command:
    `firebase login`

2. This will open a browser window, prompting you to log in with your Google account. After logging in, return to your terminal.

## Run Firebase Emulator

### Initialize Firebase Project

1. Navigate to your project directory:
    `cd /path/to/wallApp/firebase-backend`

2. Initialize Firebase in the project:
   `firebase init`

3. Select the following options (use the arrow keys to navigate and the space bar to select):
    - `Emulators`
    - `Firestore`
    - `Storage`

Press `Enter` to confirm your selections.

In the subsequent prompts, select the default options (`"N"`) by pressing Enter. This will use the files already in the `./firebase-backend` directory.

### Start Firebase Emulators

To start your local emulators for Firestore, Authentication, and Storage, run:
    `firebase emulators:start`

Upon a successful connection you should see the terminal display:

```
┌─────────────────────────────────────────────────────────────┐
│ ✔  All emulators ready! It is now safe to connect your app. │
│ i  View Emulator UI at http://127.0.0.1:4000/               │
└─────────────────────────────────────────────────────────────┘
```

### Open Emulator UI

http://localhost:4000/storage/panels-oss.appspot.com

### Upload API files to Firebase Storage

Upload API data to the Emulator's Firebase Storage.

Note: you must update the full path. As an example, API files must have a `api/YYYYMMDD/` folder prefix.

### Configure the app to use the local emulator

`./shared/data/account-api/src/commonMain/kotlin/wallapp/account/AccountManager.kt#L40:`

```
const val RUN_FIREBASE_ON_LOCAL_EMULATORS = true
```

This will work for iOS and Android.

Note: because of Remote API data caching, you may load the app and not see your data changes. To fix this, do a clean app install.
