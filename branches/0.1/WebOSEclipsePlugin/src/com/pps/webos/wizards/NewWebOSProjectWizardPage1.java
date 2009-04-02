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
package com.pps.webos.wizards;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.pps.webos.WebOSEclipsePlugin;
import com.pps.webos.WebOSMessages;
import com.pps.webos.enums.AppInfoEnum;
import com.pps.webos.model.AppInfo;

/**
 * @author justinm
 *
 */
public class NewWebOSProjectWizardPage1 extends WizardPage {

	
	/**
	 * Request a project name. Fires an event whenever the text field is
	 * changed, regardless of its content.
	 */
	private final class NameGroup extends Observable implements IDialogFieldListener {

		protected final StringDialogField fNameField;

		public NameGroup() {
			// text field for project name
			fNameField = new StringDialogField();
			fNameField.setLabelText("Project Name: "); 
			fNameField.setDialogFieldListener(this);
		}

		public Control createControl(Composite composite) {
			Composite nameComposite= new Composite(composite, SWT.NONE);
			nameComposite.setFont(composite.getFont());
			nameComposite.setLayout(initGridLayout(new GridLayout(2, false), false));

			fNameField.doFillIntoGrid(nameComposite, 2);
			LayoutUtil.setHorizontalGrabbing(fNameField.getTextControl(null));

			return nameComposite;
		}

		/**
		 * fireEvent
		 */
		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		public String getName() {
			return fNameField.getText().trim();
		}

		public void postSetFocus() {
			fNameField.postSetFocusOnDialogField(getShell().getDisplay());
		}

		public void setName(String name) {
			fNameField.setText(name);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener#dialogFieldChanged(org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField)
		 */
		public void dialogFieldChanged(DialogField field) {
			fireEvent();
		}
	}
	
	/**
	 * AppInfoGroup
	 *
	 */
	private final class AppInfoGroup extends Observable implements Observer, IDialogFieldListener  {

		private AppInfo appInfo = null;

		
		// fTitle;
		// fType;
		// fMain;
		// fId;
		// fVersion;
		// fNoWindow;
		// fIcon;
		// fMiniIcon;
		// fCategory;
		protected final StringDialogField fTitle;
		protected final StringDialogField fType;
		protected final StringDialogField fMain;
		protected final StringDialogField fId;
		protected final StringDialogField fVersion;
		protected final StringDialogField fNoWindow;
		protected final StringDialogField fIcon;
		protected final StringDialogField fMiniIcon;
		protected final StringDialogField fCategory;
		
		public AppInfoGroup() {
			
			appInfo = new AppInfo();
			
			// fTitle
			fTitle = new StringDialogField();
			fTitle.setLabelText(AppInfoEnum.TITLE.getDisplayText());
			fTitle.setDialogFieldListener(this);

			// fType TODO - make uneditable
			fType = new StringDialogField();
			fType.setLabelText(AppInfoEnum.TYPE.getDisplayText()); 
			fType.setDialogFieldListener(this);
			
			// fMain TODO - make uneditable
			fMain = new StringDialogField();
			fMain.setLabelText(AppInfoEnum.MAIN.getDisplayText()); 
			fMain.setDialogFieldListener(this);

			// fId
			fId = new StringDialogField();
			fId.setLabelText(AppInfoEnum.ID.getDisplayText()); 
			fId.setDialogFieldListener(this);
			
			// fVersion
			fVersion = new StringDialogField();
			fVersion.setLabelText(AppInfoEnum.VERSION.getDisplayText()); 
			fVersion.setDialogFieldListener(this);
			
			// fNoWindow
			fNoWindow = new StringDialogField();
			fNoWindow.setLabelText(AppInfoEnum.NOWINDOW.getDisplayText()); 
			fNoWindow.setDialogFieldListener(this);			
			
			// fIcon - valid file path
			fIcon = new StringDialogField();
			fIcon.setLabelText(AppInfoEnum.ICON.getDisplayText()); 
			fIcon.setDialogFieldListener(this);
			
			// fMiniIcon
			fMiniIcon = new StringDialogField();
			fMiniIcon.setLabelText(AppInfoEnum.MINIICON.getDisplayText()); 
			fMiniIcon.setDialogFieldListener(this);

			// fCategory
			fCategory = new StringDialogField();
			fCategory.setLabelText(AppInfoEnum.CATEGORY.getDisplayText()); 
			fCategory.setDialogFieldListener(this);
	
			
		}
		
