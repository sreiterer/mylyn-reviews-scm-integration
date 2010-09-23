package org.eclipse.mylyn.reviews.ui.editors.compare;

import java.io.BufferedInputStream;
import java.io.InputStream;

import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

/**
 * This class represents an item which should be presented inside an eclipse
 * compare editor.
 * 
 * @author Stefan Reiterer
 * 
 */
public class ChangesetCompareItem implements IStreamContentAccessor, ITypedElement, IModificationDate {

	private IStorage item;
	private long modificationDate;
	private String extension;
	private String revision;

	/**
	 * Default constructor that initializes the compare item with the given
	 * values.
	 * 
	 * @param item
	 *            Item that should be compared with another item.
	 * @param modificationDate
	 *            Modification date of the compare item.
	 * @param extension
	 *            File extension of the compare item.
	 * @param revision
	 *            Revision of the compare item.
	 */
	public ChangesetCompareItem(IStorage item, long modificationDate, String extension, String revision) {

		if (item != null)
			this.item = item;
		this.modificationDate = modificationDate;
		this.extension = extension;
		this.revision = revision;
	}

	/**
	 * Get the content of the compare item.
	 */
	public InputStream getContents() throws CoreException {
		if (item == null)
			return new BufferedInputStream(null);
		return item.getContents();
	}

	/** not implemented. */
	public Image getImage() {
		return null;
	}

	/** Get the name of the compare item (=revision). */
	public String getName() {
		return revision;
	}

	/**
	 * Get the type of the compare item.
	 */
	public String getType() {
		return extension;
	}

	public String getString() {
		return Long.toString(modificationDate);
	}

	/**
	 * Get the modification date of the compare item.
	 */
	public long getModificationDate() {
		return modificationDate;
	}
}
