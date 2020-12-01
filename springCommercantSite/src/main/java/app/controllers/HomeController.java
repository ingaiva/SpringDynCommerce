package app.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import data.entitys.CategorieProduit;
import data.entitys.PanierWrapper;
import data.entitys.ParamMainPage;
import data.entitys.PointVente;
import data.entitys.Produit;
import data.entitys.User;


@Controller
public class HomeController {
	@Autowired
	data.repositorys.RepoUser usrR;
	
	@Autowired
	data.repositorys.RepoCommande cmdR;
	
	@Autowired
	data.repositorys.RepoCommandeProduit lignesCmdR;
	
	@Autowired
	data.repositorys.RepoProduit prodR;
	
	@Autowired
	data.repositorys.RepoCategorieProduit catR;
	
	@Autowired
	data.repositorys.RepoParamMainPage paramR;
	
	@Autowired
	data.repositorys.RepoPhoto_param phParamR;
	
	@Autowired
	data.repositorys.RepoPhoto_CategorieProduit phR;
	
	@Autowired
	data.repositorys.RepoPointVente ptsVR;
	//private static ParamMainPage param= null;
	
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
		//session.setAttribute("panier", panier);
		
		User connectedCli=(User) session.getAttribute("connectedCli");		
		model.addAttribute("connectedCli", connectedCli);	
		getParam(session);
		//model.addAttribute("paramP", getParam(session));			
	
	}
	
	@GetMapping({ "/","/accueil","/home" })
	public String getAcceuilFrm(Model model, HttpSession session) {	
		
		addStandardParams(model,session);				
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();	
		model.addAttribute("lstCat", lstCat);			
		session.removeAttribute("cmdRequest");	//pour les cas d'annulation de login ou creation du compte suite à la validation du panier
		
		return "viewHome";
	}
	
	@GetMapping({ "/listProd" })
	public String getLstProduitFrm(Model model , HttpSession session) {
		addStandardParams(model,session);
	
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
		
		//model.addAttribute("paramP", getParam());	
		model.addAttribute("hasProd", hasProd);	
		model.addAttribute("lstCat", lstCat);	
		model.addAttribute("lstProdSansCat", lstProdSansCat);	
		 
		return "viewLstProduit";	
	}
	
	@GetMapping("/login")
	public String getLoginFrm(@ModelAttribute("user") User user, Model model, HttpSession session) {
		session.removeAttribute("connectedCli");
		addStandardParams(model,session);
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();	
		model.addAttribute("lstCat", lstCat);	
		return "viewLogin";
	}
	
	@PostMapping({ "/login" })
	public String checkLogin(@ModelAttribute("user") User curUser, HttpSession session, RedirectAttributes ra) {		
				
		Set<User> matches = usrR.getUserByMail(curUser.getMail());	
		
		boolean isCltTrouve = false;
		for (User cltInDb : matches) {
			if (cltInDb.getPassword().equals(curUser.getPassword())) {
				isCltTrouve = true;
				cltInDb.setPointsVente(ptsVR.findAllByUsers(cltInDb));
				session.setAttribute("connectedCli", cltInDb);
				
				if (session.getAttribute("cmdRequest") != null && session.getAttribute("cmdRequest").equals("1")) {					
					session.removeAttribute("cmdRequest");
					return "redirect:/creerCommande";
				} else
					return "redirect:/accueil";
			}
		}

		if (!isCltTrouve) {
			ra.addFlashAttribute("user", curUser);
			ra.addFlashAttribute("msglogin", "E-mail ou le mot de passe incorrects! ");			
		}

		return "redirect:/login";
	}

	@GetMapping({ "/creerCompte" })
	public String getViewNewCompteFrm(@ModelAttribute("user") User curUser, Model model, HttpSession session) {
		session.removeAttribute("connectedCli");
		addStandardParams(model,session);
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();	
		model.addAttribute("lstCat", lstCat);
		
		List<PointVente> pointsV = ptsVR.findAll();
		model.addAttribute("pointsV",pointsV);
		model.addAttribute("pointsVselected",null);
		
		return "viewCompte";
	}
	@GetMapping({ "/modifierCompte" })
	public String getViewCompteFrm(Model model, HttpSession session) {
				
		addStandardParams(model,session);
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();	
		model.addAttribute("lstCat", lstCat);
		
		User connectedCli=(User) session.getAttribute("connectedCli");	
		model.addAttribute("user", connectedCli);
		
		List<PointVente> pointsV = ptsVR.findAll();
		model.addAttribute("pointsV",pointsV);
		model.addAttribute("pointsVselected",connectedCli.getLstIdPtv());
		
		if (connectedCli!=null) 
			return "viewCompte";
		else
			return "redirect:/login";		
	}
	
	@PostMapping({ "/saveCompte" })
	public String saveClient(@ModelAttribute("user") @Valid User user, BindingResult bindingRes,
			RedirectAttributes ra,
			@RequestParam(name = "ptV", required = false) List<Long> selectedValues, 
			HttpSession session) {
		
		if (bindingRes.hasErrors()) {
			String msgErr = "";
			for (ObjectError elt : bindingRes.getAllErrors()) {
				msgErr += elt.toString();
			}
			System.out.println(" les erreurs dans la saisie: " + msgErr);
			return "viewCompte";
		}

		Long idToExclude=0l;
		if (user.getId()!=null) 
			idToExclude=user.getId();
		else
			user.setDateCreation(new Date());
		
		Set<User> matches = usrR.getUserByMail(user.getMail(), idToExclude);
		if (matches.size() > 0) {			
			ra.addFlashAttribute("user", user);
			ra.addFlashAttribute("msg", "Compte avec cet E-mail exist déjà");
			return "viewCompte";
		}

		if (selectedValues == null)
			selectedValues = new ArrayList<Long>();
				
		List<PointVente> lstInDb = null;//ptsVR.findAllByUsers(user);
		if (user.getId()!=null) 
			lstInDb = ptsVR.findAllByUsers(user);
		else
			lstInDb = new ArrayList<PointVente>();
		
		for (Long idPtV : selectedValues) {
			PointVente pt = ptsVR.getOne(idPtV);
			if (lstInDb.contains(pt) == false)
				lstInDb.add(pt);
		}

		for (PointVente ptU : new ArrayList<>(lstInDb)) {
			if (selectedValues.contains(ptU.getId()) == false)
				lstInDb.remove(ptU);
		}
		user.setPointsVente(lstInDb);	
		
		usrR.save(user);

		session.setAttribute("connectedCli", user);
		if (session.getAttribute("cmdRequest") != null && session.getAttribute("cmdRequest").equals("1")) {		
			session.removeAttribute("cmdRequest");
			return "redirect:/creerCommande";	// redirection vers le panier pour finaliser la commande
		} else
			return "redirect:/accueil";

	}
	
	@GetMapping("/supprimerCompte")
	public String getSuppressionCompteFrm(Model model, HttpSession session) {
		addStandardParams(model,session);
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();	
		model.addAttribute("lstCat", lstCat);
		
		User connectedCli=(User) session.getAttribute("connectedCli");	
		model.addAttribute("user", connectedCli);
		model.addAttribute("suppressionCompteRequest", true);
		
		List<PointVente> pointsV = ptsVR.findAll();
		model.addAttribute("pointsV",pointsV);
		model.addAttribute("pointsVselected",connectedCli.getLstIdPtv());
		
		if (connectedCli!=null) 
			return "viewSuppressionCompte";
		else
			return "redirect:/login";
	}
	
	@PostMapping("supprimerCompte")
	public String supprimerCompte(Model model, HttpSession session) {
		User connectedCli=(User) session.getAttribute("connectedCli");			
		
		if (connectedCli!=null) {
			lignesCmdR.deleteByUser(connectedCli.getId());			
			cmdR.deleteCommandesByUser(connectedCli.getId());
			usrR.delete(connectedCli);
			return "redirect:/deconnexion";}
		else
			return "redirect:/login";
	}
	
	@GetMapping("/deconnexion")
	public String deconnexion(Model model, HttpSession session) {
		session.removeAttribute("connectedCli");
		addStandardParams(model,session);
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();	
		model.addAttribute("lstCat", lstCat);
		return "redirect:/accueil";
	}
	
	@GetMapping("/navbarUserFragment")
	public String loadNavbarUserFragment(Model model, HttpSession session) {
		session.removeAttribute("connectedCli");
		addStandardParams(model,session);
		return "fragments/general.html :: navbarUser";
	}
	
	@GetMapping("/pointsVente")
	public String getPointsVenteFrm(Model model, HttpSession session) {
		addStandardParams(model,session);
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();	
		model.addAttribute("lstCat", lstCat);
		List<PointVente> pointsV = ptsVR.findAll();
		model.addAttribute("pointsV",pointsV);
		return "viewPointsVente";
	}
	
	@GetMapping("/pointVenteFragment/{id}")
	public String loadPointVenteFragment(Model model, @PathVariable("id") Long idPtV, HttpSession session) {
		if(ptsVR.existsById(idPtV)) {
			addStandardParams(model,session);
			model.addAttribute("ptV", ptsVR.getOne(idPtV));
			return "fragments/general.html :: detailPointVente";			
		}
		else
			return "";
	}
}
