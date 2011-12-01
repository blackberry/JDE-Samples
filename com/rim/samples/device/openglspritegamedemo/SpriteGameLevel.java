/*
 * SpriteGameLevel.java
 *
 * Copyright © 1998-2011 Research In Motion Limited
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Note: For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 */

package com.rim.samples.device.openglspritegamedemo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL11;

import net.rim.device.api.animation.Animation;
import net.rim.device.api.animation.Animator;
import net.rim.device.api.math.BoundingBox;
import net.rim.device.api.math.BoundingSphere;
import net.rim.device.api.math.Bounds;
import net.rim.device.api.math.Matrix4f;
import net.rim.device.api.math.Transform3D;
import net.rim.device.api.math.Vector3f;
import net.rim.device.api.opengles.GL20;
import net.rim.device.api.system.Application;
import net.rim.device.api.xml.parsers.ParserConfigurationException;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Represents the current level in the game. This class is responsible for
 * initializing, rendering, and updating the different game objects such as the
 * enemies, tiles, character, etc. There is also logic for collision detection.
 */
public class SpriteGameLevel {
    /** Represents the margin of error allowed during collision detection */
    private static final float EPSILON = 0.0005f;

    /** Holds the LOSE state of the level */
    public static final int LEVEL_LOSE = -1;

    /** Holds the ACTIVE state of the level */
    public static final int LEVEL_ACTIVE = 0;

    /** Holds the WIN state of the level */
    public static final int LEVEL_WIN = 1;

    /** Holds the level's end object */
    private Sprite _levelGoal = null;

    /** Holds the level's blocks */
    private final Vector _tiles = new Vector();

    /** Holds the level's obstacles */
    private final Vector _enemies = new Vector();

    /** Holds the hero's translation during render */
    private final Vector3f _t = new Vector3f();

    /** Hold's the level's good guy character */
    private SpriteGameCharacter _character;

    /** A flag indicating if the level has been initialized */
    private boolean _isLevelInitialized = false;

    /** A flag indicating if the level has been loaded */
    private boolean _isLevelLoaded = false;

    /** Holds the level width */
    private float _levelWidth = 0;

    /** Holds the level height */
    private float _levelHeight;

    /**
     * Creates a new SpriteGameLevel object
     * 
     * @param levelPath
     *            The path where the level file is located
     * @param animator
     *            The animator that will be used for the level's animations
     */
    public SpriteGameLevel(final String levelPath, final Animator animator) {
        loadLevel(levelPath, animator);
    }

    /**
     * Initializes the game level
     * 
     * @param gl
     *            The OpenGL v1.1 object that will be used to render graphics on
     *            the display
     */
    public void initialize(final GL11 gl) {
        Sprite.Batch.clearBatch(gl);

        // Initialize all the tiles in the level
        int count = _tiles.size();
        Sprite tile;

        for (int i = 0; i < count; i++) {
            tile = (Sprite) _tiles.elementAt(i);
            tile.initialize(gl);
            tile.setIsBatched(gl, true);
        }

        // Initialize all the enemies in the level
        count = _enemies.size();

        for (int i = 0; i < count; i++) {
            ((Sprite) _enemies.elementAt(i)).initialize(gl);
        }

        // Initialize the level goal and the character
        _levelGoal.initialize(gl);
        _character.initialize(gl);
        _isLevelInitialized = true;
    }

    /**
     * Initializes the game level
     * 
     * @param gl
     *            The OpenGL v2.0 object that will be used to render graphics on
     *            the display
     */
    public void initialize(final GL20 gl) {
        Sprite.Batch.clearBatch(gl);

        // Initialize all the tiles in the level
        int count = _tiles.size();
        Sprite tile;

        for (int i = 0; i < count; i++) {
            tile = (Sprite) _tiles.elementAt(i);
            tile.initialize(gl);
            tile.setIsBatched(gl, true);
        }

        // Initialize all the enemies in the level
        count = _enemies.size();

        for (int i = 0; i < count; i++) {
            ((Sprite) _enemies.elementAt(i)).initialize(gl);
        }

        // Initialize the level goal and the character
        _levelGoal.initialize(gl);
        _character.initialize(gl);
        _isLevelInitialized = true;
    }

