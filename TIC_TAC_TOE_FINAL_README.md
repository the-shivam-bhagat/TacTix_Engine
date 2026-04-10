# 🎮 TIC TAC TOE — Console Edition

> *A battle-tested, persistence-driven, terminal Tic Tac Toe core built in pure Java.*
> Clean architecture. Live leaderboards. No shortcuts.

---

## 📖 Table of Contents

- [What Is This?](#-what-is-this)
- [Live Demo — What You'll See](#-live-demo--what-youll-see)
- [Features](#-features)
- [Architecture Overview](#-architecture-overview)
- [File Structure](#-file-structure)
- [Class Breakdown](#-class-breakdown)
- [Game Flow](#-game-flow)
- [Data Persistence](#-data-persistence)
- [Win Detection Logic](#-win-detection-logic)
- [Board Rendering Engine](#-board-rendering-engine)
- [Special Commands](#-special-commands)
- [How to Run](#-how-to-run)
- [Design Decisions & Trade-offs](#-design-decisions--trade-offs)
- [Known Limitations & Future Scope](#-known-limitations--future-scope)

---

## 🕹 What Is This?

This is not your tutorial Tic Tac Toe.

This is a **fully-featured, multi-round, multi-session, persistent** Tic Tac Toe game that runs in the terminal. Two human players battle across as many rounds as they want, with their stats saved to disk — so the leaderboard remembers who won, even after the program closes.

Built entirely in **Java (no external libraries)**, it demonstrates real-world software design patterns: separation of concerns, encapsulation, defensive input handling, and a custom sorting registry — all inside a console application.

---

## 🎬 Live Demo — What You'll See

```
T IC TAC TOE
═════════════

👋 WELCOME TO THE GAME!

🎮 This is a fun and interactive Tic Tac Toe experience...
```

```
╔═══════════════════════════╗
║   🏆 LEADERBOARD 🏆      ║
╠════╦═══════════╦══════════╣
║ #  ║  Player   ║  Wins    ║
╟────╫───────────╫──────────╢
║ 1  ║  ALICE    ║  14      ║
║ 2  ║  BOB      ║  9       ║
╚════╩═══════════╩══════════╝
```

```
╔═══════════════════════════════════╗
║           🎮 PLAY BOARD 🎮       ║
╠═══════════╦═══════════╦═══════════╣
║           ║           ║           ║
║     1     ║     2     ║     3     ║
║           ║           ║           ║
╠═══════════║═══════════║═══════════╣
║           ║           ║           ║
║     4     ║     5     ║     6     ║
║           ║           ║           ║
╠═══════════║═══════════║═══════════╣
║           ║           ║           ║
║     7     ║     8     ║     9     ║
║           ║           ║           ║
╚═══════════╩═══════════╩═══════════╝
X and O style 
╔═══════════════════════════╦═══════════════════════════╗
║ #                       # ║       #  #  #  #  #       ║
║    #                 #    ║    #                 #    ║
║       #           #       ║  #                     #  ║
║          #     #          ║ #                       # ║
║             #             ║ #                       # ║
║          #     #          ║ #                       # ║
║       #           #       ║  #                     #  ║
║    #                 #    ║    #                 #    ║
║ #                       # ║       #  #  #  #  #       ║
╚═══════════════════════════╩═══════════════════════════╝
```

---

## ✨ Features

| Feature | Details |
|---|---|
| 🎮 **Multi-Round Matches** | Play as many rounds per session as you want |
| 🔁 **Multi-Session Games** | Chain multiple full matches in one run |
| 🏆 **Live Leaderboard** | Top 10 players ranked by lifetime wins |
| 💾 **Persistent Storage** | Player data saved to `players.dat` between runs |
| 🛡 **Fault-Tolerant Input** | Invalid input never crashes the game |
| 🔄 **Self-Recovery** | IO errors trigger a controlled restart prompt |
| 👤 **Auto-Named Players** | Empty name input auto-assigns `PLAYER_1`, `PLAYER_2`, etc. |
| 🔧 **Admin Management** | Password-protected player deletion panel |
| 📜 **Session Summary** | Full match history printed at end of session |
| 🎨 **Dynamic Board Rendering** | ASCII board constructed from live game state |

---

## 🏗 Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│                    GameEngine                       │
│  (Entry point · Orchestrates all top-level flow)    │
└────────┬───────────┬──────────────┬─────────────────┘
         │           │              │
         ▼           ▼              ▼
   InputHandler  PlayerRegistry  GameHistory
   (All I/O)     (Players +      (Session list
                  Rankings)       + summary)
                      │
                      ▼
                   Player
              (Data + compareTo)
              
              
              
           (per game)
          Game-Session
   (Rounds + scoring + result)
         │
         ▼
     Utility
   (Board rendering · Win logic · XO blocks)
```

**Key principle:** Each class owns exactly one concern. `GameEngine` drives the loop. `GameSession` owns a match. `InputHandler` owns the console. `PlayerRegistry` owns persistence. `Utility` owns board math. None of them bleed into each other's business.

---

## 📁 File Structure

```
📦 project-root/
 ┣ 📄 GameEngine.java       ← Main class, entry point, top-level orchestration
 ┣ 📄 GameHistory.java      ← Session accumulator + GameSession (match logic)
 ┣ 📄 InputHandler.java     ← All console I/O, validation, special commands
 ┣ 📄 Player.java           ← Player data class + Comparable ranking logic
 ┣ 📄 PlayerRegistry.java   ← HashMap + TreeSet registry, file persistence
 ┣ 📄 Strings.java          ← All display strings / UI constants
 ┣ 📄 Utility.java          ← Board builder, XO renderer, win-check algorithm
 ┗ 📄 players.dat           ← (Auto-generated) Persistent player storage
```

---

## 🔍 Class Breakdown

### `GameEngine` — The Orchestrator
The `main()` entry point. Owns the outer `do-while` game loop, creates players via `createPlayer()`, spawns `GameSession` objects, and manages `GameHistory`. Also holds the static `PlayerRegistry` reference so other classes can reach it without being passed it everywhere.

Notable: the `getPlayer()` method handles both returning existing players and registering new ones — a clean lookup-or-create pattern.

### `GameSession` — The Match Manager
One `GameSession` = one full match (multiple rounds). It tracks wins per player (`wins1`, `wins2`, `ties`), manages who plays first each round, and calls `playRound()` in a loop until the players choose to stop. After all rounds, `declareMatchResult()` determines the match winner.

`playRound()` is the core game loop: it manages the `freq[]` state array, places X/O marks, calls `Utility.winnerCheck()`, and handles the tie condition on step 9.

### `InputHandler` — The I/O Firewall
Every single console read goes through this class. It handles:
- `readLine()` — exits on `"exit"`, opens admin panel on `"manage"`
- `readYesNo()` — empty = Yes (user-friendly default)
- `readYesNo_Specific()` — explicit Y required (used for feature discovery prompt)
- `readCellChoice(freq[])` — validates 1–9 and checks cell availability

No game logic here — only input sanitisation and routing.

### `Player` — The Data Model
Simple, clean, immutable-ish entity. Implements `Comparable<Player>` for `TreeSet` ordering: **higher wins rank first; ties broken alphabetically by name.** The `incrementLifetimeWins()` pattern requires the caller (`PlayerRegistry`) to remove and re-insert the player into the `TreeSet` around the mutation — because `TreeSet` does not auto-reorder on field changes.

### `PlayerRegistry` — The Persistence Layer
Dual data structure approach:
- `HashMap<String, Player>` → O(1) name lookup
- `TreeSet<Player>` → always-sorted ranking

Reads from `players.dat` on startup, writes back on `trimToMaxPlayers()`. Hard cap of 50 players; only the top 10 are displayed. On file IO failure, it delegates to `GameEngine.restart()`.

### `Utility` — The Rendering Engine
Builds the 83×32 character board as a raw `char[][]` grid. Embeds grid lines (`═`, `║`) and position numbers (1–9) at fixed coordinates. The X and O marks are pre-computed as 9×25 character blocks (`xo[0]` = X block, `xo[1]` = O block) and stamped into the board grid at the right offset using `placeXO()`.

### `Strings` — The String Constants
All user-facing text lives here. Keeps business logic files clean and makes UI copy changes trivially easy — change one place, see it everywhere.

---

## 🔄 Game Flow

```
START
  │
  ├─► Intro sequence (features, instructions, leaderboard)
  │
  └─► [Game Loop]
        │
        ├─► Enter Player 1 name → lookup or register
        ├─► Enter Player 2 name → lookup or register (duplicate blocked)
        │
        └─► [Match Loop — GameSession.play()]
              │
              ├─► Ask who goes first this round
              │
              └─► [Round Loop — playRound()]
                    │
                    ├─► Players alternate moves (1–9)
                    ├─► Update freq[], render board
                    ├─► Check win after step 5+
                    ├─► Check tie after step 9
                    │
                    └─► Round ends → update score → show scoreboard
              │
              ├─► Ask: another round?
              │     YES → swap first-mover, loop
              │     NO  → declareMatchResult()
              │
              └─► Ask: another game?
                    YES → new GameSession
                    NO  → print GameHistory + final leaderboard → EXIT
```

---

## 💾 Data Persistence

Player data is saved to a flat CSV file `players.dat` in the working directory:

```
ALICE,14
BOB,9
CHARLIE,3
```

**On startup:** `PlayerRegistry.loadPlayers()` reads the file and reconstructs all `Player` objects into the HashMap and TreeSet.

**On exit:** `trimToMaxPlayers()` enforces the 50-player cap (evicts lowest-ranked players), then `savePlayers()` writes all remaining players back.

**Failure handling:** If the file read fails mid-load, `GameEngine.restart()` is called — offering the user a clean restart rather than silent corruption.

---

## ♟ Win Detection Logic

Win checking uses a `freq[]` array of length 9, mapping positions 0–8 (corresponding to board cells 1–9):

```
 0 | 1 | 2
---+---+---
 3 | 4 | 5
---+---+---
 6 | 7 | 8
```

- `freq[i] = 1`  → Player 1 (X) occupies cell i  
- `freq[i] = -1` → Player 2 (O) occupies cell i  
- `freq[i] = 0`  → Empty

The `winnerCheck()` method checks all 8 winning lines — 3 rows, 3 columns, 2 diagonals — by comparing equality of freq values. Returns:
- `Boolean.TRUE` → Player 1 wins
- `Boolean.FALSE` → Player 2 wins
- `null` → No winner yet

Win checking only begins at step 5 (minimum moves needed for a win), which avoids redundant checks on early turns.

---

## 🎨 Board Rendering Engine

The play board is an `char[][]` grid built by `Utility.getPlayBoard()`.

X and O marks are pre-built as character block sprites (`getBlocksXO()`). When a player claims a cell, `placeXO()` copies the sprite block into the correct position in the grid using pre-computed start index offsets from `getStartIndexesOfEachBlock_1_to_9()`.

This approach separates **display** from **game state** cleanly — the `freq[]` array is the truth; the board is just a rendered view of it.

---

## ⌨️ Special Commands

These can be typed at **any input prompt** during the game:

| Command | Effect |
|---|---|
| `exit` | Gracefully exits the game, saves data first |
| `manage` | Prompts for admin password (`123456`), opens player management panel (view all players, delete by name) |

---

## ▶️ How to Run

**Requirements:** Java 17+ (uses text blocks and records-style patterns)

```bash
# 1. Clone or download all .java files into one directory

# 2. Compile
javac *.java

# 3. Run
java GameEngine
```

`players.dat` is created automatically in the same directory on first exit. Keep it alongside the compiled classes to retain leaderboard data between sessions.

---

## 🧠 Design Decisions & Trade-offs

**Why `TreeSet` + `HashMap` for the registry?**  
A `TreeSet` alone gives sorted iteration but O(log n) lookup by name. A `HashMap` alone gives O(1) lookup but no ordering. Using both gives O(1) lookup AND always-sorted rankings — at the cost of keeping two structures in sync. This sync responsibility is explicitly handled in `incrementWin()` with a remove-mutate-reinsert pattern.

**Why is win checking started at step 5?**  
The earliest a win is mathematically possible is after 5 total moves (3 by one player, 2 by the other). Checking before that is guaranteed to return `null`, so it's skipped as an optimization.

**Why static references on `GameEngine`?**  
`playerRegistry` and `input` are static on `GameEngine` so that classes like `InputHandler` and `PlayerRegistry` can reach them without being tightly constructor-coupled. This is a pragmatic trade-off for a single-threaded console app — in a multi-threaded or testable context, dependency injection would be preferred.

**Why flat CSV over serialization for persistence?**  
`players.dat` is human-readable, trivially debuggable, and immune to Java serialization version mismatches. For a game at this scale, it's the right call.

**Why is `winCheck` computed in `GameHistory` but consumed in `playRound`?**  
`Utility.winnerCheck()` returns `Boolean` (nullable) rather than a primitive — this is an intentional tri-state: win for first, win for second, or no result yet. The nullable Boolean acts as an optional type, avoiding a separate enum for three states.

---

## 🔭 Known Limitations & Future Scope

| Limitation | Potential Fix |
|---|---|
| No AI opponent | Implement Minimax algorithm for single-player mode |
| Password is hardcoded (`123456`) | Move to a config file or hashed credential |
| `players.dat` is plain text | Encrypt or use a lightweight DB (SQLite via JDBC) |
| `GameEngine.restart()` calls `main()` recursively | Replace with a proper restart loop to avoid stack growth |
| No unit tests | Add JUnit 5 tests for `Utility.winnerCheck()`, `PlayerRegistry`, `Player.compareTo()` |
| Single-machine multiplayer only | Add socket-based networking for remote play |

---

## 👨‍💻 Author

**Shivam Bhagat**  
B.Tech CSE Student · Java Development & DSA Enthusiast

> *Built with clean logic, structured design, and a lot of passion.*

---
