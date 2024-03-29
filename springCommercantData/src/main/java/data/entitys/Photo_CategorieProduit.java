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
@Table(name = "photo_CategorieProduit")
public class Photo_CategorieProduit   implements Serializable  {
	private static final long serialVersionUID = 8448098959951279990L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_photo")
	private Long id;
	
	@Column(columnDefinition = "integer default 1")
	private Integer ordre;
	
	@Column(name = "pathPhoto")
	private String pathPhoto;
	
	private String legende;
	
	@Lob @Column(columnDefinition="MEDIUMBLOB",nullable = true)
	private byte[] imgData;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categorieProduit")
	private CategorieProduit categorieProduit;

	public Photo_CategorieProduit(CategorieProduit categorieProduit) {
		this.categorieProduit = categorieProduit;
	}
	
	@Transient
	public String getImageStr() {
		return Utilitys.getImgDataAs64String(this.getImgData());
	}
	
	@Transient
	public String getImageThStr() {
		return Utilitys.getImgDataThAs64String(this.getImgData());
	}

	@Transient
	public String getImageThStrMd() {
		return Utilitys.getImgDataThAs64String(this.getImgData(),300,300);
	}
	
	@Override
	public String toString() {
		return "Photo_CategorieProduit [" + (id != null ? "id=" + id + ", " : "")
				+ (ordre != null ? "ordre=" + ordre + ", " : "")
				+ (pathPhoto != null ? "pathPhoto=" + pathPhoto + ", " : "")
				+ (legende != null ? "legende=" + legende : "") + "]";
	}
	
	
}
