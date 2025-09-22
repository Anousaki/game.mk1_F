package com.game.ecs;
import com.game.world.*;
import java.util.List;

public class AiSystem {
    public void update(float dt, Player p, List<Enemy> enemies) {
        for (var e : enemies) {
            if (e.hp <= 0) continue;
            var dir = p.pos.cpy().sub(e.pos);
            float d2 = dir.len2();
            if (d2 < 16f) { dir.nor().scl(e.moveSpeed * dt); e.pos.add(dir); }
        }
    }
}