    /**
     * Updates the level
     * 
     * @return LEVEL_LOSE if the character died; LEVEL_WIN if the character
     *         reached the level goal; LEVEL_ACTIVE if the level is still in
     *         play.
     */
    public int update() {
        // Update the obstacle animations
        _character.update();

        // Calculate the character's movement restrictions and whether
        // the level has been lost or won using collision detection.
        return detectCollisions();
    }

    /**
     * Renders the level using OpenGL v1.1
     * 
     * @param gl
     *            The reference to the OpenGL v1.1 object.
     */
    public void render(final GL11 gl) {
        // Save current matrix.
        gl.glPushMatrix();

        // Load the identity matrix
        gl.glLoadIdentity();

        _character._transform.getTranslation(_t);

        // Determine which horizontal section of the level to render
        if (_t.x > 0.0f) {
            gl.glTranslatef(-_t.x, 0.0f, 0.0f);
        }

        // Determine which vertical section of the level to render
        if (_t.y > 0.0f && _t.y <= _levelHeight) {
            gl.glTranslatef(0.0f, -_t.y, 0.0f);
        } else if (_t.y > _levelHeight) {
            gl.glTranslatef(0.0f, -_levelHeight, 0.0f);
        }

        // Render the level's tiles
        int count = _tiles.size();
        for (int i = 0; i < count; i++) {
            ((Sprite) _tiles.elementAt(i)).render(gl);
        }

        // Render the level's enemies
        count = _enemies.size();
        for (int i = 0; i < count; i++) {
            ((Sprite) _enemies.elementAt(i)).render(gl);
        }

        // Render the level's goal and the character
        _levelGoal.render(gl);
        _character.render(gl);
        gl.glPopMatrix();
    }

    /**
     * Renders the level using OpenGl v2.0
     * 
     * @param gl
     *            The reference to the OpenGL v2.0 object
     */
    public void render(final GL20 gl, final Matrix4f modelview) {
        final Matrix4f m = new Matrix4f(modelview);
        _character._transform.getTranslation(_t);

        // Determine which horizontal section of the level to render
        if (_t.x > 0.0f) {
            m.translate(-_t.x, 0.0f, 0.0f);
        }

        // Determine which vertical section of the level to render
        if (_t.y > 0.0f && _t.y <= _levelHeight) {
            m.translate(0.0f, -_t.y, 0.0f);
        } else if (_t.y > _levelHeight) {
            m.translate(0.0f, -_levelHeight, 0.0f);
        }

        // Render the level's tiles
        int count = _tiles.size();
        for (int i = 0; i < count; i++) {
            ((Sprite) _tiles.elementAt(i)).render(gl, m);
        }

        // Render the level's enemies
        count = _enemies.size();
        for (int i = 0; i < count; i++) {
            ((Sprite) _enemies.elementAt(i)).render(gl, m);
        }

        // Render the level's goal and the character
        _levelGoal.render(gl, m);
        _character.render(gl, m);
    }

    /**
     * Retrieves the current loaded state
     * 
     * @return True if the level is loaded; false if the level isn't loaded
     */
    public boolean isLevelLoaded() {
        return _isLevelLoaded;
    }

    /**
     * Retrieves if the level is initialized or not
     * 
     * @return True if the level is initialized; false if the level isn't
     *         initialized
     */
    public boolean isLevelInitialized() {
        return _isLevelInitialized;
    }

    /**
     * Retrieves the level's character object
     * 
     * @return The level's character object
     */
    public SpriteGameCharacter getCharacter() {
        return _character;
    }

