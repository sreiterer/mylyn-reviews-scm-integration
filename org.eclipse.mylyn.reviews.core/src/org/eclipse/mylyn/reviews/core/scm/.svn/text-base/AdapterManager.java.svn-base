package org.eclipse.mylyn.reviews.core.scm;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

public class AdapterManager {
	private static Logger logger = Logger.getLogger(AdapterManager.class.getName());
	
	public static final String PLUGIN_ID = "org.eclipse.mylyn.reviews.core";
	public static final String EXTENSION_POINT = "scmadapter";
	
	private Map<String, ISCMAdapter> SCMAdapters;
	
	public AdapterManager() {
		SCMAdapters = new HashMap<String, ISCMAdapter>();
	}
	
	public ISCMAdapter getSCMAdapter(String providerId) {
		logger.info(" lookup with " + providerId);
		return SCMAdapters.get(providerId);
	}
	
	public void initSCMAdapters() {
		try {
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint(PLUGIN_ID, EXTENSION_POINT);
			
			if (extensionPoint != null) {
				IExtension[] extensions = extensionPoint.getExtensions();
				
				if (extensions.length == 0) {
					throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID,
							"No extension was found!"));
				} else {
					for (IExtension ext : extensions) {
						ISCMAdapter adapter = getExecutableAdapter(ext);
						
						SCMAdapters.put(adapter.getTeamId(), adapter);
					}
				}
			} else {
				throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID,
						"No extension point defined"));
			}
		} catch (CoreException e) {
			logger.info("Cannot create scm adapter: " + e.getMessage()); //$NON-NLS-1$
		}
	}
	
	private ISCMAdapter getExecutableAdapter(IExtension extension) throws CoreException {
		IConfigurationElement[] configElements = extension
			.getConfigurationElements();
		
	// check number of config elements
	if (configElements.length != 1) {
		throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID,
				"Invalid extension point!"));
	} else {
		try {
			ISCMAdapter adapter = (ISCMAdapter) configElements[0]
					.createExecutableExtension("class");//$NON-NLS-1$
			logger.info("SCMAdapter for " + adapter.getTeamId() + " created");
			
			return adapter;
		} catch (CoreException e) {
			throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, e
					.getMessage()));
		}
	}
	}
}
