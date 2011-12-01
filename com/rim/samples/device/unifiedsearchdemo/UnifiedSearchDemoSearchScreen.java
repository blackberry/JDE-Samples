/*
 * UnifiedSearchDemoSearchScreen.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
 */

package com.rim.samples.device.unifiedsearchdemo;

import java.util.Vector;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.image.Image;
import net.rim.device.api.unifiedsearch.SearchField;
import net.rim.device.api.unifiedsearch.SearchResponse;
import net.rim.device.api.unifiedsearch.UnifiedSearchServices;
import net.rim.device.api.unifiedsearch.action.UiAction;
import net.rim.device.api.unifiedsearch.entity.SearchableEntity;
import net.rim.device.api.unifiedsearch.searchables.Searchable;
import net.rim.device.api.unifiedsearch.searchables.SearchableContentTypeConstants;

/**
 * A MainScreen with a search field and a result list
 */
public class UnifiedSearchDemoSearchScreen extends MainScreen implements
        ListFieldCallback, FieldChangeListener {
    private final EditField _searchInput;
    private Vector _displayables;
    private final ListField _listField;
    private final GraphicsHelper _graphicsHelper;

    /**
     * Creates a new UnifiedSearchDemoSearchScreen
     */
    public UnifiedSearchDemoSearchScreen() {
        super(NO_VERTICAL_SCROLL);

        _graphicsHelper = new GraphicsHelper();

        setTitle("Search Screen");

        // This manager will facilitate input being directed to the search field
        final SearchScreenVerticalFieldManager scvfm =
                new SearchScreenVerticalFieldManager();

        // This manager will contain the search result list field
        final VerticalFieldManager vfm =
                new VerticalFieldManager(VERTICAL_SCROLL);

        _searchInput = new EditField("Search:", "", 30, TextField.NO_NEWLINE);
        _searchInput.setChangeListener(this);

        _listField = new PointOfInterestListField();
        _listField.setCallback(this);

        // Add fields to respective managers
        vfm.add(_listField);
        scvfm.add(_searchInput);
        scvfm.add(new SeparatorField());
        scvfm.add(vfm);
        add(scvfm);
    }

    /**
     * @see Screen#keyChar(char, int, int)
     */
    protected boolean keyChar(final char c, final int status, final int time) {
        // Open the menu if the enter key is pressed while a list item is
        // selected
        if (c == Characters.ENTER && _listField.getSelectedIndex() >= 0) {
            onMenu(Menu.INSTANCE_DEFAULT);
            return true;
        }

        return super.keyChar(c, status, time);
    }

    /**
     * @see Screen#invokeAction(int)
     */
    protected boolean invokeAction(final int action) {
        if (action != Field.ACTION_INVOKE) {
            return false;
        }

        // Open the menu if a list item is clicked
        onMenu(Menu.INSTANCE_DEFAULT);
        return true;
    }

    /**
     * @see Screen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }

    /**
     * Stops any current search and begins a new search on the given text
     * 
     * @param keyword
     *            Text to search on
     */
    private void search(final String keyword) {
        if (_displayables == null) {
            _displayables = new Vector();
        } else {
            _displayables.removeAllElements();
        }

        SearchResponse results = null;
        final UnifiedSearchServices services =
                UnifiedSearchServices.getInstance();

        // Retrieve all searchables
        final Vector vector =
                services.getDeviceSearchables(SearchableContentTypeConstants.CONTENT_TYPE_DEFAULT_ALL);
        final Searchable[] searchables = new Searchable[vector.size()];
        vector.copyInto(searchables);

        // Get search fields for each searchable
        final SearchField[][] searchFields = new SearchField[vector.size()][0];
        for (int i = 0; i < searchables.length; i++) {
            searchFields[i] = searchables[i].defineSupportedSearchFields();
        }

        // Get SearchResponse for keyword
        try {
            results = services.search(keyword, searchables, searchFields);
        } catch (final Exception e) {
            UnifiedSearchDemo.errorDialog(e.toString());
        }

        if (results != null) {
            // Parse the results
            parseResponse(results);
        }
    }

    /**
     * Parse the search results
     * 
     * @param results
     *            The search results to parse
     */
    private void parseResponse(final SearchResponse results) {
        if (results != null) {
            final Searchable[] dataSources = results.getSearchables();
            if (dataSources != null) {
                SearchField[] dataSourceFields;
                for (int i = 0; i < dataSources.length; i++) {
                    dataSourceFields = results.getSearchFields(dataSources[i]);
                    for (int j = 0; j < dataSourceFields.length; j++) {
                        final Object[] objects =
                                results.getSearchResults(dataSources[i],
                                        dataSourceFields[j]);
                        for (int id = 0; id < objects.length; id++) {
                            final Object obj = objects[id];
                            if (obj instanceof PointOfInterestEntity
                                    && !_displayables.contains(obj)) {
                                _displayables.addElement(obj);
                            }
                        }
                    }
                }

                synchronized (Application.getEventLock()) {
                    // Update result list
                    _listField.setSize(_displayables.size());
                }
            }
        }
    }

    /**
     * @see FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        if (field == _searchInput) {
            final BasicEditField searchField = (BasicEditField) field;
            final String keyword = searchField.getText().trim();

            if (keyword.length() > 0) {
                final Thread worker = new Thread(new Runnable() {
                    /**
                     * @see Runnable.run()
                     */
                    public void run() {
                        try {
                            search(keyword);
                        } catch (final Exception e) {
                            UnifiedSearchDemo.errorDialog(e.toString());
                        }
                    }

                }, "Unified Search Demo: Search thread");

                worker.start();
            } else {
                _listField.setSize(0);
            }
        }
    }

    /**
     * @see ListFieldCallback#drawListRow(ListField, Graphics, int, int, int)
     */
    public void drawListRow(final ListField listField, final Graphics graphics,
            final int index, final int y, final int width) {
        if (_displayables.isEmpty()) {
            return;
        }

        final Object element = _displayables.elementAt(index);

        if (element instanceof SearchableEntity) {
            final SearchableEntity item = (SearchableEntity) element;

            final int rowHeight = listField.getRowHeight();

            // Paint icon for this item/searchable
            Image image = item.getIcon();
            if (image == null) {
                image = item.getSearchable().getIcon();
            }
            if (image != null) {
                // Paint icon with equal height and width
                image.paint(graphics, 3, y - y % rowHeight, rowHeight,
                        rowHeight);
            }

            // Build up display text
            final StringBuffer buffer = new StringBuffer();
            final String title = item.getTitle();
            final String summary = item.getSummary();
            final boolean equal = title.equals(summary);
            if (title != null) {
                buffer.append(title);
                if (summary != null && summary.length() != 0 && !equal) {
                    buffer.append(", ");
                }
            }
            buffer.append(summary == null || equal ? "" : summary);

            // Draw text with 10 pixels padding between icon and text
            final XYRect rect =
                    new XYRect(rowHeight + 10, y, listField.getWidth(),
                            rowHeight);
            _graphicsHelper.drawTextWithHighlight(graphics, buffer.toString(),
                    _searchInput.getText(), rect);
        }
    }

    /**
     * @see ListFieldCallback#get(ListField, int)
     */
    public Object get(final ListField listField, final int i) {
        // Not implemented
        return null;
    }

    /**
     * @see ListFieldCallback#getPrferredWidth(ListField)
     */
    public int getPreferredWidth(final ListField listField) {
        return Integer.MAX_VALUE;
    }

    /**
     * @see ListFieldCallback#indexOfList(ListField, String, int)
     */
    public int indexOfList(final ListField listField, final String prefix,
            final int start) {
        // Not applicable (user input is redirected to the input field)
        return -1;
    }

    /**
     * A VerticalFieldManager that will direct input to the screen's input field
     */
    final class SearchScreenVerticalFieldManager extends VerticalFieldManager {
        /**
         * @see Field#keyChar(char, int, int)
         */
        protected boolean keyChar(final char ch, final int status,
                final int time) {
            _searchInput.setFocus();
            return super.keyChar(ch, status, time);
        }
    }

    /**
     * A ListField with a context menu for the points of interest
     */
    private class PointOfInterestListField extends ListField {
        /**
         * @see ListField#getContextMenu()
         */
        public ContextMenu getContextMenu() {
            final ContextMenu menu = super.getContextMenu();
            menu.clear();
            final int i = getSelectedIndex();

            if (_displayables != null && i < _displayables.size()) {
                final Object element = _displayables.elementAt(i);

                if (element instanceof PointOfInterestEntity) {
                    final PointOfInterestEntity item =
                            (PointOfInterestEntity) element;
                    final UiAction action = item.getUiActions(null, null);

                    if (action != null) {
                        // Add the appropriate UiAction to the context menu
                        menu.addItem(new MenuItem(action.toString(), 0, 0) {
                            public void run() {
                                action.performAction(item);
                            }
                        });
                    }
                }
            }

            return menu;
        }
    }
}
