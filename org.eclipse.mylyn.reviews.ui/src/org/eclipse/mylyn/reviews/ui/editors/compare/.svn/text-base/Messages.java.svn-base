package org.eclipse.mylyn.reviews.ui.editors.compare;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Message bundle for compare dialog.
 * 
 * @author Stefan Reiterer
 * 
 */
public class Messages {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.reviews.ui.editors.compare.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	
	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
