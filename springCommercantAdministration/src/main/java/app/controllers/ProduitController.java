package app.controllers;

import java.util.List;

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
import data.entitys.InfoCompProduit;
import data.entitys.LigneInfoCompProduit;
import data.entitys.Photo_CategorieProduit;
import data.entitys.Photo_Produit;
import data.entitys.Produit;


@Controller
public class ProduitController {
	@Autowired
	data.repositorys.RepoProduit prodR;
	
	@Autowired
	data.repositorys.RepoInfoCompProduit infoPR;
	@Autowired
	data.repositorys.RepoLigneInfoCompProduit lnInfoPR;
	
	@Autowired
	data.repositorys.RepoCategorieProduit catR;
	
	@Autowired
	data.repositorys.RepoPhoto_Produit photoR;

	
	private String redirectToEditProduit(Produit produitToEdit,	RedirectAttributes ra) {
		System.out.println("dans redirectToEditProduit " + produitToEdit);
		ra.addAttribute("produit", produitToEdit);		
		//ra.addFlashAttribute("produit", produitToEdit);		
		return "redirect:/editProduit";	
	}
	
	@GetMapping("/editProduit")
	public String modifierProduit(Model model,  Produit produitToEdit) {				

		if (produitToEdit!=null) {	
			if (produitToEdit.getId()!=null) {
				produitToEdit.setLstInfoComp(infoPR.getByProduit(produitToEdit.getId()));
				produitToEdit.setPhotos( photoR.getByProduit(produitToEdit.getId()));
			}			
			
			model.addAttribute("statutProduit", Produit.StatutProduit.values());
			model.addAttribute("lstCat", catR.findAll());
			model.addAttribute("produit", produitToEdit);	
			System.out.println("dans editProduit avant la vue: " + produitToEdit);
			
			return "viewEditProd";		
		}
		else
			return "redirect:/accueil";	
	}
	
	@GetMapping("/modifierProd")
	public String modifierProduit(Model model, @RequestParam(name="id") Long id, RedirectAttributes ra) {
		
		Produit produitToEdit =prodR.getProduitWithDependency(id);
				
		if (produitToEdit==null) 
			return "redirect:/accueil";		
		else {
			return redirectToEditProduit(produitToEdit,ra);
		}
	}	
	
	
	@GetMapping("addProdInCat")
	public String addProduitInCatFrm(Model model, @RequestParam(name="id") Long id, RedirectAttributes ra) {
		CategorieProduit categorieToEdit = null;
		
		categorieToEdit=catR.getCategoriesWithDependencyById(id);	
				
		if (categorieToEdit!=null) {
			
			Produit newProduit = new Produit();
			newProduit.setStatut(Produit.StatutProduit.Disponible.toString());
			newProduit.setCategorieProduit(categorieToEdit);
			model.addAttribute("statutProduit", Produit.StatutProduit.values());
			model.addAttribute("lstCat", catR.findAll());
			model.addAttribute("produit", newProduit);				
			
			return "viewEditProd";
			//return redirectToEditProduit(newProduit,ra);		
		}
		else
			return "redirect:/accueil";
	}
	
