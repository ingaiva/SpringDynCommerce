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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString
@Entity
@Table(name = "infoComp_Produit")
public class InfoCompProduit  implements Serializable {	
	private static final long serialVersionUID = -242127637566887577L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "\"id_infoCompProduit\"")
	private Long id;
	private String titre;
	
	@Column(length = 2048)
	private String description;
	
	private String pathLogo;
	
	@Lob @Column(columnDefinition="MEDIUMBLOB",nullable = true)
	private byte[] imgData;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_produit")
	private Produit produit;
	
	
	@OneToMany (mappedBy = "infoComp",fetch = FetchType.LAZY)
	List<LigneInfoCompProduit> lignesInfosComp = new ArrayList<LigneInfoCompProduit>();	//List<LigneInfoCompProduit> lignesInfosComp = new ArrayList<LigneInfoCompProduit>();


	@Override
	public String toString() {
		return "InfoCompProduit [" + (id != null ? "id=" + id + ", " : "")
				+ (titre != null ? "titre=" + titre + ", " : "")
				+ (description != null ? "description=" + description + ", " : "")
				+ (pathLogo != null ? "pathLogo=" + pathLogo + ", " : "")
				+ (produit != null ? "produit=" + produit.getLibelle() + ", " : "")
				+ (lignesInfosComp != null ? "lignesInfosComp=" + lignesInfosComp.size() : "") + "]";
	}


	public InfoCompProduit(Produit produit) { 		
		this.produit = produit;
	}
	
	public boolean isEmpty() {
		if (this.titre.isBlank() && this.description.isBlank() && this.pathLogo.isBlank()) {
			if (this.lignesInfosComp.size()>0) {
				for (LigneInfoCompProduit ligne : this.lignesInfosComp) {
					if (! ligne.isEmpty()) {
						return false;
					}
				}
				return true;
			}
			else
				return true;
		}
		else
			return false;
	}
}
