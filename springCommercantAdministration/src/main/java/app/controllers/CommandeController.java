package app.controllers;

import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import app.utility.StatisticProduit;
import data.entitys.CategorieProduit;
import data.entitys.Commande;
import data.entitys.Commande.StatutCommande;
import data.entitys.CommandeProduit;
import data.entitys.PointVente;
import data.entitys.Produit;

@Controller
public class CommandeController {
	@Autowired
	data.repositorys.RepoUser usrR;

	@Autowired
	data.repositorys.RepoCommande cmdR;

	@Autowired
	data.repositorys.RepoCommandeProduit lignesCmdR;

	@Autowired
	data.repositorys.RepoCategorieProduit catR;

	@Autowired
	data.repositorys.RepoPointVente ptsVR;
	
	private void addStandardParams(Model model, HttpSession session) {
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();
		model.addAttribute("lstCat", lstCat);
	}
	
	@PostMapping("/listCmd")
	public String postToViewCmdFrm(Model model, 
			@RequestParam(name="action",required = false) String action, 
			@RequestParam(name = "statutFilter", required = false) List<String> statutValues, 
			@RequestParam(name = "ptVFilter", required = false) List<Long> ptVFilterValues,
			HttpSession session) {
		
		if(action!=null && action.equalsIgnoreCase("effacerFilters")) {
			ptVFilterValues = new ArrayList<Long>();
			statutValues = new ArrayList<String>();
		}
//		if (ptVFilterValues == null) {			
//			@SuppressWarnings("unchecked")
//			List<Long> sessionPtvSelectedValues = (List<Long>) session.getAttribute("ptVFilterValuesS");
//			if(sessionPtvSelectedValues==null)							
//				ptVFilterValues = new ArrayList<Long>();			
//			else
//				ptVFilterValues=sessionPtvSelectedValues;
//		}
//		
//		if (statutValues == null) {			
//			@SuppressWarnings("unchecked")
//			List<String> sessionSelectedValues = (List<String>) session.getAttribute("statutSelectedValuesS");
//			if(sessionSelectedValues==null)							
//				statutValues = new ArrayList<String>();			
//			else
//				statutValues=sessionSelectedValues;
//		}
//		session.setAttribute("statutSelectedValuesS", statutValues);
//		session.setAttribute("ptVFilterValuesS", ptVFilterValues);		
		return getViewCmdFrm(model,action,statutValues,ptVFilterValues,session);
	}
		
	@GetMapping("/listCmd")
	public String getViewCmdFrm(Model model, 
			@RequestParam(name="action",required = false) String action, 
			@RequestParam(name = "statutFilter", required = false) List<String> statutValues, 
			@RequestParam(name = "ptVFilter", required = false) List<Long> ptVFilterValues,
			HttpSession session) {
		
		if(ptVFilterValues==null ) 
			ptVFilterValues = new ArrayList<Long>();			
		
		if(statutValues==null ) 			
			statutValues = new ArrayList<String>();
		
//		if(action!=null && action.equalsIgnoreCase("effacerFilters")) {
//			statutValues = new ArrayList<String>();
//		}
//		if (statutValues == null) {
//			
//			List<String> sessionSelectedValues = (List<String>) session.getAttribute("statutSelectedValuesS");
//			if(sessionSelectedValues==null)
//			{				
//				statutValues = new ArrayList<String>();
//			}
//			else
//				statutValues=sessionSelectedValues;
//		}
		
		System.out.println(statutValues + "------" + ptVFilterValues);
		
		addStandardParams(model, session);		
		if ((statutValues ==null || statutValues.size() == 0) && (ptVFilterValues == null || ptVFilterValues.size()==0))
			model.addAttribute("lstCmd", cmdR.getCommandesOrdered());
		else if (statutValues.size() > 0 &&  (ptVFilterValues == null || ptVFilterValues.size()==0))
			model.addAttribute("lstCmd", cmdR.getCommandesFilteredByStatut(statutValues));
		else if ((statutValues ==null || statutValues.size() == 0) && ptVFilterValues.size()>0)
			model.addAttribute("lstCmd", cmdR.getCommandesFilteredByPtV(ptVFilterValues));
		else
			model.addAttribute("lstCmd", cmdR.getCommandesFiltered(statutValues,ptVFilterValues));
		
		model.addAttribute("statutValues", Commande.StatutCommande.values());
		model.addAttribute("statutSelectedValues", statutValues);
		//session.setAttribute("statutSelectedValuesS", statutValues);
		
		model.addAttribute("pointsV", ptsVR.findAll());
		model.addAttribute("ptvSelectedValues", ptVFilterValues);	
	
		List<Commande> lstCmd =(List<Commande>) model.getAttribute("lstCmd");
		//List<Produit> lstProduits = new ArrayList<Produit>();
		StatisticProduit stat=new StatisticProduit();
		for (Commande cmd : lstCmd) {
			for (CommandeProduit cp : cmd.getLignesCommandeProduit()) {
				Produit curProd=cp.getProduit();
				stat.addProduit(curProd, cp.getQte());				
			}
		}
		model.addAttribute("stat", stat);	
		
		return "viewLstCommande";
	}
	
//	private Produit findProduit(List<Produit> lstProduits, Produit curProduit) {
//		for (Produit p : lstProduits) {
//			if(p.getId().equals(curProduit.getId()))
//				return p;
//		}
//		return null;
//	}
	