	@GetMapping("/addProduit")
	public String addProduitFrm(Model model, @ModelAttribute("produit") Produit newProduit,	RedirectAttributes ra) {	
		newProduit.setStatut(Produit.StatutProduit.Disponible.toString());		
		model.addAttribute("statutProduit", Produit.StatutProduit.values());
		model.addAttribute("lstCat", catR.findAll());
		model.addAttribute("produit", newProduit);	
		return "viewEditProd";
		//return redirectToEditProduit(newProduit,ra);	
	}
			
	
	@PostMapping("/saveProduit")
	public String saveProduit(@RequestParam(name="action",required = false) String action,
			@RequestParam(name="choixImg",required = false) MultipartFile file,	
			@ModelAttribute("produit") @Valid  Produit produitToEdit,
			BindingResult bindingRes,
			RedirectAttributes ra) {
				
		
		if (produitToEdit != null) {
			if (bindingRes.hasErrors()) {
				String msgErr = "";
				for (ObjectError elt : bindingRes.getAllErrors()) {
					msgErr += elt.toString();
				}
				System.out.println(" les erreurs dans la saisie: " + msgErr);				
				return "viewEditProd";
			}			

			if (action!=null && action.equalsIgnoreCase("addInfoComp")) {
				
				if (produitToEdit.getId()==null) {
					produitToEdit=prodR.save(produitToEdit);					
					InfoCompProduit newInfo=new InfoCompProduit(produitToEdit);					
					produitToEdit.getLstInfoComp().add(newInfo);
					infoPR.save(newInfo);						
					ra.addAttribute("id", produitToEdit.getId());
					return "redirect:/modifierProd";//Bizarrement, si on continue execution avec redirectToEditProduit(), ca produit StackOverFlow...
				}
				else					
					produitToEdit.getLstInfoComp().add(new InfoCompProduit(produitToEdit));
				
				saveProduitWithDependency(produitToEdit);	

				return redirectToEditProduit(produitToEdit,ra);			
			
			}	
			else if (action!=null && action.startsWith("addLigneInfo")) {
				
				Long idInfo=Long.valueOf(action.replace("addLigneInfo", ""));	
				InfoCompProduit infoToEdit =null;
				for (InfoCompProduit info : produitToEdit.getLstInfoComp()) {
						if (info.getId().equals(idInfo)) {
							infoToEdit=info;
							break;
						}
					}				
				if (infoToEdit!=null) {	
					LigneInfoCompProduit line = new LigneInfoCompProduit(infoToEdit);//
					infoToEdit.getLignesInfosComp().add(line);						
					lnInfoPR.save(line);
				}
					
				saveProduitWithDependency(produitToEdit);	
				return redirectToEditProduit(produitToEdit,ra);
				
			}
			else if (action!=null && action.equalsIgnoreCase("addImg") && file!=null) {
				return savePhoto(file, produitToEdit, ra);
			}
			
			else if (action!=null && action.startsWith("downImg")) {
				movePhoto(produitToEdit,action);//	
				return redirectToEditProduit(produitToEdit,ra);			

//				ra.addAttribute("id", produitToEdit.getId());
//				return "redirect:/modifierProd";
			}
			else if (action!=null && action.startsWith("upImg")) {
				movePhoto(produitToEdit,action);
//				ra.addAttribute("id", produitToEdit.getId());
//				return "redirect:/modifierProd";
				return redirectToEditProduit(produitToEdit,ra);			

			}
			else
				saveProduitWithDependency(produitToEdit);

		}
		return "redirect:/accueil";		
		
	}
	private Produit saveProduitWithDependency(Produit produitToEdit) {			
		prodR.save(produitToEdit);
		for (InfoCompProduit elt : produitToEdit.getLstInfoComp()) {			
			infoPR.save(elt);	
			
			for (LigneInfoCompProduit line : elt.getLignesInfosComp()) {
				lnInfoPR.save(line);
			}
		}	
		for (Photo_Produit photo : produitToEdit.getPhotos()) {
			photoR.updatePhotoInfo(photo.getId(), photo.getLegende(), photo.getOrdre());
		}
		return produitToEdit;
	}
	
	
	private void movePhoto(Produit produitToEdit, String action	) {		
	
		Long idPhoto=0l;		
		boolean isUp=true;
		try {
			if (action != null && action.startsWith("upImg")) {
				idPhoto = Long.valueOf(action.replace("upImg", ""));
			} else if (action != null && action.startsWith("downImg")) {
				isUp = false;
				idPhoto = Long.valueOf(action.replace("downImg", ""));				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		Photo_Produit photoToUpdate=null;
		for (Photo_Produit photo : produitToEdit.getPhotos()) {
			if (photo.getId().equals(idPhoto)) {
				photoToUpdate=photo;
				break;
			}
		}
		if (photoToUpdate!=null) {
			List<Integer>  lstOrders=null;
			if (isUp) 
				lstOrders=photoR.getPrevOrdre(idPhoto, photoToUpdate.getOrdre(), photoR.firstPage);
			else
				lstOrders=photoR.getNextOrdre(idPhoto, photoToUpdate.getOrdre(), photoR.firstPage);
					
			if (lstOrders.size()>0) {
				photoR.updatePhotoOrder(idPhoto, photoToUpdate.getOrdre(), lstOrders.get(0));
				photoR.updatePhotoInfo(idPhoto, photoToUpdate.getLegende(), lstOrders.get(0));						
			}
		}
		
	}
	
	public String savePhoto(
			@RequestParam("choixImg") MultipartFile file, 
			Produit produitToEdit,			
			RedirectAttributes ra) {
		
		
		if (produitToEdit != null) {
			prodR.save(produitToEdit);
			Integer prevOrdre=photoR.maxOrdre(produitToEdit.getId());
			if (prevOrdre==null) 
				prevOrdre=0;
			
			Photo_Produit newPhoto=new Photo_Produit();
			newPhoto.setImgData(Utilitys.getImageData(file)) ;
			newPhoto.setProduit(produitToEdit);
			newPhoto.setOrdre(prevOrdre + 1);
			photoR.save(newPhoto);			

			ra.addAttribute("id", produitToEdit.getId());
			return "redirect:/modifierProd";
			
		}
		return "redirect:/accueil";
	}
	
	@GetMapping("/supprimerPhotoProduit")
	public String supprimerPhoto(@RequestParam(name="id") Long id, 
			@RequestParam(name="idProd") Long idProd, RedirectAttributes ra) {		
		photoR.deleteById(id);
		ra.addAttribute("id",idProd);
		return "redirect:/modifierProd";
	}
		
	
	@GetMapping("/effacerCatProd")
	public String effacerCatProd(@RequestParam(name="id") Long id) {
		Produit produitToEdit=prodR.getOne(id);
		if (produitToEdit!=null) {
			produitToEdit.setCategorieProduit(null);
			prodR.save(produitToEdit);
		}
		return "redirect:/accueil";				
	}
	
	@GetMapping("/supprimerProd")
	public String supprimerProd(@RequestParam(name="id") Long id) {
		if (id!=null) {
			Produit produitToDelete=prodR.getOne(id);
			if (produitToDelete!=null) {
				lnInfoPR.deleteByProduit(id);			
				infoPR.deleteByProduit(id);
				photoR.deleteByProduit(id);
				prodR.delete(produitToDelete);		
			}
								
		}
		return "redirect:/accueil";		
	}
	
	@GetMapping("/supprimerInfo")
	public String supprimerInfo(Model model, @RequestParam(name="id") Long id, @RequestParam(name="idProd") Long idProd, RedirectAttributes ra) {
		if (id!=null) {
			InfoCompProduit infoToDelete = infoPR.getOne(id);
			if (infoToDelete!=null) {
				infoToDelete.setProduit(null);
				lnInfoPR.deleteByInfoComp(id);			
				infoPR.delete(infoToDelete);				
			}
		}
		if (idProd!=null) {
			Produit produitToEdit =prodR.getProduitWithDependency(idProd);
			if (produitToEdit!=null) {
				return redirectToEditProduit(produitToEdit,ra);		
			}
		}
		return "redirect:/accueil";	
	}
	
	@GetMapping("/supprimerLigneInfo")
	public String supprimerLigneInfo(Model model, 
			@RequestParam(name="id") Long id, @RequestParam(name="idProd") Long idProd, 
			RedirectAttributes ra) {
		
		if (id!=null) {
			LigneInfoCompProduit entToDelete = lnInfoPR.getOne(id);
			if (entToDelete!=null) {
				entToDelete.setInfoComp(null);
				lnInfoPR.delete(entToDelete);	
			}
		}
		if (idProd!=null) {
			Produit produitToEdit =prodR.getProduitWithDependency(idProd);
			if (produitToEdit!=null) {
				return redirectToEditProduit(produitToEdit,ra);		
			}
		}
		return "redirect:/accueil";
	}
}


