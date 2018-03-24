package com.kktam.lectio.model;
import java.util.HashSet;
import java.util.Set;



/**
 * <!-- begin-user-doc -->
 * <!--  end-user-doc  -->
 * @generated
 */
 
@javax.persistence.Entity 
public class Tag
{	/**
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
	 
	@javax.persistence.Column(nullable = false) 
	protected String name;

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	 
	@javax.persistence.OneToMany(cascade = javax.persistence.CascadeType.ALL) 
	@javax.persistence.JoinTable 
	protected Set<UiPreference> uiPreference;


	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 */
	public Tag(){
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public Set<UiPreference> getUiPreference() {
		if(this.uiPreference == null) {
				this.uiPreference = new HashSet<UiPreference>();
		}
		return (Set<UiPreference>) this.uiPreference;
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
	 * @ordered
	 */
	public void addAllUiPreference(Set<UiPreference> newUiPreference) {
		if (this.uiPreference == null) {
			this.uiPreference = new HashSet<UiPreference>();
		}
		this.uiPreference.addAll(newUiPreference);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!--  end-user-doc  -->
	 * @generated
	 * @ordered
	 */
	public void removeAllUiPreference(Set<UiPreference> newUiPreference) {
		if(this.uiPreference == null) {
			return;
		}
		
		this.uiPreference.removeAll(newUiPreference);
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
	public void addUiPreference(UiPreference newUiPreference) {
		if(this.uiPreference == null) {
			this.uiPreference = new HashSet<UiPreference>();
		}
		
		this.uiPreference.add(newUiPreference);
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
	public void removeUiPreference(UiPreference oldUiPreference) {
		if(this.uiPreference == null)
			return;
		
		this.uiPreference.remove(oldUiPreference);
	}

	
}

