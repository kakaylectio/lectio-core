package com.kktam.lectio.model;
import javax.persistence.Column;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.kakay.lectio.rest.resources.views.Views;



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
					columnNames= {"name"}, 
					name="UniqueUserName"
					),
					@UniqueConstraint
					(
					columnNames= {"email"},
					name="UniqueUserEmail"
					)
			}
		)
@javax.persistence.NamedQueries({
	@javax.persistence.NamedQuery(name="User.byExactName",
			query="select u FROM User as u where u.name = :name"
			),
//	@javax.persistence.NamedQuery(name="User.byId",
//		query="select u FROM User as u where  u.id = :id"
//			),

	@javax.persistence.NamedQuery(name="User.byEmail",
		query="select u FROM User as u where  u.email = :email"
			),

})

public class User
{
	public static final String UNIQUE_NAME = "UniqueUserName";
	public static final String UNIQUE_EMAIL = "UniqueUserEmail";
	public static final String QUERY_USER_BYNAME = "User.byExactName";
//	public static final String QUERY_USER_BYID = "User.byId";
	public static final String QUERYPARAM_USER_NAME = "name";
	public static final String QUERYPARAM_USER_ID = "id";
	public static final String QUERY_USER_BYEMAIL = "User.byEmail";
	public static final String QUERYPARAM_USER_EMAIL = "email";
	
	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	@Column(nullable = false)
	protected String name;

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	@javax.persistence.Column(nullable = false) 
	protected String email;

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
	@JsonProperty 
	public int getId() {
		return this.id;
	}

	
	public void setId(int myId) {
		this.id = myId;
	}

	@javax.persistence.Column(nullable = true) 
	protected String uiPreference;
	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	@JsonProperty 
	public String getUiPreference() {
		
		return this.uiPreference;
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void unsetUiPreference() {
		this.uiPreference = "";
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void setUiPreference(String myUiPreference) {
		this.uiPreference = myUiPreference;
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 */
	public User(){
		super();
	}


	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	@JsonProperty 
	public String getName() {
		return this.name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	@JsonView(Views.UserDetails.class)
	public String getEmail() {
		return this.email;
	}




	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void setName(String myName) {
		this.name = myName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void setEmail(String myEmail) {
		this.email = myEmail;
	}


	

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void unsetName() {
		this.name = "";
	}

	public void unsetEmail() {
		this.email = "";
	}



	
}

