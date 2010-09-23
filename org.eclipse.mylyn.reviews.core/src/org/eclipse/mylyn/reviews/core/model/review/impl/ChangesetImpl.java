/**
 * Copyright (c) 2010 Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Vienna University of Technology - initial API and implementation
 *
 * $Id$
 */
package org.eclipse.mylyn.reviews.core.model.review.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.mylyn.reviews.core.model.review.Changeset;
import org.eclipse.mylyn.reviews.core.model.review.ReviewPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Changeset</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.mylyn.reviews.core.model.review.impl.ChangesetImpl#getRevision <em>Revision</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.core.model.review.impl.ChangesetImpl#getProjecturl <em>Projecturl</em>}</li>
 *   <li>{@link org.eclipse.mylyn.reviews.core.model.review.impl.ChangesetImpl#getComment <em>Comment</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ChangesetImpl extends ScopeItemImpl implements Changeset {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ChangesetImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ReviewPackage.Literals.CHANGESET;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRevision() {
		return (String)eGet(ReviewPackage.Literals.CHANGESET__REVISION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRevision(String newRevision) {
		eSet(ReviewPackage.Literals.CHANGESET__REVISION, newRevision);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getProjecturl() {
		return (String)eGet(ReviewPackage.Literals.CHANGESET__PROJECTURL, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProjecturl(String newProjecturl) {
		eSet(ReviewPackage.Literals.CHANGESET__PROJECTURL, newProjecturl);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getComment() {
		return (String)eGet(ReviewPackage.Literals.CHANGESET__COMMENT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setComment(String newComment) {
		eSet(ReviewPackage.Literals.CHANGESET__COMMENT, newComment);
	}

} //ChangesetImpl
