package app.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import data.entitys.CategorieProduit;
import data.entitys.Produit;



@Controller
public class HomeController {
	@Autowired
	data.repositorys.RepoProduit prodR;
	
	@Autowired
	data.repositorys.RepoCategorieProduit catR;
	
	@Autowired
	data.repositorys.RepoPhoto_CategorieProduit phR;
	
	@Autowired
	data.repositorys.RepoPromotion promoR;
	
	@GetMapping({ "/","/accueil" })
	public String getAcceuilFrm(Model model) {	
		//, HttpSession session
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();		
		Set<Produit> lstProdSansCat=prodR.getProduitWithDependencyByCategorieNull();
		
		boolean hasProd=(lstProdSansCat.size()>0);
		if (! hasProd) {
			for (CategorieProduit cat : lstCat) {
				cat.setPhotos(phR.getByCategorie(cat.getId()));				
				if (cat.getProduits().size()>0) {
					hasProd=true;
					break;
				}
			}			
		}
		
		model.addAttribute("hasProd", hasProd);	
		model.addAttribute("lstCat", lstCat);	
		model.addAttribute("lstProdSansCat", lstProdSansCat);	
		 
		return "viewAccueil";
	}
	
}
