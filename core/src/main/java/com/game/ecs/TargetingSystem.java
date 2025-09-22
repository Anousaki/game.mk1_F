package com.game.ecs;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.game.world.*;

import java.util.List;

public class TargetingSystem {
    public Entity selected;

    public void update(InputSystem input, OrthographicCamera cam, List<Entity> candidates) {
        if (!input.leftClick) return;
        input.leftClick = false;

        // 屏幕坐标 -> 世界坐标
        Vector3 w = cam.unproject(new Vector3(input.mouseX, input.mouseY, 0));
        float best = Float.MAX_VALUE; Entity bestE = null;
        for (var e : candidates) {
            if (e.hp <= 0) continue;
            float d2 = e.pos.dst2(w.x, w.y);
            if (d2 < best && d2 <= (e.radius*e.radius*12)) { // 选中阈值
                best = d2; bestE = e;
            }
        }
        selected = bestE;
    }
}
