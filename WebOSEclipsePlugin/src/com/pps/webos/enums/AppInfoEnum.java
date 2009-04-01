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
 * 
 * @author justinm
 *
 */
public enum AppInfoEnum {

	TITLE ("title", "_title", "Title: "),
	TYPE ("type", "_type", "Type: "),
	MAIN ("main", "_main", "Main: "),
	ID ("id", "_id", "Id: "),
	VERSION ("version", "_version", "Version: "),
	NOWINDOW ("noWindow", "_noWindow", "No Window: "),
	ICON ("icon", "_icon", "Icon: "),
	MINIICON ("minicon", "_minicon", "MiniIcon: "),
	CATEGORY ("category", "_category", "Category: ");
	
	private String fieldName = null;
	private String replaceVal = null;
	private String displayText = null;

	public static final String WEBOS_FILE_APPINFO = "appinfo.json";	
	
	/**
	 * @param key
	 * @param replaceVal
	 * @param displayText
	 */
	AppInfoEnum (String key, String replaceVal, String displayText) {
		this.fieldName = key;
		this.replaceVal = replaceVal;
		this.displayText = displayText;
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
	
}
