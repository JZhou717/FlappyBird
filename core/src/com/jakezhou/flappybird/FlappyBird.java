package com.jakezhou.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture bg;

	// Bird state information
	Texture[] birds;
	boolean isFlapping = false;
		// initial position is in the center of the screen (48 is half the height of bird sprite)
	float position;
	float velocity = 0;
	float acceleration = .6f;

	// List of tubes
	List<Tubes> tubes = new ArrayList<>();

	final int GAME_READY = 0;
	final int GAME_ACTIVE = 1;
	final int GAME_OVER = 2;

	final int TUBE_GAP = 325;

	int gameState = GAME_READY;

	
	@Override
	public void create () {
		batch = new SpriteBatch();
		bg = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		position = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;
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
			batch.draw(bg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			batch.draw(bird, Gdx.graphics.getWidth() / 2 - bird.getWidth() / 2,
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

			if(position < 0 || position > Gdx.graphics.getHeight() - birds[0].getHeight()) {
				gameState = GAME_OVER;
			}

			batch.begin();
			// reset blank background
			batch.draw(bg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

			// Alternating flappy bird sprite flapping and not
			Texture bird = isFlapping ? birds[0] : birds[1];
			batch.draw(bird, Gdx.graphics.getWidth() / 2 - bird.getWidth() / 2,
					position);
			isFlapping = !isFlapping;

			// Moving the tubes
			for(Tubes t : tubes) {
				t.move();
				batch.draw(t.top, t.x_pos, t.y_pos + TUBE_GAP);
				batch.draw(t.bottom, t.x_pos, t.y_pos - t.bottom.getHeight());
			}

			batch.end();

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
		//int x_pos = Gdx.graphics.getWidth() - top.getWidth();
		int x_pos;
		// Leave a 200 value gap on top and botttom
		final int y_pos;

		public Tubes() {
			top = new Texture("toptube.png");
			bottom = new Texture("bottomtube.png");
			// starts off screen with slight buffer
			x_pos = Gdx.graphics.getWidth() + 100;
			y_pos = new Random().nextInt(Gdx.graphics.getHeight() - TUBE_GAP - 300) + 200;
		}

		void move() {
			// remove when off screen
			if(x_pos == top.getWidth() * -1) {
				tubes.remove(this);
			}
			// add in new tubes
			if(x_pos == Gdx.graphics.getWidth() - 620) {
				tubes.add(new Tubes());
			}

			// move this
			x_pos -= 6;

		}

	}
}
