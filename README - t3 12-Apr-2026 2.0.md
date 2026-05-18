# TactixEngine

### *A production-grade adversarial game engine built to demonstrate clean architecture, multi-tier AI decision systems, and backend engineering depth — entirely in Java.*

---

> **The domain is Tic-Tac-Toe. The engineering is not.**
>
> This system was built using a deliberately simple game domain so the architecture has nowhere to hide.
> What you see here is not a game project. It is a backend engineering showcase that happens to play Tic-Tac-Toe.

---

## At a Glance

| | |
|---|---|
| **Language** | Java 24 (JDK 24) |
| **Session Modes** | Player vs Player · Player vs Bot · Bot vs Bot |
| **AI Bot Tiers** | 6 bots — ELO 1000 → 2000 + draw-optimization agent |
| **Architecture** | 4-layer, interface-driven, SOLID-compliant |
| **Design Patterns** | 10+ (Command, Factory, Strategy, Memento, Repository, Facade, DI, and more) |
| **View Interfaces** | 13 rendering interfaces — zero renderer coupling in business logic |
| **Auth** | SHA-256 + `SecureRandom` salt · 4-attempt session lockout |
| **Persistence** | Custom char-pair encoded flat file · backward-compatible across format versions |
| **Classes / Interfaces** | ~50 classes · ~24 interfaces across 13 packages |
| **Error Handling** | Typed `GameException` hierarchy · 13 `GameErrorCode` values · self-recovering engine |

---

## Table of Contents