    /**
     * Detects collisions and whether the level has been won or lost
     * 
     * @return 0 if the level is still active, -1 if the level has been lost and
     *         1 if the level has been won.
     */
    private int detectCollisions() {
        Bounds boundsLevel;

        final BoundingBox bounds = (BoundingBox) _character.getBounds();
        final BoundingBox prevBounds = (BoundingBox) _character.getPrevBounds();

        // Get the character's corners
        final Vector3f[] corners = new Vector3f[8];

        for (int i = 0; i < corners.length; i++) {
            corners[i] = new Vector3f();
        }
        bounds.getCorners(corners, 0);

        final float charTop = corners[0].y;
        final float charBottom = corners[1].y;
        final float charLeft = corners[0].x;
        final float charRight = corners[2].x;

        prevBounds.getCorners(corners, 0);
        final float prevCharTop = corners[0].y;
        final float prevCharBottom = corners[1].y;
        final float prevCharLeft = corners[0].x;
        final float prevCharRight = corners[2].x;

        final Vector3f characterCenter =
                new Vector3f((charLeft + charRight) / 2.0f,
                        (charTop + charBottom) / 2.0f, 0.0f);

        // Die if you fell off the level
        if (characterCenter.y < -20.0f) {
            return -1;
        }

        // Detect collision with the level end object
        boundsLevel = _levelGoal.getBounds();
        if (bounds.intersects(boundsLevel)) {
            _character.addMovementRestriction(SpriteGameCharacter.MOVE_DOWN
                    | SpriteGameCharacter.MOVE_UP
                    | SpriteGameCharacter.MOVE_LEFT
                    | SpriteGameCharacter.MOVE_RIGHT);
            return 1;
        }

        // Detect collisions with obstacles
        int count = _enemies.size();

        for (int i = 0; i < count; i++) {
            boundsLevel = ((Sprite) _enemies.elementAt(i)).getBounds();
            if (bounds.intersects(boundsLevel)) {
                _character.addMovementRestriction(SpriteGameCharacter.MOVE_DOWN
                        | SpriteGameCharacter.MOVE_UP
                        | SpriteGameCharacter.MOVE_LEFT
                        | SpriteGameCharacter.MOVE_RIGHT);
                return -1;
            }
        }

        _character.clearMovementRestrictions();

        // Detect collisions with blocks and calculate the character's movement
        // restrictions
        BoundingBox tileBounds;
        count = _tiles.size();
        Sprite tile;

        for (int i = 0; i < count; i++) {
            tile = (Sprite) _tiles.elementAt(i);
            tileBounds = (BoundingBox) tile.getBounds();
            if (bounds.intersects(tileBounds)) {
                // Calculate the center point of the block's bounding box
                final Vector3f min = new Vector3f();
                final Vector3f max = new Vector3f();
                tileBounds.getMin(min);
                tileBounds.getMax(max);
                final Vector3f blockCenter = new Vector3f(min);
                blockCenter.add(max);
                blockCenter.scale(0.5f);

                // Get character scale
                final Vector3f s = new Vector3f();
                _character._transform.getScale(s);

                // Determine which way(s) the character's
                // movement should be restricted. We also set the character's
                // translation vector
                // so that it does not visibly intersect the blocks.
                final float tileTop = max.y;
                final float tileBottom = min.y;
                final float tileLeft = min.x;
                final float tileRight = max.x;

                final float tileMidX = (tileLeft + tileRight) / 2.0f;
                final float tileMidY = (tileBottom + tileTop) / 2.0f;

                final Vector3f v = new Vector3f();

                // If character's top or bottom is between tile's
                // top and bottom, restrict horizontally
                if (charTop < tileTop - 0.1f && charTop > tileBottom + 0.1f
                        || charBottom > tileBottom + 0.1f
                        && charBottom < tileTop - 0.1f) {
                    // If left edge of character is against right
                    // edge of block, cannot move left.
                    if (prevCharLeft > tileRight - 0.1f && charLeft < tileRight
                            && charLeft > tileMidX) {
                        _character._transform.getTranslation(v);
                        v.x = tileRight + s.x - EPSILON;
                        _character._transform.setTranslation(v);
                        _character
                                .addMovementRestriction(SpriteGameCharacter.MOVE_LEFT);
                    }

                    // Right edge of character is against left
                    // edge of block -> cannot move right.
                    if (prevCharRight < tileLeft + 0.1f && charRight > tileLeft
                            && charRight < tileMidX) {
                        _character._transform.getTranslation(v);
                        v.x = tileLeft - s.x + EPSILON;
                        _character._transform.setTranslation(v);
                        _character
                                .addMovementRestriction(SpriteGameCharacter.MOVE_RIGHT);
                    }
                }

                // If character's left or right edge is between tile's left
                // and right edges, restrict vertically.
                if (charLeft > tileLeft + 0.1f && charLeft < tileRight - 0.1f
                        || charRight < tileRight - 0.1f
                        && charRight > tileLeft + 0.1f) {
                    // Bottom edge of character is against top
                    // edge of block -> cannot move down.
                    if (prevCharBottom > tileTop - 0.1f && charBottom < tileTop
                            && charBottom > tileMidY) {
                        _character._transform.getTranslation(v);
                        v.y = tileTop + s.y - EPSILON;
                        _character._transform.setTranslation(v);
                        _character.setTy(v.y);
                        _character
                                .addMovementRestriction(SpriteGameCharacter.MOVE_DOWN);
                    }

                    // Top edge of character is against bottom
                    // edge of block -> cannot move up.
                    if (prevCharTop < tileBottom + 0.1f && charTop > tileBottom
                            && charTop < tileMidY) {
                        _character._transform.getTranslation(v);
                        v.y = tileBottom - s.y + EPSILON;
                        _character._transform.setTranslation(v);
                        _character
                                .addMovementRestriction(SpriteGameCharacter.MOVE_UP);
                    }
                }
            }
        }

        return 0;
    }

