package data.entitys;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import data.Utilitys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter //@ToString
@Entity
@Table(name = "produit")
public class Produit  implements Serializable  {	
	private static final long serialVersionUID = -7666937523617575644L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_produit")
	private Long id;
	
	@Column(length = 2048)
	private String libelle;
	
	@Column(length = 2048)
	private String description;
	
	private String conditionnement;
	private Float prix;
	private Float stock;
	private String statut;//liste des statuts
	private Integer delai;
	private String uniteDelai;
	
	@Transient
	private Float qte;
	
	public Float getTotalProduit() {
		if (this.prix!=null && this.qte!=null) {
			return	this.getPrix() * this.getQte();			
		}
		else
			return 0f;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"id_categorieProduit\"")
	private CategorieProduit categorieProduit;

	@OneToMany (mappedBy = "produit",fetch = FetchType.LAZY)
	List<InfoCompProduit> lstInfoComp = new ArrayList<InfoCompProduit>();
	//Set<InfoCompProduit> lstInfoComp = new HashSet<InfoCompProduit>();

	@OneToMany (mappedBy = "produit",fetch = FetchType.LAZY)
	private List<Photo_Produit> photos = new ArrayList<Photo_Produit>();//private List<Photo_Produit> photos = new ArrayList<Photo_Produit>();

	
	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable( name = "promotionActivation_Produit",
    			joinColumns = @JoinColumn (name = "id_produit"),
                inverseJoinColumns = @JoinColumn( name = "\"id_promotionActivation\"" ))//
	private Set<PromotionActivation> promoActivations = new HashSet<PromotionActivation>();	//private List<PromotionActivation> promoActivations = new ArrayList<PromotionActivation>();
	
	
	@OneToMany (mappedBy = "produit",fetch = FetchType.LAZY)
	Set<CommandeProduit> lignesCommandeProduit = new HashSet<CommandeProduit>();	//List<CommandeProduit> lignesCommandeProduit = new ArrayList<CommandeProduit>();


	public enum StatutProduit {
		Disponible, SurCommande, Epuise, NonDefini;
	}
	
	public enum UniteDelaiApprovisionnnement {
		H,J;
	}
	public static String getUniteDelaiLibelle(String unite) {
		if (unite.equals(UniteDelaiApprovisionnnement.H.toString())) 
			return "heures";
		else if (unite.equals(UniteDelaiApprovisionnnement.J.toString())) 
			return "jours";
		else
			return unite;
	}
	public Integer calculeDelaisJours() {
		Integer ret=0;
		if (this.isSurCommande() && this.getDelai()!=null && this.getDelai()>0) {
			if(this.getUniteDelai()!=null) {
				if(this.getUniteDelai().equals(UniteDelaiApprovisionnnement.J.toString())) {
					ret=this.getDelai();
				}
				else if(this.getUniteDelai().equals(UniteDelaiApprovisionnnement.H.toString())) {
					ret=Math.round(this.getDelai()/24);
				}
			}
		}
		return ret;
	}
	public String getDelaisSurCommande() {
		if (this.isSurCommande() && this.getDelai()!=null && this.getDelai()>0) {
			String ret="disponible en " + this.getDelai();
			if(this.getUniteDelai()!=null && this.getUniteDelai().length()>0)
				ret+=" " + getUniteDelaiLibelle(this.getUniteDelai());
			else
				ret+=" j/h";
			return ret;
		}
		else
			return "";
	}
	public static String getStatutSurCommandeLibelle() {
		return getStatutLibelle(StatutProduit.SurCommande.toString());
	}
	public static String getStatutLibelle(String statutValue) {
		if (statutValue.equals(StatutProduit.Disponible.toString())) 
			return "Disponible";
		else if (statutValue.equals(StatutProduit.Epuise.toString()))
			return "Epuisé";
		else if (statutValue.equals(StatutProduit.NonDefini.toString()))
			return "Aucun statut";
		else if (statutValue.equals(StatutProduit.SurCommande.toString()))
			return "Sur Commande";
		else
			return statutValue;
	}
	public boolean isSurCommande() {
		return (this.statut!=null && this.statut.equalsIgnoreCase(StatutProduit.SurCommande.toString()));
	}
	
	public boolean isEpuise() {
		return (this.statut!=null && this.statut.equalsIgnoreCase(StatutProduit.Epuise.toString()));
	}
	
	public boolean isAucunStatut() {
		return (this.statut==null || this.statut.equalsIgnoreCase(StatutProduit.NonDefini.toString()));
	}
	
	public Produit(String libelle, String description, String conditionnement, Float prix, Float stock) {
		this.libelle = libelle;
		this.description = description;
		this.conditionnement = conditionnement;
		this.prix = prix;
		this.stock = stock;
	}

//	@Override
//	public String toString() {
//		return "Produit [" + (id != null ? "id=" + id + ", " : "")
//				+ (libelle != null ? "libelle=" + libelle + ", " : "")
//				+ (description != null ? "description=" + description + ", " : "")
//				+ (conditionnement != null ? "conditionnement=" + conditionnement + ", " : "")
//				+ (prix != null ? "prix=" + prix + ", " : "") + (stock != null ? "stock=" + stock + ", " : "")
//				+ (statut != null ? "statut=" + statut + ", " : "")
//				+ (categorieProduit != null ? "categorieProduit=" + categorieProduit.getTitre() + ", " : "")
//				+ (lstInfoComp != null ? "lstInfoComp=" + lstInfoComp.size() : "") + "]";
//	}

	public String defPhotoData() {
		Photo_Produit mainPhoto=null;
		for (Photo_Produit photo : photos) {
			if (mainPhoto==null) 
				mainPhoto=photo;
			else if (mainPhoto.getOrdre()> photo.getOrdre()) 
				mainPhoto=photo;
			}
		if (mainPhoto!=null) {			
			return mainPhoto.getImageThStr();
		}
		return "";
	}
	public String defPhotoDataMd() {
		Photo_Produit mainPhoto=null;
		for (Photo_Produit photo : photos) {
			if (mainPhoto==null) 
				mainPhoto=photo;
			else if (mainPhoto.getOrdre()> photo.getOrdre()) 
				mainPhoto=photo;
			}
		if (mainPhoto!=null) {			
			return "data:image/png;base64," + mainPhoto.getImageThStrMd();
		}
		return "";
	}
	public String defPhotoDataSm() {
		Photo_Produit mainPhoto=null;
		for (Photo_Produit photo : photos) {
			if (mainPhoto==null) 
				mainPhoto=photo;
			else if (mainPhoto.getOrdre()> photo.getOrdre()) 
				mainPhoto=photo;
			}
		if (mainPhoto!=null) {			
			return "data:image/png;base64," + Utilitys.getImgDataThAs64String(mainPhoto.getImgData(),150,150);
		}
		return "";
	}	
	
}
