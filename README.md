# Honeyword (Standalone Android Word Game)

This is a **NYT Spelling Bee–style** word game built with **Kotlin + Jetpack Compose**, but using **original code, assets, and name**.

## Build & Run
1. Open the folder in **Android Studio (Giraffe or newer)**.
2. Let Gradle sync and press **Run**.

## Word List
- A tiny demo word list lives at `app/src/main/assets/words.txt`.  
- For real play, replace it with a larger English word list (one word per line, lowercase). Consider permissive lists like **wordfreq top lists** or **wordfreq.zip**, **wordfreq-leeds**, or **wordfreq "words.txt"** alternatives that are free to use.

## Gameplay
- Seven letters; the **center letter is required** in every guess.
- Words must be **4+ letters** and use only the puzzle letters.
- Scoring: 4-letter words = 1 point; otherwise length points + **+7 pangram bonus** when a word uses all seven letters.

## Notes 
- This project  is not affiliated with the New York Times. Avoid their name/brand for any public app store listing.
