package com.game.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import com.game.ecs.InputSystem;
import com.game.world.Player;

public class PlayScreen implements Screen {
    private final Game game;

    // === 相机：世界坐标按“格”为单位（1 格 ≈ 1 世界单位） ===
    private final OrthographicCamera cam = new OrthographicCamera(20, 11.25f);
    // 背景相机（新增，用于视差背景）
    private final OrthographicCamera bgCam = new OrthographicCamera(20, 11.25f);

    // 视差系数（小于 1，移动得更慢）
    private float parallaxX = 0.3f;
    private float parallaxY = 0.3f;
    // 画玩家/HUD
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font = new BitmapFont();

    private final InputSystem input = new InputSystem();
    private final Player player = new Player(4, 4);

    // === 地图相关 ===
    private static final float TILE_PX = 32f;         // 你的 tiles 大概率是 32 像素
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMapTileLayer walls;                  // 碰撞层（Tiled 中的层名要叫 "Walls"）
    private int bgIdx = -1;                         //背景
    private float acc;
    private int[] mainIdx = null;

    public PlayScreen(Game game) {
        this.game = game;
        cam.position.set(10, 6, 0);
        cam.update();
        Gdx.input.setInputProcessor(input);

        // 加载地图（确保 spawnmap.tmx 在 core/assets/maps/ 下）
        map = new TmxMapLoader().load("maps/spawnmap.tmx");
        // unitScale = 1/32：把像素换成“世界单位”，这样 1格=1.0
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1f / TILE_PX);

        // 取到名为 "Walls" 的图层（如果你用别的名字，这里改成你的）
        walls = (TiledMapTileLayer) map.getLayers().get("Walls");
        //背景
        bgIdx = map.getLayers().getIndex("0");  // 层名 "0" 作为背景
        if (bgIdx < 0) {
            Gdx.app.log("PARALLAX", "Layer '0' not found");
        } else {
            // 预构建：除去 bgIdx 的所有层
            int n = map.getLayers().size();
            mainIdx = new int[n - 1];
            for (int i = 0, k = 0; i < n; i++) {
                if (i == bgIdx) continue;
                mainIdx[k++] = i;
            }
        }
    }

    @Override public void render(float delta) {
        // 固定步更新
        acc += delta;
        while (acc >= 1/60f) {
            step(1/60f);
            acc -= 1/60f;
        }

        ScreenUtils.clear(0.12f, 0.13f, 0.16f, 1f);

        // === 先渲染地图 ===
        cam.update();
        // 先画背景层（视差相机）
        // 先画背景层（视差相机）
        if (bgIdx >= 0) {
            bgCam.position.set(cam.position.x * parallaxX, cam.position.y * parallaxY, 0);
            bgCam.update();
            mapRenderer.setView(bgCam);
            mapRenderer.render(new int[]{ bgIdx });
        }

        // 再画主场景（排除背景层）
        mapRenderer.setView(cam);
        if (mainIdx != null) {
            mapRenderer.render(mainIdx); // ← 仅渲染非背景层
        } else {
            mapRenderer.render();        // 如果没找到背景层，就全部渲染
        }

        // === 再画玩家（圆形占位） ===
        shapes.setProjectionMatrix(cam.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        // 阴影
        shapes.setColor(0,0,0,0.25f);
        shapes.ellipse(player.pos.x-0.35f, player.pos.y-0.15f, 0.7f, 0.3f, 16);
        // 本体
        shapes.setColor(0.35f,0.65f,1f,1f);
        shapes.circle(player.pos.x, player.pos.y, 0.35f, 20);
        shapes.end();

        // HUD
        batch.begin();
        font.draw(batch, "[WASD] 移动, [Shift] 冲刺, [Space] 普攻, [Q] 治疗", 10, Gdx.graphics.getHeight() - 10);
        batch.end();
    }

    private void step(float dt) {
        input.pollHeld();

        float speed = player.moveSpeed * (input.sprint ? 1.6f : 1f);
        float nx = player.pos.x + input.move.x * speed * dt;
        float ny = player.pos.y + input.move.y * speed * dt;

        float r = 0.3f;  // 玩家半径（和绘制时的圆半径一致）

        // X 方向碰撞（四个探测点）
        boolean blockX = isBlocked(nx + r, player.pos.y)
                || isBlocked(nx - r, player.pos.y)
                || isBlocked(nx, player.pos.y + r)
                || isBlocked(nx, player.pos.y - r);

        if (!blockX) player.pos.x = nx;

        // Y 方向碰撞
        boolean blockY = isBlocked(player.pos.x, ny + r)
                || isBlocked(player.pos.x, ny - r)
                || isBlocked(player.pos.x + r, ny)
                || isBlocked(player.pos.x - r, ny);

        if (!blockY) player.pos.y = ny;

        cam.position.lerp(new Vector3(player.pos, 0), 0.12f);
        cam.update();
    }

    /** 把世界坐标 (单位=格) 转成 tile 坐标去检查 "Walls" 层是否有 blocked=true 的图块 */
    private boolean isBlocked(float worldX, float worldY) {
        if (walls == null) return false;

        int tx = (int)Math.floor(worldX);
        int ty = (int)Math.floor(worldY);
        if (tx < 0 || ty < 0 || tx >= walls.getWidth() || ty >= walls.getHeight())
            return true; // 越界就当阻挡，避免走出地图

        TiledMapTileLayer.Cell cell = walls.getCell(tx, ty);
        if (cell == null || cell.getTile() == null) return false;

        // 在 Tileset 里给该 Tile 加了 boolean 属性 blocked=true 就算墙
        var props = cell.getTile().getProperties();
        return props.containsKey("blocked") && Boolean.TRUE.equals(props.get("blocked", Boolean.class));
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {
        cam.viewportWidth  = 20f;            // 你原本的世界尺寸
        cam.viewportHeight = 11.25f;
        cam.update();

        bgCam.viewportWidth  = cam.viewportWidth;
        bgCam.viewportHeight = cam.viewportHeight;
        bgCam.update();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        shapes.dispose();
        batch.dispose();
        font.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        if (map != null) map.dispose();
    }
}