    /**
     * Loads from the given material XML file into the given model's material
     * 
     * @param levelPath
     *            Path to the material file
     * @param animator
     *            The animator that the material can use
     */
    public void loadLevel(final String levelPath, final Animator animator) {
        try {
            _levelWidth = 0.0f;
            _levelHeight = 0.0f;

            _tiles.removeAllElements();
            _enemies.removeAllElements();

            // Set up the SAXParser that will parse the XML file
            final SAXParser parser =
                    SAXParserFactory.newInstance().newSAXParser();
            InputStream stream;
            stream =
                    Application.getApplication().getClass()
                            .getResourceAsStream(levelPath);
            final DefaultHandler dh = new LevelSaxHandler(animator);
            parser.parse(stream, dh);
        } catch (final ParserConfigurationException e) {
            SpriteGame.errorDialog(e.toString());
        } catch (final SAXException e) {
            SpriteGame.errorDialog(e.toString());
        } catch (final IOException e) {
            SpriteGame.errorDialog(e.toString());
        }
    }

    /**
     * This class parses the level file and creates the different level objects
     * (tile, enemy, character, etc) with their specific attributes.
     */
    private class LevelSaxHandler extends DefaultHandler {
        private static final String ANIMATION = "animation";
        private static final String CURVE = "curve";
        private static final String DURATION = "duration";
        private static final String ENEMY = "enemy";
        private static final String FRAMES = "frames";
        private static final String GOAL = "goal";
        private static final String CHARACTER = "character";
        private static final String LEVEL = "level";
        private static final String LINEAR = "linear";
        private static final String POSITION = "position";
        private static final String REPEAT = "repeat";
        private static final String ROTATE = "rotate";
        private static final String SCALE = "scale";
        private static final String TEXTURE = "texture";
        private static final String TILE = "tile";
        private static final String TIMES = "times";
        private static final String TRANSLATE = "translate";
        private static final String TYPE = "type";
        private static final String VALUES = "values";

        private int _enemyCount = 0;

        private final Animator _animator;

        /**
         * Creates a new LevelSaxHandler object
         * 
         * @param animator
         *            The animator that the different level entities can use
         */
        public LevelSaxHandler(final Animator animator) {
            _animator = animator;
        }

        /**
         * @see org.xml.sax.helpers.DefaultHandler#startElement(String, String,
         *      String, Attributes)
         */
        public void startElement(final String uri, final String localName,
                final String qName, final Attributes attributes)
                throws SAXException {
            // Determine what kind of level object we are about to parse
            if (qName.equalsIgnoreCase(TILE)) {
                parseTile(attributes);
            } else if (qName.equalsIgnoreCase(ENEMY)) {
                parseEnemy(attributes);
            } else if (qName.equalsIgnoreCase(CHARACTER)) {
                parseHero(attributes);
            } else if (qName.equalsIgnoreCase(GOAL)) {
                parseLevelGoal(attributes);
            } else if (qName.equalsIgnoreCase(ANIMATION)) {
                parseAnimation(attributes);
            }
        }