		public Control createControl(Composite composite) {

			final int numColumns= 2;
			
			final Group group= new Group(composite, SWT.NONE);
			group.setFont(composite.getFont());
			group.setText("AppInfo Attributes"); 
			
			GridLayout layout= initGridLayout(new GridLayout(4, false), true);;
			layout.horizontalSpacing = 3;
			layout.verticalSpacing = 6;
			group.setLayout(layout);
					
			// fTitle
			fTitle.doFillIntoGrid(group, numColumns);
			fTitle.getTextControl(group).setText("HEY");
			fTitle.getTextControl(group).setToolTipText("TITLE SHOULD BE FILLED");
			//LayoutUtil.setHorizontalGrabbing(fTitle.getTextControl(null));
			
			// fType
			fType.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fType.getTextControl(null));

			// fMain
			fMain.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fMain.getTextControl(null));

			// fId
			fId.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fId.getTextControl(null));

			// fVersion
			fVersion.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fVersion.getTextControl(null));

			// fNoWindow
			fNoWindow.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fNoWindow.getTextControl(null));		
			
			//fIcon
			fIcon.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fIcon.getTextControl(null));

			//fMiniIcon
			fMiniIcon.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fMiniIcon.getTextControl(null));			

			//fCategory
			fCategory.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fCategory.getTextControl(null));
			
			LayoutUtil.setHorizontalGrabbing(group);
			
			// Bind text fields to the model
			// probably a cleaner way to do this by looping through field vals dynamically...
			DataBindingContext bindingContext = new DataBindingContext();
			
			// fTitle;
			bindingContext.bindValue(
					SWTObservables.observeText(fTitle.getTextControl(group), SWT.Modify), 
					PojoObservables.observeValue(appInfo, AppInfoEnum.TITLE.getFieldName()),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
			
			// fType;
			bindingContext.bindValue(
					SWTObservables.observeText(fType.getTextControl(group), SWT.Modify), 
					PojoObservables.observeValue(appInfo, AppInfoEnum.TYPE.getFieldName()),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));

			// fMain;
			bindingContext.bindValue(
					SWTObservables.observeText(fMain.getTextControl(group), SWT.Modify), 
					PojoObservables.observeValue(appInfo, AppInfoEnum.MAIN.getFieldName()),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));

			// fId;
			bindingContext.bindValue(
					SWTObservables.observeText(fId.getTextControl(group), SWT.Modify), 
					PojoObservables.observeValue(appInfo, AppInfoEnum.ID.getFieldName()),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));

			// fVersion;
			bindingContext.bindValue(
					SWTObservables.observeText(fVersion.getTextControl(group), SWT.Modify), 
					PojoObservables.observeValue(appInfo, AppInfoEnum.VERSION.getFieldName()),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));

			// noWindow
			bindingContext.bindValue(
					SWTObservables.observeText(fNoWindow.getTextControl(group), SWT.Modify), 
					PojoObservables.observeValue(appInfo, AppInfoEnum.NOWINDOW.getFieldName()),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
			
			// fIcon;
			bindingContext.bindValue(
					SWTObservables.observeText(fIcon.getTextControl(group), SWT.Modify), 
					PojoObservables.observeValue(appInfo, AppInfoEnum.ICON.getFieldName()),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));

			// fMiniIcon;
			bindingContext.bindValue(
					SWTObservables.observeText(fMiniIcon.getTextControl(group), SWT.Modify), 
					PojoObservables.observeValue(appInfo, AppInfoEnum.MINIICON.getFieldName()),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));

			// fcategory;			
			bindingContext.bindValue(
					SWTObservables.observeText(fCategory.getTextControl(group), SWT.Modify), 
					PojoObservables.observeValue(appInfo, AppInfoEnum.CATEGORY.getFieldName()),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
			
			// DEFAULTS, tooltips, etc : Needed to be placed after the bindings
			
			// fTitle;
			fTitle.setText(AppInfoEnum.TITLE.getDefaultValue());
			fTitle.getTextControl(group).setToolTipText(AppInfoEnum.TITLE.getToolTip());
			
			// fType;
			fType.setText(AppInfoEnum.TYPE.getDefaultValue());
			fType.getTextControl(group).setEditable(false);
			fType.getTextControl(group).setToolTipText(AppInfoEnum.TYPE.getToolTip());
			
			// fMain;
			fMain.setText(AppInfoEnum.MAIN.getDefaultValue());
			fMain.getTextControl(group).setToolTipText(AppInfoEnum.MAIN.getToolTip());
			
			// fId;
			fId.setText(AppInfoEnum.ID.getDefaultValue());
			fId.getTextControl(group).setToolTipText(AppInfoEnum.ID.getToolTip());
			
			// fVersion;
			fVersion.setText(AppInfoEnum.VERSION.getDefaultValue());
			fVersion.getTextControl(group).setToolTipText(AppInfoEnum.VERSION.getToolTip());
			
			// fNoWindow;
			fNoWindow.setText(AppInfoEnum.NOWINDOW.getDefaultValue());
			fNoWindow.getTextControl(group).setToolTipText(AppInfoEnum.NOWINDOW.getToolTip());
			
			// fIcon;
			fIcon.setText(AppInfoEnum.ICON.getDefaultValue());
			fIcon.getTextControl(group).setToolTipText(AppInfoEnum.ICON.getToolTip());
			
			// fMiniIcon;
			fMiniIcon.setText(AppInfoEnum.ICON.getDefaultValue());
			fMiniIcon.getTextControl(group).setToolTipText(AppInfoEnum.MINIICON.getToolTip());
			
			// fCategory;
			fCategory.getTextControl(group).setToolTipText(AppInfoEnum.CATEGORY.getToolTip());
			
			return group;
		}
		
		/**
		 * fireEvent
		 */
		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener#dialogFieldChanged(org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField)
		 */
		public void dialogFieldChanged(DialogField field) {
			fireEvent();
		}

		/* (non-Javadoc)
		 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
		 */
		public void update(Observable arg0, Object arg1) {
			fireEvent();
		}
		
		/**
		 * @return the appInfo
		 */
		public AppInfo getAppInfo() {
			return appInfo;
		}
		
	}

	/**
	 * Validate this page and show appropriate warnings and error NewWizardMessages.
	 */
	private final class Validator implements Observer {

		public void update(Observable arg0, Object arg1) {

			/** NameGroup **/
			IWorkspace workspace= ResourcesPlugin.getWorkspace();
			
			String projectName = fNameGroup.getName();
			
			// validate length of name
			if (projectName == null || projectName.length() == 0) {
				setErrorMessage(null);
				setMessage("Please enter project name"); 
				setPageComplete(false);
				return;				
			}
			
			// check whether the project name is valid
			final IStatus nameStatus= workspace.validateName(projectName, IResource.PROJECT);
			if (!nameStatus.isOK()) {
				setErrorMessage(nameStatus.getMessage());
				setPageComplete(false);
				return;
			}		
			
			// check whether project already exists
			final IProject handle = workspace.getRoot().getProject(projectName);
			if (handle.exists()) {
				setErrorMessage("Project already exists"); 
				setPageComplete(false);
				return;
			}			
			
			/** AppInfoGroup **/
//			fTitle; = required
//			fType; = required 
//			fMain; = required
//			fId; = required
//			fVersion; = required
//			fNoWindow; = not required
//			fIcon; = not required (FILE PATH: if present should validate as file location)
//			fMiniIcon; = not required (FILE PATH: if present should validate as file location)
//			fCategory; = not required				
			
//			if (appInfoGroup.fTitle() == null ||)
//			
//			
//			appInfoGroup.fType
//			appInfoGroup.fMain
//			appInfoGroup.fId
//			appInfoGroup.fVersion
//			appInfoGroup.fNoWindow
//			appInfoGroup.fIcon
//			appInfoGroup.fMiniIcon
//			appInfoGroup.fCategory
			
			
			System.out.println("Validator");
			
			setPageComplete(true);
			setErrorMessage(null);
			setMessage(null);			
			
		}
	
		

		
	}
	
