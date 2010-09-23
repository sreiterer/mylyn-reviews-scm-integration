package org.eclipse.mylyn.reviews.ui.editors.validators;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.mylyn.reviews.ui.editors.Messages;

public class ReporterInputValidator implements IInputValidator {

	public String isValid(String name) {
		if (!name.equals(""))
			return null;
		else
			return Messages.ReporterInputValidator_Error_Message;
	}

}
