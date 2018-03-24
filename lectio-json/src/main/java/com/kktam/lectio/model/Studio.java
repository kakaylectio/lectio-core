package com.kktam.lectio.model;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;



/**
 * <!-- begin-user-doc -->
 * <!--  end-user-doc  -->
 * @generated
 */
 
@javax.persistence.Entity 
@javax.persistence.NamedQueries({
	@javax.persistence.NamedQuery(name="Studio.ByOwner",
			query="select studio0 FROM Studio as studio0 where studio0.owner = :owner order by studio0.name"
			),
	@javax.persistence.NamedQuery(name="Studio.ById",
	query="select studio0 FROM Studio as studio0 where studio0.id = :id"
	)
})
public class Studio
{
	
	public final static String QUERY_FINDSTUDIOSOWNEDBY = "Studio.ByOwner";
	public final static String QUERY_FINDSTUDIOSOWNEDBY_OWNERPARAM = "owner";
	public final static String QUERY_FINDSTUDIOBYID = "Studio.ById";
	public final static String QUERYPARAM_FINDSTUDIOBYID = "id";

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	@JsonProperty  
	@javax.persistence.Column(nullable = false) 
	protected String name;

	 

	@javax.persistence.ManyToOne 
	@javax.persistence.JoinColumn(nullable = false) 
	protected User owner;

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
	@JsonIgnore
	public User getOwner() {
		return this.owner;
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
	public void setOwner(User myOwner) {
		this.owner = myOwner;
		
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


	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void unsetOwner() {

		this.owner = null;
	}



	
}

