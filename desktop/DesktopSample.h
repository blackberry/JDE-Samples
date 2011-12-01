/*
 * DesktopSample.h
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

// DesktopSample.h : Declaration of the CDesktopSample

#ifndef __DESKTOPSAMPLE_H_
#define __DESKTOPSAMPLE_H_

#include "resource.h"       // main symbols
#include "ContactList.h"

/////////////////////////////////////////////////////////////////////////////
// CDesktopSample
class ATL_NO_VTABLE CDesktopSample : 
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<CDesktopSample, &CLSID_DesktopSample>,
	public IDispatchImpl<IRimExtension, &IID_IRimExtension, &LIBID_DESKTOPAPILib>
{
public:
	CDesktopSample();

DECLARE_REGISTRY_RESOURCEID(IDR_DESKTOPSAMPLE)

DECLARE_PROTECT_FINAL_CONSTRUCT()

BEGIN_COM_MAP(CDesktopSample)
	COM_INTERFACE_ENTRY(IRimExtension)
	COM_INTERFACE_ENTRY(IDispatch)
END_COM_MAP()

// IDesktopSample
public:
	 virtual HRESULT __stdcall raw_GetExtensionInfo (
        BSTR * extensionInfo );
    virtual HRESULT __stdcall raw_Process (
        IRimUtility * pRimUtility,
        IRimDatabaseAccess * pRimDeviceAccess );
    virtual HRESULT __stdcall raw_Configure (
        IRimUtility * pRimUtility,
        long hWnd );
    virtual HRESULT __stdcall raw_GetErrorString (
        int errorCode,
        BSTR * extensionInfo );
private:
	void AddField(IRimFieldsPtr fields, int fieldtag, CString data);
	char * GetField(IRimFieldsPtr fields, int fieldtag);
private:
	ContactList m_data;
};

//			'TypeLib' = s '{C4299B4F-CF7F-43F0-8B67-7054B7B380C0}'



#endif //__DESKTOPSAMPLE_H_
