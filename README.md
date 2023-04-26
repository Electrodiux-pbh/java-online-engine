# Java Online Engine

This a prototipe for a Game Engine, this project contains the Server / Client arquitecture to make it easy to send data through.
The project also contains a small 3D physics engine to simulate the world and a 3D OpenGL renderer using LWJGL to render the world.
As a fun feature I temporaly added the posibility to automatically apply the Minecraft skin associated to your input username.

# Libraries Required:

You can use the libraly jars that I left in the `/libraries` folder or download them from the official download pages

## List of Libraries

- LWJGL: https://www.lwjgl.org
- JOML: (You can download next to LWJGL)

# How to execute?

There is a `com.electrodiux.main` packet where you can find 3 different Main classes, the three clases execute different things:

- `Main`: This class executes the `MainServer` class if receives "server" as a parameter if it dosen't it will execute `MainClient`
- `MainServer`: This class starts the server
- `MainClient`: This class starts the client, before starting it shows you a bunch of options like if we are going to connect to an external server or we will play in local

