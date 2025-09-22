package com.game.ecs;
import com.badlogic.gdx.math.Vector2;
import com.game.util.Cooldown;
import com.game.world.*;
import com.game.net.NetClient;

import java.util.List;

public class CombatSystem {
    private final Cooldown cdAtk = new Cooldown(0.4f);
    private final Cooldown cdHeal = new Cooldown(3f);

    public void update(float dt, Player p, List<Entity> world, InputSystem in,
                       TargetingSystem tgt, NetClient net)
    {
        cdAtk.tick(dt); cdHeal.tick(dt);

        // 普攻（近身/对目标）
        if (in.attackPressed) {
            in.attackPressed = false;
            if (cdAtk.fire()) {
                Entity target = chooseTargetForAttack(p, tgt.selected, world);
                if (target != null && target.faction == Faction.ENEMY) {
                    target.hp -= 12; // 本地原型：直接结算
                    net.sendAttack(target); // 多人：上行事件
                }
            }
        }

        // Q=治疗：优先对选中友方/自己
        if (in.healPressed) {
            in.healPressed = false;
            if (cdHeal.fire()) {
                Entity target = (tgt.selected!=null && tgt.selected.faction!=Faction.ENEMY)
                        ? tgt.selected : p;
                target.hp = Math.min(target.maxHp, target.hp + 18);
                net.sendCast("HEAL", target);
            }
        }
    }

    private Entity chooseTargetForAttack(Player p, Entity selected, List<Entity> world) {
        if (selected!=null && selected.faction==Faction.ENEMY && selected.pos.dst2(p.pos) < 1.0f) {
            return selected;
        }
        // 没选中则找最近敌人
        Entity best = null; float bestD2 = Float.MAX_VALUE;
        for (var e: world) if (e.faction==Faction.ENEMY && e.hp>0) {
            float d2 = e.pos.dst2(p.pos);
            if (d2 < bestD2 && d2 < 1.0f) { bestD2=d2; best=e; }
        }
        return best;
    }
}