	@GetMapping("/viewCommande")
	public String getViewCommandeFrm(Model model,@RequestParam(name = "id") Long idCommande,  HttpSession session) {
		addStandardParams(model,session);
		Commande cmd= cmdR.getOne(idCommande);		
		model.addAttribute("cmd", cmd);	
		List<PointVente> pointsV = ptsVR.findAll();
		model.addAttribute("pointsV",pointsV);	
		return "viewCommande";	
	}
	
	@PostMapping("/saveCommande")
	public String saveCommande(Model model, @RequestParam(name="action",required = false) String action,
			@RequestParam(name = "ptV", required = false) Long selectedPtV,
			@ModelAttribute("cmd") @Valid Commande cmd, BindingResult bindingRes, HttpSession session, RedirectAttributes ra) {
		
		
		
		boolean isCmdExistante=false;
		boolean deleteProduitRequest=false;
		

			
		if (cmd!=null ) {
			if (bindingRes.hasErrors()) {
				String msgErr = "";
				for (ObjectError elt : bindingRes.getAllErrors()) {
					msgErr += elt.toString();
				}
				System.out.println(" les erreurs dans la saisie: " + msgErr);				
				return "viewCommande";
			}
			
			if (action !=null && action.equalsIgnoreCase("validerCmd")) {				
				cmdR.updateStatut(cmd.getId(), StatutCommande.Valide.toString());
				return getViewCommandeFrm(model,cmd.getId(),session);
			}
			else if (action !=null && action.equalsIgnoreCase("finaliserCmd")) {				
				cmdR.updateStatut(cmd.getId(), StatutCommande.Finalise.toString());
				return getViewCommandeFrm(model,cmd.getId(),session);
				
			}
			else if(action !=null && action.equalsIgnoreCase("statutPrecedentCmd")) {
				if(cmd.isValide())
					cmdR.updateStatut(cmd.getId(), StatutCommande.EnAttente.toString());
				else if (cmd.isCanceled())
					cmdR.updateStatut(cmd.getId(), StatutCommande.EnAttente.toString());
				else if (cmd.isFinalise())
					cmdR.updateStatut(cmd.getId(), StatutCommande.Valide.toString());
				
				return getViewCommandeFrm(model,cmd.getId(),session);
			}
			else {
				
				isCmdExistante=(cmd.getId()!=null);				
				if(selectedPtV!=null && isCmdExistante) {
					if(ptsVR.existsById(selectedPtV)) {
						PointVente selectedPt=ptsVR.getOne(selectedPtV);
						cmd.setPointVente(selectedPt);	
						cmdR.updatePointVente(cmd.getId(), selectedPt);						
					}
				}	
				
				ArrayList<Long> lstToExclude = new ArrayList<Long>();			
				
				if (cmd.getLignesCommandeProduit().size()==0) {
					deleteProduitRequest=true;
				}
				for (CommandeProduit cp : cmd.getLignesCommandeProduit()) {				
					
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
				//cmdR.save(cmd);
				cmdR.updateInfo(cmd.getId(), cmd.getInfoComp(), cmd.getMsgCommercant(), cmd.getStatut(), cmd.getTotalSansPromo(), cmd.getTotalReductionStandard(), cmd.getReductionSpeciale(), cmd.getTotalFinal());
				for (CommandeProduit cp : cmd.getLignesCommandeProduit()) {
					if (cp.getQte()!=null && cp.getQte() > 0 && cp.getProduit()!=null && cp.getProduit().getId()!=null) {
						lignesCmdR.save(cp);					
					}				
				}
				if(isCmdExistante ) {
					if (lstToExclude.size()>0) {
						lignesCmdR.deleteNotIncluded(cmd.getId(), lstToExclude);
					}
					else
						lignesCmdR.deleteByCommande(cmd.getId());
				}	
			}
			
		}
		return "redirect:/listCmd";	
		
	}
	
	@GetMapping("/deleteCommande")
	public String deleteCommande(Model model,@RequestParam(name = "id") Long idCommande,  HttpSession session) {
		Commande cmd= cmdR.getOne(idCommande);		
		if (cmd!=null) {
			cmdR.updateStatut(cmd.getId(), StatutCommande.AnnuleParCommercant.toString());			
		}
		return "redirect:/listCmd";	
	}
}
