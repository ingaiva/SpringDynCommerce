package app.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import app.utility.FilterCmd;
import app.utility.StatisticProduit;
import app.utility.UserModel;
import data.Utilitys;
import data.entitys.CategorieProduit;
import data.entitys.Commande;
import data.entitys.CommandeProduit;
import data.entitys.PointVente;
import data.entitys.Produit;
import data.entitys.User;



@Controller
//@RequestMapping("/")
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
	
	@Autowired
	data.repositorys.RepoPointVente ptsVR;
	
	@GetMapping({ "/","/accueil" })
	public String getAcceuilFrm(Model model) {			
		return "index";

	}
	
	@PostMapping("/listUser")
	public String postListUserFrm(Model model, 
			@RequestParam(name="action",required = false) String action, 
			@RequestParam(name = "ptVFilter", required = false) List<Long> ptVFilterValues,
			HttpSession session) {
		
		if(action!=null && action.equalsIgnoreCase("effacerFilters")) {
			ptVFilterValues = new ArrayList<Long>();
		}
		if (ptVFilterValues == null) {
			
			List<Long> sessionSelectedValues = (List<Long>) session.getAttribute("ptVFilterValuesS");
			if(sessionSelectedValues==null)
			{				
				ptVFilterValues = new ArrayList<Long>();
			}
			else
				ptVFilterValues=sessionSelectedValues;
		}
				

		session.setAttribute("ptVFilterValuesS", ptVFilterValues);		
		return getListUserFrm(model,ptVFilterValues);
	}
	
	@GetMapping("/listUser")
	public String getListUserFrm(Model model, @RequestParam(name = "ptVFilter", required = false) List<Long> ptVFilterValues) {
		

		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();
		model.addAttribute("lstCat", lstCat);		
		
		model.addAttribute("pointsV", ptsVR.findAll());
		model.addAttribute("ptvSelectedValues", ptVFilterValues);
		List<User> users = null;
		if (ptVFilterValues != null && ptVFilterValues.size() > 0) {
			users = usrR.findAllByListPointsVente(ptVFilterValues);
		} else
			users = usrR.findAll();

//		List<String> statutValues = new ArrayList<String>();
//		statutValues.add(Commande.StatutCommande.Finalise.toString());
//		for (User usr : users) {
//			usr.setNbCmd(cmdR.getCountByUserFiltered(usr.getId(), statutValues));
//			usr.setTotalCmd(cmdR.getTotalByUserFiltered(usr.getId(), statutValues));
//		}
		model.addAttribute("lstUsr", users);
		model.addAttribute("redirectAction", "listUser");
		return "viewLstUser";
	}
	
	@PostMapping("/viewUser")
	public ModelAndView postUser(Model model,@ModelAttribute("userModel")UserModel uModel,
			@RequestParam(name="action",required = false) String action, 
			@RequestParam(name = "ptV", required = false) List<Long> selectedValues,
			@RequestParam(name="redirectAction",required = false) String redirectToAction) {	
		
		
		if(uModel.getFilter()==null ) 
			uModel.setFilter(new FilterCmd()) ;
		if(action!=null && action.equalsIgnoreCase("effacerFilters")) 
			uModel.getFilter().effacer();	
		else if(action!=null ) 
			uModel.getFilter().checkDates();
		
		if(action!=null && action.equalsIgnoreCase("savePtV")) {
			return savePointsVente(model, uModel, action, selectedValues,redirectToAction);//			
		}
		else if(action!=null ) {
			return getUserFrm(model,uModel.getUser().getId(),action,redirectToAction,uModel.getFilter());
		}
		
		
		return new ModelAndView("listUser"); //getListUserFrm(model,null);;
	}
	
	private ModelAndView savePointsVente(Model model, @ModelAttribute("userModel")UserModel uModel,
			@RequestParam(name = "action", required = false) String action,
			@RequestParam(name = "ptV", required = false) List<Long> selectedValues,
			@RequestParam(name="redirectAction",required = false) String redirectToAction) {
		
				
		if (selectedValues == null)
			selectedValues = new ArrayList<Long>();
		
		
		User user = usrR.getOne(uModel.getUser().getId());		
		List<PointVente> lstInDb = ptsVR.findAllByUsers(user);
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
		return getUserFrm(model,user.getId(),action,redirectToAction,uModel.getFilter());		
	}
	

	@PostMapping("/user")
	public ModelAndView postUserFrm(Model model,
			@RequestParam(name = "id") Long id,
			@RequestParam(name="action",required = false) String action,
			@RequestParam(name="redirectAction",required = false) String redirectToAction,
			RedirectAttributes ra) {		
		
		return getUserFrm(model,id,action,redirectToAction,null);
	}

	@GetMapping("/viewUser")
	public ModelAndView getUserView(@RequestParam(name = "id") Long id,Model model) {
		return getUserFrm(model, id, null, null,null);
	}
	//@GetMapping("/viewUser")
	public ModelAndView getUserFrm(Model model,
			@RequestParam(name = "id") Long id,
			@RequestParam(name="action",required = false) String action,
			@RequestParam(name="redirectAction",required = false) String redirectToAction,
			@RequestParam(name = "filter",required = false) FilterCmd filter) {
		
		if(usrR.existsById(id)) {
			
			if(redirectToAction==null) {
				redirectToAction="/listUser";
			}
			model.addAttribute("redirectAction", redirectToAction);
			
			Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();
			model.addAttribute("lstCat", lstCat);
			
			if(filter==null)
				filter = new FilterCmd(id);
			
			User usr=(User) usrR.getOne(id)	;		
			usr.setCommandesFiltered(cmdR.findAll(filter.getCriteria(),Sort.by(Sort.Direction.DESC, "id")));		
					
			UserModel uModel=new UserModel(usr,filter);
			model.addAttribute("userModel", uModel);
			
			List<PointVente> pointsV = ptsVR.findAll();
			model.addAttribute("pointsV",pointsV);
			model.addAttribute("pointsVselected",usr.getLstIdPtv());
			
			List<Commande> lstCmd =usr.getCommandesFiltered();		
			StatisticProduit stat=new StatisticProduit();
			if (action != null && action.equalsIgnoreCase("loadStats") && lstCmd!=null) {
				stat.setLoaded(true);
				for (Commande cmd : lstCmd) {
					for (CommandeProduit cp : cmd.getLignesCommandeProduit()) {
						Produit curProd = cp.getProduit();
						stat.addProduit(curProd, cp.getQte());
					}
				}
			}			
			model.addAttribute("stat", stat);	
			
			return new ModelAndView("viewCompte", model.asMap());
			
		}
		else {
			model.addAttribute("msg", "Client demandé n'existe pas");
			return new ModelAndView("error", model.asMap());//"error";
		}
			
		//return 	"viewCompte";
	}
	
	@GetMapping("/listPointVente")
	public String getListPointVenteFrm(Model model) {
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();
		model.addAttribute("lstCat", lstCat);
		List<PointVente> pointsV = ptsVR.findAll();
		model.addAttribute("pointsV",pointsV);
		
		return "viewLstPointVente";
	}
	@GetMapping("/addPtVente")
	public String getNewPointVenteFrm(@ModelAttribute("ptV") PointVente newPointVente) {		
		return "viewPointVente";
	}
	
	@GetMapping("/viewPtVente")
	public String getPointVenteFrm(Model model, @RequestParam(name="id") Long id) {
		if(ptsVR.existsById(id)) {
			PointVente pointVenteToEdit =ptsVR.getOne(id);
			if (pointVenteToEdit==null) 
				return "redirect:/accueil";		
			else {			
				model.addAttribute("ptV", pointVenteToEdit);
				return "viewPointVente";
			}		
			
		}
		else
			model.addAttribute("msg", "Le point de vente demandé n'existe pas");
			return "error";
	}
	
	private void updatePointVenteSansPhoto(PointVente pointVenteToEdit) {
		
		ptsVR.updateInfo(pointVenteToEdit.getId(), pointVenteToEdit.getLibelle(), pointVenteToEdit.getDescription(), 
				pointVenteToEdit.getEmplacementText(), pointVenteToEdit.getHorairesText(), 
				pointVenteToEdit.getInfoComp(), pointVenteToEdit.getPhotoTitre(), pointVenteToEdit.isActif());
		
		ptsVR.updateHoraires(pointVenteToEdit.getId(), 
				pointVenteToEdit.isLundi(), pointVenteToEdit.getHorairesLundi(), 
				pointVenteToEdit.isMardi(), pointVenteToEdit.getHorairesMardi(), 
				pointVenteToEdit.isMercredi(), pointVenteToEdit.getHorairesMercredi(), 
				pointVenteToEdit.isJeudi(), pointVenteToEdit.getHorairesJeudi(), 
				pointVenteToEdit.isVendredi(), pointVenteToEdit.getHorairesVendredi(), 
				pointVenteToEdit.isSamedi(), pointVenteToEdit.getHorairesSamedi(), 
				pointVenteToEdit.isDimanche(), pointVenteToEdit.getHorairesDimanche());
		//updateHoraires
	}
	
	@PostMapping("/savePtV")
	public String savePointVente(Model model, @RequestParam(name="action",required = false) String action, 
			@RequestParam(name="choixImg",required = false) MultipartFile file,			
			@ModelAttribute("ptV") @Valid PointVente pointVenteToEdit, 
			BindingResult bindingRes,
			RedirectAttributes ra) {
		
		if (pointVenteToEdit != null) {
			if (bindingRes.hasErrors()) {
				String msgErr = "";
				for (ObjectError elt : bindingRes.getAllErrors()) {
					msgErr += elt.toString();
				}
				System.out.println(" les erreurs dans la saisie: " + msgErr);	
				
				return "viewPointVente";
			}
			
			if (action!=null && action.equalsIgnoreCase("addImg") && file!=null) {
				if(pointVenteToEdit.getId()==null) {
					ptsVR.save(pointVenteToEdit);	
				}	
				updatePointVenteSansPhoto(pointVenteToEdit);				
				ptsVR.updatePhoto(pointVenteToEdit.getId(), Utilitys.getImageData(file));
				return getPointVenteFrm(model,pointVenteToEdit.getId());	
			}		
			else if (action!=null && action.equalsIgnoreCase("delImg")) {
				if(pointVenteToEdit.getId()==null) {
					ptsVR.save(pointVenteToEdit);	
				}	
				updatePointVenteSansPhoto(pointVenteToEdit);				
				ptsVR.deletePhoto(pointVenteToEdit.getId());				
				return getPointVenteFrm(model,pointVenteToEdit.getId());	
			}
			else
				if(pointVenteToEdit.getId()==null) {
					ptsVR.save(pointVenteToEdit);	
				}
				updatePointVenteSansPhoto(pointVenteToEdit);			
					
		}
		
		return "redirect:/listPointVente";	
	}
	@PostMapping("/deletePtVente")
	public String deletePtVente(Model model,@ModelAttribute("ptV") PointVente pointVenteToDelete) {
		PointVente pointVenteDb =ptsVR.getOne(pointVenteToDelete.getId());
		pointVenteDb.setUsers(new ArrayList<User>());
		cmdR.deletePointVenteFromCommande(pointVenteToDelete.getId());
		pointVenteDb.setCommandes(null);
		ptsVR.save(pointVenteDb);
		ptsVR.delete(pointVenteDb);
		return "redirect:/listPointVente";	
	}
	
	@GetMapping("/deletePtVenteRequest")
	public String deletePtVenteFrm(Model model, @RequestParam(name="id") Long id) {
		
		if (! ptsVR.existsById(id)) 
			return "redirect:/accueil";		
		else {	
			PointVente pointVenteToEdit =ptsVR.getOne(id);
			model.addAttribute("ptV", pointVenteToEdit);
			return "viewSuppressionPointVente";
		}	
	}
}