        /**
         * @see org.xml.sax.helpers.DefaultHandler#endElement(String, String,
         *      String)
         */
        public void endElement(final String uri, final String localName,
                final String qName) {
            // Check if we made it to the end of the file
            if (qName.equalsIgnoreCase(LEVEL)) {
                _isLevelLoaded = true;
                _levelWidth += 1.0f;
                _levelHeight += 1.0f;
            } else if (qName.equalsIgnoreCase(ENEMY)) {
                // We've gone to the next obstacle
                _enemyCount++;
            }
        }

        /**
         * Parses data from the level load file to create a row of game tiles
         * 
         * @param attributes
         *            The attributes of the tile
         */
        private void parseTile(final Attributes attributes) {
            final float[] pos = new float[2];
            parseString(attributes.getValue(POSITION), ',', pos, 0);
            final String texture = attributes.getValue(TEXTURE);
            final int repeat = Integer.parseInt(attributes.getValue(REPEAT));
            final String scaleStr = attributes.getValue(SCALE);
            float scale = 1.0f;

            // Check the tiles scale
            if (scaleStr != null) {
                scale = Float.parseFloat(scaleStr);
            }

            // Potentially increase the width of the level
            if (pos[0] + repeat > _levelWidth) {
                _levelWidth = pos[0] + repeat;
            }

            // Potentially increase the height of the level
            if (pos[1] > _levelHeight) {
                _levelHeight = pos[1];
            }

            // Add tiles for the amount of times that the tile should be
            // repeated
            for (int i = 0; i < repeat; i++) {
                final Sprite tile =
                        new Sprite(texture, new Vector3f(-11.0f + (pos[0] + i)
                                * 2.0f, -11.0f + pos[1] * 2.0f, 0.0f));
                tile._transform.scale(scale);
                _tiles.addElement(tile);
            }
        }

        /**
         * Parses the data from the level load file to create an enemy
         * 
         * @param attributes
         *            The attributes of the enemy
         */
        private void parseEnemy(final Attributes attributes) {
            final float[] pos = new float[2];
            parseString(attributes.getValue(POSITION), ',', pos, 0);
            final String texture = attributes.getValue(TEXTURE);
            final String scaleStr = attributes.getValue(SCALE);
            float scale = 1.0f;

            // Check the enemy's scale
            if (scaleStr != null) {
                scale = Float.parseFloat(scaleStr);
            }

            // Create the enemy
            Sprite enemy;
            Bounds bounds = null;

            if (texture.equals("obstacle_ball.png")) {
                bounds =
                        new BoundingBox(new Vector3f(-0.9f, -0.9f, 0.0f),
                                new Vector3f(0.9f, 0.9f, 0.0f));
            } else if (texture.equals("obstacle_spikewheel.png")) {
                bounds = new BoundingSphere(new Vector3f(), 0.75f);
            }

            enemy =
                    new Sprite(texture, bounds, new Vector3f(-11.0f + pos[0]
                            * 2.0f, -11.0f + pos[1] * 2.0f, 0.0f));
            enemy._transform.scale(scale);
            _enemies.addElement(enemy);
        }

        /**
         * Parses the data form the level load file to create the character
         * 
         * @param attributes
         *            The attributes of the character
         */
        private void parseHero(final Attributes attributes) {
            final float[] pos = new float[2];
            parseString(attributes.getValue(POSITION), ',', pos, 0);
            final String texture = attributes.getValue(TEXTURE);
            final String scaleStr = attributes.getValue(SCALE);
            float scale = 1.0f;

            // Check the character's scale
            if (scaleStr != null) {
                scale = Float.parseFloat(scaleStr);
            }

            // Create the character
            _character =
                    new SpriteGameCharacter(texture, _animator, new Vector3f(
                            -11.0f + pos[0] * 2.0f, -11.0f + pos[1] * 2.0f,
                            0.0f));
            _character._transform.scale(scale);
        }

