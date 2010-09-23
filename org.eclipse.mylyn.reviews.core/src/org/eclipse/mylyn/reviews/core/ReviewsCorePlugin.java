/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.core;

import java.util.logging.Logger;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.mylyn.reviews.core.scm.AdapterManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * @author Kilian Matt
 */
public class ReviewsCorePlugin extends Plugin {
	private static Logger logger = Logger.getLogger(ReviewsCorePlugin.class.getName());
	
	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.mylyn.reviews.core";

	// The shared instance
	private static ReviewsCorePlugin plugin;
	
	// Adapter-management
	AdapterManager adapterManager;

	/**
	 * The constructor
	 */
	public ReviewsCorePlugin() {
		adapterManager = new AdapterManager();
	}

	public AdapterManager getAdapterManager() {
		return adapterManager;
	}
	
	/**
	 * Returns the shared instance
	 * @return the shared instance
	 */
	public static ReviewsCorePlugin getPlugin() {
		return plugin;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		// loading the adapters
		logger.info("Loading scm adapters ...");
		adapterManager.initSCMAdapters();
		
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ReviewsCorePlugin getDefault() {
		return plugin;
	}

}
