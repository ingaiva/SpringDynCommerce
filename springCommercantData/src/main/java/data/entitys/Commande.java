package data.entitys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import org.springframework.format.annotation.DateTimeFormat;

import data.entitys.Produit.StatutProduit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
	Set<CommandeProduit> lignesCommandeProduit = new HashSet<CommandeProduit>();	//List<CommandeProduit> lignesCommandeProduit = new ArrayList<CommandeProduit>();

	
	public enum StatutCommande {
		EnAttente, Valide, Finalise, Annule;
	}
	
	public static String getStatutLibelle(String statutValue) {
		if (statutValue.equals(StatutCommande.EnAttente.toString())) 
			return "En attente de validation";
		else if (statutValue.equals(StatutCommande.Valide.toString()))
			return "Validée";
		else if (statutValue.equals(StatutCommande.Finalise.toString()))
			return "Finalisée";
		else if (statutValue.equals(StatutCommande.Annule.toString()))
			return "Annulée";
		else
			return statutValue;
	}

	public void setStatut(StatutCommande newStatut) {
		this.statut=newStatut.toString();		
	}
	
	public Float getTotal() {
		Float total = 0f;
		for (CommandeProduit cp : this.getLignesCommandeProduit()) {
			total += cp.getTotalProduit();
		}
		return total;
	}
}
