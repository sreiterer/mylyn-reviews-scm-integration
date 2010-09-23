package org.eclipse.mylyn.reviews.core.planning;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="User")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {

	@XmlAttribute(name="username", required=true)
	private String username;

	public User() {
		super();
	}

	public User(String username) {
		super();
		this.username = username;
	}

	/* (non-Javadoc)
	 * @see com.frequentis.mylyn.reviews.planning.model.IUser#getUsername()
	 */
	public String getUsername() {
		return username;
	}

	/* (non-Javadoc)
	 * @see com.frequentis.mylyn.reviews.planning.model.IUser#setUsername(java.lang.String)
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	/* (non-Javadoc)
	 * @see com.frequentis.mylyn.reviews.planning.model.IUser#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		User otherUser = (User) other;
		
		return this.username.equals(otherUser.getUsername());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	/* (non-Javadoc)
	 * @see com.frequentis.mylyn.reviews.planning.model.IUser#hashCode()
	 */
	public int hashCode() {
		return this.username.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	/* (non-Javadoc)
	 * @see com.frequentis.mylyn.reviews.planning.model.IUser#toString()
	 */
	public String toString() {
		return this.username;
	}
	
	
}
