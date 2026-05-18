# TriGrid Engine — Adversarial Game Engine with Multi-Tier AI Decision System

> *A production-grade, CLI-based game simulation framework built on clean architecture principles — featuring a 6-tier AI ladder, persistent player registry, session lifecycle management, and a fully decoupled rendering layer. Built entirely in Java.*

---

## Table of Contents

- [Overview](#overview)
- [Why This Is Not a Simple Game](#why-this-is-not-a-simple-game)
- [Core Engineering Highlights](#core-engineering-highlights)
- [Architecture Overview](#architecture-overview)
- [Package Diagram](#package-diagram)
- [System Control Flow](#system-control-flow)
- [UML — Key Class Relationships](#uml--key-class-relationships)
- [AI Bot System — Deep Dive](#ai-bot-system--deep-dive)
- [Game Modes](#game-modes)
- [Feature Set (Verified from Source)](#feature-set-verified-from-source)
- [Design Patterns Used](#design-patterns-used)
- [Error Handling Architecture](#error-handling-architecture)
- [Player Registry & Persistence](#player-registry--persistence)
- [Authentication System](#authentication-system)
- [Admin Control Panel](#admin-control-panel)
- [Session History & Replay Engine](#session-history--replay-engine)
- [Leaderboard System](#leaderboard-system)
- [Command Pipeline](#command-pipeline)
- [Project Structure](#project-structure)
- [How to Run](#how-to-run)
- [Extensibility Guide](#extensibility-guide)
- [Tech Stack](#tech-stack)
- [Key Highlights — Resume-Worthy](#key-highlights--resume-worthy)
- [Future Scope](#future-scope)
- [Author](#author)

---

## Overview

**TriGrid Engine** is a modular, adversarial game simulation system built on top of a Tic-Tac-Toe ruleset. While the game domain is simple and well-understood (intentionally so — to keep the focus on architecture), the system underneath is anything but.

This project was built to demonstrate backend engineering fundamentals: how you structure a system when you care about separation of concerns, how you model AI difficulty as a first-class design concern, how you build a rendering layer that can be swapped without touching game logic, and how you handle failures gracefully at every layer.

The engine supports three session types — **Player vs Player**, **Player vs Bot**, and **Bot vs Bot** — with a 6-tier AI bot system ranging from a purely random agent to a minimax player with alpha-beta pruning and equimax output selection. Every match is tracked, persisted, and replayable. Players are stored with hashed passwords across sessions. An admin panel can be accessed mid-game at any prompt without interrupting state.

This is what backend architecture looks like when it's applied to a game engine.

---

## Why This Is Not a Simple Game

Most developers who implement Tic-Tac-Toe write ~200 lines of procedural code. This system crosses **40+ classes**, **12 packages**, and implements the following concerns that have nothing to do with the game rules themselves:

| Concern | Implementation |
|---|---|
| AI Decision Making | 6 distinct bot algorithms, from random to alpha-beta minimax |
| Session Lifecycle | `SessionContext` tracks inSession / inRound / undoEnabled state |
| Input Pipeline | Every keystroke passes through a `CommandProcessor` before reaching the game |
| Rendering Abstraction | All output goes through view interfaces — zero direct `System.out` in business logic |
| Persistence | Custom char-pair encoded flat file with backward-compatible deserialization |
| Security | SHA-256 + random salt password hashing via `PasswordUtil` |
| Exception Hierarchy | Typed `GameException` subclasses with `GameErrorCode` enum for structured error handling |
| Undo System | Memento-based board snapshots enabling move reversal with state consistency |
| Match Replay | Cell-by-cell step-through replay of any historical round |
| Admin Control | Mid-session player management panel accessible from any input prompt |
| Self-Recovery | Fatal exceptions trigger a controlled restart decision, not a crash |

---

## Core Engineering Highlights

**Dependency Inversion Everywhere**
`GameEngine` depends on `Registry` and `RankingView` interfaces — never on `PlayerRegistry` directly. Sessions depend on `SessionView`, `PlayBoardView`, and `Input` interfaces. Renderers can be swapped independently of game logic.

**Exception-Driven Control Flow**
`SessionEndException` and `UndoRequestException` are thrown from deep inside the input pipeline and propagate up through session → engine layers. This allows commands like `end` and `undo` to interrupt game flow without polluting every method signature with boolean return flags.

**Dual-Role Registry**
`PlayerRegistry` implements both `Registry` (mutable operations: add, delete, increment wins) and `RankingView` (read-only: top players, all players). Callers are injected with only the interface they need — `AdminControl` gets `Registry + RankingView`, display components get only `RankingView`.

**Snapshot-Based Undo**
`GameBoard.makeMove()` pushes a `Snapshot` record (freq array + visual board + stepCount) onto a `Deque<Snapshot>` before every mutation. `undo()` pops and restores. This is the Memento pattern applied correctly — undo is `O(1)` per move and state is perfectly consistent.

**Bot ELO Calibration**
Each bot's ELO rating and difficulty percentage are not estimates — they are the result of simulated match calibration. The FLINT bot, for example, carries an 8% FLUX defect rate (intentional random lapse) to produce its 83% difficulty floor rather than 100%, making it feel like a skilled but fallible human player.

---

## Architecture Overview

The system is organized into **4 logical layers**:

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│   EngineRenderer · SessionRenderer · PlayBoardRenderer       │
│   PlayerTableRenderer · HistoryRenderer · ReplayRenderer     │
│   (All implement view interfaces — zero coupling to logic)   │
└────────────────────────┬────────────────────────────────────┘
                         │ view interfaces only
┌────────────────────────▼────────────────────────────────────┐
│                    APPLICATION LAYER                         │
│   GameEngine · SessionFactory · GameHistory · ReplayEngine   │
│   AdminControl · PlayerCreator · CommandHandler              │
└────────────────────────┬────────────────────────────────────┘
                         │ domain interfaces only
┌────────────────────────▼────────────────────────────────────┐
│                     DOMAIN LAYER                             │
│   GameBoard · Player · PlayerRegistry · GameResult           │
│   Bot implementations · GameSession implementations          │
│   SessionContext · GameErrorCode · Exception hierarchy       │
└────────────────────────┬────────────────────────────────────┘
                         │ store interface only
┌────────────────────────▼────────────────────────────────────┐
│                  INFRASTRUCTURE LAYER                        │
│   FilePlayerStore · Logger · Config                          │
│   (Persistence, logging — fully swappable)                   │
└─────────────────────────────────────────────────────────────┘
```

**Key architectural decision:** The `GameEngine` never imports a concrete renderer class. It holds an `EngineView` reference. Swap `EngineRenderer` for a `WebRenderer` tomorrow and the engine doesn't change a line.

---

## Package Diagram

```
src/
├── admin/              ← AdminControl, AdminInput, AdminService
│
├── auth/               ← AuthService (interface)
│                         SessionAuthManager (4-attempt lockout)
│                         PlayerCreator (registration + auth flow)
│                         PasswordUtil (SHA-256 + salt)
│
├── bot/                ← Bot (interface)
│                         BotFactory (level → instance)
│                         BeginnerBot (RAVE)
│                         EasyBot (GREX)
│                         MediumBot (WIRE)
│                         HardBot (FLINT)
│                         UnbeatableBot (PROBE)
│                         StallBot (STALL)
│                         UtilBot (shared board utilities)
│
├── command/            ← Command (interface), CommandProcessor (interface)
│   └── impl/           ← ExitCommand, ManageCommand, EndCommand, UndoCommand
│                         CommandHandler (registry + dispatch)
│
├── core/               ← GameEngine (orchestrator)
│                         GameBoard (state + undo)
│                         GameHistory (in-session storage)
│                         GameResult (serializable match data)
│                         SessionFactory (session construction)
│                         SessionType (enum)
│
├── exception/          ← GameErrorCode (enum)
│                         GameException (typed base)
│                         InvalidBotSelectionException
│                         InvalidSessionException
│                         SessionEndException
│                         UndoRequestException
│
├── input/              ← Input (interface)
│                         InputHandler (command interception + routing)
│
├── player/             ← Player (domain model)
│   │                     PlayerRegistry (Registry + RankingView impl)
│   │                     PlayerResult, RankingView, Registry
│   └── store/          ← PlayerStore (interface), FilePlayerStore
│
├── renderer/
│   ├── classes/        ← EngineRenderer, SessionRenderer, PlayBoardRenderer,
│   │                     PlayerTableRenderer, HistoryRenderer
│   └── view/           ← AdminView, AuthView, EngineView, EndCommandView,
│                         ExitCommandView, HistoryView, InputView,
│                         ManageCommandView, PlayBoardView, PlayerTableView,
│                         SessionView, SetupView, UndoCommandView
│
├── replay/             ← ReplayEngine, ReplayRenderer, ReplayView
│
├── sessions/           ← GameSession (interface)
│                         PlayerVSPlayerSession
│                         PlayerVSBotSession
│                         BotVSBotSession
│                         SessionContext
│
└── utility/            ← Config, Logger, Strings
```

---

## System Control Flow

```
MainExecution.main()
       │
       ▼
Logger.init()          ← Initialize timestamped log file
       │
       ▼
GameEngine.start()     ← Outer restart loop begins
       │
       ├── initialize()
       │       ├── Build all renderers (EngineRenderer, SessionRenderer, etc.)
       │       ├── PlayerRegistry(FilePlayerStore) ← Load players.dat
       │       ├── SessionContext ← Shared state tracker
       │       ├── CommandHandler ← Register: exit, manage, end, undo
       │       ├── InputHandler(Scanner, CommandHandler) ← Input pipeline
       │       ├── SessionFactory ← Constructs sessions with all dependencies
       │       ├── ReplayEngine
       │       └── GameHistory
       │
       ├── runIntroSequence()
       │       ├── MODULE 1: Feature Overview (skippable)
       │       ├── MODULE 2: Bot System Panel
       │       ├── MODULE 3: Instructions
       │       └── MODULE 4: Global Leaderboard
       │
       ├── startGameLoop()
       │       │
       │       └── [while playAnother]
       │               ├── Show session types (PVP / PVB / BVB)
       │               ├── SessionFactory.createGameSession(type)
       │               │       └── For PVP: PlayerCreator x2 (auth/register)
       │               │           For PVB: PlayerCreator x1 + BotFactory
       │               │           For BVB: BotFactory x2
       │               │
       │               ├── sessionContext.enterSession()
       │               │
       │               ├── session.play()   ← Core game loop
       │               │       │
       │               │       ├── showUndoOffer() → context.enableUndo() or not
       │               │       │
       │               │       └── [while keepPlaying]
       │               │               ├── showRoundStart()
       │               │               ├── showFirstMovePrompt()
       │               │               ├── context.enterRound()
       │               │               ├── playRound()  ← Move loop
       │               │               │       │
       │               │               │       └── [while !winner && !full]
       │               │               │               ├── input.readCellChoice(board)
       │               │               │               │    └── [CommandProcessor intercepts]
       │               │               │               │         ├── "undo"  → throw UndoRequestException
       │               │               │               │         ├── "end"   → throw SessionEndException
       │               │               │               │         ├── "exit"  → save + System.exit(0)
       │               │               │               │         └── "manage"→ AdminControl.show()
       │               │               │               ├── board.makeMove(cell, flag)
       │               │               │               │    └── push Snapshot → history
       │               │               │               └── board.checkWinner()
       │               │               │
       │               │               ├── context.exitRound()
       │               │               ├── showScoreboard()
       │               │               └── showNextRoundPrompt()
       │               │
       │               ├── [catch SessionEndException] → record abandoned match
       │               └── sessionContext.exitSession()
       │
       └── shutdown()
               ├── gameHistory.showHistory()   ← Session table + replay offer
               ├── playerRegistry.trimToMaxPlayers() + save
               └── showLeaderboard()
```

---

## UML — Key Class Relationships

```
                    «interface»
                    GameSession
                   /     |     \
                  /      |      \
   PlayerVSPlayerSession  |  BotVSBotSession
                  PlayerVSBotSession

                    «interface»
                       Bot
                        │
           ┌────────────┼────────────┐────────────┐────────────┐
           │            │            │            │            │
      BeginnerBot   EasyBot    MediumBot      HardBot   UnbeatableBot
        (RAVE)      (GREX)      (WIRE)        (FLINT)     (PROBE)
                                                         StallBot (STALL)

                    «interface»
                     Registry  ←────────────── AdminControl
                        │                         │
                        │       «interface»       │
                     PlayerRegistry ──► RankingView
                        │
                    FilePlayerStore («interface» PlayerStore)


                    «interface»
                     EngineView
                    (extends: AuthView, SetupView, AdminView,
                     ExitCommandView, EndCommandView,
                     ManageCommandView, UndoCommandView, InputView)
                        │
                   EngineRenderer


    GameEngine
        │
        ├── Input (InputHandler)
        │       └── CommandProcessor (CommandHandler)
        │               ├── ExitCommand
        │               ├── ManageCommand ──► AdminControl
        │               ├── EndCommand    ──► SessionContext
        │               └── UndoCommand   ──► SessionContext
        │
        ├── SessionFactory ──► GameSession (PVP / PVB / BVB)
        ├── PlayerRegistry
        ├── SessionContext   ← shared between Engine + Sessions + Commands
        └── GameHistory ──► ReplayEngine
```

---

## AI Bot System — Deep Dive

The bot system is the most carefully engineered part of this project. Each bot is a distinct **strategy**, not a difficulty knob. They are architecturally isolated — adding a new bot means implementing `Bot`, adding a case to `BotFactory`, and updating `Config.BotData`. Nothing else changes.

```
Level  Name    ELO    Win Rate    Algorithm Class
─────────────────────────────────────────────────────────────────────────
  1    RAVE    1000     ~0%       BeginnerBot   — Random-dominant
  2    GREX    1368    ~37%       EasyBot       — Heuristic-probabilistic
  3    WIRE    1716    ~72%       MediumBot     — Win/Block rule engine
  4    FLINT   1833    ~83%       HardBot       — Fork-aware + FLUX defects
  5    PROBE   2000   ~100%       UnbeatableBot — Alpha-beta minimax + EquiSelect
  0    STALL    ??    69.7% DAR   StallBot      — Draw optimization (win avoidance)
```

### RAVE — BeginnerBot (ELO 1000)

The weakest agent. 50% of the time it plays completely random. When it does engage heuristics, it occasionally tries to extend an existing piece adjacently, and even more rarely detects an immediate win opportunity. There is no blocking logic whatsoever.

```
chooseMove():
  if random() < 0.50 → play random valid cell
  if random() < 0.50 → check for immediate win (rare awareness)
  extend from existing piece (instinctive, not strategic)
  fallback → random
```

**Design intent:** A player who has never thought about Tic-Tac-Toe.

---

### GREX — EasyBot (ELO 1368)

Heuristic-aware but unreliable. Introduces probabilistic blocking (70% chance of actually blocking a threat), corner preference, and center bias. 40% of moves are still random. This creates a bot that feels like a casual player — sometimes smart, often absent-minded.

```
chooseMove():
  if random() < 0.40 → play random
  detect immediate win → take it
  if random() < 0.70 → detect and block opponent win
  prefer center (70% chance)
  prefer corners
  fallback → random
```

**Design intent:** A player who occasionally pays attention but can't sustain it.

---

### WIRE — MediumBot (ELO 1716)

Fully reactive. Always wins when it can, always blocks when it must, and extends its own lines otherwise. Carries a 15% opening awareness rate — occasionally plays optimal openings but usually doesn't. No fork detection.

```
chooseMove():
  if stepNo < 2 && random() < 0.15 → optimal opening
  check win     → take it
  check block   → block it
  extend line   → fill adjacent to own piece
  fallback → random valid
```

**Design intent:** A competent casual player who never plans more than one move ahead.

---

### FLINT — HardBot (ELO 1833)

The first strategically complete bot. Implements the full rule priority chain: win → block → create fork → block fork → positional fallback. Fork blocking uses a forcing-move strategy — instead of directly blocking, FLINT tries to create a threat that forces the opponent to respond, neutralizing the fork opportunity.

The **FLUX defect** is FLINT's defining characteristic: an 8% chance per move of completely ignoring all strategy and playing random. This isn't a bug — it's calibrated to produce the 83% win rate. Without it, FLINT would perform near 100%.

```
chooseMove():
  if random() < 0.08 → FLUX: play completely random (human-like lapse)
  apply opening strategy if early game
  1. Win immediately if possible
  2. Block opponent's immediate win
  3. Create a fork (2+ simultaneous threats)
  4. Block opponent fork:
       single fork  → block directly
       multi fork   → force opponent to respond to your threat
       fallback     → block any fork
  5. Positional: center → corners → edges
```

**Design intent:** A strong, strategic player with occasional blind spots.

---

### PROBE — UnbeatableBot (ELO 2000)

Full minimax with alpha-beta pruning. PROBE is theoretically unbeatable — it computes the optimal move from every game state. The implementation adds two engineering refinements:

**Priority move ordering:** Moves are evaluated in the order `[4, 0, 2, 6, 8, 1, 3, 5, 7]` (center → corners → edges). This is not arbitrary — it dramatically improves alpha-beta pruning efficiency since better moves are evaluated first, causing more cutoffs.

**EquiSelect:** When multiple moves produce identical minimax scores, PROBE picks uniformly at random among them. This prevents PROBE from playing the same game every time while maintaining perfect correctness. The randomness is in the output selection, not the evaluation.

```
chooseMove():
  apply opening strategy if early game
  for each move in priorityOrder:
    score = minMax(board, false, botFlag, depth=0, α=-∞, β=+∞)
    track all moves with equal best score (equimax candidates)
  return random pick from equimax candidates

minMax():
  terminal: return (10-depth) for win, (depth-10) for loss, 0 for draw
  maximizing: iterate priorityOrder, alpha-beta prune
  minimizing: iterate priorityOrder, alpha-beta prune
```

**Design intent:** Mathematically optimal — the ceiling of what's achievable in this game.

---

### STALL — StallBot (Special)

The most philosophically unusual bot. STALL's goal is not to win — it is to **never lose while maximizing draws**. This is a non-competitive equilibrium agent.

STALL uses a modified minimax where winning for itself scores 0 (neutral), losing scores -100 (catastrophic), and drawing scores +1 (preferred). It explicitly prefers moves that lead to draws over moves that might accidentally win.

An additional fork-detection penalty (-50) is applied to any move that allows the opponent to create a fork on the next turn, since a fork usually leads to a forced win for the opponent.

Current Draw Achievement Rate: **69.7%** (actively being improved).

```
chooseMove():
  stepNo == 0 → always take center (index 4)
  stepNo == 1 → respond to opponent's opening correctly
  
  for each valid move:
    board[move] = botFlag
    score = minimax(board, opponent_turn, ...)
    if opponentCanForkNext → score -= 50  (strong penalty)
    board[move] = 0
  
  prefer drawMove (score == 1) over bestMove
  
minimax(scoring):
  win for STALL  → score = 0   (avoid winning — neutral)
  win for opponent → score = -100 (worst outcome)
  draw           → score = 1   (preferred)
```

**DAR by opponent (Draw Achievement Rate):**

| vs STALL | vs PROBE | vs FLINT | vs WIRE | vs GREX | vs RAVE |
|----------|----------|----------|---------|---------|---------|
| 100% | 100% | 93.6% | 77.1% | 57.1% | 21.0% |

STALL paradoxically struggles most against RAVE — the weakest bot — because RAVE's random play is hard to predict and often accidentally wins.

---

### UtilBot — Shared Algorithmic Utilities

All bots share `UtilBot` for board analysis:

- `getWinIndexes(board, flag)` — returns all cells that complete a win for `flag`
- `getExtendIndexes(board, flag)` — returns adjacent empty cells to existing `flag` pieces
- `getValidIndexes(board)` — returns all empty cells
- `getForkIndexes(board, flag)` — returns cells where placing `flag` creates 2+ simultaneous winning threats
- `winnerCheck(board)` — returns winner flag or 0
- `getOpeningStrategyMove(board, step, random)` — shared opening book for bots that use it
- `pickRandom(set, random)` — equimax selection from a `HashSet<Integer>`

---

## Game Modes

### Player vs Player (PVP)

Two human players. Each player is resolved through `PlayerCreator` — looking them up in the registry, authenticating if they have a password, or registering them as new. Undo is available if opted in (with leaderboard exclusion). First mover is chosen per round.

### Player vs Bot (PVB)

One human player against one selected bot. Bot selection happens through `BotFactory`. Undo undoes both the player's move and the bot's response (up to 2 moves), keeping the board at a state just before the player's last input. Undo is blocked if the session was started without it.

### Bot vs Bot (BVB)

Automated simulation. No undo. First mover chosen by human user. Mover alternates each round automatically. Human presses Enter to advance between moves, enabling observation. All rounds recorded and replayable.

---

## Feature Set (Verified from Source)

Every feature below is confirmed from the source code — nothing speculative.

**Session Management**
- Three session types: PVP, PVB, BVB
- Per-session undo opt-in with automatic leaderboard exclusion
- Mid-session admin panel access via `manage` command at any prompt
- Clean session abandonment via `end` command with history preservation

**AI Bot System**
- 6 distinct bots: RAVE (L1), GREX (L2), WIRE (L3), FLINT (L4), PROBE (L5), STALL (L0)
- Bot vs Bot mode with automatic first-mover alternation
- Animated bot thinking delay (configurable ms per dot, different for PVB vs BVB)

**Player Management**
- Persistent player registry stored across program restarts
- Auto-generated names (`PLAYER_N`) for nameless players
- SHA-256 + salt password hashing for optional account security
- Session-scoped account lockout after 4 failed password attempts
- Duplicate name prevention within a session

**Admin Panel**
- Password-protected (accessible mid-game at any input prompt)
- View all players sorted by rank with: wins, last active, days old, member since
- Select player by name or rank
- Operations: change name, add/change/remove password, set lifetime wins, delete player
- Table refresh without exiting panel

**Leaderboard & History**
- Global leaderboard showing top 10 (configurable) players by lifetime wins
- Per-session game history table with result, lead, and round count
- Full match replay: select any game, any round, step through move by move
- Abandoned round detection (null entry) — replay gracefully reports these

**Game Board**
- 83×32 character ASCII render with custom X and O art (9×25 blocks per cell)
- Snapshot-based undo with perfect visual and state restoration
- Win detection gated at 5 moves minimum (optimization)

**Reliability**
- Typed exception hierarchy with `GameErrorCode` enum
- Self-recovering engine: fatal exceptions trigger restart prompt, not crash
- Persistent logger writing timestamped entries to `loggers.log`
- `IOException` from storage is wrapped as `GameException(STORAGE_LOAD_FAILED)` — never leaks checked exceptions to `GameEngine`

---

## Design Patterns Used

| Pattern | Where | Purpose |
|---|---|---|
| **Strategy** | `Bot` interface + 6 implementations | Each bot is a different decision strategy, swappable at runtime |
| **Factory Method** | `BotFactory`, `SessionFactory` | Decouples creation from usage; GameEngine never uses `new BotXxx()` |
| **Command** | `Command` interface, `CommandHandler`, `ExitCommand`, `ManageCommand`, `EndCommand`, `UndoCommand` | Commands are objects; registered in a map; input pipeline dispatches them |
| **Memento** | `GameBoard.Snapshot` record + `Deque<Snapshot>` | Full board state saved before every move for `O(1)` undo |
| **Template Method** | `GameEngine.start()` → `initialize()` → `runIntroSequence()` → `startGameLoop()` → `shutdown()` | Defines the invariant lifecycle; each phase is overrideable |
| **Facade** | `GameEngine` | Single entry point orchestrates all subsystems |
| **Repository** | `PlayerRegistry` implementing `Registry` + `RankingView` | Encapsulates player storage and ranking behind interfaces |
| **Observer-like** | `SessionContext` shared between engine, sessions, and command implementations | Central state tracker without event bus complexity |
| **Dependency Injection** | Constructor injection throughout — `GameEngine` injects into `SessionFactory`, which injects into sessions | No service locator; every dependency is explicit |
| **Null Object** | Abandoned rounds stored as `null` in parallel lists, handled gracefully by `ReplayEngine` | Avoids null checks in display logic |

---

## Error Handling Architecture

The exception system is typed and hierarchical, not a pile of `RuntimeException` catches.

```
GameErrorCode (enum)
├── INVALID_SESSION_TYPE
├── SESSION_ALREADY_ACTIVE
├── SESSION_NOT_INITIALIZED
├── INVALID_BOT_LEVEL
├── BOT_INSTANTIATION_FAILED
├── INVALID_MOVE
├── INVALID_INPUT
├── POSITION_ALREADY_OCCUPIED
├── GAME_NOT_STARTED
├── GAME_ALREADY_OVER
├── INVALID_BOARD_STATE
├── STORAGE_LOAD_FAILED
└── STORAGE_SAVE_FAILED

GameException (base, extends RuntimeException)
├── InvalidBotSelectionException  — bot level out of [0,5]
├── InvalidSessionException       — session type out of [1,3]
└── (others via direct GameException construction)

SessionEndException   — thrown by EndCommand, caught by GameEngine
UndoRequestException  — thrown by UndoCommand, caught by session round loop
```

**Error propagation strategy:**

- `UndoRequestException` is caught *inside* the round loop. The loop undoes the last move and continues — the game does not exit.
- `SessionEndException` propagates up through `session.play()` to `GameEngine.startGameLoop()`, which records the abandoned match and prompts for a new game.
- `GameException` (storage, bot errors) is caught by `GameEngine.startGameLoop()` — the loop can continue to the next game.
- Unhandled `Exception` propagates to `GameEngine.start()` — triggers `handleFatalError()` + restart prompt. Engine never crashes silently.
- Storage `IOException` is wrapped at `PlayerRegistry` constructor — no checked exception leaks to the engine.

---

## Player Registry & Persistence

`PlayerRegistry` is the central player store. It maintains two data structures simultaneously:

- `HashMap<String, Player>` — O(1) lookup by name
- `TreeSet<Player>` — sorted by (wins DESC, name ASC) via `Player.compareTo()`

The sorted set ensures that `getTopPlayers(N)` is always O(N) iteration, and win updates are O(log N) (remove, increment, re-add to trigger re-sort).

**File format (`players.dat`):**

Each player is stored as a single line of char-pair encoded text. The encoding splits each character into two parts whose ASCII values sum to the original character value, providing a simple obfuscation that makes the file non-trivially human-readable.

Raw format (before encoding):
```
name,wins,passwordHash,passwordSalt,joinDate,lastActive
```

The decoder handles 2-field, 4-field, 5-field, and 6-field formats for backward compatibility. Corrupted password data (hash without salt) is detected and reset rather than crashing.

Maximum registered players: **1,000** (configurable in `Config.PlayerConfig`).
Auto-name pool: `PLAYER_1` through `PLAYER_1500` with timestamp fallback.

---

## Authentication System

Optional per-player password protection implemented in `auth` package.

**Password rules** (enforced by `PasswordUtil.getValidationError()`):
- 4–32 characters
- No spaces
- Printable ASCII only (chars 33–126)

**Storage:** SHA-256 hash of `(password + salt)`, salt generated via `SecureRandom` (16 bytes, hex-encoded). Hash and salt stored in player file.

**Session lockout:** `SessionAuthManager` tracks failed attempts in-memory per session. After 4 failures, the account is locked for the current session. Lock resets on program restart. Attempts remaining displayed after each failure.

**Returning player flow:**
```
PlayerCreator.createPlayer()
  → name entered
  → registry.getOrCreatePlayer(name)
  → isNew? → register + offer password setup
  → returning? → hasPassword? → runAuthLoop()
                              → maxAttempts reached → account locked → re-prompt for different name
```

---

## Admin Control Panel

Accessible at any input prompt by typing `manage` followed by the admin password. The panel runs on the raw `Scanner` (bypassing the `CommandProcessor` pipeline) to prevent command interference during admin operations.

**Available operations:**

```
Select player by:
  [1] Name
  [2] Rank
  [3] Refresh player table

Operations on selected player:
  [1] Change name       — checks for duplicate, logs rename
  [2] Manage password   — add / change / remove
  [3] Set lifetime wins — admin-authorized direct override
  [4] Delete player     — confirmation required
```

The panel is fully loop-based — multiple actions can be performed before returning to the game. The game state is unaffected — `SessionContext.isInSession()` remains true, and the session resumes exactly where it left off.

---

## Session History & Replay Engine

Every completed session (including abandoned ones) is added to `GameHistory` as a `GameResult`.

`GameResult` stores three parallel lists, all indexed by round number:
- `List<List<Integer>> roundMoves` — ordered cell indexes (0–8) for each round; `null` if abandoned
- `List<String> roundFirstPlayerStarts` — name of the first mover that round
- `List<String> roundWinners` — winner name, `"TIE"`, or `null` if abandoned

**Replay flow:**

```
ReplayEngine.offerReplay(sessions)
  → user selects game number
  → showMatchSummaryBox() + showRoundTable()
  → user selects round number
  → replayRound():
       reconstruct freq[] state move-by-move
       for each move: showReplayStep() + showReplayBoard() + waitForEnter()
       show round result
  → loop: replay another round / another game
```

The compact replay board renders in a different style from the game board — a clean `5×5` cell grid with `[X]`/`[O]` highlighting the last move, making it easy to follow the game progression.

---

## Leaderboard System

Two leaderboard views are maintained:

**Public Leaderboard** (top 10, shown at startup and session end):
- Rank, Player name, Wins, Last Active, Days Old

**Admin Table** (all players, admin panel only):
- Rank, Player name, Wins, Last Active, Days Old, Member Since

Both are dynamically column-widthed — the table layout adjusts to the longest content in each column automatically.

Ranking uses `TreeSet<Player>` with `Player.compareTo()`:
1. Sort by `lifetimeWins` descending
2. Tiebreak alphabetically by name ascending

Win increments require removing from `TreeSet`, mutating, and re-inserting — this is the only safe way to maintain `TreeSet` sort order after value mutation.

Wins are NOT incremented when undo is enabled for that session — the game tracks this via `SessionContext.isUndoEnabled()` and skips `registry.incrementWin()` on the winning path.

---

## Command Pipeline

Every line of user input passes through `InputHandler.readLine()`, which:

1. Reads raw line from `Scanner`
2. Uppercases for comparison
3. Calls `commandProcessor.handle(uppercased)` before returning to caller
4. If command recognized → execute (may throw exception) → return `null`
5. If not a command → return the uppercased string to caller

This pipeline is **transparent** — callers (sessions, engine) never know if a command was processed. They just re-prompt when `readLine()` returns `null`.

```
User types "manage" during their move prompt
       ↓
InputHandler.readLine()
       ↓
CommandProcessor.handle("MANAGE") → ManageCommand.execute()
       ↓
ManageCommand: request admin password
              → AdminControl.show(sc)   ← full admin panel
              → returns normally
       ↓
InputHandler returns null
       ↓
Session re-prompts: "Enter your move (X) [1-9]:"
       ↓
Game continues — session state unchanged
```

Password-sensitive reads (`readRawLine()`) still intercept commands but don't uppercase, preserving password case.

---

## Project Structure

```
TIC_TAC_TOE_FINAL/
├── src/
│   ├── MainExecution.java          ← Entry point: Logger.init() + GameEngine.start()
│   ├── admin/
│   │   ├── AdminControl.java       ← Admin panel logic
│   │   ├── AdminInput.java         ← Raw Scanner wrapper (bypasses command pipeline)
│   │   └── AdminService.java       ← Interface
│   ├── auth/
│   │   ├── AuthService.java        ← Interface: authenticate, isLocked, attemptsRemaining
│   │   ├── PasswordUtil.java       ← SHA-256 + SecureRandom salt generation + validation
│   │   ├── PlayerCreator.java      ← Player resolution: lookup → auth → register
│   │   └── SessionAuthManager.java ← In-memory 4-attempt lockout implementation
│   ├── bot/
│   │   ├── Bot.java               ← Interface: chooseMove, getName, getEloRating, etc.
│   │   ├── BotFactory.java        ← Factory: level (int) → Bot instance
│   │   ├── BeginnerBot.java       ← RAVE: random-dominant
│   │   ├── EasyBot.java           ← GREX: heuristic-probabilistic
│   │   ├── MediumBot.java         ← WIRE: win/block/extend
│   │   ├── HardBot.java           ← FLINT: fork-aware + FLUX defect
│   │   ├── UnbeatableBot.java     ← PROBE: alpha-beta minimax + EquiSelect
│   │   ├── StallBot.java          ← STALL: draw optimization (modified minimax)
│   │   └── UtilBot.java           ← Shared: win/block/fork/extend detection
│   ├── command/
│   │   ├── Command.java           ← Interface: execute()
│   │   ├── CommandHandler.java    ← Map<String, Command> registry + dispatch
│   │   ├── CommandProcessor.java  ← Interface: handle(String) → boolean
│   │   └── impl/
│   │       ├── EndCommand.java    ← Throws SessionEndException if inSession
│   │       ├── ExitCommand.java   ← Save players → System.exit(0)
│   │       ├── ManageCommand.java ← Password check → AdminControl.show()
│   │       └── UndoCommand.java   ← Throws UndoRequestException if eligible
│   ├── core/
│   │   ├── GameBoard.java         ← Board state + Snapshot undo + XO rendering
│   │   ├── GameEngine.java        ← System orchestrator + lifecycle + error recovery
│   │   ├── GameHistory.java       ← In-session GameResult collection
│   │   ├── GameResult.java        ← Serializable match data (parallel round lists)
│   │   ├── SessionFactory.java    ← Builds PVP/PVB/BVB sessions with all deps
│   │   └── SessionType.java       ← Enum: PLAYER_VS_PLAYER, PLAYER_VS_BOT, BOT_VS_BOT
│   ├── exception/
│   │   ├── GameErrorCode.java     ← Enum: 13 typed error codes
│   │   ├── GameException.java     ← Base typed exception with errorCode
│   │   ├── InvalidBotSelectionException.java
│   │   ├── InvalidSessionException.java
│   │   ├── SessionEndException.java
│   │   └── UndoRequestException.java
│   ├── input/
│   │   ├── Input.java             ← Interface: readLine, waitForEnter, readCellChoice, etc.
│   │   └── InputHandler.java      ← Scanner wrapper with command interception
│   ├── player/
│   │   ├── Player.java            ← Domain model: name, wins, password, dates
│   │   ├── PlayerRegistry.java    ← HashMap + TreeSet dual-store implementing Registry + RankingView
│   │   ├── PlayerResult.java      ← Wrapper: Player + isNew flag
│   │   ├── RankingView.java       ← Interface: read-only ranking operations
│   │   ├── Registry.java          ← Interface: mutable player operations
│   │   └── store/
│   │       ├── FilePlayerStore.java ← Char-pair encoded flat-file persistence
│   │       └── PlayerStore.java     ← Interface: loadAll(), saveAll()
│   ├── renderer/
│   │   ├── classes/               ← Concrete renderers (one per view group)
│   │   └── view/                  ← 13 view interfaces (one per concern)
│   ├── replay/
│   │   ├── ReplayEngine.java      ← Match selection → round selection → step replay
│   │   ├── ReplayRenderer.java    ← Compact board + step display
│   │   └── ReplayView.java        ← Interface
│   ├── sessions/
│   │   ├── GameSession.java       ← Interface: play(), toResult(), getSessionType()
│   │   ├── PlayerVSPlayerSession.java
│   │   ├── PlayerVSBotSession.java
│   │   ├── BotVSBotSession.java
│   │   └── SessionContext.java    ← Shared state: inSession, inRound, undoEnabled
│   └── utility/
│       ├── Config.java            ← All constants (nested static classes per domain)
│       ├── Logger.java            ← Timestamped file logger (INFO/WARN/ERROR)
│       └── Strings.java           ← All multi-line UI strings (centralized)
│
├── players.dat                    ← Persistent player storage (char-pair encoded)
└── loggers.log                    ← Timestamped system event log
```

---

## How to Run

**Prerequisites:**
- Java 21 or later (uses records, pattern switch, sealed types in JDK 21+)
- IntelliJ IDEA (recommended) or any Java IDE
- Terminal with minimum **~120 columns** width (game board is 83 chars wide; recommend full-screen)

**Steps:**

```bash
# Clone or download the project
git clone <repository-url>
cd TIC_TAC_TOE_FINAL

# Compile
javac -d out/production/FinalT3 src/**/*.java src/*.java

# Run
java -cp out/production/FinalT3 MainExecution
```

**From IntelliJ:**
1. Open project
2. Mark `src/` as Sources Root
3. Run `MainExecution.main()`

**Important:** Run in full-screen terminal. The game board requires approximately 90 columns. If the board wraps, columns are misaligned.

**Built-in commands (available at any prompt):**

| Command | Effect |
|---|---|
| `exit` | Save all players and exit cleanly |
| `manage` | Open admin panel (password required: `123456`) |
| `end` | Abandon current session (history still recorded) |
| `undo` | Undo last move(s) (only when undo enabled for session) |

---

## Extensibility Guide

### Adding a New Bot

1. Create `bot/NewBot.java` implementing `Bot` interface
2. Add constructor for single + dual instance (`boolean firstInstance`)
3. Add `case N -> new NewBot()` in `BotFactory.createBot(int level)`
4. Add display data to `Config.BotData.BOT_TABLE`

Nothing else in the system changes. Sessions, factory, renderers all adapt automatically.

### Adding a New Session Type

1. Add enum value to `SessionType`
2. Implement `GameSession` interface in a new session class
3. Add `case` in `SessionFactory.createGameSession()`
4. Add display row to `EngineRenderer.showSessionTypes()`

### Swapping the Renderer

Every renderer implements a view interface. To swap `EngineRenderer` for a different output target (GUI, web, JSON logging):

1. Create a class implementing `EngineView` (which extends 8 sub-interfaces)
2. Pass the new instance into `GameEngine.initialize()` where `new EngineRenderer(output)` currently appears

The engine, sessions, commands — all depend only on the interface. Zero other changes required.

### Adding a New Command

1. Create a class implementing `Command`
2. Register it in `CommandHandler` constructor: `commands.put("mycommand", new MyCommand(...))`
3. The input pipeline will start intercepting `"mycommand"` at every prompt automatically

---

## Tech Stack

| Component | Technology |
|---|---|
| Language | Java 24 (JDK 24) |
| IDE | IntelliJ IDEA 2025.2 |
| Build | Manual javac (no Maven/Gradle — deliberate for portability) |
| Persistence | Custom char-pair encoded flat file |
| Hashing | SHA-256 via `java.security.MessageDigest` |
| Salt Generation | `java.security.SecureRandom` |
| Sorting | `java.util.TreeSet` with `Comparable<Player>` |
| Undo Stack | `java.util.ArrayDeque` as `Deque<Snapshot>` |
| Logging | Custom `Logger` with `PrintWriter` + timestamp |
| AI | Minimax with Alpha-Beta pruning + heuristic rule engines |

---

## Key Highlights — Resume-Worthy

- **6-tier adversarial AI system** from random agent to alpha-beta minimax with EquiMinMax output selection
- **Exception-driven control flow** — commands interrupt game state via typed exceptions propagated through the call stack
- **Dual-interface registry** — `PlayerRegistry` implements both `Registry` and `RankingView`, consumed independently by callers
- **Memento pattern for undo** — full board snapshot stack enables state-consistent move reversal across all session types
- **Fully decoupled rendering layer** — 13 view interfaces mean zero renderer dependencies in business logic
- **SHA-256 + salt password security** with session-scoped account lockout
- **Draw-optimization AI agent (STALL)** — inverted minimax scoring demonstrating non-competitive equilibrium strategy
- **Replay engine** — any round from any historical match can be replayed step-by-step post-session
- **Self-recovering engine** — structured exception hierarchy with restart-on-failure at the top level
- **Admin panel accessible mid-game** — command pipeline intercepts `manage` at any input without interrupting session state
- **Custom obfuscated file encoding** with backward-compatible deserialization across format versions

---

## Future Scope

Items that are architecturally realistic extensions given the current codebase:

**STALL Bot Completion**
Bring STALL's Draw Achievement Rate from 69.7% to ≥95% across all opponents. The current minimax scoring and fork detection is close — additional endgame pattern recognition should close the gap.

**Persistent Match History**
`GameHistory` currently lives only in-session memory. Extending `FilePlayerStore` or adding a `MatchStore` interface would enable cross-session replay without changing any session or engine code.

**Graphical / Web Rendering**
All renderers implement interfaces. A Swing or JavaFX renderer, or an HTTP/WebSocket adapter, can be injected without touching the engine. The foundation is ready.

**Bot ELO Tournament Mode**
`BotVSBotSession` already supports any pairing. A tournament orchestrator running N×N matches and updating a live ELO table would slot in cleanly as a new session type.

**More Bot Levels**
The strategy gap between FLINT (rule-based fork detection) and PROBE (perfect minimax) is large. A bot using limited-depth minimax (depth 4–6) would fit naturally at Level 4.5.

**Testing Layer**
The `testingHelpers/` directory already exists in the project. Adding JUnit 5 tests for `UtilBot`, `PlayerRegistry`, `GameBoard.undo()`, and bot strategy verification would formalize correctness guarantees.

---

## Author

**Shivam Bhagat**
B.Tech CSE | Java Development | Data Structures & Algorithms

- LinkedIn: [shivam-bhagat-](https://linkedin.com/in/shivam-bhagat-)
- LeetCode: [shivam_bhagat_](https://leetcode.com/shivam_bhagat_)

---

> *"This project represents a progression from fundamental programming concepts to advanced AI-driven gameplay systems and design thinking."*

---

*Built over 3 weeks. Every class, interface, and design decision was deliberate.*
