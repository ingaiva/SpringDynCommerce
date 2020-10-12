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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString
@Entity
@Table(name = "photo_param")
public class Photo_param  implements Serializable {	
	private static final long serialVersionUID = -2310189421082851894L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_photo")
	private Long id;
	
	@Column(columnDefinition = "integer default 1")
	private Integer ordre;

	@Column(name = "pathPhoto")
	private String pathPhoto;	
	
	
	@Lob @Column(columnDefinition="MEDIUMBLOB",nullable = true)
	private byte[] imgData;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_param")
	private ParamMainPage param;
		
}
