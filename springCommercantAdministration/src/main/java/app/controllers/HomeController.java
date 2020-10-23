package app.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import data.entitys.CategorieProduit;
import data.entitys.Commande;
import data.entitys.Produit;
import data.entitys.User;



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
	
	@Autowired
	data.repositorys.RepoUser usrR;
	
	@Autowired
	data.repositorys.RepoCommande cmdR;
	
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
	
	@GetMapping("/listUser")
	public String getListUserFrm(Model model) {
		//
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();
		model.addAttribute("lstCat", lstCat);
		List<User> users = usrR.findAll();
		List<String> statutValues = new ArrayList<String>();
		statutValues.add(Commande.StatutCommande.Finalise.toString());
		for (User usr : users) {
			usr.setNbCmd(cmdR.getCountByUserFiltered(usr.getId(),statutValues ));		
			usr.setTotalCmd(cmdR.getTotalByUserFiltered(usr.getId(),statutValues ));
		}
		model.addAttribute("lstUsr",users);
		
		
		return "viewLstUser";
	}
	
	@GetMapping("/viewUser")
	public String getUserFrm(Model model,@RequestParam(name = "id") Long id,@RequestParam(name="action",required = false) String action) {
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();
		model.addAttribute("lstCat", lstCat);
		
		List<String> statutValues = new ArrayList<String>();
		statutValues.add(Commande.StatutCommande.Finalise.toString());
		User usr=(User) usrR.getOne(id)	;
		usr.setCommandes(cmdR.getCommandesUser(id));
		usr.setNbCmd(cmdR.getCountByUserFiltered(id,statutValues ));		
		usr.setTotalCmd(cmdR.getTotalByUserFiltered(id,statutValues ));
		
		String redirectAction="listUser";
		if (action!=null && action.equalsIgnoreCase("viewUserFromCommande")) {
			redirectAction="listCmd";
		}
		System.out.println("red " + redirectAction);
		model.addAttribute("redirectAction", redirectAction);
		
		model.addAttribute("user", usr);
		return 	"viewCompte";
	}
}
