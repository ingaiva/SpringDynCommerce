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
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import data.Utilitys;
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
@Table(name = "promotion")
public class Promotion implements Serializable {
	private static final long serialVersionUID = 522186519939085373L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_promotion")
	private Long id;
	
	@Column(length = 2048)
	private String libelle;
	
	@Column(length = 2048)
	private String description;
	
	private String codePromo;
	private String pathLogo;
	
	@Lob @Column(columnDefinition="MEDIUMBLOB",nullable = true)
	private byte[] imgData;
	
	@Column( name = "\"newPrix\"")
	private Float newPrix;
	
	@Column( name = "\"pourcentageDiminution\"")
	private Integer pourcentageDiminution;
	
	@Column( name = "\"montantDiminution\"")
	private Float montantDiminution;
	
	@Column( name = "\"qteMin\"")
	private Float qteMin;
	
	@Column( name = "\"prixAdditionMin\"")
	private Float prixAdditionMin;
	
	@Column( name = "\"isUnitaire\"")
	private boolean isUnitaire;
	
	@OneToMany (mappedBy = "promo",fetch = FetchType.LAZY)
	List<PromotionActivation> promoActivations = new ArrayList<PromotionActivation>();	//List<PromotionActivation> promoActivations = new ArrayList<PromotionActivation>();

	
	@Transient
	public boolean hasLogo() {
		if (this.getImgData() != null) {
			return true;			
		}
		return false;
	}
	
	public String getLogoStr() {
		String retVal ="";
		if (this.getImgData()!=null) {			
			retVal="data:image/png;base64,"+Utilitys.getImgDataAs64String(this.getImgData());			
		}				
		return retVal;
	}
}
