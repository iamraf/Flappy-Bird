package com.kappa.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter
{
    private SpriteBatch batch;
    //ShapeRenderer shapeRenderer;

    private BitmapFont font;
    private Random randomGenerator;

    private Circle birdCircle;
    private Rectangle[] topTubeRectangles;
    private Rectangle[] botTubeRectangles;

    private Texture background;
    private Texture main;
    private Texture gameover;
    private Texture topTube;
    private Texture botTube;
    private Texture[] birds;

    private final int TUBES = 4;
    private final float GRAVITY = 1.8f;
    private final float GAP = 500.0f;
    private final float TUBEVELOCITY = 5.0f;
    private final float DISTANCE = 900.0f;

    private int gameState = 0; // 0 Main Screen, 1 In Game, 2 Game Over
    private int flapState = 0;

    private float birdY = 0;
    private float velocity = 0;

    private int score = 0;
    private int scoringTube = 0;
    
    private float maxTubeOffset;

    private float[] tubes = new float[TUBES];
    private float[] tubeOffset = new float[TUBES];

    @Override
    public void create()
    {
        batch = new SpriteBatch();
        //shapeRenderer = new ShapeRenderer();

        background = new Texture("bg.png");
        gameover = new Texture("gameover.png");
        main = new Texture("main.png");

        birdCircle = new Circle();

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(5);

        birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");

        topTube = new Texture("toptube.png");
        botTube = new Texture("bottomtube.png");

        randomGenerator = new Random();
        topTubeRectangles = new Rectangle[TUBES];
        botTubeRectangles = new Rectangle[TUBES];

        maxTubeOffset = Gdx.graphics.getHeight() / 2 - GAP / 2 - 100;

        initializeGame();
    }

    private void initializeGame()
    {
        birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

        for(int i = 0; i < TUBES; i++)
        {
            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - GAP - 200);

            tubes[i] = (Gdx.graphics.getWidth() / 2) - (topTube.getWidth() / 2) + (Gdx.graphics.getWidth() + i * DISTANCE);

            topTubeRectangles[i] = new Rectangle();
            botTubeRectangles[i] = new Rectangle();
        }

        score = 0;
        scoringTube = 0;
        velocity = 0;
    }

    @Override
    public void render()
    {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if(gameState == 0)
        {
            batch.draw(main, (Gdx.graphics.getWidth() / 2) - ((main.getWidth() * 2) / 2), (Gdx.graphics.getHeight() / 2) - ((main.getHeight() * 2) / 2), main.getWidth() * 2, main.getHeight() * 2);

            if(Gdx.input.justTouched())
            {
                gameState = 1;
            }
        }
        else if(gameState == 1)
        {
            if(tubes[scoringTube] < (Gdx.graphics.getWidth() / 2))
            {
                score++;

                if(scoringTube < TUBES - 1)
                {
                    scoringTube++;
                }
                else
                {
                    scoringTube = 0;
                }
            }

            if(Gdx.input.justTouched())
            {
                velocity = -30;

                flapState = 7;
            }

            for(int i = 0; i < TUBES; i++)
            {
                if(tubes[i] < -topTube.getWidth())
                {
                    tubes[i] += TUBES * DISTANCE;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - GAP - 200);
                }
                else
                {
                    tubes[i] = tubes[i] - TUBEVELOCITY;
                }

                batch.draw(topTube, tubes[i], (Gdx.graphics.getHeight() / 2) + (GAP / 2) + tubeOffset[i]);
                batch.draw(botTube, tubes[i], (Gdx.graphics.getHeight() / 2) - (GAP / 2) - botTube.getHeight() + tubeOffset[i]);

                topTubeRectangles[i] = new Rectangle(tubes[i], (Gdx.graphics.getHeight() / 2) + (GAP / 2) + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                botTubeRectangles[i] = new Rectangle(tubes[i], (Gdx.graphics.getHeight() / 2) - (GAP / 2) - botTube.getHeight() + tubeOffset[i], botTube.getWidth(), botTube.getHeight());
            }

            if(birdY > 0 && birdY < Gdx.graphics.getHeight() - birds[0].getHeight())
            {
                velocity = velocity + GRAVITY;
                birdY -= velocity;
            }
            else
            {
                gameState = 2;
            }

            if(flapState > 0)
            {
                flapState--;
            }

            birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[0].getHeight() / 2, birds[0].getWidth() / 2);

            for(int i = 0; i < TUBES; i++)
            {
                if(Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, botTubeRectangles[i]))
                {
                    gameState = 2;
                }
            }

            batch.draw(birds[flapState == 0 ? 0 : 1], (Gdx.graphics.getWidth() / 2) - (birds[flapState == 0 ? 0 : 1].getWidth() / 2), birdY);

            font.draw(batch, String.valueOf(score), 50, Gdx.graphics.getHeight() - 50);
        }
        else if(gameState == 2)
        {
            for(int i = 0; i < TUBES; i++)
            {
                batch.draw(topTube, tubes[i], (Gdx.graphics.getHeight() / 2) + (GAP / 2) + tubeOffset[i]);
                batch.draw(botTube, tubes[i], (Gdx.graphics.getHeight() / 2) - (GAP / 2) - botTube.getHeight() + tubeOffset[i]);
            }

            batch.draw(birds[0], (Gdx.graphics.getWidth() / 2) - (birds[0].getWidth() / 2), birdY);

            font.draw(batch, String.valueOf(score), 50, Gdx.graphics.getHeight() - 50);

            batch.draw(gameover, (Gdx.graphics.getWidth() / 2) - ((gameover.getWidth() * 2) / 2), (Gdx.graphics.getHeight() / 2) - ((gameover.getHeight() * 2) / 2), gameover.getWidth() * 2, gameover.getHeight() * 2);

            if(Gdx.input.justTouched())
            {
                gameState = 0;
                initializeGame();
            }
        }

        batch.end();

        /*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

        for(int i = 0; i < TUBES; i++)
        {
            shapeRenderer.rect(tubes[i], Gdx.graphics.getHeight() / 2 + GAP / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
            shapeRenderer.rect(tubes[i], Gdx.graphics.getHeight() / 2 - GAP / 2 - botTube.getHeight() + tubeOffset[i], botTube.getWidth(), botTube.getHeight());
        }

        shapeRenderer.end();*/
    }

    @Override
    public void dispose()
    {
        batch.dispose();
        background.dispose();
        main.dispose();
        gameover.dispose();
        topTube.dispose();
        botTube.dispose();
        birds[0].dispose();
        birds[1].dispose();
    }
}