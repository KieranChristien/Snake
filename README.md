# Snake

A recreation of the classic Snake game, built in Java using Swing.

The goal is simple: control the snake, collect fruit, grow longer, and avoid crashing into yourself or the walls.

## Gameplay

- Use the arrow keys to control the snake.
- Eat fruit to grow your snake.
- Avoid hitting the walls.
- Avoid running into yourself.

## Built With

- Java
- Java Swing / AWT
- IntelliJ IDEA

## Getting Started

### Prerequisites

You will need:

- A Java Development Kit (JDK)
- An IDE such as IntelliJ IDEA or another Java development environment

A reasonably recent JDK is recommended.

### Installation

Clone the repository:

```bash
git clone https://github.com/KieranChristien/Snake.git
```

Open the project in your Java IDE and ensure that the source and resource directories are correctly configured.

### Running

Run the `Main` class located at:

```text
src/main/java/snake/Main.java
```

The game will open in a window and display the start menu.

## Controls

| Key | Action |
|---|---|
| `Up Arrow` | Move Up |
| `Down Arrow` | Move Down |
| `Left Arrow` | Move Left |
| `Right Arrow` | Move Right |
| `Escape` | Pause |
| `Enter` | Restart after Game Over |
| `H` | Toggle Debug Hitboxes |

## How It Works

The game is driven by a game loop that handles input, game updates, collision detection, and rendering.

The main components are:

### GameLoop

Handles the main game loop, keyboard input, game state updates, collision detection, and rendering.

### GameState

Keeps track of the current state of the game, such as the start menu, active gameplay, and game-over state.

### Level

Manages the game level and objects within the level.

### Entities

The entity package contains the objects used within the game, including the snake and fruit.

### UI

Contains the components responsible for displaying menus and other user-interface elements.

## Project Status

This project is a rough recreation of Google Snake and is primarily intended as a personal and learning project.

## License
This project is licensed under the [MIT License](LICENSE).