        /**
         * Parses the data from the load level file to create the level's goal
         * 
         * @param attributes
         *            The attributes of the level goal
         */
        private void parseLevelGoal(final Attributes attributes) {
            final float[] pos = new float[2];
            parseString(attributes.getValue(POSITION), ',', pos, 0);
            final String texture = attributes.getValue(TEXTURE);
            final String scaleStr = attributes.getValue(SCALE);
            float scale = 1.0f;

            // Check the goal's scale
            if (scaleStr != null) {
                scale = Float.parseFloat(scaleStr);
            }

            // Create the level goal object
            _levelGoal =
                    new Sprite(texture, new Vector3f(-11.0f + pos[0] * 2.0f,
                            -11.0f + pos[1] * 2.0f, 0.0f));
            _levelGoal._transform.scale(scale);
        }

        /**
         * Parses the data from the level load file to create an animation
         * 
         * @param attributes
         *            The attributes of the animation
         */
        private void parseAnimation(final Attributes attributes) {
            final Sprite enemy = (Sprite) _enemies.elementAt(_enemyCount);

            // Get the number of key frames and the key times
            final int keyframes = Integer.parseInt(attributes.getValue(FRAMES));
            final float[] keyTimes = new float[keyframes];
            parseString(attributes.getValue(TIMES), ';', keyTimes, 0);

            // Get the duration of the animation
            final long duration = Long.parseLong(attributes.getValue(DURATION));

            // Default to linear
            int curve = Animation.EASINGCURVE_LINEAR;

            if (attributes.getValue(CURVE).equalsIgnoreCase(LINEAR)) {
                curve = Animation.EASINGCURVE_LINEAR;
            }

            Animation animation = null;

            // Check if the animation is a TRANSLATE
            if (attributes.getValue(TYPE).equalsIgnoreCase(TRANSLATE)) {
                animation =
                        parseTranslationAnimation(enemy, keyframes, keyTimes,
                                duration, curve, attributes);
            }

            // Check if the animation is a ROTATE
            else if (attributes.getValue(TYPE).equalsIgnoreCase(ROTATE)) {
                animation =
                        parseRotationAnimation(enemy, keyframes, keyTimes,
                                duration, curve, attributes);
            }

            // Check if the animation is a SCALE
            else if (attributes.getValue(TYPE).equalsIgnoreCase(SCALE)) {
                animation =
                        parseScaleAnimation(enemy, keyframes, keyTimes,
                                duration, curve, attributes);
            }

            // Add the animation to the enemy
            if (animation != null) {
                enemy.addAnimation(animation);

                final int repeatCount =
                        Integer.parseInt(attributes.getValue(REPEAT));
                if (repeatCount == -1) {
                    animation.setRepeatCount(Animation.REPEAT_COUNT_INDEFINITE);
                } else {
                    animation.setRepeatCount(repeatCount);
                }
            }
        }

        /**
         * Parses a translation animation
         * 
         * @param enemy
         *            Enemy to add the animation to
         * @param keyframes
         *            The animations key frames
         * @param keyTimes
         *            The animations key times
         * @param duration
         *            The duration of the animation
         * @param curve
         *            The curve at which the animation will travel
         * @param attributes
         *            The attributes of the animation
         * @return True if adding the animation was a success; false if adding
         *         the animation failed
         */
        private Animation parseTranslationAnimation(final Sprite enemy,
                final int keyframes, final float[] keyTimes,
                final long duration, final int curve,
                final Attributes attributes) {
            final Vector3f t = new Vector3f();
            enemy._transform.getTranslation(t);

            // Get the key values
            final String[] strs = parseString(attributes.getValue(VALUES), ';');

            // Get key values out as floats
            final float[] values = new float[keyframes * 3];

            for (int i = 0; i < keyframes; i++) {
                // Parse the string
                parseString(strs[i], ',', values, i * 3);

                // Convert the coords
                values[i * 3] = values[i * 3] * 2 + t.x;
                values[i * 3 + 1] = values[i * 3 + 1] * 2 + t.y;
                values[i * 3 + 2] = t.z;
            }

            // Create the animation
            return _animator.addAnimation(enemy._transform,
                    Transform3D.ANIMATION_PROPERTY_TRANSLATE, keyframes,
                    keyTimes, 0, values, 0, curve, duration);

        }

