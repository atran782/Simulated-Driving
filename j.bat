javac -cp .;\LWJGL\lwjgl.jar;\LWJGL\lwjgl-opengl.jar;\LWJGL\lwjgl-glfw.jar %1.java
java -cp .;\LWJGL\lwjgl.jar;\LWJGL\lwjgl-opengl.jar;\LWJGL\lwjgl-glfw.jar -Djava.library.path=\LWJGL %1 %2 %3 %4 %5