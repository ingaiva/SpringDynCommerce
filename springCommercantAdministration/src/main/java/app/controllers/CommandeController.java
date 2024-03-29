package app.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import app.utility.FilterCmd;
import app.utility.StatisticProduit;
import data.entitys.CategorieProduit;
import data.entitys.Commande;
import data.entitys.Commande.StatutCommande;
import data.entitys.CommandeProduit;
import data.entitys.PointVente;
import data.entitys.Produit;

@Controller
//@RequestMapping("/")
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
	
	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
	 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	 dateFormat.setLenient(false);
	 webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	 }
	
	private void addStandardParams(Model model, HttpSession session) {
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();
		model.addAttribute("lstCat", lstCat);
	}
	
	@PostMapping("/listCmd")
	public String postToViewCmdFrm(Model model, 
			@RequestParam(name="action",required = false) String action, 
			@ModelAttribute(name = "filter") FilterCmd filter, 			
			HttpSession session) {
		
		if(action!=null && action.equalsIgnoreCase("effacerFilters")) 
			filter.effacer();	
		else
			filter.checkDates();		
		
//		System.out.println(action);
		
		return getViewCmdFrm(model,action,filter,session);
	}
	
	
	@GetMapping("/listCmd")
	public String getViewCmdFrm(Model model, 
			@RequestParam(name="action",required = false) String action, 
			@ModelAttribute(name = "filter") FilterCmd filter,
			HttpSession session) {
		
		if(filter==null ) 
			filter = new FilterCmd();	
		
		addStandardParams(model, session);			
		
		model.addAttribute("lstCmd", cmdR.findAll(filter.getCriteria(),Sort.by(Sort.Direction.DESC, "id")));
		
		model.addAttribute("filter", filter);		
		
		model.addAttribute("pointsV", ptsVR.findAll());			
	//System.out.println("---------------");
		List<Commande> lstCmd =(List<Commande>) model.getAttribute("lstCmd");		
		StatisticProduit stat=new StatisticProduit();

		if (action != null && action.equalsIgnoreCase("loadStats")) {
			stat.setLoaded(true);
			for (Commande cmd : lstCmd) {
				for (CommandeProduit cp : cmd.getLignesCommandeProduit()) {
					Produit curProd = cp.getProduit();
					stat.addProduit(curProd, cp.getQte());
				}
			}
		}
			
		model.addAttribute("stat", stat);
		model.addAttribute("redirectAction", "listCmd");
		//<input type="hidden" name="redirectAction" th:value="${redirectAction}">
		return "viewLstCommande";
	}
	
	private Map<String, String> getHeadersInfo(HttpServletRequest request) {

        Map<String, String> map = new HashMap<String, String>();

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }
	
	
	@GetMapping("/viewCommande")
	public String getViewCommandeFrm(Model model,@RequestParam(name = "id") Long idCommande,  HttpSession session
			,HttpServletRequest request) {
		if(cmdR.existsById(idCommande)) {
			addStandardParams(model,session);
			Commande cmd= cmdR.getOne(idCommande);		
			model.addAttribute("cmd", cmd);	
			List<PointVente> pointsV = ptsVR.findAll();
			model.addAttribute("pointsV",pointsV);	
			
			String referer = request.getHeader("Referer");
			if(referer==null) {
				model.addAttribute("redirectAction", "listCmd");
			}
			else {
				String host=request.getHeader("host");
				if(referer.contains(host)) {
					int partToRemoveLength=referer.indexOf(host)+host.length()+1;
					if(referer.length()>partToRemoveLength) {
						referer=referer.substring(partToRemoveLength);
						model.addAttribute("redirectAction", referer);
					}				
				}	
			}	
			return "viewCommande";	
			
		}
		else {
			model.addAttribute("msg", "La commande demandée n'existe pas");
			return "error";	
		}
	}
	
	@PostMapping("/saveCommande")
	public String saveCommande(Model model, @RequestParam(name="action",required = false) String action,
			@RequestParam(name = "ptV", required = false) Long selectedPtV,
			@RequestParam(name="radioDateLivraison", required = false) String choixDateLivraison,
			@RequestParam(name="InputDateLivraison", required = false) Date dateLivraison,
			@RequestParam(name="redirectAction", required = false) String redirectAction,
			@ModelAttribute("cmd") @Valid Commande cmd, BindingResult bindingRes, HttpSession session,HttpServletRequest request) {
		
			
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
				saveDateLivraison(cmd, selectedPtV, choixDateLivraison, dateLivraison);
				cmdR.updateStatut(cmd.getId(), StatutCommande.Valide.toString());
				cmd.setStatutCmd(StatutCommande.Valide);
				saveCommande(cmd,selectedPtV);
//				return getViewCommandeFrm(model,cmd.getId(),session);
				return "redirect:/listCmd";	
			}
			else if (action !=null && action.equalsIgnoreCase("finaliserCmd")) {				
				cmdR.updateStatut(cmd.getId(), StatutCommande.Finalise.toString());
				cmd.setStatutCmd(StatutCommande.Finalise);
				saveCommande(cmd,selectedPtV);
//				return getViewCommandeFrm(model,cmd.getId(),session);
				return "redirect:/listCmd";	
			}
			else if(action !=null && action.equalsIgnoreCase("statutPrecedentCmd")) {
				data.entitys.Commande.StatutCommande statutToSet=StatutCommande.EnAttente;
				if(cmd.isValide())
					statutToSet=StatutCommande.EnAttente;
//					cmdR.updateStatut(cmd.getId(), StatutCommande.EnAttente.toString());
				
				else if (cmd.isCanceled())
					statutToSet=StatutCommande.EnAttente;
//					cmdR.updateStatut(cmd.getId(), StatutCommande.EnAttente.toString());
				else if (cmd.isFinalise())
					statutToSet=StatutCommande.Valide;
//					cmdR.updateStatut(cmd.getId(), StatutCommande.Valide.toString());
				cmdR.updateStatut(cmd.getId(), statutToSet.toString());
				cmd.setStatutCmd(statutToSet);
				saveCommande(cmd,selectedPtV);
				
				return getViewCommandeFrm(model,cmd.getId(),session,request);
			}
			else if(action!=null && action.equalsIgnoreCase("editDateLivraisonRequest")) {
				saveCommande(cmd,selectedPtV);
				model.addAttribute("isEditDateLivraison", true);
				return getViewCommandeFrm(model,cmd.getId(),session,request);
			}
			else if(action!=null && action.equalsIgnoreCase("editDateLivraison")) {
				saveCommande(cmd,selectedPtV);				
				saveDateLivraison(cmd, selectedPtV, choixDateLivraison, dateLivraison);
				return getViewCommandeFrm(model,cmd.getId(),session,request);
				
			}
			else if (action!=null && action.equalsIgnoreCase("cancelEditDateLivraison")) {
				saveCommande(cmd,selectedPtV);				
				return getViewCommandeFrm(model,cmd.getId(),session,request);
			}
			else if (action!=null && action.equalsIgnoreCase("retour")) {
				if(redirectAction!=null && redirectAction.toLowerCase().contains("user"))	{
					System.out.println("redirection vers " + "redirect:/viewUser/"+ cmd.getUser().getId());
					return "redirect:/viewUser?id="+ cmd.getUser().getId();	
				}
				else if(redirectAction!=null) {
					System.out.println("redirection vers " + "redirect:/"+ redirectAction);
					return "redirect:/"+ redirectAction;			
					
				}
			}
			else {
				
				saveCommande(cmd,selectedPtV);			

			}
			
		}
		return "redirect:/listCmd";	
		
	}
	private void saveDateLivraison(Commande cmd, Long selectedPtV, String choixDateLivraison, Date dateLivraison) {
		
		if(choixDateLivraison!=null) {			
			Date dateLivraisonToSet=null;
			if(choixDateLivraison.equalsIgnoreCase("def")) {
				if(cmd.getDateChoixLivraison() != null) {					
					dateLivraisonToSet=cmd.getDateChoixLivraison();
				}
				else {							
					if(selectedPtV!=null && ptsVR.existsById(selectedPtV)) {
						PointVente selectedPt=ptsVR.getOne(selectedPtV);
						dateLivraisonToSet=selectedPt.getNextDateValidation(cmd);							
					}
				}
			}
			else if (choixDateLivraison.equalsIgnoreCase("input") && dateLivraison!=null){
				dateLivraisonToSet=dateLivraison;
			}
			cmdR.updateDateLivraison(cmd.getId(), dateLivraisonToSet);
		}
	}
	
	private void saveCommande(Commande cmd, Long selectedPtV) {
		boolean isCmdExistante = false;
		boolean deleteProduitRequest = false;
		isCmdExistante = (cmd.getId() != null);
		if (selectedPtV != null && isCmdExistante) {
			if (ptsVR.existsById(selectedPtV)) {
				PointVente selectedPt = ptsVR.getOne(selectedPtV);
				cmd.setPointVente(selectedPt);
				cmdR.updatePointVente(cmd.getId(), selectedPt);
			}
		}

		ArrayList<Long> lstToExclude = new ArrayList<Long>();

		if (cmd.getLignesCommandeProduit().size() == 0) {
			deleteProduitRequest = true;
		}
		for (CommandeProduit cp : cmd.getLignesCommandeProduit()) {

			if (cp.getQte() != null && cp.getQte() > 0 && cp.getProduit() != null && cp.getProduit().getId() != null) {
				cp.setCommande(cmd);
				cp.calculeTotaux();
				if (cp.getId() != null)
					lstToExclude.add(cp.getId());
			} else
				deleteProduitRequest = true;

		}
		cmd.calculeTotaux();
		
		cmdR.updateInfo(cmd.getId(), cmd.getInfoComp(), cmd.getMsgCommercant(), cmd.getStatut(),
				cmd.getTotalSansPromo(), cmd.getTotalReductionStandard(), cmd.getReductionSpeciale(),
				cmd.getTotalFinal());
		
		for (CommandeProduit cp : cmd.getLignesCommandeProduit()) {
			if (cp.getQte() != null && cp.getQte() > 0 && cp.getProduit() != null && cp.getProduit().getId() != null) {
				lignesCmdR.save(cp);
			}
		}
		if (isCmdExistante) {
			if (lstToExclude.size() > 0) 
				lignesCmdR.deleteNotIncluded(cmd.getId(), lstToExclude);
			else
				lignesCmdR.deleteByCommande(cmd.getId());
		}
	}
	
	
	@GetMapping("/deleteCommande")
	public String deleteCommande(Model model,@RequestParam(name = "id") Long idCommande,  HttpSession session) {
		Commande cmd= cmdR.getOne(idCommande);		
		if (cmd!=null) {
			cmdR.updateStatut(cmd.getId(), StatutCommande.AnnuleParCommercant.toString());			
		}
		return "redirect:/listCmd";	
	}
	
	
	@GetMapping("/apercuCmdFragment/{id}")
	public String getapercuCmdFragment(Model model, @PathVariable("id") Long id, HttpSession session) {
		if(cmdR.existsById(id)) {
			addStandardParams(model,session);
			Commande cmd= cmdR.getOne(id);		
			model.addAttribute("cmd", cmd);	
			List<PointVente> pointsV = ptsVR.findAll();
			model.addAttribute("pointsV",pointsV);	
			return "fragments/cmdFragments.html :: apercuCmd";	
			
		}
		return "";
	}	
	
	
	
	
	
	
