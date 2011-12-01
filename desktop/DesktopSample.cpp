/*
 * DesktopSample.cpp
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

// DesktopSample.cpp : Implementation of CDesktopSample

#include "stdafx.h"
#include "Desktop.h"
#include "DesktopSample.h"

/////////////////////////////////////////////////////////////////////////////
// CDesktopSample

CDesktopSample::CDesktopSample(): 
	m_data(DATAFILE)
{
		
}

/*
 * Implement the info query method - just return a simple string describing us
 */
HRESULT __stdcall CDesktopSample::raw_GetExtensionInfo (
        BSTR * extensionInfo )
{


	::SysReAllocString(extensionInfo, L"<?xml version=\"1.0\" encoding=\"UTF-8\" ?><extensioninfo version=\"1.0.0\">"
							L"<vendorname>Rim</vendorname>"
							L"<vendorversion>1.0</vendorversion>"
							L"<path>C:\\Program Files\\Research In Motion\\BlackBerry SDK 2.5.0\\Desktop</path>"
							L"<description>A Rim Desktop Extension API Sample</description><displayname>Rim Desktop Contacts Sample</displayname>"
							L"<clsid>{7C23B673-FB1D-45D8-83BE-390398BA9876}</clsid>"
							L"<access><database>Contacts</database></access>"
							L"</extensioninfo>"); //the L token unicodes this string
	return 0;
}

/*
 * Act on the data! 
 * In this particular case we act on the sample database data, the database called "Contacts".
 * Just do a simple synchronize operation based on a sample list of contacts, stored locally
 * in a file.
 */
HRESULT __stdcall CDesktopSample::raw_Process (
        IRimUtility * pRimUtility,
        IRimDatabaseAccess * pRimDeviceAccess )
{
	try {
		IRimUtilityPtr utilities = pRimUtility; //wrap in a smar pointer for ease of use
		IRimProgressPtr progress = utilities; //this will throw an exception if the query fails
		IRimDatabaseAccessPtr p = pRimDeviceAccess; //create a smart pointer for ease of use
		IRimTablesPtr tables = p->GetTables();
		
		//get the contacts table
		IRimTablePtr contactTable;
		for(long i=1; i<=tables->GetCount(); i++)
		{
			_bstr_t tableName = tables->GetItem(_variant_t(i))->GetName();
			if(tableName == _bstr_t("Contacts"))
			{
				contactTable = tables->GetItem(_variant_t(i));
			}
		}

		//sync from device to host, values in device replace those in host (just a simple one way sync)
		// unless the list on the device is empty, in which case we reload the list from the host
		//setup a progress dialog
		progress->Notify(RIM_Progress_Show, 0, 0);
		progress->SetProgressDlgText(RIM_ProgressText_Title, _bstr_t("Contacts Sample Progress"));
		progress->SetProgressDlgText(RIM_ProgressText_Msg, _bstr_t("Processing records..."));
		IRimRecordsPtr contacts = contactTable->LoadRecords(RIM_Mode_ReadWrite);
		
		ContactList cl(DATAFILE);
		if ( contacts->Count <= 0 )
		{
			//populate the device with records from the store
			int count = 0;
			ContactList::Contact ** list = cl.get(&count);
			progress->Notify(RIM_Progress_Count, count, 1);
			for (int i = 0; i < count; ++i)
			{
				progress->Notify(RIM_Progress_Pos, i, 0);
				IRimRecordPtr r = contacts->AddRecord();
				if ( r != NULL )
				{
					IRimFieldsPtr fields = r->Getfields();
					if ( fields != NULL )
					{
						AddField(fields, FIELDTAG_FIRST_NAME, list[i]->m_firstName);
						AddField(fields, FIELDTAG_LAST_NAME, list[i]->m_lastName);
						AddField(fields, FIELDTAG_EMAIL_ADDRESS, list[i]->m_email);
					}
					long id = r->Update();
					id;
					
				}
				delete list[i];
				list[i] = 0;
			}
			delete[] list;
		}
		else
		{
			progress->Notify(RIM_Progress_Count, contacts->Count, 1);
			for (long i = 1; i <= contacts->Count; ++i) //the Item methods are 1 based indexed, rather than 0
			{
				IRimRecordPtr r = contacts->Item[i];
				if ( r != NULL )
				{
					progress->Notify(RIM_Progress_Pos, i, 0);
					IRimFieldsPtr fields = r->fields;
				
					//we've unwrapped things here a bit to keep it easy to read
					char * firstname = GetField(fields, FIELDTAG_FIRST_NAME);
					char * lastname = GetField(fields, FIELDTAG_LAST_NAME);
					char * email = GetField(fields, FIELDTAG_EMAIL_ADDRESS);
					ContactList::Contact c(firstname, lastname, email);
					cl.put(&c); //this will implicitly overwrite the data contained, if present (ie, if an element matches the first/last pair)								
					if ( firstname ) delete[] firstname;
					if ( lastname ) delete[] lastname;
					if ( email ) delete[] email;
				}
			}
			cl.save(DATAFILE);
		}
	
		
	} catch (_com_error e) {
		CString error;
		error.Format("Exception in Desktop Sample [%d]:\n%s", e.Error() & 0xFFFF, (char *)e.Description());
		::MessageBox(NULL, (LPCTSTR)error, "Desktop Sample", MB_OK | MB_ICONERROR);
	}
	return 0;
}

char * CDesktopSample::GetField(IRimFieldsPtr fields, int fieldtag)
{
	try {
		IRimFieldPtr f = fields->FindField((char)fieldtag);
		if ( f != NULL )
		{
			_variant_t data = f->value;
			SAFEARRAY * sa = data.parray;
			char * pdata;
			char * retVal = NULL;
			if ( SUCCEEDED( SafeArrayAccessData( sa, (void**)&pdata ) ) )
			{
				retVal = new char[strlen(pdata) + 1];
				strcpy(retVal, pdata);
				SafeArrayUnaccessData( sa );
			}
			return retVal;
		}
	} catch (_com_error e) {
		//likely due to a fieldtag not found, so just swallow this error
	}
	return NULL;
}

void CDesktopSample::AddField(IRimFieldsPtr fields, int fieldtag, CString data)
{
	IRimFieldPtr f = fields->AddField();
	if ( NULL != f )
	{
		f->Id = (short)fieldtag;
		f->value = _variant_t((LPCTSTR)data);
	}
}

HRESULT __stdcall CDesktopSample::raw_Configure (
        IRimUtility * pRimUtility,
        long hWnd )
{
	hWnd;
	pRimUtility;
	//no op
	return 0;
}

HRESULT __stdcall CDesktopSample::raw_GetErrorString (
        int errorCode,
        BSTR * extensionInfo )
{
	extensionInfo;
	errorCode;
	return 0;
}

