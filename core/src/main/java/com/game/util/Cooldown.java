package com.game.util;
public class Cooldown {
    private float left = 0, cd;
    public Cooldown(float seconds){ cd = seconds; }
    public void tick(float dt){ left = Math.max(0, left - dt); }
    public boolean fire(){ if (left <= 0){ left = cd; return true; } return false; }
    public float left(){ return left; }
}
