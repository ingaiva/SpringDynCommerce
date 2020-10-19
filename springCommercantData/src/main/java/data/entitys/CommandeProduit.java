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
	
	public Float getTotalProduit() {
		if (this.produit!=null && this.produit.getPrix()!=null && this.getQte()!=null) {
			return	(float) (Math.round((this.produit.getPrix() * this.getQte()) * 100) / 100);
			
		}
		else
			return 0f;
	}	
}
