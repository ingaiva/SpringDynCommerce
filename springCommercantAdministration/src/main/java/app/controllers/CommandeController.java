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

import data.entitys.CategorieProduit;
import data.entitys.Commande;
import data.entitys.CommandeProduit;
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
	data.repositorys.RepoCategorieProduit catR;

	private void addStandardParams(Model model, HttpSession session) {
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();
		model.addAttribute("lstCat", lstCat);
	}
	
	@PostMapping("/listCmd")
	public String postToViewCmdFrm(Model model, @RequestParam(name="action",required = false) String action, @RequestParam(name = "statutFilter", required = false) List<String> statutValues, HttpSession session) {
		
		return getViewCmdFrm(model,action,statutValues,session);
	}
		
	@GetMapping("/listCmd")
	public String getViewCmdFrm(Model model, @RequestParam(name="action",required = false) String action, @RequestParam(name = "statutFilter", required = false) List<String> statutValues, HttpSession session) {
		
		if(action!=null && action.equalsIgnoreCase("effacerFilters")) {
			statutValues = new ArrayList<String>();
		}
		if (statutValues == null) {
			
			List<String> sessionSelectedValues = (List<String>) session.getAttribute("statutSelectedValuesS");
			if(sessionSelectedValues==null)
			{				
				statutValues = new ArrayList<String>();
			}
			else
				statutValues=sessionSelectedValues;
		}
		addStandardParams(model, session);		
		if (statutValues.size() == 0)
			model.addAttribute("lstCmd", cmdR.getCommandesOrdered());
		else
			model.addAttribute("lstCmd", cmdR.getCommandesFiltered(statutValues));
		
		model.addAttribute("statutValues", Commande.StatutCommande.values());
		model.addAttribute("statutSelectedValues", statutValues);
		session.setAttribute("statutSelectedValuesS", statutValues);
		return "viewLstCommande";
	}
	
	@GetMapping("/viewCommande")
	public String getViewCommandeFrm(Model model,@RequestParam(name = "id") Long idCommande,  HttpSession session) {
		addStandardParams(model,session);
		Commande cmd= cmdR.getOne(idCommande);		
		model.addAttribute("cmd", cmd);		
		return "viewCommande";	
	}
	
	@PostMapping("/saveCommande")
	public String saveCommande(Model model, @RequestParam(name="action",required = false) String action,
			@ModelAttribute("cmd") @Valid Commande cmd, BindingResult bindingRes, HttpSession session, RedirectAttributes ra) {
		
		System.out.println(action);
		
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
				if(isCmdExistante && deleteProduitRequest ) {
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