- [Why This Is Not a Simple Game](#why-this-is-not-a-simple-game)
- [Core Engineering Highlights](#core-engineering-highlights)
- [Architecture Overview](#architecture-overview)
- [Package Map](#package-map)
- [System Control Flow](#system-control-flow)
- [UML — Key Class Relationships](#uml--key-class-relationships)
- [AI Bot System](#ai-bot-system)
- [Session Modes](#session-modes)
- [Feature Set — Verified from Source](#feature-set--verified-from-source)
- [Design Patterns Used](#design-patterns-used)
- [Error Handling Architecture](#error-handling-architecture)
- [Player Registry & Persistence](#player-registry--persistence)
- [Authentication System](#authentication-system)
- [Admin Control Panel](#admin-control-panel)
- [Session History & Replay Engine](#session-history--replay-engine)
- [Leaderboard System](#leaderboard-system)
- [Command Pipeline](#command-pipeline)
- [Engineering Trade-offs](#engineering-trade-offs)
- [Project Structure](#project-structure)
- [How to Run](#how-to-run)
- [Extensibility Guide](#extensibility-guide)
- [By the Numbers](#by-the-numbers)
- [Tech Stack](#tech-stack)
- [Interview Talking Points](#interview-talking-points)
- [Future Scope](#future-scope)
- [Author](#author)

---

## Why This Is Not a Simple Game

Most Tic-Tac-Toe implementations are ~200 lines of procedural code. This system implements the following concerns that have nothing to do with game rules:

| Concern | Implementation |
|---|---|
| **AI Decision Making** | 6 distinct bot algorithms — random to alpha-beta minimax with EquiSelect |
| **Session Lifecycle** | `SessionContext` tracks `inSession` / `inRound` / `undoEnabled` shared across layers |
| **Command Pipeline** | Every input keystroke passes through `CommandProcessor` before reaching the game |
| **Rendering Abstraction** | All output is routed through 13 view interfaces — no `System.out` in business logic |
| **Persistence** | Custom char-pair obfuscated flat file with backward-compatible multi-version deserialization |
| **Authentication** | SHA-256 + 16-byte `SecureRandom` salt · per-session lockout after 4 failed attempts |
| **Exception Hierarchy** | Typed `GameException` subclasses with `GameErrorCode` enum — structured error identity |
| **Undo System** | Memento-based `Snapshot` records enabling move reversal with full state consistency |
| **Match Replay** | Cell-by-cell step-through replay of any historical round, including abandoned ones |
| **Admin Panel** | Password-protected management panel accessible mid-game at any input prompt |
| **Draw Optimization AI** | Inverted minimax scoring (winning = neutral, losing = catastrophic, drawing = preferred) |
| **Self-Recovery** | Fatal exceptions trigger a controlled restart decision, never a silent crash |

---

## Core Engineering Highlights

### Dependency Inversion Everywhere

`GameEngine` holds an `EngineView` — not an `EngineRenderer`. It holds a `Registry` — not a `PlayerRegistry`. Sessions hold `SessionView`, `PlayBoardView`, and `Input` — never their concrete implementations. Constructor injection is used throughout. There is no service locator, no static coupling.

Replace `EngineRenderer` with a Swing renderer, a JSON logger, or an HTTP adapter. The engine changes nothing.

### Exception-Driven Control Flow

`SessionEndException` and `UndoRequestException` are thrown from deep inside the input pipeline and propagate up through session → engine without polluting method signatures with boolean return flags or state checks at every layer.

- `UndoRequestException` is caught inside the round loop — the round continues after reversal.
- `SessionEndException` propagates to `GameEngine.startGameLoop()` — match is recorded as abandoned, then engine prompts for next session.

### Dual-Role Registry with Segregated Interfaces

`PlayerRegistry` implements both `Registry` (mutable: add, increment wins, delete, rename, set password) and `RankingView` (read-only: top players, all players, size). `AdminControl` receives both. Display components receive only `RankingView`. The concrete `PlayerRegistry` class is never exposed to callers — only the interface they need.

### Memento-Pattern Undo with Full State Consistency

`GameBoard.makeMove()` pushes a `Snapshot` record — containing a cloned `freq[]` array, a deep-cloned visual `char[][]` board, and `stepCount` — onto a `Deque<Snapshot>` before every mutation. `undo()` pops and restores all three fields atomically. Undo is O(1) per move. Both the logical state and visual render are perfectly consistent after reversal.

### ELO-Calibrated Bot Architecture

Each bot's ELO and win-rate percentage are not estimates. FLINT's 8% `FLUX` defect rate (a configurable constant in `Config.BotData.HARD_BOT_DEFECT_RATE`) is calibrated to produce an 83% win floor — removing it would bring FLINT near 100%. PROBE adds `EquiSelect`: when multiple minimax moves score identically, one is chosen uniformly at random from the set, giving variety without sacrificing correctness.

---

## Architecture Overview

The system follows a clean 4-layer separation:

```
┌─────────────────────────────────────────────────────────────────┐
│                       PRESENTATION LAYER                         │
│  EngineRenderer · SessionRenderer · PlayBoardRenderer            │
│  PlayerTableRenderer · HistoryRenderer · ReplayRenderer          │
│  [ 13 view interfaces — all rendering fully decoupled ]          │
└───────────────────────────┬─────────────────────────────────────┘
                            │ depends only on view interfaces
┌───────────────────────────▼─────────────────────────────────────┐
│                       APPLICATION LAYER                          │
│  GameEngine · SessionFactory · GameHistory · ReplayEngine        │
│  AdminControl · PlayerCreator · CommandHandler                   │
└───────────────────────────┬─────────────────────────────────────┘
                            │ depends only on domain interfaces
┌───────────────────────────▼─────────────────────────────────────┐
│                        DOMAIN LAYER                              │
│  GameBoard · Player · PlayerRegistry · GameResult                │
│  Bot implementations · GameSession implementations               │
│  SessionContext · GameErrorCode · Exception hierarchy            │
└───────────────────────────┬─────────────────────────────────────┘
                            │ depends only on store interface
┌───────────────────────────▼─────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER                          │
│  FilePlayerStore · Logger · Config                               │
│  [ Fully swappable — no domain logic here ]                      │
└─────────────────────────────────────────────────────────────────┘
```

---

## Package Map

```
src/
├── admin/              AdminControl · AdminInput · AdminService (interface)
│
├── auth/               AuthService (interface)
│                       SessionAuthManager — 4-attempt session lockout
│                       PlayerCreator      — lookup → auth → register flow
│                       PasswordUtil       — SHA-256 + SecureRandom salt
│
├── bot/                Bot (interface) · BotFactory
│                       BeginnerBot (RAVE) · EasyBot (GREX) · MediumBot (WIRE)
│                       HardBot (FLINT)   · UnbeatableBot (PROBE)
│                       StallBot (STALL)  · UtilBot (shared board analysis)
│
├── command/            Command (interface) · CommandProcessor (interface)
│   └── impl/           ExitCommand · ManageCommand · EndCommand · UndoCommand
│                       CommandHandler — Map<String, Command> registry + dispatch
│
├── core/               GameEngine     — system orchestrator + lifecycle + recovery
│                       GameBoard      — board state + Snapshot undo + XO rendering
│                       GameHistory    — in-session match collection
│                       GameResult     — serializable parallel round lists
│                       SessionFactory — constructs PVP/PVB/BVB with all dependencies
│                       SessionType    — enum: PLAYER_VS_PLAYER, PLAYER_VS_BOT, BOT_VS_BOT
│
├── exception/          GameErrorCode (enum, 13 codes) · GameException (typed base)
│                       InvalidBotSelectionException · InvalidSessionException
│                       SessionEndException · UndoRequestException
│
├── input/              Input (interface) · InputHandler — Scanner + command interception
│
├── player/             Player · PlayerRegistry (Registry + RankingView impl)
│   │                   PlayerResult · Registry (interface) · RankingView (interface)
│   └── store/          PlayerStore (interface) · FilePlayerStore
│
├── renderer/
│   ├── classes/        EngineRenderer · SessionRenderer · PlayBoardRenderer
│   │                   PlayerTableRenderer · HistoryRenderer
│   └── view/           AdminView · AuthView · EndCommandView · EngineView
│                       ExitCommandView · HistoryView · InputView
│                       ManageCommandView · PlayBoardView · PlayerTableView
│                       SessionView · SetupView · UndoCommandView
│
├── replay/             ReplayEngine · ReplayRenderer · ReplayView (interface)
│
├── sessions/           GameSession (interface) · SessionContext
│                       PlayerVSPlayerSession · PlayerVSBotSession · BotVSBotSession
│
└── utility/            Config — all constants (nested static classes per domain)
                        Logger — timestamped file logger
                        Strings — all multi-line UI strings, centralized
```

---

## System Control Flow

```
MainExecution.main()
       │
       └── Logger.init() → GameEngine.start()    ← outer restart loop
               │
               ├── initialize()
               │       ├── EngineRenderer, SessionRenderer, PlayBoardRenderer, ...
               │       ├── PlayerRegistry(FilePlayerStore) — loads players.dat
               │       ├── SessionContext               — shared state tracker
               │       ├── CommandHandler               — register: exit, manage, end, undo
               │       ├── InputHandler(Scanner, CommandHandler)
               │       ├── SessionFactory               — assembles sessions with deps
               │       ├── ReplayEngine
               │       └── GameHistory
               │
               ├── runIntroSequence()
               │       ├── MODULE 1: Feature Overview (skippable Y/N)
               │       ├── MODULE 2: AI Bot System Panel
               │       ├── MODULE 3: Instructions
               │       └── MODULE 4: Global Leaderboard
               │
               └── startGameLoop()   [while playAnother]
                       │
                       ├── showSessionTypes() → readSessionChoice()
                       ├── SessionFactory.createGameSession(type)
                       │       PVP: PlayerCreator × 2 (auth/register each)
                       │       PVB: PlayerCreator × 1 + BotFactory
                       │       BVB: BotFactory × 2
                       │
                       ├── sessionContext.enterSession()
                       │
                       ├── session.play()
                       │       │
                       │       ├── showUndoOffer() → context.enableUndo() / disableUndo()
                       │       │
                       │       └── [while keepPlaying — round loop]
                       │               ├── context.enterRound()
                       │               ├── playRound()
                       │               │       └── [while !winner && !full — move loop]
                       │               │               ├── input.readCellChoice(board)
                       │               │               │     └── CommandProcessor intercepts:
                       │               │               │           "undo"   → throw UndoRequestException
                       │               │               │           "end"    → throw SessionEndException
                       │               │               │           "exit"   → save + System.exit(0)
                       │               │               │           "manage" → AdminControl.show()
                       │               │               ├── board.makeMove() → push Snapshot
                       │               │               └── board.checkWinner()
                       │               ├── context.exitRound()
                       │               └── showScoreboard() → showNextRoundPrompt()
                       │
                       ├── [catch SessionEndException] → record abandoned match
                       └── sessionContext.exitSession()

               shutdown()
                       ├── gameHistory.showHistory() → replayEngine.offerReplay()
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
 PlayerVSPlayerSession  PlayerVSBotSession  BotVSBotSession


               «interface»
                  Bot
                   │
     ┌──────┬──────┼──────┬──────┬─────────┐
     │      │      │      │      │         │
  RAVE    GREX   WIRE   FLINT  PROBE    STALL


               «interface»          «interface»
                Registry    ◄──── AdminControl ──► RankingView
                   │                                    │
             PlayerRegistry  ─────────────────────────►┘
                   │
             PlayerStore «interface»
                   │
            FilePlayerStore


               «interface»
                EngineView   (extends: AuthView, SetupView, AdminView,
                    │          ExitCommandView, EndCommandView,
                    │          ManageCommandView, UndoCommandView, InputView)
              EngineRenderer


 GameEngine
     ├── Input (InputHandler)
     │       └── CommandProcessor (CommandHandler)
     │               ├── ExitCommand
     │               ├── ManageCommand ──► AdminControl
     │               ├── EndCommand    ──► SessionContext (throws SessionEndException)
     │               └── UndoCommand   ──► SessionContext (throws UndoRequestException)
     │
     ├── SessionFactory ──► GameSession (PVP / PVB / BVB)
     ├── PlayerRegistry (via Registry + RankingView interfaces)
     ├── SessionContext ◄── shared by engine, sessions, and command implementations
     └── GameHistory ──► ReplayEngine
```

---

## AI Bot System

The bot system is the most carefully engineered subsystem. Each bot is a distinct **strategy**, not a difficulty slider. They are architecturally isolated — `BotFactory` is the only place that knows concrete bot types.

### Bot Roster

| Level | Name | ELO | Win Rate | Algorithm Family |
|-------|------|-----|----------|-----------------|
| 1 | **RAVE** | 1000 | ~0% | Random-dominant with rare instinctive extension |
| 2 | **GREX** | 1368 | ~37% | Heuristic-probabilistic with unreliable blocking |
| 3 | **WIRE** | 1716 | ~72% | Win/Block rule engine with partial opening awareness |
| 4 | **FLINT** | 1833 | ~83% | Fork-aware priority rules + calibrated 8% FLUX defect |
| 5 | **PROBE** | 2000 | 100% | Alpha-beta minimax + priority ordering + EquiSelect |
| 0 | **STALL** | — | 69.7% DAR | Draw-optimization via inverted minimax (win avoidance) |

*DAR = Draw Achievement Rate.*

---

### RAVE — BeginnerBot (ELO 1000)

The weakest agent. On 50% of moves it plays completely random. When it engages heuristics, it may extend from an existing piece or — rarely — detect an immediate win. There is **no blocking logic** whatsoever.

```
chooseMove():
  50% → random valid cell
  50% chance → check for immediate win (often skipped)
  extend from own piece adjacently (instinctive, not strategic)
  fallback → random
```

> **Design intent:** A player who has never consciously thought about Tic-Tac-Toe.

---

### GREX — EasyBot (ELO 1368)

Heuristic-aware but unreliable. 40% of moves are random. Blocking is probabilistic — GREX blocks threats only 70% of the time. Creates a bot that feels like a distracted casual player: sometimes sharp, often absent-minded.

```
chooseMove():
  40% → random
  detect immediate win → take it
  70% chance → detect and block opponent win
  prefer center (70% probability)
  prefer corner cells
  fallback → random
```

> **Design intent:** A player who occasionally pays attention but cannot sustain it.

---

### WIRE — MediumBot (ELO 1716)

Fully reactive. Always wins when it can, always blocks when threatened, and extends its own lines otherwise. Carries a 15% opening-awareness rate — occasionally plays optimal openings. No fork detection.

```
chooseMove():
  if stepNo < 2 && random() < 0.15 → optimal opening
  check win → take it
  check block → block it
  extend existing line adjacently
  fallback → random valid
```

> **Design intent:** A competent casual player who never plans more than one move ahead.

---

### FLINT — HardBot (ELO 1833)

The first strategically complete bot. Implements the full rule-priority chain with fork detection and multi-fork forcing logic. The defining characteristic is the **FLUX defect**: an 8% probability per move of ignoring all strategy entirely and playing random.

FLUX is not a bug — it is the calibrated mechanism that produces an 83% win rate. Removing it would push FLINT close to 100%.

```
chooseMove():
  8% → FLUX: play completely random (human-like lapse)
  apply opening strategy if early game
  1. Win immediately
  2. Block opponent's immediate win
  3. Create fork (2+ simultaneous threats)
  4. Block opponent fork:
       single fork  → block directly
       multi-fork   → create forcing threat (opponent must respond)
       fallback     → block any fork
  5. Positional: center → corners → edges
```

> **Design intent:** A strong, strategic player with occasional and unpredictable blind spots.

---

### PROBE — UnbeatableBot (ELO 2000)

Full minimax with alpha-beta pruning. Theoretically unbeatable — optimal play from every game state. Two engineering refinements distinguish this from a naive minimax:

**Priority move ordering:** Moves are evaluated in the sequence `[4, 0, 2, 6, 8, 1, 3, 5, 7]` (center → corners → edges). Better moves evaluated first means more alpha-beta cutoffs, which means fewer nodes explored. The ordering is not aesthetic — it is a performance optimization.

**EquiSelect:** When multiple moves produce identical minimax scores, PROBE picks uniformly at random among them. The randomness is in output selection only — evaluation correctness is unchanged. This prevents PROBE from playing an identical game every session.

```
chooseMove():
  apply opening strategy if early game
  for each move in priorityOrder:
    score = minMax(board, opponent_turn, depth=0, α=-∞, β=+∞)
    accumulate equimax candidates (equal best score)
  return random from equimax set

minMax():
  terminal: (10-depth) for win, (depth-10) for loss, 0 for draw
  alpha-beta pruning on both maximizing and minimizing branches
  priority order used for both branches
```

> **Design intent:** Mathematically optimal — the hard ceiling of what is achievable in this game.

---

### STALL — StallBot (Special — Draw Optimizer)

The most philosophically unusual agent. STALL's objective is not to win — it is to **never lose while maximizing draws**. This is a non-competitive equilibrium strategy.

A modified minimax is used where:
- Winning for STALL → score **0** (neutral — acceptable but not preferred)
- Losing → score **−100** (catastrophic — worst possible outcome)
- Drawing → score **+1** (preferred outcome)

An additional fork-detection penalty (−50) discourages moves that allow the opponent to create a fork on the next turn.

```
chooseMove():
  stepNo == 0 → always take center
  stepNo == 1 → correct opening response based on opponent placement

  for each valid move:
    score = minimax(board, ...)
    if opponentCanForkNext → score -= 50
  
  prefer drawMove (score == 1) over any higher-scoring move

minimax() scoring:
  STALL wins  → 0    (neutral — avoid winning)
  STALL loses → -100 (worst)
  draw        → +1   (best)
```

**Draw Achievement Rate by opponent:**

| vs STALL | vs PROBE | vs FLINT | vs WIRE | vs GREX | vs RAVE |
|:--------:|:--------:|:--------:|:-------:|:-------:|:-------:|
| 100% | 100% | 93.6% | 77.1% | 57.1% | 21.0% |
| **Overall DAR: 69.7%** | | | | | |

STALL paradoxically struggles most against RAVE — the weakest bot — because RAVE's random play is structurally unpredictable and accidentally wins before STALL can steer toward a draw.

---

### UtilBot — Shared Algorithmic Core

All bots share `UtilBot` (final utility class) for board analysis:

| Method | Purpose |
|---|---|
| `getWinIndexes(board, flag)` | All cells that complete an immediate win for `flag` |
| `getExtendIndexes(board, flag)` | Adjacent empty cells to existing `flag` pieces |
| `getValidIndexes(board)` | All empty cells |
| `getForkIndexes(board, flag)` | Cells where placing `flag` creates 2+ simultaneous winning threats |
| `winnerCheck(board)` | Returns winner flag or 0 |
| `getOpeningStrategyMove(board, step, random)` | Shared opening book |
| `pickRandom(set, random)` | Equimax selection from `HashSet<Integer>` |

---

## Session Modes

### Player vs Player

Two human players resolved through `PlayerCreator` — registry lookup, authentication if password-protected, or fresh registration. Undo is optional (opted in per session) with automatic leaderboard exclusion. First mover is chosen per round.

### Player vs Bot

One human player against a bot selected from the 6-level roster. Undo undoes both the player's move **and** the bot's response (up to 2 moves), restoring the board to just before the player's last input. Undo is blocked if the session started without it — the context check happens inside `UndoCommand.execute()`, not in the session logic.

### Bot vs Bot

Fully automated simulation. No undo. First mover is chosen by the human user at session start; first-mover alternates automatically each round thereafter. Human presses Enter to advance between moves, allowing observation. All rounds are recorded and replayable post-session.

---

## Feature Set — Verified from Source

Every feature listed here is confirmed directly from the source code.

**Session Management**
- Three session modes: PVP, PVB, BVB
- Per-session undo opt-in with automatic leaderboard exclusion
- Mid-session admin panel access via `manage` command without interrupting session state
- Clean session abandonment via `end` with history preservation and null-safe replay handling

**AI System**
- 6 distinct bots across ELO 1000–2000, each a separate implementation class
- Bot vs Bot simulation with human-paced step-through and automatic first-mover alternation
- Configurable bot think delay in milliseconds (`BOT_THINK_DOT_DELAY_MS_PVG` vs `BOT_THINK_DOT_DELAY_MS_BVB`)

**Player Management**
- Persistent registry across restarts (`players.dat`)
- Auto-generated names (`PLAYER_1` through `PLAYER_1500`, timestamp fallback)
- Optional SHA-256 + salt password protection per player
- Session-scoped lockout after 4 failed password attempts; lock resets on restart
- Duplicate-name prevention during session setup

**Admin Panel**
- Password-protected, accessible mid-game at any prompt (bypasses command pipeline deliberately)
- View all players sorted by rank: wins, last active, days old, member since
- Player selection by name or rank
- Operations: rename, add/change/remove password, set lifetime wins (authorized), delete
- Table refresh without exiting panel

**Leaderboard & History**
- Global top-10 leaderboard (configurable) shown at startup and session end
- Per-session game history table: result, lead margin, round count
- Full match replay: select game → select round → step through each move
- Abandoned-round detection: null entry in parallel lists, handled gracefully by replay engine

**Game Board**
- 83 × 32 character ASCII render with custom X and O art (each cell: 25 × 9 character block)
- Snapshot-based undo with perfect visual and logical state restoration
- Win detection gated at 5 moves minimum (no winner possible before move 5)

**Reliability**
- Typed exception hierarchy with `GameErrorCode` enum (13 error codes)
- Self-recovering engine: unhandled exceptions prompt restart — engine never crashes silently
- `IOException` from storage is wrapped as `GameException(STORAGE_LOAD_FAILED)` — no checked exceptions leak to `GameEngine`
- Timestamped log file (`loggers.log`) with INFO/WARN/ERROR levels

---

## Design Patterns Used

| Pattern | Location | Purpose |
|---|---|---|
| **Strategy** | `Bot` interface + 6 implementations | Each bot is an independent decision strategy, swappable at runtime via `BotFactory` |
| **Factory Method** | `BotFactory`, `SessionFactory` | Decouples creation from usage; `GameEngine` never calls `new BotXxx()` or `new SessionXxx()` |
| **Command** | `Command`, `CommandHandler`, `ExitCommand`, `ManageCommand`, `EndCommand`, `UndoCommand` | Commands are objects registered in a `Map<String, Command>`, dispatched by keyword |
| **Memento** | `GameBoard.Snapshot` record + `Deque<Snapshot>` | Full board state captured before every mutation; `O(1)` undo via stack pop |
| **Repository** | `Registry` + `RankingView` interfaces → `PlayerRegistry` → `PlayerStore` → `FilePlayerStore` | Three-tier abstraction separates storage concern from domain logic |
| **Facade** | `GameEngine` | Single orchestrator entry point; all subsystem complexity hidden from `MainExecution` |
| **Dependency Injection** | Constructor injection throughout | Every dependency is explicit; no static access, no service locator |
| **Template Method** | `GameEngine.start()` skeleton: `initialize()` → `runIntroSequence()` → `startGameLoop()` → `shutdown()` | Invariant lifecycle with distinct overrideable phases |
| **Observer-like** | `SessionContext` shared between engine, sessions, and command impls | Lightweight shared state without a full event bus |
| **Null Object** | Abandoned rounds stored as `null` in parallel lists, handled gracefully by `ReplayEngine` | Avoids null-checks in display logic; semantics are explicit |

---

## Error Handling Architecture

The exception system is typed and hierarchical — not a catch-everything `RuntimeException`.

```
GameErrorCode (enum, 13 values)
├── INVALID_SESSION_TYPE      SESSION_ALREADY_ACTIVE    SESSION_NOT_INITIALIZED
├── INVALID_BOT_LEVEL         BOT_INSTANTIATION_FAILED
├── INVALID_MOVE              INVALID_INPUT             POSITION_ALREADY_OCCUPIED
├── GAME_NOT_STARTED          GAME_ALREADY_OVER         INVALID_BOARD_STATE
└── STORAGE_LOAD_FAILED       STORAGE_SAVE_FAILED

GameException (RuntimeException base with errorCode field)
├── InvalidBotSelectionException  — level outside [0, 5]
└── InvalidSessionException       — session type outside [1, 3]

SessionEndException    — thrown by EndCommand, propagates to GameEngine (flow control)
UndoRequestException   — thrown by UndoCommand, caught by round loop (flow control)
```

**Propagation strategy, from innermost to outermost:**

| Exception | Thrown By | Caught By | Effect |
|---|---|---|---|
| `UndoRequestException` | `UndoCommand` | Session round loop | Undoes move(s), round continues |
| `SessionEndException` | `EndCommand` | `GameEngine.startGameLoop()` | Match recorded as abandoned, prompts next game |
| `InvalidSessionException` | `SessionFactory` | `GameEngine.startGameLoop()` | Error shown, loop continues |
| `InvalidBotSelectionException` | `BotFactory` | `GameEngine.startGameLoop()` | Error shown, loop continues |
| `GameException` (other) | Various | `GameEngine.startGameLoop()` | Error shown, prompts next game |
| `Exception` (unexpected) | Anywhere | `GameEngine.start()` | `handleFatalError()` + restart prompt |

---

## Player Registry & Persistence

`PlayerRegistry` maintains two concurrent data structures:

- `HashMap<String, Player>` — O(1) lookup by name for authentication and quick access
- `TreeSet<Player>` — automatically sorted by `Player.compareTo()` for ranking

`compareTo()` sorts by: wins descending → name ascending (alphabetical tiebreak).

Win increments follow remove-mutate-reinsert: the player is removed from `TreeSet` before mutation and re-added after, maintaining sort order correctly. This is the only correct approach for mutable `TreeSet` elements.

**File format (`players.dat`):**

Raw format per player:
```
name,wins,passwordHash,passwordSalt,joinDate,lastActive
```

Each character is split into two chars whose ASCII values sum to the original value — a lightweight encoding that makes the file non-trivially human-readable without formal encryption. The decoder handles 2-, 4-, 5-, and 6-field formats for backward compatibility across storage version changes. Corrupted password data (hash present without salt) is detected and silently reset rather than throwing.

**Capacity:** Max 1,000 registered players (configurable in `Config.PlayerConfig.MAX_PLAYERS`). Auto-name pool: `PLAYER_1` through `PLAYER_1500` with `System.currentTimeMillis()` fallback.

---

## Authentication System

Optional per-player password protection, implemented in the `auth` package.

**Password rules** (enforced by `PasswordUtil.getValidationError()`):
- 4–32 characters
- No spaces
- Printable ASCII only (characters 33–126)

**Storage:** `SHA-256(password + salt)` — salt is 16 bytes from `SecureRandom`, hex-encoded to a 32-character string. Both hash and salt stored in `players.dat`.

**Lockout:** `SessionAuthManager` tracks failed attempts per player in-memory. After 4 failures, the account is locked for the current session. Lock does not persist — it resets on program restart. Remaining attempts are displayed after each failure.

**Returning player flow:**

```
PlayerCreator.createPlayer()
  ├── name resolved via input
  ├── registry.getOrCreatePlayer(name)
  ├── isNew → showNewPlayerWelcome() → offerPasswordSetup()
  └── returning
        ├── no password → proceed directly
        └── has password → runAuthLoop()
              ├── authenticate() → success → proceed
              └── 4 failures → account locked → re-prompt for different name
```

---

## Admin Control Panel

Accessible at any input prompt by typing `manage` followed by the admin password. The panel runs on the raw `Scanner`, bypassing `InputHandler`'s command pipeline deliberately — this prevents recursive command interception during admin operations.

**Operations:**

```
Player selection:
  [1] By name   [2] By rank   [3] Refresh table   [0] Exit

Operations on selected player:
  [1] Change name            — validates uniqueness, logs rename
  [2] Manage password        — add / change / remove; validation-looped
  [3] Set lifetime wins      — admin-authorized direct override
  [4] Delete player          — confirmation required
```

The panel is loop-based — multiple actions can be performed before returning. `SessionContext.isInSession()` remains `true` throughout; the session resumes at exactly the same prompt that triggered `manage`.

---

## Session History & Replay Engine

Every session — including abandoned ones — is recorded as a `GameResult` in `GameHistory`.

`GameResult` stores three **parallel lists**, all indexed by round number:

| Field | Type | Content |
|---|---|---|
| `roundMoves` | `List<List<Integer>>` | Ordered cell indexes 0–8 per move; `null` if abandoned |
| `roundFirstPlayerStarts` | `List<String>` | Name of the first mover that round |
| `roundWinners` | `List<String>` | Winner name, `"TIE"`, or `null` if abandoned |

**Replay flow:**

```
ReplayEngine.offerReplay(sessions)
  → user selects game number
  → showMatchSummaryBox() + showRoundTable()
  → user selects round number
  → replayRound()
       ├── null moves → showRoundAbandoned() → return
       └── for each move: reconstruct freq[] → showReplayStep() + showReplayBoard() + waitForEnter()
  → "Replay another round?" loop → "Replay another match?" loop
```

The replay board renders in compact `5×5` cell style with `[X]`/`[O]` brackets highlighting the last-placed piece — distinct from the full game board, optimized for step-by-step review.

---

## Leaderboard System

Two views from the same `TreeSet<Player>`:

**Public leaderboard** (startup and session end) — Rank, Player, Wins, Last Active, Days Old.

**Admin table** (admin panel only) — adds Member Since column.

Both tables use dynamic column widths: each column is sized to its widest content entry at render time — no hardcoded padding.

Win increments are skipped when `SessionContext.isUndoEnabled()` is true for that session. The check happens in the winning path of each session class — `registry.incrementWin(winner)` is not called, keeping the leaderboard clean for practice sessions.

---

## Command Pipeline

Every line of user input passes through `InputHandler.readLine()`:

```
User input (any prompt)
       │
       ▼
InputHandler.readLine()
       ├── sc.nextLine().trim()
       ├── uppercase
       └── commandProcessor.handle(uppercased)
               ├── recognized → Command.execute() [may throw or return normally]
               │       ExitCommand  → save + System.exit(0)
               │       ManageCommand → AdminControl.show()  → returns normally
               │       EndCommand   → throw SessionEndException
               │       UndoCommand  → throw UndoRequestException (if eligible)
               └── unrecognized → return false

       ├── command handled → InputHandler returns null
       │       caller re-prompts (while loop reads null as "retry")
       └── not a command → return uppercased string to caller
```

**Example — `manage` typed during a move prompt:**

```
Session: "> [INPUT] NIKHIL, enter your move (X) [1-9]: "
User types: "manage"
       → InputHandler intercepts → ManageCommand.execute()
       → Admin panel runs fully (may take minutes)
       → ManageCommand returns normally
       → InputHandler returns null
       → Session re-prompts: "> [INPUT] NIKHIL, enter your move (X) [1-9]: "
       → Game continues, session state unchanged
```

Password-sensitive reads use `readRawLine()` — commands are still intercepted (uppercase check) but the raw line is not uppercased before returning, preserving password case sensitivity.

---

## Engineering Trade-offs

This section documents deliberate trade-offs made during design — the kind of reasoning that matters in engineering discussions.

**Snapshot-per-move vs. Inverse-move undo**

Each `makeMove()` pushes a full board snapshot: cloned `int[9] freq`, deep-cloned `char[][] board`, and `stepCount`. Memory cost: O(moves × boardSize). Alternative: store the inverse move only (cell + flag) and recompute. Chosen approach favors simplicity and state consistency over memory. For a 9-move game this is immaterial.

**Exception as flow control for `end` and `undo`**

`SessionEndException` and `UndoRequestException` are used as control-flow signals, not error indicators. The alternative — returning sentinel values up the call stack — would require every method between the input reader and the session to check a flag. The exception approach keeps method signatures clean at the cost of using exceptions for non-error conditions, which is a known trade-off in Java applications.

**In-memory session lockout only**

Auth lockout resets on program restart. Persistent lockout would require writing lockout state to `players.dat` or a separate file, adding coordination complexity between storage reads and in-memory state. The simpler in-memory approach was chosen; the security boundary is within a single executable session.

**`TreeSet` remove-mutate-reinsert**

Mutable `TreeSet` elements that change their ordering key (`lifetimeWins`) must be removed before mutation and re-added after, or the set's internal ordering becomes corrupted. This is a known Java collections constraint. The alternative — recalculate order lazily — would break O(N) leaderboard iteration. The current approach maintains correctness at the cost of 3 operations per win increment.

**No dependency injection framework (Spring/Guice)**

Dependencies are wired manually in `GameEngine.initialize()`. This is deliberate: the project demonstrates DI as a design principle (constructor injection throughout, interface-only coupling) without obscuring the pattern behind framework magic. Every dependency relationship is explicitly visible in code.

---

## Project Structure

```
TIC_TAC_TOE_FINAL/
├── src/
│   ├── MainExecution.java             ← Logger.init() + GameEngine.start()
│   ├── admin/
│   │   ├── AdminControl.java          ← Admin loop: select player → run operation
│   │   ├── AdminInput.java            ← Raw Scanner wrapper (bypasses command pipeline)
│   │   └── AdminService.java          ← Interface: show(Scanner)
│   ├── auth/
│   │   ├── AuthService.java           ← Interface: authenticate, isLocked, attemptsRemaining
│   │   ├── PasswordUtil.java          ← SHA-256 hashing + SecureRandom salt + validation
│   │   ├── PlayerCreator.java         ← Player resolution: lookup → auth → register
│   │   └── SessionAuthManager.java    ← In-memory 4-attempt lockout per session
│   ├── bot/
│   │   ├── Bot.java                   ← Interface: chooseMove, getName, getEloRating, etc.
│   │   ├── BotFactory.java            ← Factory: level (int) → Bot instance
│   │   ├── BeginnerBot.java           ← RAVE: random-dominant, no blocking
│   │   ├── EasyBot.java               ← GREX: heuristic-probabilistic
│   │   ├── MediumBot.java             ← WIRE: win/block/extend rule engine
│   │   ├── HardBot.java               ← FLINT: fork-aware + 8% FLUX defect
│   │   ├── UnbeatableBot.java         ← PROBE: alpha-beta minimax + EquiSelect
│   │   ├── StallBot.java              ← STALL: draw optimization, inverted minimax
│   │   └── UtilBot.java               ← Shared board analysis utilities
│   ├── command/
│   │   ├── Command.java               ← Interface: execute()
│   │   ├── CommandHandler.java        ← Map<String, Command> + dispatch
│   │   ├── CommandProcessor.java      ← Interface: handle(String) → boolean
│   │   └── impl/
│   │       ├── EndCommand.java        ← Throws SessionEndException when inSession
│   │       ├── ExitCommand.java       ← trimToMaxPlayers → save → System.exit(0)
│   │       ├── ManageCommand.java     ← Password check → AdminControl.show()
│   │       └── UndoCommand.java       ← Throws UndoRequestException when eligible
│   ├── core/
│   │   ├── GameBoard.java             ← Board state + Snapshot Deque + XO rendering
│   │   ├── GameEngine.java            ← Orchestrator + lifecycle + error recovery
│   │   ├── GameHistory.java           ← In-session List<GameResult>
│   │   ├── GameResult.java            ← Serializable: parallel round lists
│   │   ├── SessionFactory.java        ← Constructs PVP/PVB/BVB with full dependency graph
│   │   └── SessionType.java           ← Enum: PLAYER_VS_PLAYER, PLAYER_VS_BOT, BOT_VS_BOT
│   ├── exception/
│   │   ├── GameErrorCode.java         ← Enum: 13 typed error codes
│   │   ├── GameException.java         ← Base typed exception with errorCode field
│   │   ├── InvalidBotSelectionException.java
│   │   ├── InvalidSessionException.java
│   │   ├── SessionEndException.java   ← Flow control signal
│   │   └── UndoRequestException.java  ← Flow control signal
│   ├── input/
│   │   ├── Input.java                 ← Interface: readLine, readCellChoice, readBoundedInt, etc.
│   │   └── InputHandler.java          ← Scanner + command interception layer
│   ├── player/
│   │   ├── Player.java                ← Domain model: name, wins, password, dates
│   │   ├── PlayerRegistry.java        ← HashMap + TreeSet, implements Registry + RankingView
│   │   ├── PlayerResult.java          ← Wrapper: Player + isNew flag
│   │   ├── RankingView.java           ← Interface: read-only ranking access
│   │   ├── Registry.java              ← Interface: mutable player operations
│   │   └── store/
│   │       ├── FilePlayerStore.java   ← Char-pair encoding, backward-compatible decode
│   │       └── PlayerStore.java       ← Interface: loadAll(), saveAll()
│   ├── renderer/
│   │   ├── classes/
│   │   │   ├── EngineRenderer.java    ← Implements EngineView (8 sub-interfaces)
│   │   │   ├── SessionRenderer.java   ← Implements SessionView
│   │   │   ├── PlayBoardRenderer.java ← Implements PlayBoardView
│   │   │   ├── PlayerTableRenderer.java ← Implements PlayerTableView (dual-mode)
│   │   │   └── HistoryRenderer.java   ← Implements HistoryView
│   │   └── view/
│   │       ← AdminView · AuthView · EndCommandView · EngineView
│   │         ExitCommandView · HistoryView · InputView · ManageCommandView
│   │         PlayBoardView · PlayerTableView · SessionView · SetupView · UndoCommandView
│   ├── replay/
│   │   ├── ReplayEngine.java          ← Match selection → round replay → step-through
│   │   ├── ReplayRenderer.java        ← Compact board display + step annotation
│   │   └── ReplayView.java            ← Interface
│   ├── sessions/
│   │   ├── GameSession.java           ← Interface: play(), toResult(), getSessionType()
│   │   ├── PlayerVSPlayerSession.java ← PVP: auth, undo, round loop, match result
│   │   ├── PlayerVSBotSession.java    ← PVB: bot integration, undo-2 logic
│   │   ├── BotVSBotSession.java       ← BVB: automated, alternating first-mover
│   │   └── SessionContext.java        ← Shared: inSession, inRound, undoEnabled
│   └── utility/
│       ├── Config.java                ← All constants (nested static classes per domain)
│       ├── Logger.java                ← Timestamped file logger: INFO / WARN / ERROR
│       └── Strings.java               ← All multi-line UI strings, centralized
│
├── players.dat                        ← Char-pair encoded persistent player store
└── loggers.log                        ← Timestamped system event log
```

---

## How to Run

**Prerequisites:**
- Java 21 or later (project built and tested on JDK 24)
- IntelliJ IDEA (recommended) or any standard Java IDE
- Terminal width of **at least 90 columns** (board is 83 chars; use full-screen mode)

**From IntelliJ IDEA:**
1. Open project root
2. Mark `src/` as Sources Root
3. Run `MainExecution.main()`

**From command line:**
```bash
# Compile
javac -d out/production/FinalT3 src/**/*.java src/*.java

# Run
java -cp out/production/FinalT3 MainExecution
```

**Built-in commands** (available at any prompt during a session):

| Command | Effect |
|---|---|
| `exit` | Save all player data and exit cleanly |
| `manage` | Open admin panel (default password: `123456`) |
| `end` | Abandon current session — match is still recorded |
| `undo` | Undo last move(s) — only if undo was enabled at session start |

> The admin password is a hardcoded default for demonstration. In a production context, it would be loaded from a secure configuration source.

---

## Extensibility Guide

### Adding a New Bot

1. Create `bot/NewBot.java` implementing the `Bot` interface
2. Add constructors for single and dual instance (`boolean firstInstance`)
3. Add `case N -> new NewBot()` in `BotFactory.createBot(int level)`
4. Add a display row to `Config.BotData.BOT_TABLE`

Nothing else in the system changes. Sessions, commands, and renderers adapt automatically.

### Adding a New Session Type

1. Add value to `SessionType` enum
2. Implement `GameSession` interface in a new session class
3. Add `case` branch in `SessionFactory.createGameSession()`
4. Add a display row to `EngineRenderer.showSessionTypes()`

### Swapping the Renderer

Every renderer implements a view interface. To replace `EngineRenderer` with a Swing, JavaFX, or HTTP adapter:

1. Create a class implementing `EngineView` (which extends 8 sub-interfaces)
2. In `GameEngine.initialize()`, replace `new EngineRenderer(output)` with the new instance

The engine, sessions, and commands depend only on interfaces. No other changes required.

### Adding a New Command

1. Create a class implementing `Command`
2. In `CommandHandler` constructor: `commands.put("mycommand", new MyCommand(...))`
3. The input pipeline begins intercepting `"mycommand"` at every prompt automatically

---

## By the Numbers

| Metric | Value |
|---|---|
| Top-level packages | 13 |
| Total classes | ~50 |
| Interfaces | ~24 |
| View interfaces | 13 |
| Concrete renderers | 5 |
| Bot implementations | 6 |
| Session types | 3 |
| Commands | 4 (exit, manage, end, undo) |
| GameErrorCode values | 13 |
| Auth: max failed attempts | 4 |
| Auth: salt size | 16 bytes (128-bit SecureRandom) |
| Board dimensions | 83 × 32 characters |
| Cell block size | 25 × 9 characters |
| Min moves before win check | 5 |
| Max registered players | 1,000 (configurable) |
| Auto-name pool | PLAYER_1 → PLAYER_1500 |
| FLINT FLUX defect rate | 8% |
| PROBE priority order | `[4, 0, 2, 6, 8, 1, 3, 5, 7]` |
| STALL Draw Achievement Rate | 69.7% overall |

---

## Tech Stack

| Component | Technology |
|---|---|
| Language | Java 24 |
| IDE | IntelliJ IDEA 2025.2 |
| Build | Manual `javac` — no Maven/Gradle (deliberate, for portability) |
| Persistence | Custom char-pair obfuscated flat file |
| Password hashing | SHA-256 via `java.security.MessageDigest` |
| Salt generation | `java.security.SecureRandom` (16 bytes) |
| Player ranking | `java.util.TreeSet` with `Comparable<Player>` |
| Undo stack | `java.util.ArrayDeque` as `Deque<Snapshot>` |
| Player lookup | `java.util.HashMap<String, Player>` |
| Logging | Custom `Logger` with `PrintWriter` + `LocalDateTime` timestamps |
| AI | Minimax + alpha-beta pruning · heuristic rule engines · probabilistic agents |

---

## Interview Talking Points

If you're reviewing this project for a technical role, these are the conversations it supports:

**"Walk me through your architecture."**
The system uses 4-layer separation: infrastructure (file I/O, config), domain (game logic, bots, player model), application (orchestration, session management), and presentation (renderers). Every layer boundary is crossed via interfaces only — no concrete types leak between layers. This means the storage layer, rendering layer, and AI layer can each be replaced independently.

**"How did you handle authentication?"**
SHA-256 with a per-player `SecureRandom` 16-byte salt, stored alongside the hash. Password validation rules are centralized in `PasswordUtil.getValidationError()` and reused by both the user registration flow and the admin panel. Lockout is in-memory per session — persistent lockout would require coordination between file storage and runtime state, which the current design intentionally avoids.

**"How does your undo system work?"**
Memento pattern. `GameBoard.makeMove()` pushes a `Snapshot` record (immutable, Java 16 record) containing a cloned `freq[]` array, a deep-cloned visual board, and `stepCount`. `undo()` pops and restores all three atomically. Undo and the visual render are always in sync because they're captured in the same snapshot. PvP undoes 1 move; PvB undoes 2 (player move + bot response) to restore the board to before the player's last decision.

**"How does the command pipeline work?"**
Every line of user input passes through `InputHandler.readLine()`, which calls `commandProcessor.handle()` before returning to the caller. If a command is recognized, it executes and `null` is returned — the caller's loop sees `null` and re-prompts. If not, the original input is returned. This means commands like `manage` and `undo` work at every prompt in the system without any session or engine code needing to know about them.

**"Tell me about your AI design."**
Six isolated strategy classes, each implementing `Bot`. `BotFactory` is the only class that knows which concrete class corresponds to which difficulty level. The spectrum runs from pure random to minimax with alpha-beta pruning and EquiSelect. FLINT uses a calibrated 8% defect rate to hit an 83% win floor — intentional imperfection as a design parameter, not a bug. STALL uses an inverted minimax where winning scores 0 and drawing scores +1 — a non-competitive equilibrium agent.

**"What would it take to add a GUI or web interface?"**
The entire rendering layer sits behind 13 view interfaces. `GameEngine` holds an `EngineView` reference, sessions hold `SessionView` and `PlayBoardView`. Replacing all five concrete renderers with a Swing, JavaFX, or HTTP adapter requires no changes to the engine, sessions, bots, or any other layer. Constructor injection ensures the swap happens in one place: `GameEngine.initialize()`.

**"What trade-offs did you consciously make?"**
See the [Engineering Trade-offs](#engineering-trade-offs) section. The major ones: exception-as-flow-control for `end`/`undo` commands (cleaner signatures vs. semantic purity), in-memory auth lockout only (simpler than persistent, acceptable within the security boundary), and snapshot-per-move undo (consistency over memory optimization).

---

## Future Scope

Realistic next steps given the current architecture:

**STALL Bot Completion**
Bring STALL's Draw Achievement Rate from 69.7% to ≥95% across all opponents. The inverted minimax foundation is correct — additional endgame pattern recognition and improved fork-threat anticipation should close the gap against RAVE and GREX.

**Persistent Match History**
`GameHistory` is in-session only. A `MatchStore` interface (parallel to `PlayerStore`) would enable cross-session replay without changing any session or engine code.

**GUI or Web Rendering**
All renderers implement view interfaces. A JavaFX renderer or a Spring Boot + WebSocket adapter slots into `GameEngine.initialize()` without touching any other code. The foundation is ready.

**Bot Tournament Mode**
`BotVSBotSession` already supports any bot pairing. A `TournamentSession` running N×N matches with ELO updates would add naturally as a new `SessionType`.

**Limited-Depth Minimax Bot (Level 4.5)**
The strategy gap between FLINT (rule-based) and PROBE (perfect play) is large. A minimax bot with depth limit 4–6 would fit between them architecturally and fill the difficulty curve.

**Formalized Test Suite**
The `testingHelpers/` directory exists. Adding JUnit 5 tests for `UtilBot` board analysis, `PlayerRegistry` sorting correctness, `GameBoard.undo()` state consistency, and bot strategy verification would formalize correctness guarantees without any structural changes.

---

## Author

**Shivam Bhagat**
B.Tech CSE · Java Development · Data Structures & Algorithms

- LinkedIn: [shivam-bhagat-](https://linkedin.com/in/shivam-bhagat-)
- LeetCode: [shivam_bhagat_](https://leetcode.com/shivam_bhagat_)

---

> *"The simplest domain is the hardest test of architecture. If your design holds up under Tic-Tac-Toe, it will hold up under anything."*

---

*Every class, interface, and design decision in this project was deliberate. The domain is minimal by design. The engineering is not.*
