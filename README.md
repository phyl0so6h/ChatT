# Chat'T

A real-time one-to-one messenger for Android, built with Java and Firebase.

Chat'T lets users create an account with a profile photo, find other users, and exchange text and image messages that sync instantly. It shows who is online and lists recent conversations on the home screen.

![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/language-Java-007396)
![Min SDK](https://img.shields.io/badge/minSdk-24-blue)
![Firebase](https://img.shields.io/badge/backend-Firebase-FFCA28?logo=firebase&logoColor=black)
![License](https://img.shields.io/badge/license-GPL--3.0-green)

## Features

- **Email sign-up and sign-in** with Firebase Authentication. Users must verify their email before they can sign in.
- **Profile photo and unique username**, chosen at sign-up.
- **Real-time chat** for text and image messages, using Firestore snapshot listeners.
- **Recent conversations** on the home screen, sorted by the latest message.
- **Online and offline status**, updated from each user's activity lifecycle.
- **Grouped message bubbles**: consecutive messages from the same sender are drawn as one group, and the last message in a group shows a timestamp.
- **User profile screen** and a full-size avatar preview.
- **Day and night themes.**

## Tech stack

| Area | Library |
| --- | --- |
| Language | Java 11 (Android) |
| UI | AndroidX AppCompat, Material Components, ConstraintLayout, RecyclerView, ViewBinding |
| Auth | Firebase Authentication (email and password) |
| Database | Cloud Firestore |
| File storage | Firebase Storage |
| Push | Firebase Cloud Messaging and Retrofit (see [Known limitations](#known-limitations)) |
| Other | RoundedImageView, SDP/SSP scalable sizes, AndroidX Biometric |

## Getting started

### Requirements

- Android Studio (recent stable release) with JDK 17 or newer. The bundled JetBrains Runtime works.
- Android SDK 35.
- A Firebase project. The free Spark plan is enough.

### 1. Clone

```bash
git clone https://github.com/phyl0so6h/ChatT.git
cd ChatT
```

### 2. Connect Firebase

The Firebase config file isn't committed. Each developer connects their own Firebase project:

1. In the [Firebase console](https://console.firebase.google.com/), create a project and add an **Android app** with the package name `com.sahak7an.chatt`.
2. Download `google-services.json` and put it at `app/google-services.json`. Git ignores this file. `app/google-services.json.example` shows the expected structure.
3. Turn on these services:
   - **Authentication**: enable the *Email/Password* sign-in provider.
   - **Cloud Firestore**: create a database.
   - **Storage**: create the default bucket.

### 3. Build and run

Open the project in Android Studio and press **Run**, or build from the command line:

```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## Project structure

```
app/src/main/java/com/sahak7an/chatt/
├── activities/   # SignIn, SignUp, Main (conversations), Users, Chat, AboutUser, BaseActivity (online status)
├── adapters/     # RecyclerView adapters: chat bubbles, users, recent conversations
├── dialogs/      # Avatar preview and settings dialogs
├── firebase/     # MessagingService (FCM notifications)
├── listeners/    # Click callbacks for users and conversations
├── models/       # User, ChatMessage
├── network/      # Retrofit client for FCM, plus an experimental socket client and server
└── utilities/    # Constants, PreferenceManager, ImageUtils
```

## Data model (Firestore)

| Collection | Main fields |
| --- | --- |
| `users` | `username`, `email`, `image` (Base64 WebP), `fcmToken`, `online`, `ip_address`, `port` |
| `chat` | `senderId`, `receiverId`, `message`, `isImage`, `timestamp`, `count` (order within the conversation) |
| `conversations` | `senderId`, `senderUserName`, `senderImage`, `receiverId`, `receiverUserName`, `receiverImage`, `lastMessage`, `isImage`, `timestamp`, `count` |

Profile photos and chat images are downscaled and compressed with `ImageUtils` before they're stored as Base64. This keeps every document under Firestore's 1 MiB limit.

### Suggested security rules

Firestore starts in a locked or test mode. A minimal starting point that only allows signed-in users:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

Tighten these rules before any real deployment. For example, let users write only their own `users` document and only messages they sent.

## Known limitations

- **Push notifications are disabled by default.** The app calls the legacy FCM HTTP API (`fcm/send` with a server key), which Google shut down in 2024. The server key in `Constants.getRemoteMsgHeaders()` is blank on purpose. Sending pushes now needs the FCM HTTP v1 API, called from a trusted backend such as a Cloud Function triggered by new `chat` documents. A server credential must never ship inside the app.
- Images are stored inline in Firestore as Base64. Firebase Storage URLs would scale better.
- The `network/` socket classes (`Client`, `Server`, `Communication`) are an unfinished peer-to-peer experiment and aren't used by the chat.

## Recent improvements

- Upgraded the build to Gradle 8.13 and AGP 8.13 (compileSdk 35), so the project builds with current Android Studio and JDK 21.
- **Fixed:** the home screen showed only one conversation, because the list was cleared for every update.
- **Fixed:** a network error while refreshing the FCM token or signing out deleted the user's account. It now shows an error and keeps the account.
- **Fixed:** sign-out didn't sign out of Firebase Auth.
- **Fixed:** chat snapshot listeners piled up on every resume and every sent message.
- **Fixed:** new messages weren't redrawn correctly, because the wrong range was passed to `notifyItemRangeInserted`.
- **Fixed:** profile and chat images were stretched to 1920×1080 regardless of aspect ratio. This distorted the photos and could exceed Firestore's document size limit. They're now scaled proportionally and cropped in the UI.
- **Fixed:** sign-up downloaded every user profile to check whether a username was taken. It now runs a single indexed query, and the loading state resets on failure.
- **Fixed:** signing in with an unverified email silently did nothing. The app now explains the problem and re-sends the verification email.
- Removed `Thread.sleep` calls from the main thread and duplicated bitmap and IP helpers.
- Moved `google-services.json` out of version control and made `gradlew` executable.

## License

Distributed under the GNU General Public License v3.0. See [LICENSE](LICENSE).
