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
package com.pps.webos.enums;

/**
 * Enum class represents attributes values used throuhout the plugin.  Three attributes:  
 * 	1) fieldname - name/attribute in the appinfo.json
 *  2) replaceVal - string value to be replaced in the appinfo.json ->  the values are different than default value of WebOS presentations
 *  3) displayText - the display text of the field/attribute
 *  4) toolTip - that will be displayed in the UI
 * @author justinm
 *
 */
public enum AppInfoEnum {
	
	TITLE ("title", "_title", "Title: ", "Application Name", "Name of application as it appears in Launcher and in app window"),
	TYPE ("type", "_type", "Type: ", "web", "Conventional application"),
	MAIN ("main", "_main", "Main: ", "index.html", "Application entry point; defaults to index.html"),
	ID ("id", "_id", "Id: ", "com.yourdomain.app", "Must be unique for each application"),
	VERSION ("version", "_version", "WebOS Version: ", "1.0", "Application version number"),
	NOWINDOW ("noWindow", "_noWindow", "No Window: ", "false", "Headless application; defaults to false"),
	ICON ("icon", "_icon", "Icon: ", "icon.png", "Application's launcher icon; defaults icon.png"),
	MINIICON ("minicon", "_minicon", "MiniIcon: ", "miniicon.png", "Notification icon; defaults to miniicon.png"),
	CATEGORY ("category", "_category", "Category: ", "", "Default category for application");
	
	private String fieldName = null;
	private String replaceVal = null;
	private String displayText = null;
	private String defaultValue = null;
	private String toolTip = null;

	public static final String WEBOS_FILE_APPINFO = "appinfo.json";	
	
	/**
	 * @param key
	 * @param replaceVal
	 * @param displayText
	 */
	AppInfoEnum (String key, String replaceVal, String displayText, String defaultValue, String toolTip) {
		this.fieldName = key;
		this.replaceVal = replaceVal;
		this.displayText = displayText;
		this.defaultValue = defaultValue;
		this.toolTip = toolTip;
	}
	

	/**
	 * @return the replaceVal
	 */
	public String getReplaceVal() {
		return replaceVal;
	}

	/**
	 * @return the displayText
	 */
	public String getDisplayText() {
		return displayText;
	}


	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}


	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}


	/**
	 * @return the toolTip
	 */
	public String getToolTip() {
		return toolTip;
	}
	
}