//	public static final IStatus validateText(String val, String ) {
////		return null;
//	}	
	
	
	
	/* Static class information */
	private IStatus fCurrStatus = null;
	private boolean fPageVisible = false;

	private IConfigurationElement fConfigurationElement = null;
	
	
	// groups
	private final AppInfoGroup appInfoGroup;
	private final NameGroup fNameGroup;
	private final Validator fValidator;
	
	/**
	 * 
	 * @param pageName
	 */
	public NewWebOSProjectWizardPage1(int pageNumber, IConfigurationElement elem) {
		super("page" + pageNumber);

		// wizard information
		fConfigurationElement = elem;
		fCurrStatus = createStatus(IStatus.OK, ""); //$NON-NLS-1$
		setTitle(getAttribute(elem, "pagetitle")); //$NON-NLS-1$
		setDescription(getAttribute(elem, "pagedescription")); //$NON-NLS-1$

		
		// make call to groups 
		fNameGroup = new NameGroup();
		appInfoGroup = new AppInfoGroup();

		// initialize all elements
		fNameGroup.notifyObservers(appInfoGroup);
		
		// Validator
		fValidator = new Validator();
		fNameGroup.addObserver(fValidator);
		appInfoGroup.addObserver(fValidator);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {

		final Composite composite= new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), true));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// create UI elements
		Control nameControl= createNameControl(composite);
		nameControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// create UI elements
		Control appInfoControl = createAppInfoControl(composite);
		appInfoControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	
		
		
		setControl(composite);
	}	
	
	/**
	 * @param layout
	 * @param margins
	 * @return
	 */
	private GridLayout initGridLayout(GridLayout layout, boolean margins) {
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		if (margins) {
			layout.marginWidth= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		} else {
			layout.marginWidth= 4;
			layout.marginHeight= 7;
		}
		return layout;
	}	
	
	
	/**
	 * Utility method for getting attributes
	 * 
	 * @param elem
	 * @param tag
	 * @return String
	 */
	private String getAttribute(IConfigurationElement elem, String tag) {
		String res= elem.getAttribute(tag);
		if (res == null) {
			return '!' + tag + '!';
		}
		return res;
	}	
	
	/**
	 * @param severity
	 * @param message
	 * @return IStatus
	 */
	private static IStatus createStatus(int severity, String message) {
		return new Status(severity, WebOSEclipsePlugin.getPluginId(), severity, message, null);
	}	
	

	
