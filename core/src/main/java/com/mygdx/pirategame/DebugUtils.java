package com.mygdx.pirategame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

/**
 * Methods to make debugging bugs easier (for example drawing lines to track pathfinding)
 */
public class DebugUtils {

    private static final ShapeRenderer debugRenderer = new ShapeRenderer();

    public static void drawDebugLine(Vector2 start, Vector2 end, int lineWidth, Color color, Matrix4 projectionMatrix) {
        Gdx.gl.glLineWidth(lineWidth);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(color);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    public static void drawDebugLine(Vector2 start, Vector2 end, Matrix4 projectionMatrix) {
        Gdx.gl.glLineWidth(5);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.RED);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    public static void drawDebugDot(Vector2 start, Matrix4 projectionMatrix) {
        Gdx.gl.glLineWidth(5);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        debugRenderer.setColor(Color.GREEN);
        debugRenderer.circle(start.x, start.y, 0.25f);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    // stop an instance being created of this class
    private DebugUtils() {

    }

}
