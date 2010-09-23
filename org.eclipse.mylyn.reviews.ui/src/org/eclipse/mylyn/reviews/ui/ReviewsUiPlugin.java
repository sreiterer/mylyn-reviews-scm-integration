/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.ui;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Kilian Matt
 * @author Stefan Reiterer
 */
public class ReviewsUiPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.mylyn.reviews.ui"; //$NON-NLS-1$
	public static final String CONFIG_EXPORT_URL = "svn.config.export"; //$NON-NLS-1$

	// The shared instance
	private static ReviewsUiPlugin plugin;

	/**
	 * The constructor
	 */
	public ReviewsUiPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ReviewsUiPlugin getDefault() {
		return plugin;
	}

	/**
	 * Initialize the default values for the preference page.
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		String workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
		store.setDefault(workspace, CONFIG_EXPORT_URL);
	}

}
