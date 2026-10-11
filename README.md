# 🧩 Block Puzzle Game (Minecraft Inspired Edition)

A Java-based **Block Puzzle** game inspired by Minecraft visuals, developed as part of university coursework at TUKE. The project features a full game engine, scoring system, interactive UI, and integration with a database for scores and feedback.

---

## 🛠️ Tech Stack & Features
* **Core Language:** Java (OOP principles, State pattern)
* **Architecture:** Component-based UI (Console & Spring Boot Web interface)
* **Storage / Backend:** SQL, Spring Data / GameStudio framework
* **Theming:** Minecraft-inspired aesthetic and game elements

---

## 🎮 Game Logic & Architecture

### Field & Tile Management
* **Field:** Manages the playing grid (`rowCount` × `columnCount`), tracks score, and handles block generation via `generateBlocks()`.
* **Tile:** Represents individual grid slots with state management (`EMPTY` ⇄ `OCCUPIED`).

### State Diagrams
Below are the lifecycles for the game process and individual tiles:

* **Game States (GameState):**
    * `PLAYING` — the game continues as long as at least one block can fit (`generateBlocks()`).
    * `FAILED` — the game ends (`isOver() == true`) when no available blocks fit on the board.

* **Tile States:**
    * `EMPTY` ⇄ `OCCUPIED` (changed via `placeBlock()` and `removeLine()` methods).

## 🚀 How to Run

1. **Clone the repository:**
   ```bash
   git clone git@github.com:polina-liubka/block-puzzle-game.git
   ```
2. **Open in IDE:**
   Open the root project folder in **IntelliJ IDEA**.
3. **Dependencies:**
   Allow Maven/Gradle to load all project dependencies.
4. **Execution Options:**
    * **Console Version:** Locate `ConsoleUI.java` (or `BlockPuzzle.java`) and run the `main` method.
    * **Web Application:** Run `GameStudioServer.java` (Spring Boot) and navigate to `http://localhost:8080` in your web browser.