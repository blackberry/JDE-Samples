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
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;

/**
 * The TicTacToe sample implements a tic tac toe game where the computer plays
 * "O". You can also play a two-player game via BlackBerry Messenger.
 */
class TicTacToeDemo extends UiApplication implements DialogClosedListener {
    // Members
    // ------------------------------------------------------------------
    private int _difficulty;
    private final MainScreen _mainScreen;
    private final DifficultyField _difficultyField = new DifficultyField(
            "Difficulty Level", new String[] { "Easy", "Medium", "Hard" },
            _MEDIUM);
    private final HorizontalFieldManager _hfm;

    private boolean _firstTurnFlag;
    private boolean _middleStartFlag;
    private boolean _gameOverFlag;
    private boolean _twoPlayerFlag;
    private boolean _yourTurnFlag;

    private final SquareField[] _squareFields = { new SquareField(_TOP_LEFT),
            new SquareField(_TOP_CENTER), new SquareField(_TOP_RIGHT),
            new SquareField(_LEFT), new SquareField(_CENTER),
            new SquareField(_RIGHT), new SquareField(_BOTTOM_LEFT),
            new SquareField(_BOTTOM_CENTER), new SquareField(_BOTTOM_RIGHT) };

    private int[][] _winLineOccurrences;
    private int[] _winLineCount;

    // Statics
    // ------------------------------------------------------------------
    private static final Bitmap _xBitmap = Bitmap.getBitmapResource("x.png");
    private static final Bitmap _oBitmap = Bitmap.getBitmapResource("o.png");
    private static final Bitmap _verticalBarBitmap = Bitmap
            .getBitmapResource("vertical_bar.gif");
    private static final Bitmap _horizontalBarBitmap = Bitmap
            .getBitmapResource("horizontal_bar.gif");
    private static final Bitmap _blankBitmap;

    private static final int _NONE = 0;
    private static final int _X = 1;
    private static final int _O = 2;
    private static final int _QUIT = -1;

    private static final int _TOP_LEFT = 0;
    private static final int _TOP_CENTER = 1;
    private static final int _TOP_RIGHT = 2;
    private static final int _LEFT = 3;
    private static final int _CENTER = 4;
    private static final int _RIGHT = 5;
    private static final int _BOTTOM_LEFT = 6;
    private static final int _BOTTOM_CENTER = 7;
    private static final int _BOTTOM_RIGHT = 8;

    private static final int _ONE_X = 1;
    private static final int _TWO_X = 2;
    private static final int _THREE_X = 3;
    private static final int _TWO_O = 8;
    private static final int _THREE_O = 12;

    private static final int _TIE = 8;

    private static final int _MEDIUM = 1;
    private static final int _HARD = 2;

    private static final int[][] winLines = {
            { _TOP_LEFT, _TOP_CENTER, _TOP_RIGHT }, // Horizontal top line
            { _LEFT, _CENTER, _RIGHT }, // Horizontal center line
            { _BOTTOM_LEFT, _BOTTOM_CENTER, _BOTTOM_RIGHT }, // Horizontal
                                                             // bottom line
            { _TOP_LEFT, _LEFT, _BOTTOM_LEFT }, // Vertical left line
            { _TOP_CENTER, _CENTER, _BOTTOM_CENTER }, // Vertical center line
            { _TOP_RIGHT, _RIGHT, _BOTTOM_RIGHT }, // Vertical right line
            { _TOP_LEFT, _CENTER, _BOTTOM_RIGHT }, // Diagonal line
            { _TOP_RIGHT, _CENTER, _BOTTOM_LEFT } // Diagonal line
            };

    private static Session _twoPlayerSession; // Used for communication with a
                                              // BlackBerry Messenger Contact

    private static final Dialog _twoPlayerRequestDialog = new Dialog(
            "Requesting two player game...", new Object[] { "Cancel" },
            new int[] { Dialog.CANCEL }, 0, null);
    private final TurnField _turnField = new TurnField();
    private static TicTacToeDemo _theGame;
    private int _yourMark;
    private int _theirMark;

    static {
        _blankBitmap = Bitmap.getBitmapResource("blanksquare.png");
    }

