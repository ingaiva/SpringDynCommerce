package data.entitys;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
@Table(name = "ligne_InfoComp_Produit")
public class LigneInfoCompProduit implements Serializable  {	
	private static final long serialVersionUID = 7491031244112567782L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "\"id_ligneInfoCompProduit\"")
	private Long id;
	private String titre;
	
	@Column(length = 2048)
	private String description;
	
	@Lob @Column(columnDefinition="MEDIUMBLOB",nullable = true)
	private byte[] imgData;
	
	@Column(name = "\"pathLogo\"")
	private String pathLogo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "\"id_infoCompProduit\"")
	private InfoCompProduit infoComp;
	
	public boolean isEmpty() {
		if (this.titre.isBlank()&& this.description.isBlank() && this.pathLogo.isBlank()) 
			return true;		
		else
			return false;
	}

	public LigneInfoCompProduit(InfoCompProduit infoComp) {		
		this.infoComp = infoComp;
	}

	@Override
	public String toString() {
		return "LigneInfoCompProduit [" + (id != null ? "id=" + id + ", " : "")
				+ (titre != null ? "titre=" + titre + ", " : "")
				+ (description != null ? "description=" + description + ", " : "")
				+ (pathLogo != null ? "pathLogo=" + pathLogo : "") + "]";
	}
	
}
