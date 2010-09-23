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
import org.eclipse.mylyn.reviews.core.planning.IPlanningUtils;
import org.eclipse.mylyn.reviews.core.scm.ws.IWSMantisConnector;

public class AdapterManager {
	private static Logger logger = Logger.getLogger(AdapterManager.class.getName());

	public static final String PLUGIN_ID = "org.eclipse.mylyn.reviews.core";
	public static final String EXTENSION_POINT = "scmadapter";
	public static final String EXTENSION_POINT_WS = "mantisconnector";
	private static final String EXTENSION_POINT_PLANNING = "planningutils";

	private Map<String, ISCMAdapter> SCMAdapters;
	private IWSMantisConnector mantisconnector;
	private IPlanningUtils planningUtils;

	public AdapterManager() {
		SCMAdapters = new HashMap<String, ISCMAdapter>();
	}

	public ISCMAdapter getSCMAdapter(String providerId) {
		logger.info(" lookup with " + providerId);
		return SCMAdapters.get(providerId);
	}

	public IWSMantisConnector getMantisConnector() {
		return mantisconnector;
	}

	public IPlanningUtils getPlanningUtils() {
		return planningUtils;
	}

	public void initWSMantisConnector() {
		try {
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID,
					EXTENSION_POINT_WS);

			if (extensionPoint != null) {
				IExtension[] extensions = extensionPoint.getExtensions();

				if (extensions.length == 0) {
					throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, "No extension was found!"));
				} else {
					for (IExtension ext : extensions) {
						IWSMantisConnector adapter = getExecutableWSMantisConnector(ext);

						mantisconnector = adapter;
					}
				}
			} else {
				throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, "No extension point defined"));
			}
		} catch (CoreException e) {
			logger.info("Cannot create scm adapter: " + e.getMessage()); //$NON-NLS-1$
		}
	}

	public void initPlanningUtils() {
		try {
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID,
					EXTENSION_POINT_PLANNING);

			if (extensionPoint != null) {
				IExtension[] extensions = extensionPoint.getExtensions();

				if (extensions.length == 0) {
					throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID,
							"No extension for planningutils was found!"));
				} else {
					for (IExtension ext : extensions) {
						IPlanningUtils adapter = getExecutablePlanningUtils(ext);

						planningUtils = adapter;
					}
				}
			} else {
				throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID,
						"No extension point for planning utils defined"));
			}
		} catch (CoreException e) {
			logger.info("Cannot create planning extension: " + e.getMessage()); //$NON-NLS-1$
		}
	}

	public void initSCMAdapters() {
		try {
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID,
					EXTENSION_POINT);

			if (extensionPoint != null) {
				IExtension[] extensions = extensionPoint.getExtensions();

				if (extensions.length == 0) {
					throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, "No extension was found!"));
				} else {
					for (IExtension ext : extensions) {
						ISCMAdapter adapter = getExecutableAdapter(ext);

						SCMAdapters.put(adapter.getTeamId(), adapter);
					}
				}
			} else {
				throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, "No extension point defined"));
			}
		} catch (CoreException e) {
			logger.info("Cannot create scm adapter: " + e.getMessage()); //$NON-NLS-1$
		}
	}

	private ISCMAdapter getExecutableAdapter(IExtension extension) throws CoreException {
		IConfigurationElement[] configElements = extension.getConfigurationElements();

		// check number of config elements
		if (configElements.length != 1) {
			throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, "Invalid extension point!"));
		} else {
			try {
				ISCMAdapter adapter = (ISCMAdapter) configElements[0].createExecutableExtension("class");//$NON-NLS-1$
				logger.info("SCMAdapter for " + adapter.getTeamId() + " created");

				return adapter;
			} catch (CoreException e) {
				throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage()));
			}
		}
	}

	private IWSMantisConnector getExecutableWSMantisConnector(IExtension extension) throws CoreException {
		IConfigurationElement[] configElements = extension.getConfigurationElements();

		// check number of config elements
		if (configElements.length != 1) {
			throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, "Invalid extension point!"));
		} else {
			try {
				IWSMantisConnector adapter = (IWSMantisConnector) configElements[0].createExecutableExtension("class");//$NON-NLS-1$
				logger.info("WSMantisconnector for created");

				return adapter;
			} catch (CoreException e) {
				throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage()));
			}
		}
	}
	
	private IPlanningUtils getExecutablePlanningUtils(IExtension extension) throws CoreException {
		IConfigurationElement[] configElements = extension.getConfigurationElements();

		// check number of config elements
		if (configElements.length != 1) {
			throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, "Invalid extension point for planning utils!"));
		} else {
			try {
				IPlanningUtils adapter = (IPlanningUtils) configElements[0].createExecutableExtension("class");//$NON-NLS-1$
				logger.info("IPlanningUtils for created");

				return adapter;
			} catch (CoreException e) {
				throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage()));
			}
		}
	}
}
