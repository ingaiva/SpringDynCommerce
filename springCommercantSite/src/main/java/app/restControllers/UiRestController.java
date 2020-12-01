package app.restControllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import data.entitys.Commande;
import data.entitys.CommandeProduit;
import data.entitys.PanierWrapper;
import data.entitys.Produit;

@RestController
public class UiRestController {
	@Autowired
	data.repositorys.RepoProduit prodR;
	@Autowired
	data.repositorys.RepoPhoto_Produit photoPR;
	
	@Autowired
	data.repositorys.RepoCommande cmdR;
	
	@Autowired
	data.repositorys.RepoCommandeProduit lignesCmdR;
	
	@PostMapping("/setProduitPanier")
	public ResponseEntity<Object> setProduitPanier(@RequestBody Produit prod, HttpSession session) {

		if (prod != null) {
			PanierWrapper wpanier = (PanierWrapper) session.getAttribute("panier");
			if (wpanier == null)
				wpanier = new PanierWrapper();
			if (prod.getId() != null && prod.getQte() != null) {
				for (Produit elt : wpanier.getProduits()) {
					if (elt.getId().equals(prod.getId())) {
						elt.setQte(prod.getQte());						
						break;
					}
				}
			}
			session.setAttribute("panier", wpanier);
			ResponseEntity<Object> response = ResponseEntity.ok().body(prod.getTotalProduit());// new
																								// ResponseEntity<>(prod.getTotalProduit(),
																								// HttpStatus.OK);
			return response;// ResponseEntity.ok().body(prod.getTotalProduit());

		} else
			return ResponseEntity.badRequest().body("Produit n'est pas trouvé");
	}
	
	@PostMapping("/removeProduitPanier")
	public ResponseEntity<Object> removeProduitPanier(@RequestBody Produit prod, HttpSession session) {
		
		if(prod!=null) {
			PanierWrapper wpanier = (PanierWrapper) session.getAttribute("panier");
			if (wpanier == null)
				wpanier = new PanierWrapper();	
			
			if(prod.getId()!=null) {
				for (Produit elt : wpanier.getProduits()) {
					if (elt.getId().equals(prod.getId())) {
						wpanier.getProduits().remove(elt);
						//System.out.println("removed " + elt.getId());
						break;
					}
				}
			}
			session.setAttribute("panier", wpanier);
			ResponseEntity<Object> response=ResponseEntity.ok().body(wpanier.getProduits().size());//new ResponseEntity<>(prod.getTotalProduit(), HttpStatus.OK);
			return response;
			
		}
			
		else
			return ResponseEntity.badRequest().body("Produit n'est pas trouvé");
	}
	
	
	@GetMapping("/getPanierNombreProd")
	public ResponseEntity<Object> getPanierNombreProd(HttpSession session){
		// Mise à jour du panier pour enlever les produits avec qte==0
		PanierWrapper sessionPanier = (PanierWrapper) session.getAttribute("panier");
		if (sessionPanier != null) {
			List<Produit> lstToRemove = new ArrayList<Produit>();				
			for (Produit elt : sessionPanier.getProduits()) {
				if (elt.getQte()==0)
					lstToRemove.add(elt);
			}
			
			if(lstToRemove.size()>0) {
				for (Produit pToRemove : lstToRemove) 
					sessionPanier.getProduits().remove(pToRemove);				
				session.setAttribute("panier", sessionPanier);
			}
			
			ResponseEntity<Object> response=ResponseEntity.ok().body(sessionPanier.getProduits().size());//new ResponseEntity<>(prod.getTotalProduit(), HttpStatus.OK);
			return response;
		}
		return ResponseEntity.ok().body(0);
	}
	
	@PostMapping("/getTotalPanier")
	public ResponseEntity<Object> getTotalPanier(@RequestBody PanierWrapper panier, HttpSession session) {

		if (panier != null) {

			ResponseEntity<Object> response = ResponseEntity.ok().body(panier.getTotal());

			return response;

		}

		else
			return ResponseEntity.badRequest().body("Panier n'est pas trouvé");
	}
	
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
				
		if(cmd!=null) {
			if(cmd.getId()!=null && cmdR.existsById(cmd.getId())) {
				
				Commande cmdFromDB = cmdR.getOne(cmd.getId());
				cmd.setReductionSpeciale(cmdFromDB.getReductionSpeciale());
			}
			
			cmd.calculeTotaux();			
			System.out.println(" calculeTotalCmd: " + cmd.getTotalFinal());
			ResponseEntity<Object> response=ResponseEntity.ok().body(cmd);//new ResponseEntity<>(prod.getTotalProduit(), HttpStatus.OK);
			
			return response;			
		}
		else
			return ResponseEntity.badRequest().body("cmd n'est pas trouvé");//.body("Produit n'est pas trouvé");//new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}
	
	/*
	 * @PostMapping("/removeProduitCmd") public ResponseEntity<Object>
	 * removeProduitCmd(@RequestBody CommandeProduit ligneProd) { if (ligneProd !=
	 * null && ligneProd.getId()!=null ) { Long idCmd=lignesCmdR.
	 * getIdCommande(ligneProd.getId());
	 * 
	 * if( idCmd!=null && cmdR.existsById(idCmd)) {
	 * System.out.println("removeProduitCmd : " + ligneProd.getId()+ " cmd: "+
	 * idCmd);
	 * 
	 * lignesCmdR.deleteById(ligneProd.getId()); Commande cmdFromDB =
	 * cmdR.findById(idCmd).get(); cmdFromDB.calculeTotaux(); ResponseEntity<Object>
	 * response=ResponseEntity.ok().body(cmdFromDB); return response; } }
	 * 
	 * return ResponseEntity.badRequest().body("cmd n'est pas trouvé"); }
	 */
	
	
	@PostMapping("/addProdExpress")
	public ResponseEntity<Object> ajouterProduitPanier(@RequestBody Produit prod, HttpSession session) {	
		
		PanierWrapper wpanier = (PanierWrapper) session.getAttribute("panier");
		if (wpanier == null)
			wpanier = new PanierWrapper();	

		if (prod != null && prod.getId()!=null) {

			Long idProduit = prod.getId();
			Float qte=1f;
			Produit curProduit = prodR.getOne(idProduit);
			if (curProduit != null) {
				Float stock = null;
				if (!curProduit.isSurCommande() && !curProduit.isEpuise() && curProduit.getStock() != null)/* curProduit.setStock(0f); */
					stock = curProduit.getStock() - qte;

				if (stock!=null &&  stock < 0) {
					
				} else {
					boolean isExist = false;
					for (Produit elt : wpanier.getProduits()) {
						if (elt.getId().equals(idProduit)) {
							isExist = true;
							Float previousQte = elt.getQte();
							elt.setQte(previousQte + qte);
						}
					}

					if (!isExist) {
						curProduit.setQte(qte);
						curProduit.setPhotos(photoPR.getByProduit(idProduit));
						wpanier.getProduits().add(curProduit);

					}

					session.setAttribute("panier", wpanier);
					ResponseEntity<Object> response=ResponseEntity.ok().body(wpanier.getProduits().size());
					return response;//ResponseEntity.ok().body("Produit ajouté dans le panier");
				}
			}
		}

		System.out.println("---------on va dans badRequest....---");
		return ResponseEntity.badRequest().body("Produit n'est pas trouvé");
	}
}
