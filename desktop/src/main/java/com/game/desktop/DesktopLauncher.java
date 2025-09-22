package com.game.desktop;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.game.GameMain;

public class DesktopLauncher {
    public static void main(String[] args) {
        var cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("Action 2D");
        cfg.setWindowedMode(1280, 720);
        cfg.useVsync(true);
        new Lwjgl3Application(new GameMain(), cfg);
    }
}
