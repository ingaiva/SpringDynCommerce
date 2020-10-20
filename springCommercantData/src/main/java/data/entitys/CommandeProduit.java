package data.entitys;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter //@ToString
@Entity
@Table(name = "commande_Produit")
public class CommandeProduit   implements Serializable  {
	private static final long serialVersionUID = -6806117569995107106L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "\"id_commandeProduit\"")
	private Long id;
	private Float qte;
	private Float total;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_commande")
	private Commande commande;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_produit")
	private Produit produit;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"id_promotionActivation\"")
	private PromotionActivation promoCommande;

	public CommandeProduit(Produit produit) {
		this.produit = produit;
		this.qte=produit.getQte();
	}
	
	public Float calculeTotalProduit() {
		if (this.produit!=null && this.produit.getPrix()!=null && this.getQte()!=null) {
			return	this.produit.getPrix() * this.getQte();
			
		}
		else
			return 0f;
	}	
	
	public Float calculeTotalReduction() {
		if (this.produit!=null && this.produit.getPrix()!=null && this.getQte()!=null) {
			return	0f;//pour l'instant...
			
		}
		else
			return 0f;
	}	
	
	public void calculeTotaux() {
		this.total=this.calculeTotalProduit() - this.calculeTotalReduction();
		if (this.total < 0) 
			this.total = 0f;		
	}
}
