# Java Online Engine

This a prototipe for a Game Engine, this project contains the Server / Client arquitecture to make it easy to send data through.
The project also contains a small 3D physics engine to simulate the world and a 3D OpenGL renderer using LWJGL to render the world.
As a fun feature I temporaly added the posibility to automatically apply the Minecraft skin associated to your input username.

# Libraries Required:

You can use the libraly jars that I left in the `/libraries` folder or download them from the official download pages

## List of Libraries

- LWJGL: https://www.lwjgl.org
- JOML: (You can download next to LWJGL)

## Required JDK:

To execute the following code you need to use the `JDK 18` or later

# How to execute?

There is a `com.electrodiux.main` packet where you can find 3 different Main classes, the three clases execute different things:

- `Main`: This class starts is able to execute server and client, it displays a JOptionPane to select the way you want to launch the program:
  - `Local Game`: Starts the game as a local game
  - `Server Connection`: Starts the game and connects to a given server
  - `Server`: Starts a server at a given port
- `MainServer`: This class starts the server

## Execute program as a Jarfile

If you want to execute the program with an external jar you can compile it including all libraries inside. Or dowload/include libraries into a separated folder and execute the jar with the following command:<br>

In this example the libraries folder is `./libraries`, the jarfile is `./OnlineEngine.jar` and the main class we want to execute is `com.electrodiux.main.Main`

```cmd
java -cp "libraries/*;OnlineEngine.jar" com.electrodiux.main.Main
```

### Using the LibrariesInstaller

If you want to automatically install the libraries you can execute the following command `java -cp "OnlineEngine.jar" com.electrodiux.libraries.LibrariesInstaller` in the cmd or with a .bat file
