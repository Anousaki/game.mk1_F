package com.game.world;
public class Enemy extends Entity {
    public float moveSpeed = 4.2f;
    public Enemy(float x, float y){ super(x,y,Faction.ENEMY); maxHp = hp = 80; }
}
