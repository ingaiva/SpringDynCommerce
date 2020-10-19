package app.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import data.entitys.CategorieProduit;
import data.entitys.Commande;
import data.entitys.CommandeProduit;
import data.entitys.PanierWrapper;
import data.entitys.ParamMainPage;
import data.entitys.Produit;
import data.entitys.User;

@Controller
public class CommandeController {
	@Autowired
	data.repositorys.RepoUser usrR;
	
	@Autowired
	data.repositorys.RepoCommande cmdR;
	
	@Autowired
	data.repositorys.RepoProduit prodR;
	
	@Autowired
	data.repositorys.RepoCategorieProduit catR;
	
	@Autowired
	data.repositorys.RepoParamMainPage paramR;
	
	@Autowired
	data.repositorys.RepoPhoto_param phParamR;
	
	private ParamMainPage getParam(HttpSession session) {
		ParamMainPage param =(ParamMainPage) session.getAttribute("paramMP");	
		if (param==null) {			
			Optional<ParamMainPage> paramObj = paramR.findById(1L);
			if (!paramObj.isPresent()) {
				param = new ParamMainPage();
				param.setId(1L);
			} else {
				param = paramObj.get();
			}
			param.setPhotos(phParamR.getPhotos(1L));
			session.setAttribute("paramMP", param);
		}
		
		return param;
	}
	
	private void addStandardParams(Model model, HttpSession session) {
		PanierWrapper panier=(PanierWrapper) session.getAttribute("panier");	
		if (panier==null) 
			panier=new PanierWrapper();		
		model.addAttribute("panier", panier);
		
		User connectedCli=(User) session.getAttribute("connectedCli");		
		model.addAttribute("connectedCli", connectedCli);	
		getParam(session);
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();	
		model.addAttribute("lstCat", lstCat);		
	}
	
	private Produit findProduit(Long id, List<Produit> lstProduit) {
		for (Produit elt : lstProduit) {
			if (elt.getId().equals(id)) {
				return elt;
			}
		}
		return null;
	}
	
	/*
	 * public String validerPanier(Model model, @ModelAttribute("panier")
	 * PanierWrapper panierFromPage, HttpSession session) {
	 */
	//@PostMapping("/valPanier")
	public void validerPanier(PanierWrapper panierFromPage, HttpSession session) {	
		
		List<Produit> lstToRemoveFromPage = new ArrayList<Produit>();
		for (Produit elt : panierFromPage.getProduits()) {
			System.out.println(elt);
			if (elt.getId() == null || elt.getQte() == 0)
				lstToRemoveFromPage.add(elt);
		}
		for (Produit ptoRemove : lstToRemoveFromPage) {
			panierFromPage.getProduits().remove(ptoRemove);
		}

		PanierWrapper sessionPanier = (PanierWrapper) session.getAttribute("panier");
		if (sessionPanier == null)
			sessionPanier = new PanierWrapper();
		
		List<Produit> lstToRemove = new ArrayList<Produit>();
		for (Produit elt : sessionPanier.getProduits()) {
			if (findProduit(elt.getId(), panierFromPage.getProduits()) == null) 
				lstToRemove.add(elt);			
		}
		for (Produit pToRemove : lstToRemove) {
			sessionPanier.getProduits().remove(pToRemove);
		}

		for (Produit elt : panierFromPage.getProduits()) {
			Produit pExistant = findProduit(elt.getId(), sessionPanier.getProduits());
			if (pExistant != null) {
				pExistant.setQte(elt.getQte());
			}
		}

		session.setAttribute("panier", sessionPanier);

		//return "redirect:/accueil";
	}

	/*
	 * @PostMapping("/addProd") public String ajouterProduitPanier(Model
	 * model, @RequestParam(name = "qteToAdd") Float qte,
	 * 
	 * @ModelAttribute("produit") @Valid Produit produitToAdd, BindingResult
	 * bindingRes, HttpSession session,RedirectAttributes ra) {
	 * 
	 * System.out.println("dans post addProd " + qte);
	 * System.out.println(model.asMap()); System.out.println(produitToAdd);
	 * 
	 * PanierWrapper wpanier = (PanierWrapper) session.getAttribute("panier"); if
	 * (wpanier == null) wpanier = new PanierWrapper();
	 * 
	 * boolean hasError = false; if (qte <= 0) { hasError = true;
	 * model.addAttribute("msgErr",
	 * "La quantité du produit doit être supérieure à zéro"); }
	 * model.addAttribute("qte", qte);
	 * 
	 * if (produitToAdd != null && !hasError) {
	 * 
	 * Long idProduit = produitToAdd.getId();
	 * 
	 * Produit curProduit = prodR.getOne(idProduit); if (curProduit != null) { if
	 * (curProduit.getStock() == null) curProduit.setStock(0f);
	 * 
	 * Float stock = curProduit.getStock() - qte;
	 * 
	 * if (stock <= 0) { hasError = true; model.addAttribute("msgErr",
	 * "Le stock insuffisant"); } else { boolean isExist = false; for (Produit elt :
	 * wpanier.getProduits()) { if (elt.getId().equals(idProduit)) { isExist = true;
	 * Float previousQte = elt.getQte(); elt.setQte(previousQte + qte); } }
	 * 
	 * if (!isExist) { curProduit.setQte(qte);
	 * wpanier.getProduits().add(curProduit); }
	 * 
	 * session.setAttribute("panier", wpanier); } } }
	 * 
	 * ra.addAttribute("id",produitToAdd.getId()); return "redirect:/produit";
	 * //return getViewProduit(model, produitToAdd.getId(), session);
	 * 
	 * }
	 */
	
	@PostMapping("/creerCommande")
	public String creerCommandeFrm(@RequestParam(name="action",required = false) String action,Model model, @ModelAttribute("panier") PanierWrapper panierFromPage,
			HttpSession session) {
		if (action !=null && action.equalsIgnoreCase("valPanier")) {
			validerPanier(panierFromPage,session);
			return "redirect:/accueil";
		}
		else if (action !=null && action.equalsIgnoreCase("cmd")) {
			validerPanier(panierFromPage,session);
			User connectedCli=(User) session.getAttribute("connectedCli");			
			
			if (connectedCli!=null) {
				//
				Commande cmd=new Commande();
				cmd.setUser(connectedCli);
				cmd.setStatut(Commande.StatutCommande.EnAttente);
				PanierWrapper sessionPanier = (PanierWrapper) session.getAttribute("panier");
				for (Produit elt : sessionPanier.getProduits()) {
					cmd.getLignesCommandeProduit().add(new CommandeProduit(elt));		
				}				
				addStandardParams(model,session);
				model.addAttribute("cmd", cmd);
				return "viewCommande";	
			}
			else
				return "redirect:/login";	
			
		}
		
		return "redirect:/accueil";
	}
	
	@GetMapping("/creerCommande")
	public String getCreationCommandeFrm(Model model, HttpSession session) {
		addStandardParams(model,session);				
		
		return "";
	}
	
}
