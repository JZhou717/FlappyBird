package com.jakezhou.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture bg;
//	ShapeRenderer shapeRenderer;
	int screen_width, screen_height;

	// Bird state information
	Texture[] birds;
	boolean isFlapping = false;
		// initial position is in the center of the screen (48 is half the height of bird sprite)
	float position;
	float velocity = 0;
	float acceleration = .6f;
	Circle birdCircle;
	Rectangle topRect, bottomRect;

	// List of tubes
	List<Tubes> tubes = new ArrayList<>();

	final int GAME_READY = 0;
	final int GAME_ACTIVE = 1;
	final int GAME_OVER = 2;

	final int TUBE_GAP = 350;

	int gameState = GAME_READY;

	
	@Override
	public void create () {
		birdCircle = new Circle();
		topRect = new Rectangle();
		bottomRect = new Rectangle();
//		shapeRenderer = new ShapeRenderer();

		screen_width = Gdx.graphics.getWidth();
		screen_height = Gdx.graphics.getHeight();

		batch = new SpriteBatch();
		bg = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		position = screen_height / 2 - birds[0].getHeight() / 2;

		tubes.add(new Tubes());

	}


	@Override
	public void render () {

		// Start game on tap
		if(gameState == GAME_READY) {
			if (Gdx.input.justTouched()) {
				gameState = GAME_ACTIVE;
			}

			batch.begin();
			Texture bird = isFlapping ? birds[0] : birds[1];
			batch.draw(bg, 0, 0, screen_width, screen_height);
			batch.draw(bird, screen_width / 2 - bird.getWidth() / 2,
					position);
			isFlapping = !isFlapping;
			batch.end();

		}
		else if(gameState == GAME_ACTIVE) {
			// bird accelerations downwards
			velocity += acceleration;
			position -= velocity;

			// reset velocity on tap
			if (Gdx.input.justTouched()) {
				velocity = -15;
			}

			if(position < 0 || position > screen_height - birds[0].getHeight()) {
				gameState = GAME_OVER;
			}

			batch.begin();
			// reset blank background
			batch.draw(bg, 0, 0, screen_width, screen_height);

			// Alternating flappy bird sprite flapping and not
			Texture bird = isFlapping ? birds[0] : birds[1];
			batch.draw(bird, screen_width / 2 - bird.getWidth() / 2,
					position);
			birdCircle.set(screen_width / 2, position + bird.getHeight() / 2, bird.getWidth() / 2);
			isFlapping = !isFlapping;

			// Moving the tubes
			for(Tubes t : tubes) {
				t.move();
				batch.draw(t.top, t.x_pos, t.y_pos + TUBE_GAP);
				topRect.set(t.x_pos, t.y_pos + TUBE_GAP, t.top.getWidth(), t.top.getHeight());
				batch.draw(t.bottom, t.x_pos, t.y_pos - t.bottom.getHeight());
				bottomRect.set(t.x_pos, t.y_pos - t.bottom.getHeight(), t.bottom.getWidth(),
						t.bottom.getHeight());
			}

			batch.end();

			// shaperender used to test where shapes are to see detection when testing
//			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//			shapeRenderer.setColor(Color.CLEAR);
//			shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
//			shapeRenderer.rect(topRect.x, topRect.y, topRect.width, topRect.height);
//			shapeRenderer.rect(bottomRect.x, bottomRect.y, bottomRect.width, bottomRect.height);

			if(Intersector.overlaps(birdCircle, topRect) || Intersector.overlaps(birdCircle,
					bottomRect)) {
				gameState = GAME_OVER;
			}

//			shapeRenderer.end();

		}

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		bg.dispose();
	}


	private class Tubes {
		Texture top;
		Texture bottom;
		//int x_pos = screen_width - top.getWidth();
		int x_pos;
		// Leave a 200 value gap on top and botttom
		final int y_pos;

		public Tubes() {
			top = new Texture("toptube.png");
			bottom = new Texture("bottomtube.png");
			// starts off screen with slight buffer
			x_pos = screen_width + 100;
			y_pos = new Random().nextInt(screen_height - TUBE_GAP - 300) + 200;
		}

		void move() {
			// remove when off screen
			if(x_pos == top.getWidth() * -1) {
				tubes.remove(this);
			}
			// add in new tubes
			if(x_pos == screen_width - 620) {
				tubes.add(new Tubes());
			}

			// move this
			x_pos -= 6;

		}

	}
}
