# 📷 PicFetch - Lightweight Image Downloader App

**PicFetch** is a minimalist Android app designed to quickly fetch and display images from the internet. Built with Kotlin and Android best practices, PicFetch leverages core Android components like Services, Broadcast Receivers, Permissions, and UI Threads to deliver a smooth and responsive experience.

---

## 🚀 Features

### 🔽 Download Images on Demand
Fetch images from URLs and save them directly into your device’s `Pictures` folder using a simple UI.

### 🖼️ Instant Preview
Automatically decode and display downloaded images using efficient memory handling techniques.

### ⚙️ Background Services
Heavy-lifting like image downloading is offloaded to background `Started Services` — keeping your app responsive and snappy.

### 📣 Notifications via Broadcast
Once an image is downloaded, a custom broadcast is triggered to update the UI or notify the user.

### 📜 Permissions Done Right
Handles runtime permissions (e.g., Internet access) and adheres to Android’s recommended permission-request flows.

---

## 🧱 Built With

- **Kotlin** – Modern, expressive programming language for Android
- **Android SDK** – Core components like `Activity`, `Service`, and `BroadcastReceiver`
- **Threading** – Ensures long-running tasks don’t block the UI
- **AlertDialogs** and **Toasts** – Clean, interactive user prompts
- **ListView + CustomAdapter** – Potential for batch downloads or history logs (extensible design)


---

## 📸 Screenshots

*Coming soon — stay tuned!*

---

## 🔧 Getting Started

1. **Clone this repo**:
   ```bash
   git clone https://github.com/yourusername/PicFetch.git

2. Open in Android Studio

3. Build and run on a real device or emulator

4. Try entering a valid image URL, then press download to watch it in action!

---

## ✅ Roadmap

- [ ] Add download queue with `ListView`
- [ ] Implement progress bar for active downloads
- [ ] Enable image caching and history
- [ ] Add option to share downloaded images

---

## 🙌 Credits



