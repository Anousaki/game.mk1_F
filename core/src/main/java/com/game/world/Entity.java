package com.game.world;
import com.badlogic.gdx.math.Vector2;

public class Entity {
    public final Vector2 pos = new Vector2();
    public float radius = 0.35f;
    public float maxHp = 100, hp = 100;
    public Faction faction = Faction.NPC;
    public Entity(float x, float y, Faction f){ pos.set(x,y); faction = f; }
}
