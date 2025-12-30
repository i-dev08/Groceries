# Groceries Android App

A simple grocery inventory and notification app built as part of a technical task. The application helps users keep track of grocery items, their categories, and expiry dates, and notifies them when items are nearing expiry.

> **Note:** Although the original instructions suggested using XML layouts with RecyclerView, this project is implemented using **Jetpack Compose** for UI, as a modern and recommended approach for Android UI development.

---

## Features

* Add and manage grocery items
* Categorize items
* Sort items based on expiry date
* Color-coded item list based on expiry
* View notifications for expiring items

---

##  Tech Stack

* **Language:** Kotlin
* **UI:** Jetpack Compose (Material 3)
* **Architecture:** Basic MVVM-style separation
* **State Management:** Compose state & ViewModel
* **Navigation:** Jetpack Navigation Component (Compose)

---

## Project Structure (High Level)

* `ui/screens/` – Screens, dialogs, and UI-related logic
* `ui/navigation/` – Bottom navigation and navigation setup
* `ui/data/` – Data models and notification entities
* `ui/notification/s` - System notifications
* `ui/viewmodels/` - Viewmodel + Livedata
  

The code is modularized to keep UI, navigation, and data concerns reasonably separated.

---

## Why Jetpack Compose?

Jetpack Compose was chosen over XML + RecyclerView to:

* Improve UI readability and maintainability
* Use a declarative UI approach aligned with modern Android development practices

LazyColumn is used instead of RecyclerView for list rendering, which serves the same purpose in Compose-based UIs.

---

##  How to Run

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle
4. Run on an emulator or physical Android device

---

## Author

Ishika R Dev
