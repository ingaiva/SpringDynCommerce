package data.entitys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
@Getter @Setter // @ToString
@Entity
@Table(name = "categorie_Produit")
public class CategorieProduit  implements Serializable {		
	private static final long serialVersionUID = -6473789559708122434L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_categorieProduit")
	private Long id;
	private String titre;
	
	@Column(length = 2048)
	private String description;
	
	@OneToMany (mappedBy = "categorieProduit",fetch = FetchType.LAZY)
	List<Produit> produits = new ArrayList<Produit>();
	
	@OneToMany (mappedBy = "categorieProduit",fetch = FetchType.LAZY)   
	private List<Photo_CategorieProduit> photos = new ArrayList<Photo_CategorieProduit>();

	@Override
	public String toString() {
		return "CategorieProduit [" + (id != null ? "id=" + id + ", " : "")
				+ (titre != null ? "titre=" + titre + ", " : "")
				+ (description != null ? "description=" + description : "") + "]";
	}
	
	public String defPhotoData() {
		Photo_CategorieProduit mainPhoto=null;
		for (Photo_CategorieProduit photo : photos) {
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
		Photo_CategorieProduit mainPhoto=null;
		for (Photo_CategorieProduit photo : photos) {
			if (mainPhoto==null) 
				mainPhoto=photo;
			else if (mainPhoto.getOrdre()> photo.getOrdre()) 
				mainPhoto=photo;
			}
		if (mainPhoto!=null) {			
			return "data:image/png;base64,"+ mainPhoto.getImageThStrMd();
		}
		return "";
	}
	
}
