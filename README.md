🌱 MiniGardens – Java Image Upload & Comment App

A simple Java Swing desktop application where users can upload images, write captions, and comment on posts.

🚀 Features

Upload garden photos (JPG, PNG, GIF, WEBP)

Add captions

Add comments on any post

All data is stored locally using Java Serialization

Simple, clean UI built using Java Swing

Beginner-friendly, no Maven/Gradle needed

🗂️ Project Structure
GardenCommunityApp/
│
├── src/
│   ├── app/                 → main application file (MiniGardensApp.java)
│   ├── model/               → Post & Comment classes
│   └── store/               → DataStore.java (handles saving/loading)
│
├── uploads/                 → uploaded images (auto-created)
├── data/                    → store.ser file (saved posts/comments)
├── README.md
└── out/                     → compiled .class files (not included in GitHub)

📦 Requirements

Java JDK 11 or higher
VS Code / IntelliJ / any editor
No build tools required (no Maven, no Gradle)

🧱 How to Compile

Open terminal in project root:

Windows PowerShell
rd /s /q out 2> $null
mkdir out
javac -d out src\model\*.java src\store\*.java src\app\*.java


Linux / Mac
rm -rf out
mkdir out
javac -d out src/model/*.java src/store/*.java src/app/*.java


▶ How to Run
java -cp out app.MiniGardensApp

💾 Data Storage

All uploaded images → saved in uploads/

All posts + comments → saved in data/store.ser

Even after closing the app, everything stays saved.


❤️ Author
Satyam