//	/**
//	 * Method should validate input text in the page 
//	 * 
//	 * @param text
//	 */
//	private void dialogueChange() {
//
//		/* 'validation' for project name */
//		IWorkspace workspace= ResourcesPlugin.getWorkspace();
//		
//		IStatus status= workspace.validateName(fNameGroup.getName(), IResource.PROJECT);
//		if (status.isOK()) {
//			/* check to see if project already exists in workspace */
//			if (workspace.getRoot().getProject(fNameGroup.getName()).exists()) {
//				status = createStatus(IStatus.ERROR, WebOSMessages.NewWebOSProjectWizardPage1_error_alreadyexists);
//			}
//		}
//		updateStatus(status);
//
//	}
//	
//	/**
//	 * Updates the status line and the ok button depending on the status
//	 * 
//	 * @param status
//	 */
//	private void updateStatus(IStatus status) {
//		fCurrStatus= status;
//		setPageComplete(!status.matches(IStatus.ERROR));
//		if (fPageVisible) {
//			applyToStatusLine(this, status);
//		}
//	}
//	
//	/**
//	 * Applies the status to a dialog page
//	 * 
//	 * @param page
//	 * @param status
//	 */
//	private static void applyToStatusLine(DialogPage page, IStatus status) {
//		String errorMessage= null;
//		String warningMessage= null;
//		String statusMessage= status.getMessage();
//		if (statusMessage.length() > 0) {
//			if (status.matches(IStatus.ERROR)) {
//				errorMessage= statusMessage;
//			} else if (!status.isOK()) {
//				warningMessage= statusMessage;
//			}
//		}
//		page.setErrorMessage(errorMessage);
//		page.setMessage(warningMessage);
//	}
	
	
	
	/**
	 * Creates the controls for the name field. 
	 *  
	 * @param composite the parent composite
	 * @return the created control
	 */		
	protected Control createNameControl(Composite composite) {
		return fNameGroup.createControl(composite);
	}
	
	/**
	 * Creates the controls for the name field. 
	 *  
	 * @param composite the parent composite
	 * @return the created control
	 */		
	protected Control createAppInfoControl(Composite composite) {
		return appInfoGroup.createControl(composite);
	}	

	/**
	 * @return
	 */
	public String getProjectName() {
		return fNameGroup.getName();
	}
	
	public AppInfo getAppInfo() {
		return appInfoGroup.getAppInfo();
	}
	
	/**
	 * Returns the configuration element of this page.
	 * @return Returns a IConfigurationElement
	 */
	public IConfigurationElement getConfigurationElement() {
		return fConfigurationElement;
	}

	
	
}
