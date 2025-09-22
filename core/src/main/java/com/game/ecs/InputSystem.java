package com.game.ecs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

public class InputSystem implements InputProcessor {
    public final Vector2 move = new Vector2();
    public boolean sprint = false;
    public boolean attackPressed = false;
    public boolean healPressed = false;

    public int mouseX, mouseY;
    public boolean leftClick;

    /** 每帧轮询持续按键（WASD/Shift） */
    public void pollHeld() {
        move.set(0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) move.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) move.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) move.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) move.x += 1;
        if (move.len2() > 0) move.nor();
        sprint = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
    }

    @Override public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE) attackPressed = true;
        if (keycode == Input.Keys.Q)      healPressed   = true;
        return false;
    }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }

    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) { mouseX = screenX; mouseY = screenY; leftClick = true; }
        return false;
    }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { mouseX = screenX; mouseY = screenY; return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }

    // ✅ LibGDX 1.12+ 需要实现
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
}
