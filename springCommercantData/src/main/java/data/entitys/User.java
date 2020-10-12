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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@AllArgsConstructor
@Getter @Setter @ToString
@Entity
@Table(name = "user")
public class User implements Serializable {
	private static final long serialVersionUID = -3189658908940475488L;
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user")
	private Long id;
	
	@Column(length = 1024)
	private String nom;
	
	@Column(length = 1024)
	private String prenom;	
	
	private String mail;
	
	private String tel1;
	private String tel2;
	
	private String password;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	@Column(name = "\"dateCreation\"")
	private Date dateCreation;
	
	@Column(name = "\"numeroVoie\"")
	private String numeroVoie;	
	
	@Column(name = "\"complNumeroVoie\"")
	private String complNumeroVoie;	
	
	@Column(name = "\"typeVoie\"")
	private String typeVoie;
	
	@Column(name = "\"nomVoie\"", length = 1024)
	private String nomVoie;	
	
	@Column(name = "\"complNomVoie\"", length = 1024)
	private String complNomVoie;
	
	private String cp;
	private String ville;	
	private String pays;	
	
	@OneToMany (mappedBy = "user",fetch = FetchType.LAZY)
	Set<Commande> commandes = new HashSet<Commande>();	//List<Commande> commandes = new ArrayList<Commande>();

}
