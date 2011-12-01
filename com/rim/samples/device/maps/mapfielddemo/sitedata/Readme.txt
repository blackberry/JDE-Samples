The format of 'MapData.txt':
The document must begin with the following tags:

Default Campus:
Number of Campuses:
Campuses of Menu Items:

The 'Default Campus' must be found as one of the site's 'Campus Display Name'

'Campuses of Menu Items' must list all the campuses present in the document in order 
for the program to display all sites. All these campuses will also have their own
menu item. 

Each site must be accompanied by the following tags:

Site Name:
Is Stand-alone Site:
Campus Display Name:
Default Campus Location:
Street Number:
Street Name:
City:
Province:
Code:
Country:
Site Number:
Site Number Placement:
Number of Points:
Shape:
Highlightable Area:

'Is Stand-alone Site' requires a 'true' or 'false'. If false the 'Site Number' will be displayed.

The value for 'Campus Display Name' will appear in the title bar and on a menu item.
E.g.: A 'Campus Display Name' of Slough will produce the title:
'Welcome to Slough' and a menu item reading 'Go To Slough'

'Default Campus Location' must be the same for all sites in the same campus.

'Site Number' and 'Site Number Placement' can be 'N\A' if the campus they reside in does not have 
campus numbers for its buildings, but all sites with more than one building must have values for the two tags.

Note: The whole document will have no space in-between. All the tag values will appear on the line below the tag.
E.g.:

City:
Ottawa
Province:
Ontario
etc...