/**
 *  Copyright (c) 2009. Knests, LLC
 *  
 *  This file is part of webOS Eclipse Plug-in.
 * 
 *  webOS Eclipse Plug-in is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  webOS Eclipse Plug-in is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with webOS Eclipse Plug-in.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  Author: Justin Musgrove
 *  		justinm@knests.com
 *  
 * */
package com.pps.webos.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import com.pps.webos.enums.AppInfoEnum;
import com.pps.webos.model.AppInfo;

/**
 * Class used to used to replace update values in the appinfo.json
 * 
 * @author justinm
 *
 */
public class AppInfoHelper {
	
	/**
	 * Method should replace values in the appinfo.json with values specified by in the UI.
	 * Reference materials:
	 * 		org.eclipse.jface.text.FindReplaceDocumentAdapter to replace matches of file with the values of the domain table
			http://dev.eclipse.org/newslists/news.eclipse.platform/msg74246.html
			http://help.eclipse.org/help32/index.jsp?topic=/org.eclipse.platform.doc.isv/reference/api/org/eclipse/core/filebuffers/package-summary.html
	 * @param projectName
	 * @param appInfo
	 */
	public void replaceAppInfoAttributes (String projectName, AppInfo appInfo) {
		
		
		try {	
			/* get workspace location, since the directory structure should only contain 1 appinfo.json
			 * logic will just look for the the name of the file **/
			IWorkspace workspace= ResourcesPlugin.getWorkspace();
			IFile file = workspace.getRoot().getProject(projectName).getFile(AppInfoEnum.WEBOS_FILE_APPINFO);
			
			//%PROJECT_NAME%archive/webos/appinfo.json
			ITextFileBufferManager fileBufferManager = FileBuffers.getTextFileBufferManager();
			IPath location = file.getLocation();
			fileBufferManager.connect(location, LocationKind.NORMALIZE, new NullProgressMonitor());
			
			ITextFileBuffer textFileBuffer = fileBufferManager.getTextFileBuffer(location, LocationKind.NORMALIZE);
			IDocument document = textFileBuffer.getDocument();
			
			FindReplaceDocumentAdapter findReplaceDocumentAdapter = new FindReplaceDocumentAdapter(document);
			
			// for each element in the enumeration
			// call method name from appInfo with MethodUtils
			PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
			Object tObj = null;
			for (AppInfoEnum appInfoEnum: AppInfoEnum.values()) {
				tObj = propertyUtilsBean.getProperty(appInfo, appInfoEnum.getFieldName());
				
				IRegion regionFind 		= findReplaceDocumentAdapter.find(0, appInfoEnum.getReplaceVal(), true, false, true, false);
				IRegion regionReplace 	= findReplaceDocumentAdapter.replace(String.valueOf(tObj), false);
			}
	
			// commit replaced changes
			textFileBuffer.commit(new NullProgressMonitor(), true);
			
			// clean up  
			fileBufferManager.disconnect(location, LocationKind.NORMALIZE, new NullProgressMonitor());
		
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
