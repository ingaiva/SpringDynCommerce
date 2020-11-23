package app.restControllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import data.entitys.Commande;
import data.entitys.CommandeProduit;
import data.entitys.PanierWrapper;
import data.entitys.Produit;

@RestController
public class ProduitRestController {
	@Autowired
	data.repositorys.RepoProduit prodR;
	@Autowired
	data.repositorys.RepoPhoto_Produit photoPR;
	
	@PostMapping("/setProduitPanier")
	public ResponseEntity<Object> setProduitPanier(@RequestBody Produit prod, HttpSession session) {
//		System.out.println(prod);

		if(prod!=null) {
			PanierWrapper wpanier = (PanierWrapper) session.getAttribute("panier");
			if (wpanier == null)
				wpanier = new PanierWrapper();	
			if(prod.getId()!=null && prod.getQte()!=null) {
				for (Produit elt : wpanier.getProduits()) {
					if (elt.getId().equals(prod.getId())) {
						elt.setQte(prod.getQte());
						break;
					}
				}
			}
			session.setAttribute("panier", wpanier);
			ResponseEntity<Object> response=ResponseEntity.ok().body(prod.getTotalProduit());//new ResponseEntity<>(prod.getTotalProduit(), HttpStatus.OK);
//			System.out.println(response);
			return response;//ResponseEntity.ok().body(prod.getTotalProduit());
			
		}
			//return prod.getTotalProduit();
		else
			return ResponseEntity.badRequest().body("Produit n'est pas trouvé");//.body("Produit n'est pas trouvé");//new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}
	
	@PostMapping("/removeProduitPanier")
	public ResponseEntity<Object> removeProduitPanier(@RequestBody Produit prod, HttpSession session) {
		System.out.println("removeProduitPanier");

		if(prod!=null) {
			PanierWrapper wpanier = (PanierWrapper) session.getAttribute("panier");
			if (wpanier == null)
				wpanier = new PanierWrapper();	
			
			if(prod.getId()!=null) {
				for (Produit elt : wpanier.getProduits()) {
					if (elt.getId().equals(prod.getId())) {
						wpanier.getProduits().remove(elt);
						System.out.println("removed " + elt.getId());
						break;
					}
				}
			}
			session.setAttribute("panier", wpanier);
			ResponseEntity<Object> response=ResponseEntity.ok().body(wpanier.getProduits().size());//new ResponseEntity<>(prod.getTotalProduit(), HttpStatus.OK);
//			System.out.println(response);
			return response;//ResponseEntity.ok().body(prod.getTotalProduit());
			
		}
			//return prod.getTotalProduit();
		else
			return ResponseEntity.badRequest().body("Produit n'est pas trouvé");//.body("Produit n'est pas trouvé");//new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}
	
	@PostMapping("/getTotalProduitCmd")
	public ResponseEntity<Object> getTotalProduit(@RequestBody CommandeProduit ligneProd) {
//		System.out.println(ligneProd);

		if(ligneProd!=null) {
			ResponseEntity<Object> response=ResponseEntity.ok().body(ligneProd.calculeTotalProduit());//new ResponseEntity<>(prod.getTotalProduit(), HttpStatus.OK);
//			System.out.println(response);
			return response;//ResponseEntity.ok().body(prod.getTotalProduit());			
		}			
		else
			return ResponseEntity.badRequest().body("Produit n'est pas trouvé");//.body("Produit n'est pas trouvé");//new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}
	
	@PostMapping("/calculeTotalCmd")
	public ResponseEntity<Object> calculeTotalCmd(@RequestBody Commande cmd) {
		System.out.println("dans calculeTotalCmd:");
		System.out.println("nb lignes " + cmd.getLignesCommandeProduit().size());
		
		if(cmd!=null) {
			cmd.calculeTotaux();
			ResponseEntity<Object> response=ResponseEntity.ok().body(cmd);//new ResponseEntity<>(prod.getTotalProduit(), HttpStatus.OK);
			System.out.println(response);
			return response;			
		}
			//return prod.getTotalProduit();
		else
			return ResponseEntity.badRequest().body("cmd n'est pas trouvé");//.body("Produit n'est pas trouvé");//new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}
	
	@PostMapping("/getTotalPanier")
	public ResponseEntity<Object> getTotalPanier(@RequestBody PanierWrapper panier) {
//		System.out.println("dans getTotalPanier:");
		/*
		 * System.out.println(panier); System.out.println("total elt:" +
		 * panier.getProduits().size());
		 */
		if(panier!=null) {
			ResponseEntity<Object> response=ResponseEntity.ok().body(panier.getTotal());//new ResponseEntity<>(prod.getTotalProduit(), HttpStatus.OK);
//			System.out.println(response);
			return response;//ResponseEntity.ok().body(prod.getTotalProduit());
			
		}
			//return prod.getTotalProduit();
		else
			return ResponseEntity.badRequest().body("Panier n'est pas trouvé");//.body("Produit n'est pas trouvé");//new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}
	
	
	@PostMapping("/addProdExpress")
	public ResponseEntity<Object> ajouterProduitPanier(@RequestBody Produit prod, HttpSession session) {		
//		System.out.println("addProdExpress");
		
		
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
//							System.out.println(elt.getLibelle() + " trouvé, qte augmentée" );
						}
					}

					if (!isExist) {
						curProduit.setQte(qte);
						curProduit.setPhotos(photoPR.getByProduit(idProduit));
						wpanier.getProduits().add(curProduit);
//						System.out.println(curProduit.getLibelle() + " ajouté, qte augmentée" );
					}

					session.setAttribute("panier", wpanier);
					ResponseEntity<Object> response=ResponseEntity.ok().body(wpanier.getProduits().size());
//					System.out.println(response);
					return response;//ResponseEntity.ok().body("Produit ajouté dans le panier");
				}
			}
		}

		System.out.println("---------on va dans badRequest....---");
		return ResponseEntity.badRequest().body("Produit n'est pas trouvé");
	}
}
