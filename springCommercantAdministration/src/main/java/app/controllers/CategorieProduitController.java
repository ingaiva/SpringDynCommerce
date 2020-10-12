package app.controllers;

import java.util.List;
import java.util.Set;

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
import data.entitys.Photo_CategorieProduit;
import data.entitys.Produit;

@Controller
public class CategorieProduitController {
	@Autowired
	data.repositorys.RepoCategorieProduit catR;
	@Autowired
	data.repositorys.RepoProduit prodR;
	
	@Autowired
	data.repositorys.RepoPhoto_CategorieProduit photoR;
	
	@GetMapping({ "/addCategorie" })
	public String getCatCreationFrm(@ModelAttribute("categorie") CategorieProduit newCategorie) {
		return "viewEditCat";
	}
	
	@GetMapping("/modifierCat")
	public String modifierCategorie(Model model, @RequestParam(name="id") Long id) {
		CategorieProduit categorieToEdit =catR.getOne(id);
		if (categorieToEdit==null) 
			return "redirect:/accueil";		
		else {
			categorieToEdit.setPhotos( photoR.getByCategorie(categorieToEdit.getId()));
			model.addAttribute("categorie", categorieToEdit);
			return "viewEditCat";
		}
	}

	
	@GetMapping("/supprCat")
	public String supprimerCategorie(@RequestParam(name="id") Long id) {
		CategorieProduit categorieToDelete =catR.getOne(id);
		if (categorieToDelete!=null) 
		{
			Set<Produit> lstProd=prodR.getProduitWithDependencyByCategorie(id);
			for (Produit prod : lstProd) {
				prod.setCategorieProduit(null);
				prodR.save(prod);
			}
			photoR.deleteByCategorie(id);
			catR.delete(categorieToDelete);			
		}
		return "redirect:/accueil";				
	}
	
	@PostMapping({"/saveCategorie"})
	public String saveCategorie(
			@RequestParam(name="action",required = false) String action, 
			@RequestParam(name="choixImg",required = false) MultipartFile file,			
			@ModelAttribute("categorie") @Valid CategorieProduit categorieToEdit, 
			BindingResult bindingRes,
			RedirectAttributes ra) {
				
		
		if (categorieToEdit != null) {
			if (bindingRes.hasErrors()) {
				String msgErr = "";
				for (ObjectError elt : bindingRes.getAllErrors()) {
					msgErr += elt.toString();
				}
				System.out.println(" les erreurs dans la saisie: " + msgErr);	
				if (categorieToEdit.getId()==null) 
					return "viewNewCat";
				else
					return "viewEditCat";				
			}
			
			if (action!=null && action.equalsIgnoreCase("addImg") && file!=null) {
				return savePhoto(file, categorieToEdit, ra);
			}
			
			else if (action!=null && action.startsWith("downImg")) {
				movePhoto(categorieToEdit,action);//				
				ra.addAttribute("id", categorieToEdit.getId());
				return "redirect:/modifierCat";
			}
			else if (action!=null && action.startsWith("upImg")) {
				movePhoto(categorieToEdit,action);
				ra.addAttribute("id", categorieToEdit.getId());
				return "redirect:/modifierCat";				
			}
			else

				catR.save(categorieToEdit);
				for (Photo_CategorieProduit photo : categorieToEdit.getPhotos()) {
					photoR.updatePhotoInfo(photo.getId(), photo.getLegende(), photo.getOrdre());
				}
		}
		return "redirect:/accueil";
	}
		
	
	private void movePhoto(CategorieProduit categorieToEdit, String action	) {
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
		
		Photo_CategorieProduit photoToUpdate=null;
		for (Photo_CategorieProduit photo : categorieToEdit.getPhotos()) {
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
			@ModelAttribute("categorie")  CategorieProduit categorieToEdit,			
			RedirectAttributes ra) {
		
		System.out.println("dans save photo");
		
		if (categorieToEdit != null) {
			catR.save(categorieToEdit);
			Integer prevOrdre=photoR.maxOrdre(categorieToEdit.getId());
			if (prevOrdre==null) 
				prevOrdre=0;
			
			Photo_CategorieProduit newPhoto=new Photo_CategorieProduit();
			newPhoto.setImgData(Utilitys.getImageData(file)) ;//file.getBytes());
			newPhoto.setCategorieProduit(categorieToEdit);
			newPhoto.setOrdre(prevOrdre + 1);
			photoR.save(newPhoto);			

			ra.addAttribute("id", categorieToEdit.getId());
			return "redirect:/modifierCat";
			
		}
		return "redirect:/accueil";
	}
	
	@GetMapping("/supprimerPhotoCategorie")
	public String supprimerPhoto(@RequestParam(name="id") Long id, 
			@RequestParam(name="idCat") Long idCat, RedirectAttributes ra) {
		
		photoR.deleteById(id);
		ra.addAttribute("id", idCat);
		return "redirect:/modifierCat";
	}
	
}
