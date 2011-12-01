/*
 * ContactList.h
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

#ifndef __CONTACTLIST_H_
#define __CONTACTLIST_H_

using namespace MSXML2;

static char const * const DATAFILE = "ContactList.xml";
static char const * QUERY_STRING = ".//contact[@first=\"%s\" and @last=\"%s\"]"; //search the current context branch
static char const * FIRST_QSTRING = "./first"; //search for the 'first' element in child nodes of current context
static char const * LAST_QSTRING = "./last"; //search for the 'last' element in child nodes of current context
static char const * EMAIL_QSTRING = "./email"; //search for the first element in child nodes of current context

const int FIELDTAG_FIRST_NAME = 1;
const int FIELDTAG_LAST_NAME = 2;
const int FIELDTAG_EMAIL_ADDRESS = 3;

class ContactList
{
public:
	class Contact {
	public:
		Contact() {}
		Contact(char const * const fn, char const * const ln, char const * const email) 
		{
			m_firstName = (LPCTSTR)fn;
			m_lastName = (LPCTSTR)ln;
			m_email = (LPCTSTR)email;
		}
			
	public:
		CString m_firstName;
		CString m_lastName;
		CString m_email;
	public:
		BSTR toXMLNode();
		
		BOOL operator==(const Contact & c1);
	};

public:
	ContactList(char const * const filename);
	
	bool contains(char const * firstname, char const * lastname);
	Contact * get(char const * firstname, char const * lastname);
	Contact** get(int * count); //get the entire list of contacts

	/*
	 * adds or replaces elements. if an element matching the first and last name fields is already present, the email field is updated
	 * using the value contained in the contact instance
	 */
	bool put(Contact * contact);

	/*
	 * save the DOM to the named file
	 */
	bool save(char const * const filename); 
	
private:
	MSXML2::IXMLDOMNodePtr getNode(char const * firstname, char const * lastname);
	Contact * toContact(MSXML2::IXMLDOMNodePtr p);

private:
	MSXML2::IXMLDOMDocument2Ptr m_data;
	MSXML2::IXMLDOMNodePtr m_node; //place holder to allow some optimization
	CString m_homePath;
};

#endif //__CONTACTLIST_H_