    /**
     * Entry point for the application
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
            _theGame = new TicTacToeDemo();

            // To make the application enter the event thread and start
            // processing
            // messages, we invoke the enterEventDispatcher method.
            _theGame.enterEventDispatcher();
        }
    }

    /**
     * The TicTacToe constructor. Creates all the RIM UI components and pushes
     * the application's screen onto the UI stack.
     */
    private TicTacToeDemo() {
        // MainScreen is the basic screen of the RIM UI
        _mainScreen =
                new TicTacToeScreen(Field.USE_ALL_HEIGHT | Field.FIELD_LEFT);

        _mainScreen.setTitle("Tic Tac Toe Demo");

        // Initialization
        newGame();

        // Add the difficulty selector (easy, medium, hard)
        _hfm = new HorizontalFieldManager(Field.FIELD_HCENTER);
        _hfm.add(_difficultyField);
        _mainScreen.add(_hfm);

        // Add a horizontal line.
        _mainScreen.add(new SeparatorField());

        // Add a blank line.
        _mainScreen.add(new RichTextField(Field.NON_FOCUSABLE));

        final Bitmap spacerBitmap =
                new Bitmap((Display.getWidth() - 92) / 2, 2);
        final Graphics g = new Graphics(spacerBitmap);
        g.clear();

        // Layout the board using FlowFieldManager since there is no grid
        // layout.
        // The horizontal bar gif has a width equal to the full width of the
        // screen
        // so that the board will actually lay itself out properly.
        final FlowFieldManager ffm = new FlowFieldManager(Field.FIELD_HCENTER);

        ffm.add(new BitmapField(spacerBitmap));
        ffm.add(_squareFields[_TOP_LEFT]);
        ffm.add(new BitmapField(_verticalBarBitmap));
        ffm.add(_squareFields[_TOP_CENTER]);
        ffm.add(new BitmapField(_verticalBarBitmap));
        ffm.add(_squareFields[_TOP_RIGHT]);
        ffm.add(new BitmapField(spacerBitmap));
        ffm.add(new BitmapField(spacerBitmap));
        ffm.add(new BitmapField(_horizontalBarBitmap));
        ffm.add(new BitmapField(spacerBitmap));
        ffm.add(new BitmapField(spacerBitmap));
        ffm.add(_squareFields[_LEFT]);
        ffm.add(new BitmapField(_verticalBarBitmap));
        ffm.add(_squareFields[_CENTER]);
        ffm.add(new BitmapField(_verticalBarBitmap));
        ffm.add(_squareFields[_RIGHT]);
        ffm.add(new BitmapField(spacerBitmap));
        ffm.add(new BitmapField(spacerBitmap));
        ffm.add(new BitmapField(_horizontalBarBitmap));
        ffm.add(new BitmapField(spacerBitmap));
        ffm.add(new BitmapField(spacerBitmap));
        ffm.add(_squareFields[_BOTTOM_LEFT]);
        ffm.add(new BitmapField(_verticalBarBitmap));
        ffm.add(_squareFields[_BOTTOM_CENTER]);
        ffm.add(new BitmapField(_verticalBarBitmap));
        ffm.add(_squareFields[_BOTTOM_RIGHT]);
        ffm.add(new BitmapField(spacerBitmap));

        _mainScreen.add(ffm);

        // Add a blank line
        _mainScreen.add(new RichTextField(Field.NON_FOCUSABLE));

        // Add a horizontal line
        _mainScreen.add(new SeparatorField());

        // Add a field that tells the player whose turn it is
        _mainScreen.add(_turnField);

        // Set the focus to the top left square
        _squareFields[_TOP_LEFT].setFocus();

        // We've completed construction of the UI objects.
        // Push the MainScreen instance onto the UI stack for rendering.
        pushScreen(_mainScreen);
    }

