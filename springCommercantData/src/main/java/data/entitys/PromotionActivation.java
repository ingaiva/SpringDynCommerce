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
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//@Data
@AllArgsConstructor
@Getter @Setter //@ToString
@Entity
@Table(name = "\"promotionActivation\"")
public class PromotionActivation  implements Serializable {	
	private static final long serialVersionUID = 3220710871571683738L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "\"id_promotionActivation\"")
	private Long id;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Column(name = "\"dateDebut\"")
	private Date dateDebut;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Column(name = "\"dateFin\"")
	private Date dateFin;
	
	private String statut;// liste des statuts
	private Integer priorite;// a afficher sur la page home ou pas, etc
	private Integer ordre;
	
	@Lob @Column(columnDefinition="MEDIUMBLOB",nullable = true)
	private byte[] imgData;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_promotion")
	private Promotion promo;
	
	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable( name = "promotionActivation_Produit",
    			joinColumns = @JoinColumn (name = "\"id_promotionActivation\""),
                inverseJoinColumns = @JoinColumn( name = "id_produit" ) )
	private Set<Produit> produits = new HashSet<Produit>();	//private List<Produit> produits = new ArrayList<Produit>();
	
	@OneToMany (mappedBy = "promoCommande",fetch = FetchType.LAZY)
	Set<CommandeProduit> lignesCommandeProduit = new HashSet<CommandeProduit>();	//List<CommandeProduit> lignesCommandeProduit = new ArrayList<CommandeProduit>();
}
