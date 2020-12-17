package data.entitys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter //@ToString
@Entity
@Table(name = "user")
public class User implements Serializable {
	private static final long serialVersionUID = -3189658908940475488L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user")
	private Long id;
	
	@Column(length = 1024)
	private String nom;
	
	@Column(length = 1024)
	private String prenom;	
	
	private String mail;
	
	private String tel1;
	private String tel2;
	
	private String password;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	@Column(name = "\"dateCreation\"")
	private Date dateCreation;
	
	@Column(name = "\"numeroVoie\"")
	private String numeroVoie;	
	
	@Column(name = "\"complNumeroVoie\"")
	private String complNumeroVoie;	
	
	@Column(name = "\"typeVoie\"")
	private String typeVoie;
	
	@Column(name = "\"nomVoie\"", length = 1024)
	private String nomVoie;	
	
	@Column(name = "\"complNomVoie\"", length = 1024)
	private String complNomVoie;
	
	private String cp;
	private String ville;	
	private String pays;	
	
	@OneToMany (mappedBy = "user",fetch = FetchType.LAZY)
	List<Commande> commandes = new ArrayList<Commande>();	//List<Commande> commandes = new ArrayList<Commande>();

	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable( name = "pointVente_User",
    			joinColumns = @JoinColumn (name = "id_user"),
                inverseJoinColumns = @JoinColumn( name = "\"id_pointVente\"" ) )
	private List<PointVente> pointsVente = new ArrayList<PointVente>();
	
	@Transient
	private List<Commande> commandesFiltered;
	
	public boolean hasCommandes() {
		if(this.commandes!=null)
			return this.commandes.size()>0;
		else
			return false;
	}
	
	public String getAdresseComplete() {
		String retVal="";
		
		if (this.nomVoie!=null) {
			retVal +=this.nomVoie;
		}
		if (this.complNomVoie!=null) {
			if(!retVal.isBlank())
				retVal+=" ";
			retVal+=this.complNomVoie;
		}
		if (this.cp!=null) {
			if(!retVal.isBlank())
				retVal+=" ";
			retVal+=this.cp;
		}
		if (this.ville!=null) {
			if(!retVal.isBlank())
				retVal+=" ";
			retVal+=this.ville;
		}
		if (this.pays!=null) {
			if(!retVal.isBlank())
				retVal+=" ";
			retVal+=this.pays;
		}
		return retVal;
	}
	
	public String getAdresse() {
		String retVal="";
		if (this.nomVoie!=null) {
			retVal +=this.nomVoie;
		}
		if (this.complNomVoie!=null) {
			if(!retVal.isBlank())
				retVal+=" ";
			retVal+=this.complNomVoie;
		}
		
		return retVal;
	}
	
	@Transient
	private Integer nbCmd;
	@Transient
	private Float totalCmd;
	
	public List<Long> getLstIdPtv(){
		List<Long> ret=new ArrayList<Long>();
		for (PointVente pt : this.pointsVente) {
			ret.add(pt.getId());
		}
		return ret;
	}
	public boolean hasPtv() {
		return (this.pointsVente.size()>0);
	}
	public String getStringPtv(){
		String ret="";
		for (PointVente pt : this.pointsVente) {
			if(ret.isBlank()==false)
				ret+=", ";
			ret+=pt.getLibelle();
		}
		return ret;
	}
}
