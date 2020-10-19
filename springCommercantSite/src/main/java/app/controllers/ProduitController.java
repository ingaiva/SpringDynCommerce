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
import data.entitys.PanierWrapper;
import data.entitys.ParamMainPage;
import data.entitys.Produit;
import data.entitys.User;

@Controller
public class ProduitController {
	@Autowired
	data.repositorys.RepoParamMainPage paramR;
	@Autowired
	data.repositorys.RepoPhoto_param phParamR;

	@Autowired
	data.repositorys.RepoProduit prodR;

	@Autowired
	data.repositorys.RepoInfoCompProduit infoPR;
	@Autowired
	data.repositorys.RepoLigneInfoCompProduit lnInfoPR;

	@Autowired
	data.repositorys.RepoCategorieProduit catR;

	@Autowired
	data.repositorys.RepoPhoto_CategorieProduit photoCR;

	@Autowired
	data.repositorys.RepoPhoto_Produit photoPR;

	private void addStandardParams(Model model, HttpSession session) {
		PanierWrapper panier = (PanierWrapper) session.getAttribute("panier");
		if (panier == null)
			panier = new PanierWrapper();
		model.addAttribute("panier", panier);

		User connectedCli = (User) session.getAttribute("connectedCli");
		model.addAttribute("connectedCli", connectedCli);

		model.addAttribute("paramP", getParam(session));
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();
		model.addAttribute("lstCat", lstCat);
		
	}

	//private static ParamMainPage param = null;

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

	@GetMapping("/listProdByCat")
	public String getViewByCat(Model model, @RequestParam(name = "idCat") Long idCat, HttpSession session) {
		addStandardParams(model, session);

		CategorieProduit categorieToEdit = catR.getOne(idCat);
		if (categorieToEdit == null)
			return "redirect:/accueil";
		else {
			categorieToEdit.setPhotos(photoCR.getByCategorie(idCat));
			model.addAttribute("categorie", categorieToEdit);
			return "viewLstProduitByCategorie";
		}
	}

	@GetMapping("/produit")
	public String getViewProduit(Model model, @RequestParam(name = "id") Long id, HttpSession session) {
		// , RedirectAttributes ra
		addStandardParams(model, session);

		Produit produitToEdit = prodR.getProduitWithDependency(id);
		produitToEdit.setLstInfoComp(infoPR.getByProduit(produitToEdit.getId()));
		produitToEdit.setPhotos(photoPR.getByProduit(produitToEdit.getId()));
		model.addAttribute("produit", produitToEdit);
		return "viewProduit";
	}

	@PostMapping("/addProd")
	public String ajouterProduitPanier(Model model, @RequestParam(name = "qteToAdd") Float qte,
			@ModelAttribute("produit") @Valid Produit produitToAdd, BindingResult bindingRes, HttpSession session) {
		//,RedirectAttributes ra
		/*
		 * System.out.println("dans post addProd " + qte);
		 * System.out.println(model.asMap()); System.out.println(produitToAdd);
		 */

		PanierWrapper wpanier = (PanierWrapper) session.getAttribute("panier");
		if (wpanier == null)
			wpanier = new PanierWrapper();

		boolean hasError = false;
		if (qte <= 0) {
			hasError = true;
			model.addAttribute("msgErr", "La quantité du produit doit être supérieure à zéro");
		}
		model.addAttribute("qte", qte);

		if (produitToAdd != null && !hasError) {

			Long idProduit = produitToAdd.getId();

			Produit curProduit = prodR.getOne(idProduit);
			if (curProduit != null) {
				if (curProduit.getStock() == null)
					curProduit.setStock(0f);

				Float stock = curProduit.getStock() - qte;

				if (stock <= 0) {
					hasError = true;
					model.addAttribute("msgErr", "Le stock insuffisant");
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
						wpanier.getProduits().add(curProduit);
					}

					session.setAttribute("panier", wpanier);
				}
			}
		}
		
		//ra.addAttribute("id",produitToAdd.getId());
		//return "redirect:/produit";
		return getViewProduit(model, produitToAdd.getId(), session);

	}
	/*
	 * @PostMapping("/valPanier") public String validerPanier(Model model,
	 * HttpSession session) { PanierWrapper sessionPanier = (PanierWrapper)
	 * session.getAttribute("panier"); if (sessionPanier == null) sessionPanier =
	 * new PanierWrapper();
	 * 
	 * System.out.println("dans le session"); for (Produit elt :
	 * sessionPanier.getProduits()) { System.out.println(elt.getDescription() +
	 * " / " + elt.getStatut()); } return "redirect:/accueil"; }
	 */

	
}
