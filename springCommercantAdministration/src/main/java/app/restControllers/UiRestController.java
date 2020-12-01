package app.restControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import data.entitys.Commande;
import data.entitys.CommandeProduit;

@RestController
public class UiRestController {
	@Autowired
	data.repositorys.RepoProduit prodR;	
	
	@Autowired
	data.repositorys.RepoCommande cmdR;
	
	
	@PostMapping("/getTotalProduitCmd")
	public ResponseEntity<Object> getTotalProduit(@RequestBody CommandeProduit ligneProd) {

		if(ligneProd!=null) {
			ResponseEntity<Object> response=ResponseEntity.ok().body(ligneProd.calculeTotalProduit());//new ResponseEntity<>(prod.getTotalProduit(), HttpStatus.OK);
			return response;//ResponseEntity.ok().body(prod.getTotalProduit());			
		}			
		else
			return ResponseEntity.badRequest().body("Produit n'est pas trouvé");
	}
	
	@PostMapping("/calculeTotalCmd")
	public ResponseEntity<Object> calculeTotalCmd(@RequestBody Commande cmd) {
		//Attention, methode differente du site commercial!!!!		
		if(cmd!=null) {
			/*
			 * if(cmd.getId()!=null && cmdR.existsById(cmd.getId())) { Commande cmdFromDB =
			 * cmdR.getOne(cmd.getId());
			 * cmd.setReductionSpeciale(cmdFromDB.getReductionSpeciale()); }
			 */
			
			cmd.calculeTotaux();
						
			ResponseEntity<Object> response=ResponseEntity.ok().body(cmd);//new ResponseEntity<>(prod.getTotalProduit(), HttpStatus.OK);
			
			return response;			
		}
		else
			return ResponseEntity.badRequest().body("cmd n'est pas trouvé");//.body("Produit n'est pas trouvé");//new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}
}
