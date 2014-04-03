#!/bin/sh
java -cp build/classes/:lib/lwjgl.jar:lib/lwjgl_util.jar -Djava.library.path=. com.timepath.lwjgl.portals.Main
