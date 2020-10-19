package data.entitys;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import data.Utilitys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter //@ToString
@Entity
@Table(name = "ParamMainPage")
public class ParamMainPage implements Serializable{	
	private static final long serialVersionUID = -4128476649413823244L;

	@Id //@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_param")
	private Long id;
	
	@Column(name = "\"texteAccueil\"", length = 2048)
	private String texteAccueil;
	
	@Lob @Column(columnDefinition="MEDIUMBLOB",nullable = true)
	private byte[] logoData;
	
	private String logoPosition;
	
	private String logoTitre;
	
	@Lob @Column(columnDefinition="MEDIUMBLOB",nullable = true)
	private byte[] bgData;
	
	@OneToMany (mappedBy = "param",fetch = FetchType.LAZY)
	private List<Photo_param> photos = new ArrayList<Photo_param>();

	@Column(columnDefinition = "tinyint(1) NOT NULL DEFAULT '1'")
	private  boolean showCategories;
	
	private String promoTitre;
	
	@Column(columnDefinition = "tinyint(1) NOT NULL DEFAULT '1'")
	private boolean showPromo;
	
	private String statutPromoToShow;
	
	
	private String contactTitre;
	
	private String contactTel1;
	
	private String contactTel2;
	
	private String contactMail;

	private String horairesTitre;
	
	@Column(length = 2048)
	private String horairesText;

	@Transient
	public boolean hasContact() {
		if (this.getContactTel1() != null || this.getContactTel2()!= null || this.getContactMail()!= null) {		
			return true;			
		}
		return false;
	}

	@Transient
	public boolean hasHoraires() {
		if ( this.getHorairesText()!= null ) {		
			return true;			
		}
		return false;
	}
	
	@Transient
	public String logoPositionClass() {
		if (this.logoPosition!=null && this.logoPosition.equalsIgnoreCase("left")) 
			return "d-flex justify-content-start";//"mr-auto";		
		else if (this.logoPosition!=null && this.logoPosition.equalsIgnoreCase("right")) 
			return "d-flex justify-content-end";//"ml-auto";		
		else
			return "d-flex justify-content-center";//"mx-auto";
	}
	
	@Transient
	public boolean hasLogo() {
		if (this.getLogoData() != null) {
		
			return true;			
		}
		return false;
	}

	@Transient
	public boolean hasBG() {
		if (this.getBgData() != null) {
			return true;	
		}
		return false;
	}
	 
	
	
	public String getLogoStr() {
		String retVal ="";
		if (this.getLogoData()!=null) {			
			retVal="data:image/png;base64,"+Utilitys.getImgDataAs64String(this.getLogoData());			
		}				
		return retVal;
	}
	
	@Transient
	public String getBgStr() {
		String retVal ="";
		if (this.getBgData()!=null) {			
			retVal="data:image/png;base64,"+Utilitys.getImgDataAs64String(this.getBgData());			
		}				
		return retVal;		
	}

//	@Override
//	public String toString() {
//		return "ParamMainPage [" + (id != null ? "id=" + id + ", " : "")
//				+ (texteAccueil != null ? "texteAccueil=" + texteAccueil + ", " : "")
//				+ (logoData != null ? "logoData=" + logoData.length + ", " : "")
//				+ (logoPosition != null ? "logoPosition=" + logoPosition + ", " : "")
//				+ (logoTitre != null ? "logoTitre=" + logoTitre + ", " : "")
//				+ (bgData != null ? "bgData=" + bgData.length + ", " : "")
//				+ (photos != null ? "photos=" + photos.size() + ", " : "") + "showCategories=" + showCategories
//				+ ", showPromo=" + showPromo + ", "
//				+ (statutPromoToShow != null ? "statutPromoToShow=" + statutPromoToShow + ", " : "")
//				+ (contactTitre != null ? "contactTitre=" + contactTitre + ", " : "")
//				+ (contactTel1 != null ? "contactTel1=" + contactTel1 + ", " : "")
//				+ (contactTel2 != null ? "contactTel2=" + contactTel2 + ", " : "")
//				+ (contactMail != null ? "contactMail=" + contactMail + ", " : "")
//				+ (horairesTitre != null ? "horairesTitre=" + horairesTitre + ", " : "")
//				+ (horairesText != null ? "horairesText=" + horairesText : "") + "]";
//	}
//	
	
}
