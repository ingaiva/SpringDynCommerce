package app.restControllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import app.utility.RestCommande;
import data.entitys.Commande;
import data.entitys.CommandeProduit;
import data.entitys.PointVente;
import data.entitys.Commande.StatutCommande;

@RestController
public class UiRestController {
	@Autowired
	data.repositorys.RepoProduit prodR;	
	
	@Autowired
	data.repositorys.RepoCommande cmdR;
	
	@Autowired
	data.repositorys.RepoPointVente ptsVR;
	
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
	
	@PostMapping("/setStatutCmd") //
	public ResponseEntity<Object> setStatutCmd(@RequestBody RestCommande restCmd) {
		System.out.println("setStatutCmd");
		System.out.println(restCmd);
			if ( restCmd != null && restCmd.getId() != null && cmdR.existsById(restCmd.getId())) {
				Commande cmd = cmdR.getOne(restCmd.getId());

				if (restCmd.getAction() != null && restCmd.getAction().equalsIgnoreCase("validerCmd")) {
					String choixDateLivraison = restCmd.getChoixDateLivraison();
					Date dateLivraison = restCmd.getDateLivraison();

					if (restCmd.getIdPtV() != null && ptsVR.existsById(restCmd.getIdPtV())) {
						PointVente selectedPt = ptsVR.getOne(restCmd.getIdPtV());
						cmd.setPointVente(selectedPt);
						cmdR.updatePointVente(cmd.getId(), selectedPt);
						cmdR.updateStatut(cmd.getId(), StatutCommande.Valide.toString());

						if (choixDateLivraison != null) {
							Date dateLivraisonToSet = null;
							if (choixDateLivraison.equalsIgnoreCase("def")) {
								if (cmd.getDateChoixLivraison() != null) 
									dateLivraisonToSet = cmd.getDateChoixLivraison();
								else 
									dateLivraisonToSet = selectedPt.getNextDateValidation(cmd);
								
							} else if (choixDateLivraison.equalsIgnoreCase("input") && dateLivraison != null) {
								dateLivraisonToSet = dateLivraison;
							}
							cmdR.updateDateLivraison(cmd.getId(), dateLivraisonToSet);
						}

					}
				} else if (restCmd.getAction() != null && restCmd.getAction().equalsIgnoreCase("finaliserCmd")) {					
					cmdR.updateStatut(cmd.getId(), StatutCommande.Finalise.toString());
					cmd.setStatutCmd(StatutCommande.Finalise);
				} else if(restCmd.getAction() !=null && restCmd.getAction().equalsIgnoreCase("statutPrecedentCmd")) {
					data.entitys.Commande.StatutCommande statutToSet=StatutCommande.EnAttente;
					if(cmd.isValide())
						statutToSet=StatutCommande.EnAttente;					
					else if (cmd.isCanceled())
						statutToSet=StatutCommande.EnAttente;
					else if (cmd.isFinalise())
						statutToSet=StatutCommande.Valide;
					
					cmdR.updateStatut(cmd.getId(), statutToSet.toString());
				}

				ResponseEntity<Object> response=ResponseEntity.ok().body(restCmd);//				
				return response;
			
			} else
				return ResponseEntity.badRequest().body("cmd n'est pas trouvé");

		
	}
}
