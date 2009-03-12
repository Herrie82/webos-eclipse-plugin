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
package com.pps.webos;

import java.net.URL;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

/**
 * @author justinm
 *
 */
public class WebOSEclipsePlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.pps.webos";
	
	// The shared instance.
	private static WebOSEclipsePlugin fgPlugin;

	/**
	 * The constructor.
	 */
	public WebOSEclipsePlugin() {
		fgPlugin= this;
	}

	/**
	 * @return Returns the shared instance.
	 */
	public static WebOSEclipsePlugin getDefault() {
		return fgPlugin;
	}

	/**
	 * @return Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Since 3.1.1. Load from icon paths with $NL$
	 * 
	 * @param bundle
	 * @param path
	 * @return
	 */
	public static ImageDescriptor createImageDescriptor(Bundle bundle, IPath path) {
		URL url= FileLocator.find(bundle, path, null);
		if (url != null) {
			return ImageDescriptor.createFromURL(url);
		}
		return ImageDescriptor.getMissingImageDescriptor();
	}

	public static String getPluginId() {
		return PLUGIN_ID; 
	}


	/**
	 * @param status
	 */
	public static void log (IStatus status) {
		getDefault().getLog().log(status);
	}


	/**
	 * @param message
	 */
	public static void log(String message) {
		log(new Status(IStatus.ERROR, getPluginId(), IStatus.ERROR, message, null));
	}


	/**
	 * @param e
	 */
	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, getPluginId(), IStatus.ERROR, "Internal Error", e));
	}

}