        /**
         * Parses a rotation animation
         * 
         * @param enemy
         *            Enemy to add the animation to
         * @param keyframes
         *            The animations key frames
         * @param keyTimes
         *            The animations key times
         * @param duration
         *            The duration of the animation
         * @param curve
         *            The curve at which the animation will travel
         * @param attributes
         *            The attributes of the animation
         * @return True if adding the animation was a success; false if adding
         *         the animation failed
         */
        private Animation parseRotationAnimation(final Sprite enemy,
                final int keyframes, final float[] keyTimes,
                final long duration, final int curve,
                final Attributes attributes) {
            final float[] values = new float[keyframes];

            // Get the key values
            parseString(attributes.getValue(VALUES), ';', values, 0);

            final int size = keyframes * 4;

            // Get the key values out as floats
            final float[] keyValues = new float[size];
            int j = 0;

            // Convert the degrees into radians
            for (int i = 0; i < size; i++) {
                if (i % 4 == 3) {
                    keyValues[i] = (float) (values[j++] * 180 / Math.PI);
                } else if (i % 4 == 2) {
                    keyValues[i] = 1.0f;
                } else {
                    keyValues[i] = 0.0f;
                }
            }

            // Create the animation
            return _animator.addAnimation(enemy._transform,
                    Transform3D.ANIMATION_PROPERTY_ROTATE, keyframes, keyTimes,
                    0, keyValues, 0, curve, duration);
        }

        /**
         * Parses a scale animation
         * 
         * @param enemy
         *            Enemy to add the animation to
         * @param keyframes
         *            The animations key frames
         * @param keyTimes
         *            The animations key times
         * @param duration
         *            The duration of the animation
         * @param curve
         *            The curve at which the animation will travel
         * @param attributes
         *            The attributes of the animation
         * @return True if adding the animation was a success; false if adding
         *         the animation failed
         */
        private Animation parseScaleAnimation(final Sprite enemy,
                final int keyframes, final float[] keyTimes,
                final long duration, final int curve,
                final Attributes attributes) {
            final float[] values = new float[keyframes];

            // Get the key values
            parseString(attributes.getValue(VALUES), ';', values, 0);

            final int size = keyframes * 3;

            // Get the key values out as floats
            final float[] keyvalues = new float[size];
            int j = 0;

            for (int i = 0; i < size; i++) {
                if (i % 3 == 2) {
                    keyvalues[i] = 1.0f;
                    j++;
                } else {
                    keyvalues[i] = values[j];
                }
            }

            return _animator.addAnimation(enemy._transform,
                    Transform3D.ANIMATION_PROPERTY_SCALE, keyframes, keyTimes,
                    0, keyvalues, 0, curve, duration);
        }

        /**
         * Parses a string
         * 
         * @param src
         *            The source string
         * @param delim
         *            The string delimiter
         * @return The string tokens
         */
        private String[] parseString(final String src, final char delim) {
            return explode(src, delim);
        }

        /**
         * Parses a string
         * 
         * @param src
         *            The source string
         * @param delim
         *            The string delimiter
         * @param dst
         *            The destination string
         * @param index
         *            The position in the destination string to start butting
         *            the string tokens
         */
        private void parseString(final String src, final char delim,
                final float[] dst, final int index) {
            final String[] tokens = explode(src, delim);
            final int count = tokens.length;

            for (int i = 0; i < count; i++) {
                dst[i + index] = Float.parseFloat(tokens[i]);
            }
        }
    }

    /**
     * Breaks the source string up into the different .tokens based on the
     * delimiter
     * 
     * @param src
     *            The source string
     * @param delim
     *            The delimiter to use
     * @return A string array containing the string tokens
     */
    private static String[] explode(final String src, final char delim) {
        // Determine the number of tokens
        int count = 1;
        for (int i = 0, length = src.length(); i < length; i++) {
            if (src.charAt(i) == delim) {
                ++count;
            }
        }
        final String[] array = new String[count];

        // Fill the string array with the tokens that
        // are separated by the delimiter.
        for (int arrayIndex = 0, strIndex = 0; arrayIndex < array.length
                && strIndex < src.length(); arrayIndex++) {
            final int nextDelimIndex = src.indexOf(delim, strIndex);

            if (nextDelimIndex < 0) {
                array[arrayIndex] = src.substring(strIndex);
            } else {
                array[arrayIndex] = src.substring(strIndex, nextDelimIndex);
                strIndex = nextDelimIndex + 1;
            }
        }
        return array;
    }
}
