package com.game.net;
import com.badlogic.gdx.math.Vector2;
import com.game.world.Entity;

import java.util.List;

public interface NetClient {
    // 连接/心跳
    default void connect(String host, int port, String name) {}
    default void disconnect() {}
    default void update(float dt) {}

    // 输入/动作上行（客户端 -> 服务器）
    default void sendMove(Vector2 dir, boolean sprint) {}
    default void sendAttack(Entity target) {}
    default void sendCast(String skillId, Entity target) {}
    default void sendInteract(Entity target) {}

    // 状态下行（服务器 -> 客户端）：实际项目里你会在实现类里更新本地世界快照
    default List<Entity> getWorldSnapshot(){ return List.of(); }
}
