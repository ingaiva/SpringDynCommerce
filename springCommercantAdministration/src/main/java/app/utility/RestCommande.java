package app.utility;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RestCommande {
	private Long id;	
	
	private Long idPtV;
	
	private String choixDateLivraison;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dateLivraison;
	
	private String action;
}
