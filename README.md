# 📺 YouTube Monitor App

Welcome to the **YouTube Monitor App**! 🎉 This project started as a simple experiment but quickly evolved into a full-fledged JavaFX application that can track YouTube channels, notify users of new videos, and keep track of API usage.

🚀 **This has been the biggest challenge of my programming journey so far, and let me tell you—it was a wild ride!** I went from "let’s just make a thing that checks a channel" to "oh wow, now we have API usage tracking, persistent storage, and a dark theme!" 😅

## ✨ Features

### 🔍 Search & Follow Channels
- Enter a YouTube channel name and search for it via the YouTube Data API.
- Results are displayed with the channel's thumbnail and description.
- Click **"Add"** to start monitoring a channel.

### 👀 Monitored Channels Page
- View all the channels you’ve added.
- Displays the **latest video title** and **upload date** for each channel.
- Click **"Watch Video"** to launch the latest video in your default browser.
- Remove channels from your monitored list if you decide they're no longer worth your precious API quota.

### 📊 Real-Time API Usage Tracking
- Tracks daily API usage based on YouTube API costs (yes, API calls aren't free, and Google makes sure you remember that!).
- **Persistent tracking** ensures your API usage count doesn’t reset when you restart the app—only a new day can clear the slate.
- A friendly reminder at the bottom of the Monitored Channels page tells you how much of your daily quota you've burned through.

### 🎨 Dark Mode (Because Light Mode is for Noobs)
- Every page is styled with a dark theme so you don’t burn your retinas while coding at 2 AM.
- Looks sleek. Feels professional. 10/10 would recommend.

### 🔔 Instant New Video Alerts
- If a new video is detected, the app **alerts you** and asks if you want to open it.
- No more manually checking YouTube for updates—this app does the stalking for you (in a completely non-creepy way).

## 🛠 Improvements We Didn't Have Time For (But Totally Want To Do)

### 💾 Database Storage
Right now, we’re saving API usage and API keys in local files. A database (even SQLite) would make this **way more scalable**. But hey, we had to ship a working prototype first!

### 📱 Mobile Version
This is currently a desktop app, but **how cool would it be to have this on Android**? We could send push notifications for new videos instead of popups! Google, hire me already. 😎

### 🎶 Sound Notifications
Wouldn’t it be hilarious if every new video notification played the **Netflix "DUN-DUN" sound**? I wanted to do it, but I also wanted to finish this project before my next birthday.

### 🔥 Auto-Open Latest Video on Launch
If there’s a new video, **why even ask?** Just open it immediately! But some users might not like surprises, so this should be a setting.

### ⚙️ API Key Management UI
Right now, you enter the API key manually. In the future, a **setup wizard** could walk new users through the process with nice UI elements (because not everyone loves text fields as much as we do).

## 🏆 Lessons Learned
- **JavaFX is awesome but also a learning curve.** Once you get used to it, UI layouts feel pretty powerful.
- **APIs are fun and frustrating at the same time.** Google’s API documentation is like a cryptic scroll that only the worthy can decipher.
- **Persistence is key.** Whether it's saving API usage or debugging for hours—sticking with a problem until it's solved is the real skill.

## 🎉 Final Thoughts
This project was an **absolute blast to build**. It stretched my knowledge of Java, APIs, UI design, and **debugging errors that made no sense whatsoever**. If you've made it this far, I hope you enjoy using the app as much as I enjoyed building it. 🚀

Want to add something cool? Fork it, improve it, and send a PR! Or just stare at the dark theme and appreciate its beauty. Either way, thanks for checking it out. 😃

