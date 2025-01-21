package de.tum.cit.fop.maze.PC_NPC_OBJ;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.fop.maze.DESIGN.AnimationMNGR;

public class HitParticle {
    private AnimationMNGR animationMNGR;
    private float time = 0f;
    private TextureRegion currentFrame;
    private float x, y;
    private boolean appear = false;
    public HitParticle(float x, float y) {
        this.animationMNGR = new AnimationMNGR();
        this.x = x;
        this.y = y;

    }

    public void update(float delta, Player player, boolean appear) {
        time += delta;
        this.currentFrame = animationMNGR.getHitAnimation1().getKeyFrame(time, true);
        x = player.getBounds().x;;
        y = player.getBounds().y;
        this.appear = appear;
    }
    public void render(SpriteBatch batch) {
        if (!appear) return;
        batch.draw(currentFrame, x, y, 16, 16);
    }


}
