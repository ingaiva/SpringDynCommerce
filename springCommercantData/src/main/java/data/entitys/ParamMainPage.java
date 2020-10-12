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
import javax.persistence.Lob;
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
@Getter @Setter @ToString
@Entity
@Table(name = "ParamMainPage")
public class ParamMainPage implements Serializable{	
	private static final long serialVersionUID = -4128476649413823244L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_param")
	private Long id;
	
	private String texteAccueil;
	
	@Lob @Column(columnDefinition="MEDIUMBLOB",nullable = true)
	private byte[] logoData;
	
	private String logoPosition;
	
	private String logoTitre;
	
	@Lob @Column(columnDefinition="MEDIUMBLOB",nullable = true)
	private byte[] bgData;
	
	@OneToMany (mappedBy = "param",fetch = FetchType.LAZY)
	private List<Photo_param> photos = new ArrayList<Photo_param>();

	private boolean showCategories;
	
	private boolean showPromo;
	
	private String statutPromoToShow;
}
