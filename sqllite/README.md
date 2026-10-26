# SQLite Names – Android App

Android app (Java, XML, `SQLiteOpenHelper`) that stores names in a local SQLite database and displays them.

## Features
- **Add Name** – validates input, then saves to SQLite.
- **Display All Names** – lists every stored name with a count.
- Inline error + Toast message for every outcome.
- Data persists after the app is closed.

## Validation
| Input | Message |
|---|---|
| Empty / spaces only | Please enter a name. |
| Under 2 or over 50 characters | Length error |
| Digits or symbols | Use letters only |
| Duplicate (case-insensitive) | Already stored |
| Valid | Added successfully |
| Display with empty database | Database is empty |

## Database
`names.db` → table `names(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE COLLATE NOCASE)`

## Files
- `MainActivity.java` – UI logic
- `DatabaseHelper.java` – `SQLiteOpenHelper` (create, insert, query)
- `NameValidator.java` – input validation
- `activity_main.xml` – layout

## Run
1. `git clone https://github.com/Athira1903/Android.git`
2. Open in Android Studio, sync Gradle.
3. Run on an emulator or device (API 24+).

## Test
- Unit: `./gradlew testDebugUnitTest`
- Instrumented: `./gradlew connectedDebugAndroidTest`

## Screenshots
Add images to `screenshots/` and link them here.
