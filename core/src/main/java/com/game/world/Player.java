package com.game.world;
public class Player extends Entity {
    public float moveSpeed = 6f;
    public Player(float x, float y){ super(x,y,Faction.PLAYER); }
}
