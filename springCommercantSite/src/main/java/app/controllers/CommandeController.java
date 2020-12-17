package app.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import data.entitys.CategorieProduit;
import data.entitys.Commande;
import data.entitys.CommandeProduit;
import data.entitys.PanierWrapper;
import data.entitys.ParamMainPage;
import data.entitys.PointVente;
import data.entitys.Produit;
import data.entitys.User;
import data.entitys.Commande.StatutCommande;


@Controller
public class CommandeController {
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
	data.repositorys.RepoPointVente ptsVR;
	
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
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();	
		model.addAttribute("lstCat", lstCat);	
		List<PointVente> pointsV = ptsVR.findAll();
		model.addAttribute("pointsV",pointsV);	
	}
	
	private Produit findProduit(Long id, List<Produit> lstProduit) {
		for (Produit elt : lstProduit) {
			if (elt.getId().equals(id)) {
				return elt;
			}
		}
		return null;
	}
		
	
	public void validerPanier(PanierWrapper panierFromPage, HttpSession session) {	
			
		
		List<Produit> lstToRemoveFromPage = new ArrayList<Produit>();
		for (Produit elt : panierFromPage.getProduits()) {			
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
	}

	@GetMapping("/deleteCommandeRequest")
	public String deleteCommandeFrm(Model model,@RequestParam(name = "id") Long idCommande,  HttpSession session) {
		addStandardParams(model,session);
		Commande cmd= cmdR.getOne(idCommande);		
		model.addAttribute("cmd", cmd);
			
		model.addAttribute("deleteRequest", true);
		return "viewCommande";	
	}
	
	@GetMapping("/deleteCmdFragment/{id}")
	public String getDeleteCmdFragment(Model model, @PathVariable("id") Long id, HttpSession session) {
		System.out.println("getDeleteCmdFragment");
		if(cmdR.existsById(id)) {
			Commande cmd= cmdR.getOne(id);	
			if(cmd.allowCancel()|| cmd.allowDelete()) {
				model.addAttribute("cmd", cmd);	
//				List<PointVente> pointsV = ptsVR.findAll();
//				model.addAttribute("pointsV",pointsV);	
//				addStandardParams(model,session);
				System.out.println("on envoi getDeleteCmdFragment");
				return "fragments/cmdFragments.html :: dlgDeleteCmd";				
			}
		}
		return "";
	}	
	
	@PostMapping("/deleteCommande")
	public String deleteCommandePost(Model model, 
			@RequestParam(name="action",required = false) String action,			
			@ModelAttribute("cmd") Commande cmd,  HttpSession session) {
		
		System.out.println("----------deleteCommandePost");	
		if (cmd!=null) {
			if (cmd.allowDelete()) {
				lignesCmdR.deleteByCommande(cmd.getId());
				cmdR.deleteById(cmd.getId());
			}
			else if(cmd.allowCancel()) {				
				cmdR.updateStatut(cmd.getId(), StatutCommande.Annule.toString());				
			}
		}
		return "redirect:/listCmd";	
	}
	
	//@GetMapping("/deleteCommande")
	public String deleteCommande(Model model,@RequestParam(name = "id") Long idCommande,  HttpSession session) {
		Commande cmd= cmdR.getOne(idCommande);		
		if (cmd!=null) {
			if (cmd.allowDelete()) {
				lignesCmdR.deleteByCommande(idCommande);
				cmdR.deleteById(idCommande);
			}
			else if(cmd.allowCancel()) {
				cmd.setStatutCmd(StatutCommande.Annule);
				cmdR.save(cmd);
			}
		}
		return "redirect:/listCmd";	
	}
	
	@GetMapping("/viewCommande")
	public String getViewCommandeFrm(Model model,@RequestParam(name = "id") Long idCommande,  HttpSession session) {
		addStandardParams(model,session);
		Commande cmd= cmdR.getOne(idCommande);	
		cmd.calculeTotaux();
		model.addAttribute("cmd", cmd);			
		return "viewCommande";	
	}
	
	@GetMapping("/creerCommande")
	public String getCreationCommandeFrm(Model model, HttpSession session) {
		
		addStandardParams(model,session);				
		
		User connectedCli=(User) session.getAttribute("connectedCli");			
		
		if (connectedCli!=null) {
			
			Commande cmd=new Commande();
			cmd.setUser(connectedCli);
			cmd.setStatutCmd(Commande.StatutCommande.EnAttente);
			cmd.setDate(new Date());
			if(connectedCli.getPointsVente().size()==1) {				
				if(ptsVR.existsById(connectedCli.getPointsVente().get(0).getId())) 
					cmd.setPointVente(ptsVR.getOne(connectedCli.getPointsVente().get(0).getId()));			
			}
				
			
			PanierWrapper sessionPanier = (PanierWrapper) session.getAttribute("panier");
			for (Produit elt : sessionPanier.getProduits()) {
				cmd.getLignesCommandeProduit().add(new CommandeProduit(elt));		
			}
			cmd.calculeTotaux();
			model.addAttribute("cmd", cmd);			
					
			return "viewCommande";	
		}
		else {			
			return "redirect:/accueil";	
		}			
		
	}
	
	@PostMapping("/creerCommande")
	public String creerCommandeFrm(@RequestParam(name="action",required = false) String action,
			@RequestParam Map<String,String> allParams,
			Model model,  @ModelAttribute("panier") PanierWrapper panierFromPage,
			HttpSession session) {
				
		if (action !=null && action.equalsIgnoreCase("valPanier")) {
			validerPanier(panierFromPage,session);
			return "redirect:/accueil";
		}
		else if (action !=null && action.equalsIgnoreCase("cmd")) {
			validerPanier(panierFromPage,session);
			User connectedCli=(User) session.getAttribute("connectedCli");			
			
			if (connectedCli!=null) {				
				return getCreationCommandeFrm(model,session);				
			}
			else {
				session.setAttribute("cmdRequest", "1");
				return "redirect:/login";	
			}			
		}
		
		return "redirect:/accueil";
	}

	@PostMapping("/saveCommande")
	public String saveCommande(Model model, @RequestParam(name="action",required = false) String action,
			@RequestParam(name = "ptV", required = false) Long selectedPtV,
			@ModelAttribute("cmd") @Valid Commande cmd, BindingResult bindingRes, HttpSession session, RedirectAttributes ra) {
		
		
		User connectedCli=(User) session.getAttribute("connectedCli");	
		if (connectedCli==null) 
			return "redirect:/login";	
		
		boolean isCmdExistante=false;
		boolean deleteProduitRequest=false;
		
		if (cmd!=null && connectedCli!=null) {
			if (bindingRes.hasErrors()) {
				String msgErr = "";
				for (ObjectError elt : bindingRes.getAllErrors()) {
					msgErr += elt.toString();
				}
				System.out.println(" les erreurs dans la saisie: " + msgErr);				
				return "viewCommande";
			}
			isCmdExistante=(cmd.getId()!=null);
			cmd.setUser(connectedCli);	
			
			if(selectedPtV!=null) {
				if(ptsVR.existsById(selectedPtV)) {
					PointVente selectedPt=ptsVR.getOne(selectedPtV);
					cmd.setPointVente(selectedPt);	
				}
			}			
			
			ArrayList<Long> lstToExclude = new ArrayList<Long>();
			
			
			if (cmd.getLignesCommandeProduit().size()==0) {
				deleteProduitRequest=true;
			}
			for (CommandeProduit cp : cmd.getLignesCommandeProduit()) {				
				/*
				 * if (cp.getProduit()!=null ) System.out.println(" lignes : " +
				 * cp.getProduit().getId() + " : " + cp.getQte()); if (cp.getProduit()==null )
				 * System.out.println(" lignes : pas de produit" + " : " + cp.getQte());
				 */
				
				if (cp.getQte()!=null && cp.getQte() > 0 && cp.getProduit()!=null && cp.getProduit().getId()!=null) {
					cp.setCommande(cmd);
					cp.calculeTotaux();	
					if(cp.getId()!=null)
						lstToExclude.add(cp.getId());
				}
				else
					deleteProduitRequest=true;
				
			}
			cmd.calculeTotaux();
			cmdR.save(cmd);
						
			for (CommandeProduit cp : cmd.getLignesCommandeProduit()) {
				if (cp.getQte()!=null && cp.getQte() > 0 && cp.getProduit()!=null && cp.getProduit().getId()!=null) {
					lignesCmdR.save(cp);					
				}				
			}
			//pour le cas de suppression d'un produit dans l'interface, impossible d'optimiser car la ligne de produit n'existe plus, on fera donc toujours cette requette....
			if(isCmdExistante) {
				if (lstToExclude.size()>0) {
					
					lignesCmdR.deleteNotIncluded(cmd.getId(), lstToExclude);
				}
				else
					lignesCmdR.deleteByCommande(cmd.getId());
			}
			
			if(!isCmdExistante) session.removeAttribute("panier");		
		}
		if(isCmdExistante)
			return "redirect:/listCmd";	
		else
		{			
			addStandardParams(model,session);	
			model.addAttribute("newCmdAlertRequest", true);
			model.addAttribute("newCmd", cmd);					
			session.removeAttribute("cmdRequest");				
			return "viewHome";
		}
	}
			
	@GetMapping("/listCmd")
	public String getViewCmdFrm(Model model,@RequestParam(name="statutFilter",required = false) List<String> statutValues, HttpSession session) {
		
		if (statutValues==null) {
			statutValues=new ArrayList<String>();
		}
		addStandardParams(model,session);		
		
		User connectedCli=(User) session.getAttribute("connectedCli");	
		if (connectedCli==null) 
			return "redirect:/login";	
		if (statutValues.size()==0) 
			connectedCli.setCommandes(cmdR.getCommandesUser(connectedCli.getId()));			
		else
			connectedCli.setCommandes(cmdR.getCommandesUserFiltered(connectedCli.getId(),statutValues));	
		
		model.addAttribute("connectedCli", connectedCli);
		model.addAttribute("statutValues", Commande.StatutCommande.values());
		model.addAttribute("statutSelectedValues", statutValues);
		return "viewLstCommande";
	}
	
}
