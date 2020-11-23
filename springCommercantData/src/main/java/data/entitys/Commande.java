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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter //@ToString
@Entity
@Table(name = "commande")
public class Commande  implements Serializable  {	
	private static final long serialVersionUID = 2051080830230557740L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_commande")
	private Long id;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	private Date date;
	
	@Column(name = "infoComp")
	private String infoComp;
	
	@Column(name = "msgCommercant", length = 2048)
	private String msgCommercant;

	private String statut;
	
	@Column(name = "totalSansPromo")
	private Float totalSansPromo;	
	
	@Column(name = "totalReductionStandard")
	private Float totalReductionStandard;
	
	@Column(name = "reductionSpeciale")
	private Float reductionSpeciale;	
	
	@Column(name = "totalFinal")
	private Float totalFinal;	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_user")
	private User user;
	
	@OneToMany (mappedBy = "commande",fetch = FetchType.LAZY)
	List<CommandeProduit> lignesCommandeProduit = new ArrayList<CommandeProduit>();	//List<CommandeProduit> lignesCommandeProduit = new ArrayList<CommandeProduit>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pointVente")
	private PointVente pointVente;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	private Date dateLivraison;
	
	public enum StatutCommande {
		EnAttente, Valide, Finalise, Annule, AnnuleParCommercant;
	}
	@Transient
	public String getCssClassStatut() {
		if (this.getStatut()!=null) {
			if (this.getStatut().equals(StatutCommande.EnAttente.toString())) 
				return "etiquetteJaune";
			else if (this.getStatut().equals(StatutCommande.Valide.toString()))
				return "etiquetteVerte";
			else if (this.getStatut().equals(StatutCommande.Finalise.toString()))
				return "etiquetteGrise";
			else if (this.getStatut().equals(StatutCommande.Annule.toString()))
				return "etiquetteRouge";
			else if (this.getStatut().equals(StatutCommande.AnnuleParCommercant.toString())) 
				return "etiquetteRouge";		
			else
				return "etiquetteJaune";			
		}
		else
			return "etiquetteJaune";		
	}
	public static String getStatutLibelle(String statutValue) {
		if (statutValue!=null) {
			if (statutValue.equals(StatutCommande.EnAttente.toString())) 
				return "En attente de validation";
			else if (statutValue.equals(StatutCommande.Valide.toString()))
				return "Validée";
			else if (statutValue.equals(StatutCommande.Finalise.toString()))
				return "Finalisée";
			else if (statutValue.equals(StatutCommande.Annule.toString()))
				return "Annulée";
			else if (statutValue.equals(StatutCommande.AnnuleParCommercant.toString())) 
				return "Annulée par commerçant";		
			else
				return statutValue;			
		}
		else
			return statutValue;	
	}

	public void setStatutCmd(StatutCommande newStatut) {
		this.statut=newStatut.toString();		
	}
	@Transient
	public boolean allowEditLimited() {
		return this.allowEdit() || this.isValide();
	}
	@Transient
	public boolean allowEdit() {
		return this.statut.equalsIgnoreCase(StatutCommande.EnAttente.toString());
	}
	@Transient
	public boolean allowDelete() {
		return this.statut.equalsIgnoreCase(StatutCommande.EnAttente.toString());
	}
	@Transient
	public boolean allowCancel() {
		return this.statut.equalsIgnoreCase(StatutCommande.Valide.toString());
	}
	@Transient
	public boolean isValide() {
		return this.statut.equalsIgnoreCase(StatutCommande.Valide.toString());
	}
	@Transient
	public boolean isEnAttente() {
		return this.statut.equalsIgnoreCase(StatutCommande.EnAttente.toString());
	}
	@Transient
	public boolean isCanceled() {
		return this.statut.equalsIgnoreCase(StatutCommande.Annule.toString()) || this.statut.equalsIgnoreCase(StatutCommande.AnnuleParCommercant.toString());
	}
	@Transient
	public boolean isCanceledByAdmin() {
		return this.statut.equalsIgnoreCase(StatutCommande.AnnuleParCommercant.toString());
	}
	@Transient
	public boolean isFinalise() {
		return this.statut.equalsIgnoreCase(StatutCommande.Finalise.toString());
	}
	public Float calculeTotal() {
		Float total = 0f;
		for (CommandeProduit cp : this.getLignesCommandeProduit()) {
			total += cp.calculeTotalProduit();
		}
		return total;
	}
	
	public Float calculeTotalReductionStandard() {
		Float total = 0f;
		for (CommandeProduit cp : this.getLignesCommandeProduit()) {
			total += cp.calculeTotalReduction();
		}		
		return total;
	}
	public void calculeTotaux() {
		this.setTotalSansPromo(this.calculeTotal());
		this.setTotalReductionStandard(this.calculeTotalReductionStandard());
		if(this.reductionSpeciale==null)
			this.reductionSpeciale=0f;
		this.totalFinal=this.totalSansPromo - calculeTotalReductionAll();
		if (this.totalFinal < 0) 
			this.totalFinal=0f;		
	}
	public Float calculeTotalReductionAll() {
		if(this.reductionSpeciale==null)
			this.reductionSpeciale=0f;
		if(this.totalReductionStandard==null)
			this.totalReductionStandard=0f;
		return	(this.totalReductionStandard + this.reductionSpeciale);
	}
	@Transient
	public String getPointVenteString() {
		if(this.pointVente!=null) 
			return this.pointVente.getLibelle();		
		else
			return "";
	}
}
