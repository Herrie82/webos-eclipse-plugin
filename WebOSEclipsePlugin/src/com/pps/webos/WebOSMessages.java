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

import org.eclipse.osgi.util.NLS;

/**
 * @author justinm
 *
 */
public class WebOSMessages extends NLS {
	
	private static final String BUNDLE_NAME= "com.pps.webos.WebOSMessages";

	/**
	 * default constructor
	 */
	private WebOSMessages() {
		// Do not instantiate
	}

	public static String NewWebOSProjectWizard_title = null;
	public static String NewWebOSProjectWizard_op_error_title = null;
	public static String NewWebOSProjectWizard_op_error_message = null;
	public static String NewWebOSProjectWizard_overwritequery_title = null;
	public static String NewWebOSProjectWizard_overwritequery_message = null;
	public static String NewWebOSProjectWizardCreationOperation_op_desc = null;
	public static String NewWebOSProjectWizardOperation_op_desc_proj = null;
	public static String NewWebOSProjectWizardPage1_error_alreadyexists = null;

	static {
		NLS.initializeMessages(BUNDLE_NAME, WebOSMessages.class);
	}

}
