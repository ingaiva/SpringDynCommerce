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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import data.Utilitys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter 
@Entity
@Table(name = "pointVente")
public class PointVente  implements Serializable  {
	private static final long serialVersionUID = -9003025264926360924L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_pointVente")
	private Long id;
	
	@Column(length = 1024)
	private String libelle;
	
	@Column(length = 2048)
	private String description;
	
	@Column(length = 2048)
	private String emplacementText;
	
	@Column(length = 2048)
	private String horairesText;
	
	@Column(name = "infoComp")
	private String infoComp;
	
	private String photoTitre;
	
	@Lob @Column(columnDefinition="MEDIUMBLOB",nullable = true)
	private byte[] photoData;
	
	@Column(columnDefinition = "tinyint(1) NOT NULL DEFAULT '1'")
	private  boolean isActif;
	
	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable( name = "pointVente_User",
    			joinColumns = @JoinColumn (name = "\"id_pointVente\""),
                inverseJoinColumns = @JoinColumn( name = "id_user" ) )
	private List<User> users = new ArrayList<User>();
	
	@OneToMany (mappedBy = "pointVente",fetch = FetchType.LAZY)
	List<Commande> commandes = new ArrayList<Commande>();	

	
	public boolean hasPhoto() {
		return this.getPhotoData()!=null;
	}
	
	public String getPhotoStrMd() {
		String retVal ="";
		if (this.getPhotoData()!=null) {		
			retVal="data:image/png;base64,"+Utilitys.getImgDataThAs64String(this.getPhotoData(),250,250);			
		}				
		return retVal;
	}
	public String getPhotoStrSm() {
		String retVal ="";
		if (this.getPhotoData()!=null) {		
			retVal="data:image/png;base64,"+Utilitys.getImgDataThAs64String(this.getPhotoData(),100,100);			
		}				
		return retVal;
	}
}
