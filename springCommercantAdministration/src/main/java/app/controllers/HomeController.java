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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import data.Utilitys;
import data.entitys.CategorieProduit;
import data.entitys.Commande;
import data.entitys.PointVente;
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
	
	@Autowired
	data.repositorys.RepoPointVente ptsVR;
	
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
		
		// System.out.println("dans listUser get: ");
		model.addAttribute("pointsV", ptsVR.findAll());
		model.addAttribute("ptvSelectedValues", ptVFilterValues);
		List<User> users = null;
		if (ptVFilterValues != null && ptVFilterValues.size() > 0) {
			users = usrR.findAllByListPointsVente(ptVFilterValues);
		} else
			users = usrR.findAll();

		List<String> statutValues = new ArrayList<String>();
		statutValues.add(Commande.StatutCommande.Finalise.toString());
		for (User usr : users) {
			usr.setNbCmd(cmdR.getCountByUserFiltered(usr.getId(), statutValues));
			usr.setTotalCmd(cmdR.getTotalByUserFiltered(usr.getId(), statutValues));
		}
		model.addAttribute("lstUsr", users);

		return "viewLstUser";
	}
	
	@PostMapping("/saveUser")
	public String postListUser(Model model,@ModelAttribute("user")User user,
			@RequestParam(name="action",required = false) String action, 
			@RequestParam(name = "ptV", required = false) List<Long> selectedValues,
			@RequestParam(name="redirectAction",required = false) String redirectToAction) {				
		
		if(action!=null && action.equalsIgnoreCase("savePtV")) {
			return savePointsVente(model, user, action, selectedValues,redirectToAction);//			
		}
		return getListUserFrm(model,null);
	}
	
	public String savePointsVente(Model model, @ModelAttribute("user") User user,
			@RequestParam(name = "action", required = false) String action,
			@RequestParam(name = "ptV", required = false) List<Long> selectedValues,
			@RequestParam(name="redirectAction",required = false) String redirectToAction) {

		if (selectedValues == null)
			selectedValues = new ArrayList<Long>();

		user = usrR.getOne(user.getId());		
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
		return getUserFrm(model,user.getId(),action,redirectToAction);	

	}
	
	@GetMapping("/viewUser")
	public String getUserFrm(Model model,@RequestParam(name = "id") Long id,@RequestParam(name="action",required = false) String action,
			@RequestParam(name="redirectAction",required = false) String redirectToAction) {
		
		
		Set<CategorieProduit> lstCat = catR.getCategoriesWithDependency();
		model.addAttribute("lstCat", lstCat);
		
		List<String> statutValues = new ArrayList<String>();
		statutValues.add(Commande.StatutCommande.Finalise.toString());
		User usr=(User) usrR.getOne(id)	;
		usr.setCommandes(cmdR.getCommandesUser(id));
		usr.setPointsVente(ptsVR.findAllByUsers(usr));
		usr.setNbCmd(cmdR.getCountByUserFiltered(id,statutValues ));		
		usr.setTotalCmd(cmdR.getTotalByUserFiltered(id,statutValues ));
		
		String redirectAction="listUser";
		if (action!=null && action.equalsIgnoreCase("viewUserFromCommande")) {
			redirectAction="listCmd";
		}
		
		model.addAttribute("redirectAction", redirectAction);
		
		model.addAttribute("user", usr);
		
		List<PointVente> pointsV = ptsVR.findAll();
		model.addAttribute("pointsV",pointsV);
		model.addAttribute("pointsVselected",usr.getLstIdPtv());
		
		return 	"viewCompte";
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
		PointVente pointVenteToEdit =ptsVR.getOne(id);
		if (pointVenteToEdit==null) 
			return "redirect:/accueil";		
		else {			
			model.addAttribute("ptV", pointVenteToEdit);
			return "viewPointVente";
		}		
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
				/*
				 * ptsVR.updateInfo(pointVenteToEdit.getId(), pointVenteToEdit.getLibelle(),
				 * pointVenteToEdit.getDescription(), pointVenteToEdit.getEmplacementText(),
				 * pointVenteToEdit.getHorairesText(), pointVenteToEdit.getInfoComp(),
				 * pointVenteToEdit.getPhotoTitre(), pointVenteToEdit.isActif());
				 */
				ptsVR.updatePhoto(pointVenteToEdit.getId(), Utilitys.getImageData(file));
				return getPointVenteFrm(model,pointVenteToEdit.getId());	
			}		
			else if (action!=null && action.equalsIgnoreCase("delImg")) {
				if(pointVenteToEdit.getId()==null) {
					ptsVR.save(pointVenteToEdit);	
				}	
				updatePointVenteSansPhoto(pointVenteToEdit);
				/*
				 * ptsVR.updateInfo(pointVenteToEdit.getId(), pointVenteToEdit.getLibelle(),
				 * pointVenteToEdit.getDescription(), pointVenteToEdit.getEmplacementText(),
				 * pointVenteToEdit.getHorairesText(), pointVenteToEdit.getInfoComp(),
				 * pointVenteToEdit.getPhotoTitre(), pointVenteToEdit.isActif());
				 */
				ptsVR.deletePhoto(pointVenteToEdit.getId());				
				return getPointVenteFrm(model,pointVenteToEdit.getId());	
			}
			else
				if(pointVenteToEdit.getId()==null) {
					ptsVR.save(pointVenteToEdit);	
				}
				updatePointVenteSansPhoto(pointVenteToEdit);
				/*
				 * ptsVR.updateInfo(pointVenteToEdit.getId(), pointVenteToEdit.getLibelle(),
				 * pointVenteToEdit.getDescription(), pointVenteToEdit.getEmplacementText(),
				 * pointVenteToEdit.getHorairesText(), pointVenteToEdit.getInfoComp(),
				 * pointVenteToEdit.getPhotoTitre(), pointVenteToEdit.isActif());
				 */
					
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
