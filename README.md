# VIT
**VIT** is a VALORANT tracker showing the **weapon skins**, **stats**, **rr**, and **latest matches** of players inside your lobby.
It's entirely written in Java and **completely automated**. Well, except some actions of course.

<br><br>

# Features

- Change the weapon skin you wish to see from all players live during the match.
- View player related information by left-clicking on the player.
  - Shows player skins.
  - Shows latest played competitive matches and their outcomes.
- Copy player names instantly. Simply double right-click on the player.
- Open player stats on tracker.gg instantly. Simply middle mouse click on the player.
- View how much RR someone has and lost after their last competitive match.
- View headshot, & win-rate of all players.
- View the current and peak rank of all players.
  - Hovering over the peak rank shows you in which season that player achieved that rank.
<br><br>

# Requirements
Requires at least [**Java 17 or higher**](https://www.oracle.com/de/java/technologies/downloads/#jdk21-windows).
<br>
Simply use the **x64 Installer** version and install it.

<br><br>

# Images

### Live GUI
![LIVE GUI](images/live-gui.png)

### Lobby GUI
![LOBBY GUI](images/lobby-gui.png)

### Player Window

![Player Window: Skins](images/player-window-skins.png)

![Player Window: Matches](images/player-window-matches.png)

<br>

**(!) About these images:**
> These images were created with fake matches and player data.
> <br><br>
> If you wish to check for yourself how it would feel to
> use VIT, then feel free to use those command lines as well, to test
> VIT first, instead of trying it inside a live match directly.
> <br> <br>
> Fake **Live Match** with **10 players**:<br>
> ``java -jar VIT.jar --test=live --num=12``
> <br><br>
> Fake **Lobby** with **5 players**:<br>
> ``java -jar VIT.jar --test=lobby --num=5``