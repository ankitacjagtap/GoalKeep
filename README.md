# Goal Breaker – Android Goal Tracking App

> A modern Android application built using **Kotlin** that helps users create goals, organize them into manageable tasks, and track their progress efficiently. The application follows the **MVVM architecture**, stores user data using **Firebase Realtime Database**, and displays motivational quotes fetched from a REST API.

---
## 📱 Screenshots

### Home Screen
<img src="https://github.com/user-attachments/assets/f905488a-6b5e-4991-a76c-02794687dae4" width="250"/>

### Add Goal
<img src="https://github.com/user-attachments/assets/01d7da60-349e-4ab1-89bb-f354cf60737" width="250"/>

### Goal Details
<img src="https://github.com/user-attachments/assets/a100fd12-28c6-4e15-b1b2-c253a36a457b" width="250"/>

## 📱 Screenshots

| Home Screen | Add Goal | Goal Details |
|--------------|----------|--------------|
| ![Home Screen](<img width="250" height="2424" alt="Home Screen" src="https://github.com/user-attachments/assets/f905488a-6b5e-4991-a76c-02794687dae4" />) | ![Add Goal] (<img width="250" height="2424" alt="Add Goal" src="https://github.com/user-attachments/assets/01d7da60-349e-4ab1-89bb-f354cf60737a" />
) | ![Goal Details](<img width="250" height="2424" alt="Goal Details" src="https://github.com/user-attachments/assets/a100fd12-28c6-4e15-b1b2-c253a36a457b" />
) |

---

# ✨ Features

### 🎯 Goal Management
- Create new goals
- Edit goal details
- Delete goals
- Store goal title and description

### ✅ Task Management
- Add tasks under each goal
- Assign time to tasks
- Mark tasks as completed
- Delete tasks

### 📊 Progress Tracking
- Automatic progress calculation
- Visual progress bar for every goal
- Updates instantly when tasks are completed

### ☁️ Firebase Realtime Database
- Stores user-created goals
- Stores goal tasks
- Retrieves user data
- Updates task completion status
- Deletes goals and tasks
- Provides persistent cloud storage

### 💬 Motivational Quotes
- Fetches motivational quotes using a REST API
- Displays a new quote on the Home Screen

### 🎨 Modern UI
- Clean and responsive user interface

---

# 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| Kotlin | Programming Language |
| Material 3 | UI Components |
| MVVM | Architecture Pattern |
| Firebase Realtime Database | Cloud Data Storage |
| Retrofit | REST API Client |
| OkHttp | HTTP Client |
| Kotlin Coroutines | Asynchronous Programming |
| StateFlow | State Management |
| Navigation Compose | Screen Navigation |

---

# 🏗️ Architecture

The project follows the **MVVM (Model–View–ViewModel)** architecture to separate the UI, business logic, and data layer.

```
        UI
        │
        ▼
    ViewModel
        │
        ▼
    Repository
   ┌────┴────────────┐
   ▼                 ▼
Firebase         Quote API
Realtime DB       Retrofit
```

---

# 📂 Project Structure

```
app/
├── data/
│   ├── remote/
│   │   ├── api/
│   │   └── model/
│   ├── firebase/
│   └── repository/
│
├── ui/
│   ├── home/
│   ├── goal/
│   ├── components/
│   ├── navigation/
│   └── theme/
│
├── viewmodel/
│
└── MainActivity.kt
```

---

# 📡 API Used

## ZenQuotes API

The application fetches motivational quotes from:

https://zenquotes.io/api/random

The quote displayed on the Home Screen is retrieved through Retrofit.

---

# ☁️ Firebase Realtime Database

The application uses **Firebase Realtime Database** as the backend for storing user-generated data.

The database stores:

- Goal titles
- Goal descriptions
- Tasks
- Task completion status
- Task timings

This allows user data to remain available even after closing and reopening the application.

---

# 📱 Application Workflow

1. Launch the application.
2. A motivational quote is displayed on the Home Screen.
3. Create a new goal by entering a title and description.
4. The goal is stored in Firebase Realtime Database.
5. Open the goal details page.
6. Add tasks and assign timings.
7. Mark tasks as completed.
8. The progress bar updates automatically.
9. Delete goals or tasks whenever required.

---

# 🚀 Build & Run

## Requirements

- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK 26+
- Kotlin 2.x
- Internet connection (for Firebase and Quotes API)

## Steps

1. Clone the repository

```bash
git clone https://github.com/ankitacjagtap/GoalKeep.git
```

2. Open the project in Android Studio.

3. Allow Gradle to sync all dependencies.

4. Connect an Android device or start an emulator.

5. Click **Run** to launch the application.

---

# 🎯 Future Enhancements

- User Authentication using Firebase Authentication
- Dark Mode
- Goal Categories
- Calendar Integration
- Push Notifications
- Deadline Reminders
- Weekly Progress Analytics
- Search & Filter Goals
- Task Priority Levels
- Profile Management

---

# 👩‍💻 Developed By

**Ankita Jagtap**

GitHub: https://github.com/ankitacjagtap

---

# 📄 License

This project was developed as an academic Android application to demonstrate Android development using Kotlin, Firebase Realtime Database, Retrofit, and the MVVM architecture.

It is intended for educational purposes only.
