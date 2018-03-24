package com.kktam.lectio.model;

import javax.persistence.UniqueConstraint;

/**
 * <!-- begin-user-doc -->
 * <!--  end-user-doc  -->
 * @generated
 */
 
@javax.persistence.Entity 
@javax.persistence.Table
(
uniqueConstraints= 
	{
			@UniqueConstraint
			(
			columnNames= {"notebook_id", "user_id"}, 
			name="UniqueUserInNotebook"
			)
	}
)
@javax.persistence.NamedQueries({
	@javax.persistence.NamedQuery(name="NotebookUserRole.byNotebook",
			query="select nbUserRole FROM NotebookUserRole as nbUserRole where nbUserRole.notebook = :givennotebook order by nbUserRole.user.name"
			),
	@javax.persistence.NamedQuery(name="NotebookUserRole.byUser",
			query="select nbUserRole FROM NotebookUserRole as nbUserRole where nbUserRole.user = :givenuser order by nbUserRole.notebook.name"
			),
	@javax.persistence.NamedQuery(name="NotebookUserRole.byUserId",
	query="select nbUserRole FROM NotebookUserRole as nbUserRole join fetch nbUserRole.notebook where nbUserRole.user.id = :userId order by nbUserRole.notebook.name"
			),
	@javax.persistence.NamedQuery(name="NotebookUserRole.byNotebookIdUserId",
	query="select nbUserRole FROM NotebookUserRole as nbUserRole join fetch nbUserRole.notebook where nbUserRole.notebook.id = :notebookId and nbUserRole.user.id = :userId order by nbUserRole.notebook.name"
	)
})
public class NotebookUserRole
{
	public static final String UNIQUE_NOTEBOOKUSER = "UniqueUserInNotebook";
	public static final String QUERY_REPUSERROLE_USER = "NotebookUserRole.byUser";
	public static final String QUERYPARAM_REPUSERROLE_USER = "givenuser";
	public static final String QUERY_REPUSERROLE_NOTEBOOK = "NotebookUserRole.byNotebook";
	public static final String QUERYPARAM_REPUSERROLE_NOTEBOOK = "givennotebook";
	public static final String QUERY_NOTEBOOKUSERROLE_USERID = "NotebookUserRole.byUserId";
	public static final String QUERY_NOTEBOOKUSERROLE_NOTEBOOKIDUSERID = "NotebookUserRole.byNotebookIdUserId";
	public static final String QUERYPARAM_NOTEBOOKUSERROLE_USERID = "userId";
	public static final String QUERYPARAM_NOTEBOOKUSERROLE_NOTEBOOKID = "notebookId";
	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	@javax.persistence.Id 
	@javax.persistence.GeneratedValue(strategy=javax.persistence.GenerationType.IDENTITY)
	protected int id;

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	 
	@javax.persistence.Enumerated(javax.persistence.EnumType.STRING) 
	@javax.persistence.Column(nullable = false) 
	protected Role role;

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	 
	@javax.persistence.ManyToOne 
	@javax.persistence.JoinColumn(nullable = false) 
	protected User user;

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	 
	@javax.persistence.ManyToOne 
	@javax.persistence.JoinColumn(nullable = false) 
	protected Notebook notebook;

	

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public Notebook getNotebook() {
		return this.notebook;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void setNotebook(Notebook myNotebook) {
		this.notebook = myNotebook;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void unsetNotebook() {
		this.notebook = null;
	}


	
	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public int getId() {
		return this.id;
	}

	
	public void setId(int myId) {
		this.id = myId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 */
	public NotebookUserRole(){
		super();
	}



	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public Role getRole() {
		return this.role;
	}



	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void setRole(Role myRole) {
		this.role = myRole;
	}



	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void unsetRole() {
		this.role = null;
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public User getUser() {
		return this.user;
	}
	
	
	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void setUser(User myUser) {
		this.user = myUser;
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void unsetUser() {
		this.user = new User();
	}

	
}

