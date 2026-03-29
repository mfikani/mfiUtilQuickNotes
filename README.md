MFI Quick Notes Utility
MFI Quick Notes is a lightweight, always‑on‑top desktop notes tool built in Java.
It’s designed for developers and power‑users who want a fast, minimal, and keyboard‑driven way to jot down thoughts without switching apps or losing focus.
The utility stores your notes in a simple text file, auto‑saves as you type, and can be shown or hidden instantly using global hotkeys.


Features
- Instant global hotkeys
- ALT + 2 → Show / hide the notes window
- ALT + 3 → Save and exit the application
- Auto‑save
- Notes are saved automatically while typing (debounced for efficiency)
- Persistent storage
- On first launch, you choose where the notes file will be stored
- The app remembers your chosen location using a small config file
- Clean, minimal UI
- Small floating window
- Always on top
- No minimize/maximize clutter
- Close button hides the window instead of exiting
- Customizable
- Adjustable font size
- Custom title bar icon supported
- Simple text file format for easy syncing or backup

📂 File Storage
The app stores two files:
- Your notes:
mfi-util-quick-notes.txt (location chosen by you)
- Config file:
%USERPROFILE%\AppData\Roaming\MFIQuickNotes\mfi-util-quick-notes-config.properties

🚀 Why this tool?
This utility was created to solve a simple problem:
“I need a tiny, fast, always‑available notes window that doesn’t get in my way.”

No heavy editors.
No loading times.
No switching apps.
Just a quick shortcut and instant notes.

🛠 Requirements
- Java 8 or later
- Windows (global hotkeys use JNativeHook)

📦 Libraries Used
- Swing — UI
- JNativeHook — Global keyboard shortcuts


File Storage
The app stores two files:
- Your notes:
mfi-util-quick-notes.txt (location chosen by you)
- Config file:
%USERPROFILE%\AppData\Roaming\MFIQuickNotes\mfi-util-quick-notes-config.properties

🚀 Why this tool?
This utility was created to solve a simple problem:
“I need a tiny, fast, always‑available notes window that doesn’t get in my way.”

No heavy editors.
No loading times.
No switching apps.
Just a quick shortcut and instant notes.