//	@PostMapping("/_listCmd")
//	public String postToViewCmdFrm(Model model, 
//			@RequestParam(name="action",required = false) String action, 
//			@RequestParam(name = "statutFilter", required = false) List<String> statutValues, 
//			@RequestParam(name = "ptVFilter", required = false) List<Long> ptVFilterValues,
//			HttpSession session) {
//		
//		if(action!=null && action.equalsIgnoreCase("effacerFilters")) {
//			ptVFilterValues = new ArrayList<Long>();
//			statutValues = new ArrayList<String>();
//		}
//
//		return getViewCmdFrm(model,action,statutValues,ptVFilterValues,session);
//	}
//		
//	@GetMapping("/_listCmd")
//	public String getViewCmdFrm(Model model, 
//			@RequestParam(name="action",required = false) String action, 
//			@RequestParam(name = "statutFilter", required = false) List<String> statutValues, 
//			@RequestParam(name = "ptVFilter", required = false) List<Long> ptVFilterValues,
//			HttpSession session) {
//		
//		if(ptVFilterValues==null ) 
//			ptVFilterValues = new ArrayList<Long>();			
//		
//		if(statutValues==null ) 			
//			statutValues = new ArrayList<String>();	
//
//		
//		addStandardParams(model, session);		
//		if ((statutValues ==null || statutValues.size() == 0) && (ptVFilterValues == null || ptVFilterValues.size()==0))
//			model.addAttribute("lstCmd", cmdR.getCommandesOrdered());
//		else if (statutValues.size() > 0 &&  (ptVFilterValues == null || ptVFilterValues.size()==0))
//			model.addAttribute("lstCmd", cmdR.getCommandesFilteredByStatut(statutValues));
//		else if ((statutValues ==null || statutValues.size() == 0) && ptVFilterValues.size()>0)
//			model.addAttribute("lstCmd", cmdR.getCommandesFilteredByPtV(ptVFilterValues));
//		else
//			model.addAttribute("lstCmd", cmdR.getCommandesFiltered(statutValues,ptVFilterValues));
//		
//		model.addAttribute("statutValues", Commande.StatutCommande.values());
//		model.addAttribute("statutSelectedValues", statutValues);
//		//session.setAttribute("statutSelectedValuesS", statutValues);
//		
//		model.addAttribute("pointsV", ptsVR.findAll());
//		model.addAttribute("ptvSelectedValues", ptVFilterValues);	
//	
//		List<Commande> lstCmd =(List<Commande>) model.getAttribute("lstCmd");
//		//List<Produit> lstProduits = new ArrayList<Produit>();
//		StatisticProduit stat=new StatisticProduit();
//		for (Commande cmd : lstCmd) {
//			for (CommandeProduit cp : cmd.getLignesCommandeProduit()) {
//				Produit curProd=cp.getProduit();
//				stat.addProduit(curProd, cp.getQte());				
//			}
//		}
//		model.addAttribute("stat", stat);	
//		
//		return "viewLstCommande";
//	}
	
	
	
	
	
}
