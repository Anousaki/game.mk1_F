package com.game;

import com.badlogic.gdx.Game;
import com.game.screen.PlayScreen;

public class GameMain extends Game {
    @Override
    public void create() {
        setScreen(new PlayScreen(this));
    }
}
