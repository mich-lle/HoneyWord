# Starrable (Standalone Android Word Game)

**Starrable** is a seven-letter word game presented on a six-pointed star. You can form words using the seven letters shown. **Starrable** is built with **Kotlin + Jetpack Compose**, using **original code, assets, and name**.

## Build & Run
1. Open the folder in **Android Studio (Giraffe or newer)**.
2. Let Gradle sync and press **Run**.

## Word List
- A small demo word list lives at `app/src/main/assets/words.txt`.  
- For real play, replace it with a larger English word list (one word per line, lowercase). Consider permissive lists like **wordfreq top lists**, **wordfreq.zip**, **wordfreq-leeds**, or **wordfreq "words.txt"** alternatives that are free to use.

## Gameplay

- Form words of **4 or more letters**.
- You may use **only the 7 letters** displayed.
- Every word must **include the center letter**.
- Letters can be reused.
- Each valid submission is scored and added to your found list.
- A **Starweave** is a special word that **uses all 7 letters at least once**. Hitting a Starweave triggers a celebration and (optionally) a bonus.

### Tips
- Tap the outer six letters to add to your current entry; the center letter is required for valid words.
- Shuffle mixes the **outer six**; the center remains fixed.
- Delete removes the last letter of your current entry.

## Notes
- This project is not affiliated with the New York Times. Avoid their name/brand for any public app store listing.
