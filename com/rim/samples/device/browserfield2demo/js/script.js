// Default search
var search = 1;

// Hooks to be called by BrowserFieldScreen
function makeContextMenu()
{ 
    var menuItems = [];
    menuItems.push({ label : "Change Search", action : "toggleSearch()", defaultItem : false });
    return menuItems;
}

function toggleSearch() 
{
    if (search == 0) {
        document.getElementById('searchLabel').innerHTML = 'Wikipedia';    
        search = 1;
    }
    else {
        document.getElementById('searchLabel').innerHTML = 'Yahoo!';    
        search = 0;
    }
    bb.toggleSearch();
}

function submitSearch()
{
    bb.submitSearch(search, document.searchForm.searchField.value);
}