    /**
     * Returns a reference to the application
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
        _turnField.clear(10);

        // If user clicked on "New Game" (against the computer), and the
        // previous game
        // played was against a BlackBerry Messenger contact, then add back the
        // deleted difficulty selector field.
        if (_twoPlayerFlag) {
            _hfm.add(_difficultyField);
        }

        for (int i = _TOP_LEFT; i <= _BOTTOM_RIGHT; ++i) {
            _squareFields[i].setSquare(_NONE);
        }

        _firstTurnFlag = true;
        _middleStartFlag = false;
        _gameOverFlag = false;
        _twoPlayerFlag = false;
        _yourMark = _X;
        _theirMark = _O;

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
     * Initializes a two player game
     * 
     * @param firstTurn
     *            <description>
     * @param session
     *            A BlackBerry Messenger session
     */
    void newTwoPlayerGame(final boolean firstTurn, final Session session) {
        newGame();

        // Difficulty selector not relevant in a two player game
        _hfm.delete(_difficultyField);

        _yourTurnFlag = firstTurn;
        _twoPlayerFlag = true;

        if (firstTurn) {
            _turnField.yourTurn();
            _yourMark = _X;
            _theirMark = _O;
        } else {
            _turnField.theirTurn();
            _yourMark = _O;
            _theirMark = _X;
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
                                             // game.
        private final MenuItem _newTwoPlayerGame; // Menu item for starting a
                                                  // two player game via
                                                  // BlackBerry Messenger.

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
                                        BlackBerryMessenger.getInstance()
                                                .chooseContact();

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
         * @see net.rim.device.api.ui.Screen#makeMenu(Menu,int)
         */
        protected void makeMenu(final Menu menu, final int instance) {
            super.makeMenu(menu, instance);
            menu.add(_newGameItem);
            menu.add(_newTwoPlayerGame);

            // If _difficultyField is highlighted, make "Change Option" item
            // default
            if (_hfm.getFieldWithFocus() == _difficultyField) {
                menu.setDefault(0);
            }
        }

        /**
         * Completes the player's move. Returns true if the game is not over,
         * otherwise it returns false.
         * 
         * @see net.rim.device.api.ui.Screen#navigationClick(int,int)
         */
        public boolean navigationClick(final int status, final int time) {

            // Display _difficultyField choices when field receives focus
            // and is clicked by the trackball or trackwheel.
            if (_hfm.getFieldWithFocus() == _difficultyField) {
                return false;
            }

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
     * Displays the game status
     */
    private static final class TurnField extends RichTextField {
        // Constructor
        private TurnField() {
            super(Field.NON_FOCUSABLE | Field.FIELD_HCENTER
                    | Field.USE_ALL_WIDTH);
            yourTurn();
        }

        /**
         * Sets the field's text to indicate it is the user's turn
         */
        private void yourTurn() {
            setText("Your turn...");
        }

        /**
         * Sets the field's text to indicate it is the opponent's turn
         */
        private void theirTurn() {
            setText("Their Turn...");
        }
    }

    /**
     * DifficultyField - start a new game when the difficulty is changed
     */
    private class DifficultyField extends ObjectChoiceField implements
            FieldChangeListener {
        /**
         * Constructor for DifficultyField class
         * 
         * @param label
         *            Label for this field
         * @param choices
         *            Choices for this field ("Easy" , "Medium" ,"Hard")
         * @param initialIndex
         *            Index of the initially selected value
         */
        private DifficultyField(final String label, final Object[] choices,
                final int initialIndex) {
            super(label, choices, initialIndex);
            setChangeListener(this);
        }

        /**
         * 
         * @see net.rim.device.api.ui.FieldChangeListener#fieldChanged(Field,int)
         */
        public void fieldChanged(final Field field, final int context) {

            // Start a new game only when a choice is selected and then clicked
            // by the trackball.
            if (context == 2) {
                newGame();
            }
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
        private int _state = _NONE;
        private Bitmap _overlay;

        /**
         * Constructor for SquareField class.
         * 
         * @param squareIndex
         *            Reference to square on the board
         */
        private SquareField(final int squareIndex) {
            super(_blankBitmap);
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

        private boolean setSquare(final int value) {

            if (value == _NONE) {
                setBitmap(_blankBitmap);
                _overlay = null;
                _state = value;
                return false;
            }

            if (_state == _NONE) {
                final int[] wlo = _winLineOccurrences[_squareIndex];

                // Add 4 to the count in each win line going through this square
                // if O was played. Add 1 if X was played. This way we can
                // determine
                // how many X's and O's are in each win line (i.e. a count of
                // 6 means there's two X's and one O in a line).
                if (value == _O) {
                    _overlay = _oBitmap;

                    for (int i = wlo.length - 1; i >= 0; --i) {
                        _winLineCount[wlo[i]] += 4;
                    }
                } else if (value == _X) {
                    _overlay = _xBitmap;

                    for (int i = wlo.length - 1; i >= 0; --i) {
                        ++_winLineCount[wlo[i]];
                    }
                }

                _winLineOccurrences[_squareIndex] = new int[] {};

                // setFocus();
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
         * Squares that have been played in cannot receive focus
         * 
         * @see net.rim.device.api.ui.Field#isFocusable()
         */
        public boolean isFocusable() {
            if (_gameOverFlag) {
                return false;
            } else if (_state == _NONE) {
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
     * @return Index of the the first empty square in a given line
     */
    private int getLastSquareInLine(final int line) {
        for (int i = 0; i < 3; ++i) {
            if (_squareFields[winLines[line][i]].getGameState() == _NONE) {
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
        _difficulty = _difficultyField.getSelectedIndex();

        if (_difficulty == _HARD) {
            // First turn
            if (_firstTurnFlag) {
                if (_squareFields[_CENTER].getGameState() == _NONE) {
                    // Play in the center square
                    _squareFields[_CENTER].setSquare(_O);
                } else {
                    // X played in the center square. Play in the top left
                    // corner
                    _squareFields[_TOP_LEFT].setSquare(_O);
                    _middleStartFlag = true;
                }

                _firstTurnFlag = false;
                return;
            }

            if (_middleStartFlag) {
                // Set the flag to false since this check only needs to be done
                // on the second turn
                _middleStartFlag = false;
                if (_squareFields[_BOTTOM_RIGHT].getGameState() == _X) {
                    // O| |
                    // -+-+-
                    // |X|
                    // -+-+-
                    // | |X
                    // O has to now play in one of the corners to prevent a fork
                    // (this check is done here to simplify the logic in the
                    // fork checking
                    // done below).
                    _squareFields[_TOP_RIGHT].setSquare(_O);
                    return;
                }
            }
        }

        // Check to see if computer can win this turn
        for (int i = 0; i < 8; ++i) {
            if (_winLineCount[i] == _TWO_O) {
                _squareFields[getLastSquareInLine(i)].setSquare(_O);
                endGame(_O);
                return;
            }
        }

        if (_difficulty >= _MEDIUM) {
            // Check to block opponent's possible win
            for (int i = 0; i < 8; ++i) {
                if (_winLineCount[i] == _TWO_X) {
                    _squareFields[getLastSquareInLine(i)].setSquare(_O);
                    return;
                }
            }

            if (_difficulty == _HARD) {
                // Check if opponent can form a fork on next turn (two lines of
                // 2).
                // (Leave this out for medium level difficulty).
                int _squareMakes2 = -1;

                for (int i = _TOP_LEFT; i <= _BOTTOM_RIGHT; ++i) {
                    int count = 0;
                    final int wlo[] = _winLineOccurrences[i];

                    for (int j = wlo.length - 1; j >= 0; --j) {
                        // This square (i) is blank, since otherwise wlo will
                        // have length zero.
                        // Count the win lines going through this square
                        // containing just one X
                        // (and two blanks).
                        if (_winLineCount[wlo[j]] == _ONE_X) {
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
                            for (int k = _TOP_LEFT; k <= _BOTTOM_RIGHT; ++k) {
                                if (k != _squareMakes2 && k != i) {
                                    if (_squareFields[k].getGameState() == _NONE) {
                                        _squareFields[k].setSquare(_O);
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
                    _squareFields[_squareMakes2].setSquare(_O);
                    return;
                }
            }
        }

        // Just choose the next available square.
        // (For easy level difficulty jump right to here).
        for (int i = _TOP_LEFT; i <= _BOTTOM_RIGHT; ++i) {
            if (_squareFields[i].getGameState() == _NONE) {
                _squareFields[i].setSquare(_O);
                return;
            }
        }

        endGame(_NONE);
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
        } else if (winner == _QUIT) {
            Dialog.alert("Opponent Quit!");
        } else {
            Dialog.alert("Tie Game!");
        }

        if (_twoPlayerFlag && _yourTurnFlag && _twoPlayerSession != null
                && _twoPlayerSession.isOpen()) {
            _twoPlayerSession.close();
        }

        _turnField.setText("Game Over!");
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
                        if (_yourMark == _X && _winLineCount[i] == _THREE_X
                                || _yourMark == _O
                                && _winLineCount[i] == _THREE_O) {
                            youWin = true;
                        }

                        if (_squareFields[i].getGameState() != _NONE) {
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
                        _turnField.theirTurn();

                        if (youWin) {
                            endGame(_yourMark);
                        } else if (numMoves == _TIE) {
                            endGame(_NONE);
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
     * Handles a two player game request
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
     * Thread to set up a two player game
     */
    private final class SetupTwoPlayerGameThread extends Thread {
        private final MessengerContact _player;

        SetupTwoPlayerGameThread(final MessengerContact player) {
            _player = player;
        }

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

        public TTTSessionListener() {
            // Not implemented
        }

        public void messageDelivered(final Session session,
                final Message message) {
            // Not implemented
        }

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
                _turnField.yourTurn();
                int numMoves = 0;

                for (int i = 0; i < 8; i++) {
                    if (_theirMark == _X && _winLineCount[i] == _THREE_X
                            || _theirMark == _O && _winLineCount[i] == _THREE_O) {
                        endGame(_theirMark);
                    }

                    if (_squareFields[i].getGameState() != _NONE) {
                        numMoves++;
                    }
                }

                if (numMoves == _TIE) {
                    endGame(_NONE);
                }
            }
        }

        public void messageSent(final Session session, final Message message) {
            // Not implemented
        }

        /**
         * @see SessionListener#sessionClosed(Session)
         */
        public void sessionClosed(final Session session) {
            if (session == _twoPlayerSession && !_gameOverFlag) {
                endGame(_QUIT);
            }
        }
    }
}
