/**
 * TicTacToeDemo.java
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

package com.rim.samples.device.tictactoedemo;

import net.rim.blackberry.api.blackberrymessenger.BlackBerryMessenger;
import net.rim.blackberry.api.blackberrymessenger.Message;
import net.rim.blackberry.api.blackberrymessenger.MessengerContact;
import net.rim.blackberry.api.blackberrymessenger.Session;
import net.rim.blackberry.api.blackberrymessenger.SessionListener;
import net.rim.blackberry.api.blackberrymessenger.SessionSetupListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;

/**
 * The TicTacToe sample implements a tic tac toe game where the computer plays
 * "O". You can also play a two-player game via BlackBerry Messenger.
 */
public final class TicTacToeDemo extends UiApplication implements
        DialogClosedListener {
    // Members
    // ------------------------------------------------------------------
    private int _difficulty = EASY;
    private final MainScreen _mainScreen;
    private boolean _firstTurnFlag;
    private boolean _middleStartFlag;
    private boolean _gameOverFlag;
    private boolean _twoPlayerFlag;
    private boolean _yourTurnFlag;

    private final SquareField[] _squareFields = { new SquareField(TOP_LEFT),
            new SquareField(TOP_CENTER), new SquareField(TOP_RIGHT),
            new SquareField(LEFT), new SquareField(CENTER),
            new SquareField(RIGHT), new SquareField(BOTTOM_LEFT),
            new SquareField(BOTTOM_CENTER), new SquareField(BOTTOM_RIGHT) };

    private int[][] _winLineOccurrences;
    private int[] _winLineCount;

    // Statics
    // ------------------------------------------------------------------
    private static String[] LEVELS = { "Easy", "Medium", "Hard" };
    private static final Bitmap X_BITMAP = Bitmap.getBitmapResource("x.png");
    private static final Bitmap O_BITMAP = Bitmap.getBitmapResource("o.png");
    private static final Bitmap BLANK_BITMAP;

    private static final int NONE = 0;
    private static final int X = 1;
    private static final int O = 2;
    private static final int QUIT = -1;

    private static final int TOP_LEFT = 0;
    private static final int TOP_CENTER = 1;
    private static final int TOP_RIGHT = 2;
    private static final int LEFT = 3;
    private static final int CENTER = 4;
    private static final int RIGHT = 5;
    private static final int BOTTOM_LEFT = 6;
    private static final int BOTTOM_CENTER = 7;
    private static final int BOTTOM_RIGHT = 8;

    private static final int ONE_X = 1;
    private static final int TWO_X = 2;
    private static final int THREE_X = 3;
    private static final int TWO_O = 8;
    private static final int THREE_O = 12;

    private static final int TIE = 8;

    private static final int EASY = 0;
    private static final int MEDIUM = 1;
    private static final int HARD = 2;

    private static final int[][] winLines = {
            { TOP_LEFT, TOP_CENTER, TOP_RIGHT }, // Horizontal top line
            { LEFT, CENTER, RIGHT }, // Horizontal center line
            { BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT }, // Horizontal bottom
                                                          // line
            { TOP_LEFT, LEFT, BOTTOM_LEFT }, // Vertical left line
            { TOP_CENTER, CENTER, BOTTOM_CENTER }, // Vertical center line
            { TOP_RIGHT, RIGHT, BOTTOM_RIGHT }, // Vertical right line
            { TOP_LEFT, CENTER, BOTTOM_RIGHT }, // Diagonal line
            { TOP_RIGHT, CENTER, BOTTOM_LEFT } // Diagonal line
            };

    private static Session _twoPlayerSession; // Used for communication with a
                                              // BlackBerry Messenger Contact

    private final Dialog _twoPlayerRequestDialog = new Dialog(
            "Requesting two player game...", new Object[] { "Cancel" },
            new int[] { Dialog.CANCEL }, 0, null);
    private final StatusField _statusField;
    private static TicTacToeDemo _theGame;
    private int _yourMark;
    private int _theirMark;

    static {
        BLANK_BITMAP = Bitmap.getBitmapResource("blanksquare.png");
    }

    /**
     * Entry point for the application.
     * 
     * @param args
     *            Command line arguments
     */
    public static void main(final String[] args) {
        if (args != null && args.length > 0 && args[0].equals("init")) {
            final ApplicationDescriptor tictactoedemo =
                    new ApplicationDescriptor(ApplicationDescriptor
                            .currentApplicationDescriptor(), new String[] {});
            final BlackBerryMessenger bbm = BlackBerryMessenger.getInstance();

            // Make sure BlackBerry Messenger is installed before attempting to
            // register listeners.
            if (bbm != null) {
                bbm.addSessionRequestListener(TTTRequestListener.getInstance(),
                        tictactoedemo);
                bbm.registerService(TTTService.getInstance(),
                        "Tic Tac Toe Sample", tictactoedemo);
            }
        } else {
            // Create a new instance of the application and make the currently
            // running thread the application's event dispatch thread.
            _theGame = new TicTacToeDemo();
            _theGame.enterEventDispatcher();
        }
    }

    /**
     * The TicTacToe constructor. Creates all the RIM UI components and pushes
     * the application's screen onto the UI stack.
     */
    public TicTacToeDemo() {
        // MainScreen is the basic screen of the RIM UI
        _mainScreen =
                new TicTacToeScreen(Field.USE_ALL_HEIGHT | Field.FIELD_LEFT);

        _mainScreen.setTitle("Tic Tac Toe Demo");

        // Add a blank line.
        _mainScreen.add(new RichTextField(Field.NON_FOCUSABLE));

        // Create three horizontal field managers, one for each row of square
        // fields
        final HorizontalFieldManager hfmRow1 =
                new HorizontalFieldManager(Field.FIELD_HCENTER);
        final HorizontalFieldManager hfmRow2 =
                new HorizontalFieldManager(Field.FIELD_HCENTER);
        final HorizontalFieldManager hfmRow3 =
                new HorizontalFieldManager(Field.FIELD_HCENTER);

        // Add square fields to first row
        hfmRow1.add(_squareFields[TOP_LEFT]);
        hfmRow1.add(_squareFields[TOP_CENTER]);
        hfmRow1.add(_squareFields[TOP_RIGHT]);

        // Add square fields to second row
        hfmRow2.add(_squareFields[LEFT]);
        hfmRow2.add(_squareFields[CENTER]);
        hfmRow2.add(_squareFields[RIGHT]);

        // Add square fields to third row
        hfmRow3.add(_squareFields[BOTTOM_LEFT]);
        hfmRow3.add(_squareFields[BOTTOM_CENTER]);
        hfmRow3.add(_squareFields[BOTTOM_RIGHT]);

        // Add the horizontal field managers to the screen
        _mainScreen.add(hfmRow1);
        _mainScreen.add(hfmRow2);
        _mainScreen.add(hfmRow3);

        // Add a blank line
        _mainScreen.add(new RichTextField(Field.NON_FOCUSABLE));

        // Add a horizontal line
        _mainScreen.add(new SeparatorField());

        // Add a status field to indicate difficulty level, turn status or game
        // status.
        _statusField = new StatusField();
        _mainScreen.add(_statusField);

        // Set the status on the event thread
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                if (!_twoPlayerFlag) {
                    // Update the status field
                    _statusField.setDifficulty(LEVELS[_difficulty]);
                }
            }
        });

        // We've completed construction of the UI objects.
        // Push the MainScreen instance onto the UI stack for rendering.
        pushScreen(_mainScreen);

        // Initialization
        newGame();
    }

    /**
     * Returns a reference to this application.
     * 
     * @return The application instance
     */
    static TicTacToeDemo getGame() {
        return _theGame;
    }

    /**
     * Initialize the variables and clear the board to start a new game (re-uses
     * the main screen).
     */
    private void newGame() {
        _statusField.setDifficulty(LEVELS[_difficulty]);

        for (int i = TOP_LEFT; i <= BOTTOM_RIGHT; ++i) {
            _squareFields[i].setSquare(NONE);
        }

        _firstTurnFlag = true;
        _middleStartFlag = false;
        _gameOverFlag = false;
        _twoPlayerFlag = false;
        _yourMark = X;
        _theirMark = O;

        // Win lines going through each square
        _winLineOccurrences = new int[][] { { 0, 3, 6 }, // Square 0 - top left
                { 0, 4 }, // Square 1 - top center
                { 0, 5, 7 }, // Square 2 - top right
                { 1, 3 }, // Square 3 - left
                { 1, 4, 6, 7 }, // Square 4 - center
                { 1, 5 }, // Square 5 - right
                { 2, 3, 7 }, // Square 6 - bottom left
                { 2, 4 }, // Square 7 - bottom center
                { 2, 5, 6 } // Square 8 - bottom right
                };

        _winLineCount = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
    }

    /**
     * Initializes a two player game with another device.
     * 
     * @param firstTurn
     *            True if the first turn belongs to this device, false if the
     *            first turn belongs to the other device.
     * @param session
     *            A BlackBerry Messenger session
     */
    void newTwoPlayerGame(final boolean firstTurn, final Session session) {
        newGame();

        _yourTurnFlag = firstTurn;
        _twoPlayerFlag = true;

        if (firstTurn) {
            _statusField.yourTurn();
            _yourMark = X;
            _theirMark = O;
        } else {
            _statusField.theirTurn();
            _yourMark = O;
            _theirMark = X;
        }

        _twoPlayerSession = session;
        final ApplicationDescriptor application =
                ApplicationDescriptor.currentApplicationDescriptor();
        _twoPlayerSession.addListener(new TTTSessionListener(), application);
    }

    /**
     * The Tic Tac Toe game's main screen. Adds a "new game" item to its menu,
     * and prevents the save screen from being displayed when the user selects
     * the default close item (there is nothing to save).
     */
    private class TicTacToeScreen extends MainScreen {
        private final MenuItem _newGameItem; // Menu item for starting a new
                                             // game
        private final MenuItem _newTwoPlayerGame; // Menu item for starting a
                                                  // two player game via
                                                  // BlackBerry Messenger
        private final MenuItem _changeDifficultyLevel; // Menu item for changing
                                                       // the difficulty level

        /**
         * Constructs a new main screen instance with provided style and creates
         * the "new game" menu item.
         * 
         * @param style
         *            Style(s) for this new screen.
         */
        private TicTacToeScreen(final long style) {
            super(style);

            _newGameItem = new MenuItem("New Game", 100000, 10) {
                public void run() {
                    TicTacToeDemo.this.newGame(); // This will set
                                                  // _twoPlayerFlag to false in
                                                  // both cases.
                }
            };

            _newTwoPlayerGame =
                    new MenuItem("New Two Player Game", 100000, 10) {
                        public void run() {
                            final BlackBerryMessenger bbm =
                                    BlackBerryMessenger.getInstance();

                            if (bbm != null) {
                                final MessengerContact player =
                                        bbm.chooseContact();

                                // Request two player game only when a contact
                                // has been selected ,
                                // handles case when "* Empty *" (player is
                                // null) is selected
                                if (player != null) {
                                    TicTacToeDemo.this
                                            .requestTwoPlayerGame(player);
                                }
                            } else {
                                Dialog.alert("BlackBerry Messenger not installed. Unable to start two player game.");
                            }
                        }
                    };

            _changeDifficultyLevel =
                    new MenuItem("Change Difficulty Level", 100000, 0) {
                        public void run() {
                            final DifficultyLevelChangeDialog dialog =
                                    new DifficultyLevelChangeDialog(_difficulty);

                            if (dialog.doModal() == Dialog.OK) {
                                final int newLevel = dialog.getLevelIndex();
                                if (newLevel != _difficulty) {
                                    _difficulty = newLevel;
                                    _statusField
                                            .setDifficulty(LEVELS[_difficulty]);
                                    newGame();
                                }
                            }
                        }
                    };
        }

        /**
         * @see Screen#touchEvent(TouchEvent)
         */
        protected boolean touchEvent(final TouchEvent message) {
            final int eventCode = message.getEvent();

            if (eventCode == TouchEvent.DOWN) {
                // Get the screen coordinates of the touch event
                final int touchX = message.getX(1);
                final int touchY = message.getY(1);

                final Manager manager = getMainManager();

                // Get the manager's top and left positions
                final int managerTop = manager.getTop();
                final int managerLeft = manager.getLeft();

                // Loop through the fields contained in the manager
                for (int i = 0; i < manager.getFieldCount(); i++) {
                    final Field field = manager.getField(i);
                    if (field instanceof SquareField) {
                        // Calculate the screen position of the current field
                        final int top = managerTop + field.getTop();
                        final int left = managerLeft + field.getLeft();

                        // If the touch coordinates fall within the current
                        // field's position, set the field's focus and execute a
                        // move
                        if (touchX > left && touchX < left + field.getWidth()
                                && touchY > top
                                && touchY < top + field.getHeight()) {
                            field.setFocus();

                            if (_twoPlayerFlag) {
                                if (_yourTurnFlag) {
                                    youMove();
                                }
                            } else {
                                youMove();
                            }
                        }
                    }
                }
            }
            if (message.isValid()) {
                return super.touchEvent(message);
            } else {
                return true;
            }
        }

        /**
         * Invoked when a key is pressed.
         * 
         * @see net.rim.device.api.ui.Screen#keyChar(char,int,int)
         */
        public boolean
                keyChar(final char key, final int status, final int time) {
            switch (key) {
            // ENTER, SPACE, x, and X can be used to place
            // the X (in addition to trackwheel or trackball click).
            case Characters.ENTER:
            case Characters.SPACE:
            case 'x':
            case 'X':
                if (_twoPlayerFlag) {
                    if (_yourTurnFlag) {
                        youMove();
                    }
                } else {
                    youMove();
                }

                break;

            // Some devices use the CONTROL_<direction> keys to navigate.
            // We want to make sure that these special keys aren't absorbed by
            // the screen.
            case Characters.CONTROL_DOWN:
            case Characters.CONTROL_RIGHT:
            case Characters.CONTROL_UP:
            case Characters.CONTROL_LEFT:
                return false;
            }

            return super.keyChar(key, status, time);
        }

        /**
         * Creates this screen's menu, which consists of the default main screen
         * menu as well as the new game menu items.
         * 
         * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
         */
        protected void makeMenu(final Menu menu, final int instance) {
            super.makeMenu(menu, instance);
            menu.add(_newGameItem);

            // Make sure BlackBerry Messenger is installed before providing
            // option to play a two player game.
            if (BlackBerryMessenger.getInstance() != null) {
                menu.add(_newTwoPlayerGame);
            }

            // Menu item to change difficulty level is available when playing
            // against the coumputer
            if (!_twoPlayerFlag) {
                menu.add(_changeDifficultyLevel);
            }

            if (_gameOverFlag) {
                menu.setDefault(_newGameItem);
            }
        }

        /**
         * Completes the player's move. Returns true if the game is not over,
         * otherwise it returns false.
         * 
         * @see net.rim.device.api.ui.Screen#navigationClick(int,int)
         */
        public boolean navigationClick(final int status, final int time) {
            if (_twoPlayerFlag) {
                if (_yourTurnFlag) {
                    youMove();
                }
            } else {
                youMove();
            }

            // Once the game is done we want to return true to show the menu
            // otherwise keep returning false to allow clicks to play.
            return !_gameOverFlag;
        }

        /**
         * Prevent the save dialog from being displayed.
         * 
         * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
         */
        public boolean onSavePrompt() {
            return true;
        }

        /**
         * @see net.rim.device.api.ui.Screen#close()
         */
        public void close() {
            if (_twoPlayerSession != null && _twoPlayerSession.isOpen()) {
                _twoPlayerSession.close();
            }

            super.close();
        }
    }

    /**
     * This class represents a field which displays the current difficulty
     * level, the turn status in the case of a two player game, or the game
     * status when the game is finished.
     */
    private static final class StatusField extends RichTextField {
        /**
         * Default constructor
         */
        private StatusField() {
            super(Field.NON_FOCUSABLE | Field.FIELD_HCENTER
                    | Field.USE_ALL_WIDTH);
            yourTurn();
        }

        /**
         * Sets the field's text to indicate it is the user's turn.
         */
        private void yourTurn() {
            setText("Your turn...");
        }

        /**
         * Sets the field's text to indicate it is the opponent's turn.
         */
        private void theirTurn() {
            setText("Their Turn...");
        }

        /**
         * Sets the field's text to indicate the AI difficulty.
         */
        private void setDifficulty(final String difficulty) {
            setText("Difficulty: " + difficulty);
        }
    }

    /**
     * A dialog window used to change difficulty level.
     */
    private static class DifficultyLevelChangeDialog extends Dialog {
        private final ObjectChoiceField _levelChooser;

        /**
         * Constructs a dialog to choose difficulty level.
         * 
         * @param index
         *            The index of the current difficulty
         */
        public DifficultyLevelChangeDialog(final int index) {
            super(Dialog.D_OK_CANCEL, "Choose Difficulty", Dialog.OK, null,
                    Dialog.GLOBAL_STATUS);
            _levelChooser =
                    new ObjectChoiceField("Difficulty Level: ", LEVELS, 0);
            _levelChooser.setSelectedIndex(index);
            add(_levelChooser);
        }

        /**
         * Retrieve the level selection.
         * 
         * @return The selected level
         */
        public int getLevelIndex() {
            return _levelChooser.getSelectedIndex();
        }
    }

    /**
     * Squares on the board are represented using this SquareField class. Each
     * square maintains a reference to its position (index) on the board, and
     * exposes a method for setting the square to an "X", "O", or a blank square
     * (for newGame).
     */
    private class SquareField extends BitmapField {
        private final int _squareIndex;
        private int _state = NONE;
        private Bitmap _overlay;

        /**
         * Constructor for SquareField class.
         * 
         * @param squareIndex
         *            Reference to square on the board
         */
        private SquareField(final int squareIndex) {
            super(BLANK_BITMAP);
            _squareIndex = squareIndex;
        }

        /**
         * Returns the game state
         * 
         * @return An integer representing the game state
         */
        private int getGameState() {
            return _state;
        }

        /**
         * Returns the index of the square
         * 
         * @return The square's index
         */
        private int getSquareIndex() {
            return _squareIndex;
        }

        /**
         * Sets a square to a certain value.
         * 
         * @param value
         *            The value to set the square to (NONE, X or O)
         * @return True if a 'X' or an 'O' is placed in the square, false
         *         otherwise
         */
        private boolean setSquare(final int value) {

            if (value == NONE) {
                setBitmap(BLANK_BITMAP);
                _overlay = null;
                _state = value;
                return false;
            }

            if (_state == NONE) {
                final int[] wlo = _winLineOccurrences[_squareIndex];

                // Add 4 to the count in each win line going through this square
                // if O was played. Add 1 if X was played. This way we can
                // determine
                // how many X's and O's are in each win line (i.e. a count of
                // 6 means there's two X's and one O in a line).
                if (value == O) {
                    _overlay = O_BITMAP;

                    for (int i = wlo.length - 1; i >= 0; --i) {
                        _winLineCount[wlo[i]] += 4;
                    }
                } else if (value == X) {
                    _overlay = X_BITMAP;

                    for (int i = wlo.length - 1; i >= 0; --i) {
                        ++_winLineCount[wlo[i]];
                    }
                }

                _winLineOccurrences[_squareIndex] = new int[] {};
                invalidate();
                _state = value;

                return true;
            }
            return false;
        }

        /**
         * @see net.rim.device.api.ui.component.BitmapField#paint(Graphics)
         */
        public void paint(final Graphics g) {
            super.paint(g);

            if (_overlay != null) {
                final Bitmap b = _overlay;

                // Just paint the overlay directly, don't clear the underlying
                // bitmap
                g.drawBitmap(0, 0, b.getWidth(), b.getHeight(), b, 0, 0);
            }
        }

        /**
         * Allow only unplayed squares to be able to receive focus.
         * 
         * @see net.rim.device.api.ui.Field#isFocusable()
         */
        public boolean isFocusable() {
            if (_gameOverFlag) {
                return false;
            } else if (_state == NONE) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * This method is called only in cases where the specified line contains one
     * and only one empty square (this is not explicitly checked).
     * 
     * @param line
     *            One of the 8 possible winning lines
     * @return The index of the the first empty square in a given line
     */
    private int getLastSquareInLine(final int line) {
        for (int i = 0; i < 3; ++i) {
            if (_squareFields[winLines[line][i]].getGameState() == NONE) {
                return winLines[line][i];
            }
        }

        throw new java.lang.IllegalStateException("No empty squares in row "
                + line);
    }

    /**
     * Calculates and plays the computer's (O's) move. First checks if it can
     * win on this turn.
     * <p>
     * If difficulty is "easy" it then just picks the next available square.
     * <p>
     * If difficulty is "medium" it checks if it has to block the user from
     * winning.
     * <p>
     * If difficulty is "hard" it also checks to stop the user from forming a
     * "fork" situation, opening up two possibilities of winning at once. These
     * situations are limited by starting in either the center or the corner,
     * and by further checks on the second turn if the user started in the
     * center.
     */
    private void computerMoves() {
        if (_difficulty == HARD) {
            // First turn
            if (_firstTurnFlag) {
                if (_squareFields[CENTER].getGameState() == NONE) {
                    // Play in the center square
                    _squareFields[CENTER].setSquare(O);
                } else {
                    // X played in the center square. Play in the top left
                    // corner
                    _squareFields[TOP_LEFT].setSquare(O);
                    _middleStartFlag = true;
                }

                _firstTurnFlag = false;
                return;
            }

            if (_middleStartFlag) {
                // Set the flag to false since this check only needs to be done
                // on the second turn
                _middleStartFlag = false;
                if (_squareFields[BOTTOM_RIGHT].getGameState() == X) {
                    // O| |
                    // -+-+-
                    // |X|
                    // -+-+-
                    // | |X
                    // O has to now play in one of the corners to prevent a fork
                    // (this check is done here to simplify the logic in the
                    // fork checking
                    // done below).
                    _squareFields[TOP_RIGHT].setSquare(O);
                    return;
                }
            }
        }

        // Check to see if computer can win this turn
        for (int i = 0; i < 8; ++i) {
            if (_winLineCount[i] == TWO_O) {
                _squareFields[getLastSquareInLine(i)].setSquare(O);
                endGame(O);
                return;
            }
        }

        if (_difficulty >= MEDIUM) {
            // Check to block opponent's possible win
            for (int i = 0; i < 8; ++i) {
                if (_winLineCount[i] == TWO_X) {
                    _squareFields[getLastSquareInLine(i)].setSquare(O);
                    return;
                }
            }

            if (_difficulty == HARD) {
                // Check if opponent can form a fork on next turn (two lines of
                // 2).
                // (Leave this out for medium level difficulty).
                int _squareMakes2 = -1;

                for (int i = TOP_LEFT; i <= BOTTOM_RIGHT; ++i) {
                    int count = 0;
                    final int wlo[] = _winLineOccurrences[i];

                    for (int j = wlo.length - 1; j >= 0; --j) {
                        // This square (i) is blank, since otherwise wlo will
                        // have length zero.
                        // Count the win lines going through this square
                        // containing just one X
                        // (and two blanks).
                        if (_winLineCount[wlo[j]] == ONE_X) {
                            ++count;
                        }
                    }

                    if (count > 1) {
                        // A forking situation exists. (If X moves in square i,
                        // there will be
                        // two possiblities for X to win on its next turn).
                        if (_squareMakes2 == -1) {
                            // First forking square found.
                            _squareMakes2 = i;
                        } else {
                            // This is the second forking square found. Because
                            // of the logic
                            // included above with the middleStartFlag we can
                            // just choose to
                            // go in any of the other available squares on the
                            // board (besides the
                            // two forking square).
                            for (int k = TOP_LEFT; k <= BOTTOM_RIGHT; ++k) {
                                if (k != _squareMakes2 && k != i) {
                                    if (_squareFields[k].getGameState() == NONE) {
                                        _squareFields[k].setSquare(O);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }

                // There was only one forking square. Move in that square to
                // block it.
                if (_squareMakes2 != -1) {
                    _squareFields[_squareMakes2].setSquare(O);
                    return;
                }
            }
        }

        // Just choose the next available square.
        // (For easy level difficulty jump right to here).
        for (int i = TOP_LEFT; i <= BOTTOM_RIGHT; ++i) {
            if (_squareFields[i].getGameState() == NONE) {
                _squareFields[i].setSquare(O);
                return;
            }
        }

        endGame(NONE);
        return;
    }

    /**
     * Sets the game over flag and pops up a dialog indicating the results.
     * Opens up the menu at the end so that the user can choose to play a new
     * game or quit.
     * 
     * @param winner
     *            Integer variable representing the winner
     */
    private void endGame(final int winner) {
        _gameOverFlag = true;

        if (winner == _yourMark) {
            Dialog.alert("You Win!!!");
        } else if (winner == _theirMark) {
            if (_twoPlayerFlag) {
                Dialog.alert("Your Opponent Wins!!!");
            } else {
                Dialog.alert("BlackBerry Wins!!!");
            }
        } else if (winner == QUIT) {
            Dialog.alert("Opponent Quit!");
        } else {
            Dialog.alert("Tie Game!");
        }

        _statusField.setText("Game Over!");

        // Check whether the game was two players and if the session is still
        // open
        if (_twoPlayerFlag && _twoPlayerSession != null
                && _twoPlayerSession.isOpen()) {
            // Prompt to start a new game
            final int cont = Dialog.ask(Dialog.D_YES_NO, "Start new game?");
            if (cont == Dialog.NO) {
                _twoPlayerSession.close();
                return;
            }

            // Check if the session is still open
            if (_twoPlayerSession.isOpen()) {
                // Start a new game using the existing session. Allows for the
                // losing player to make the first move, and if it was a tie
                // game, the player who went second is allowed to make the first
                // move.
                newTwoPlayerGame(_yourTurnFlag, _twoPlayerSession);

                // Check who is supposed to go first, and display the
                // appropriate message in the statusField.
                if (_yourTurnFlag) {
                    _statusField.yourTurn();
                } else {
                    _statusField.theirTurn();
                }

            }
        }
    }

    /**
     * Plays the move selected, and passes control over to the computer
     * (computerMoves) unless the game is over or something is in focus which is
     * not an empty square - in which case the menu is opened up.
     */
    private void youMove() {
        if (!_gameOverFlag) {
            boolean youWin = false;
            final Field leafField = getActiveScreen().getLeafFieldWithFocus();

            if (leafField instanceof SquareField) {
                if (((SquareField) leafField).setSquare(_yourMark)) {
                    int numMoves = 0;

                    for (int i = 0; i < 8; i++) {
                        if (_yourMark == X && _winLineCount[i] == THREE_X
                                || _yourMark == O
                                && _winLineCount[i] == THREE_O) {
                            youWin = true;
                        }

                        if (_squareFields[i].getGameState() != NONE) {
                            numMoves++;
                        }
                    }

                    if (_twoPlayerFlag) {
                        // Send a message representing the move
                        final Message move =
                                new Message("com.rim.tictactoe/move",
                                        new byte[] {}, null,
                                        ((SquareField) leafField)
                                                .getSquareIndex(), null);
                        _twoPlayerSession.send(move);
                        _yourTurnFlag = false;
                        _statusField.theirTurn();

                        if (youWin) {
                            endGame(_yourMark);
                        } else if (numMoves == TIE) {
                            endGame(NONE);
                        }
                    } else {
                        if (youWin) {
                            endGame(_yourMark);
                        } else {
                            computerMoves();
                        }
                    }

                    return;
                }
            }
        }
    }

    /**
     * Handles a two player game request.
     * 
     * @param player
     *            The BlackBerry Messenger contact who will be the opponent
     */
    void requestTwoPlayerGame(final MessengerContact player) {
        _twoPlayerRequestDialog.setDialogClosedListener(this);
        _twoPlayerRequestDialog.show();
        new SetupTwoPlayerGameThread(player).start();
    }

    /**
     * @see net.rim.device.api.ui.component.DialogClosedListener#dialogClosed(Dialog,int)
     */
    public void dialogClosed(final Dialog dialog, final int choice) {
        if (dialog == _twoPlayerRequestDialog && choice == Dialog.CANCEL) {
            // Tell the setup thread that the user on this end has cancelled the
            // request.
            synchronized (_twoPlayerSession) {
                _twoPlayerSession.notify();
            }
        }
    }

    /**
     * Thread to set up a two player game.
     */
    private final class SetupTwoPlayerGameThread extends Thread {
        private final MessengerContact _player;

        /**
         * Constructs a thread to set up the two player game with.
         * 
         * @param player
         *            The other player to set up the game with
         */
        SetupTwoPlayerGameThread(final MessengerContact player) {
            _player = player;
        }

        /**
         * Connects to the other player.
         * 
         * @see java.lang.Runnable#run()
         */
        public void run() {
            _twoPlayerSession = _player.getSession();

            synchronized (_twoPlayerSession) {
                final ApplicationDescriptor application =
                        ApplicationDescriptor.currentApplicationDescriptor();
                _twoPlayerSession
                        .sendRequest(new TTTSetupListener(), application,
                                "http://www.blackberry.com/developers/downloads/jde/index.shtml");

                try {
                    _twoPlayerSession.wait(180000);
                } catch (final InterruptedException ie) {

                }

                Runnable r;

                if (_twoPlayerSession.isOpen()) {
                    r = new Runnable() {
                        public void run() {
                            _twoPlayerRequestDialog.close();
                            Dialog.alert("Two player game accepted!");
                        }
                    };
                } else {
                    r = new Runnable() {
                        public void run() {
                            _twoPlayerRequestDialog.close();
                            Dialog.alert("Two player game declined");
                        }
                    };
                }

                Application.getApplication().invokeLater(r);
            }
        }
    }

    /**
     * Recieves notification of events that occur during the two player session
     * request
     */
    private final class TTTSetupListener implements SessionSetupListener {
        /**
         * Default constructor
         */
        public TTTSetupListener() {
        }

        /**
         * @see SessionSetupListener#sessionRequestAccepted(Session)
         */
        public void sessionRequestAccepted(final Session session) {
            wakeRequestThread(session);
            newTwoPlayerGame(false, session);
        }

        /**
         * @see SessionSetupListener#sessionRequestDelivered(Session)
         */
        public void sessionRequestDelivered(final Session session) {
            // Not implemeneted
        }

        /**
         * @see SessionSetupListener#sessionRequestFailed(Session,int)
         */
        public void
                sessionRequestFailed(final Session session, final int reason) {
            wakeRequestThread(session);
        }

        /**
         * @see SessionSetupListener#sessionRequestRefused(Session)
         */
        public void sessionRequestRefused(final Session session) {
            wakeRequestThread(session);
        }

        /**
         * Wakes up the two player session
         * 
         * @param session
         *            The session to wake up
         */
        private void wakeRequestThread(final Session session) {
            if (session == _twoPlayerSession && _twoPlayerSession != null) {
                synchronized (_twoPlayerSession) {
                    _twoPlayerSession.notify();
                }
            }
        }
    }

    /**
     * Recieves notifications about the two player session
     */
    private final class TTTSessionListener implements SessionListener {

        /**
         * Default constructor
         */
        public TTTSessionListener() {
            // Not implemented
        }

        /**
         * @see SessionListener#messageDelivered(Session, Message)
         */
        public void messageDelivered(final Session session,
                final Message message) {
            // Not implemented
        }

        /**
         * @see SessionListener#messageQueuedForSend(Session, Message)
         */
        public void messageQueuedForSend(final Session session,
                final Message message) {
            // Not implemented
        }

        /**
         * @see SessionListener#messageReceived(Session, Message)
         */
        public void
                messageReceived(final Session session, final Message message) {
            if (session == _twoPlayerSession) {
                final int move = message.getInteger();
                final SquareField marked = _squareFields[move];
                marked.setSquare(_theirMark);
                _yourTurnFlag = true;
                _statusField.yourTurn();
                int numMoves = 0;

                for (int i = 0; i < 8; i++) {
                    if (_theirMark == X && _winLineCount[i] == THREE_X
                            || _theirMark == O && _winLineCount[i] == THREE_O) {
                        endGame(_theirMark);
                    }

                    if (_squareFields[i].getGameState() != NONE) {
                        numMoves++;
                    }
                }

                if (numMoves == TIE) {
                    endGame(NONE);
                }
            }
        }

        /**
         * @see SessionListener#messageSent(Session, Message)
         */
        public void messageSent(final Session session, final Message message) {
            // Not implemented
        }

        /**
         * @see SessionListener#sessionClosed(Session)
         */
        public void sessionClosed(final Session session) {
            if (session == _twoPlayerSession && !_gameOverFlag) {
                endGame(QUIT);
            }
        }
    }
}
