package app.controllers;

import java.util.ArrayList;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import data.Utilitys;
import data.entitys.ParamMainPage;
import data.entitys.Photo_param;
import data.entitys.Promotion;

@Controller
public class MainPageController {
	@Autowired
	data.repositorys.RepoParamMainPage paramR;
	
	@Autowired
	data.repositorys.RepoPhoto_param phR;
	
	@Autowired
	data.repositorys.RepoCategorieProduit catR;
	
	@GetMapping("/mainPage")
	public String paramMainPageFrm(Model model) {
		
		ParamMainPage param= null;
		Optional<ParamMainPage> paramObj=paramR.findById(1L);
		if (!paramObj.isPresent()) {
			param=new ParamMainPage();
			param.setId(1L);
			paramR.save(param);
		}
		else {			
			param=paramObj.get();			
		}
		
		param.setPhotos(phR.getPhotos(1L));		
		
		model.addAttribute("lstCat", catR.getCategoriesWithDependency());
		
		
		model.addAttribute("paramP", param);	
	// pour tester
		ArrayList<Promotion> arrPromo = new ArrayList<Promotion>();
		for (int i = 0; i < 6; i++) {
			arrPromo.add(new Promotion());
		}
		model.addAttribute("lstPromo",arrPromo);
		
		return "viewMainPage";
	}
	
	
	@PostMapping("/saveParam")
	public String saveParam(@RequestParam(name="action",required = false) String action, 
			@RequestParam(name="choixLogo",required = false) MultipartFile fileLogo,
			@RequestParam(name="choixBG",required = false) MultipartFile fileBg,
			@RequestParam(name="choixImg",required = false) MultipartFile fileImg,
			@ModelAttribute("paramP") @Valid ParamMainPage param, 
			BindingResult bindingRes,
			RedirectAttributes ra) {
		
		
		if (param!=null) {			
			if (action!=null && action.equalsIgnoreCase("addLogo") && fileLogo!=null) {
				return saveLogo(fileLogo, param);
			}else if (action!=null && action.equalsIgnoreCase("addBG") && fileBg!=null) {
				return saveBG(fileBg,param);
			}else if (action!=null && action.equalsIgnoreCase("addImg") && fileImg!=null) {
				return saveImg(fileImg,param);
			}
			else if (action!=null && action.startsWith("logoPosition")) {
				String logoPosition=action.replace("logoPosition", "");
				paramR.updateInfo(param.getTexteAccueil(), param.getLogoTitre(), 
						logoPosition, param.isShowCategories(), param.isShowPromo(), param.getStatutPromoToShow(), param.getPromoTitre());	
			}
			else if(action!=null && action.equalsIgnoreCase("addContact")) {
				paramR.updateInfo(param.getTexteAccueil(), param.getLogoTitre(), 
						param.getLogoPosition(), param.isShowCategories(), param.isShowPromo(), param.getStatutPromoToShow(), param.getPromoTitre());
				paramR.updateContact(param.getContactTitre(), param.getContactTel1(), param.getContactTel2(), param.getContactMail());	
				}
			else if(action!=null && action.equalsIgnoreCase("addHoraires")) {
				paramR.updateInfo(param.getTexteAccueil(), param.getLogoTitre(), 
						param.getLogoPosition(), param.isShowCategories(), param.isShowPromo(), param.getStatutPromoToShow(), param.getPromoTitre());
				paramR.updateHoraires(param.getHorairesTitre(), param.getHorairesText());				
			}			
			else if(action!=null && action.equalsIgnoreCase("editTitrePromo")) {					
				paramR.updateInfo(param.getTexteAccueil(), param.getLogoTitre(), 
						param.getLogoPosition(), param.isShowCategories(), param.isShowPromo(), param.getStatutPromoToShow(), param.getPromoTitre());				
			}
			else
				paramR.updateInfo(param.getTexteAccueil(), param.getLogoTitre(), 
						param.getLogoPosition(), param.isShowCategories(), param.isShowPromo(), param.getStatutPromoToShow(), param.getPromoTitre());				
			
		}
		
		return "redirect:/mainPage";	
	}
	
	
	
	public String saveLogo(@RequestParam(name="choixLogo",required = false) MultipartFile fileLogo, 
			@ModelAttribute("paramP") @Valid ParamMainPage param) {				
		
		if (param != null) {
			System.out.println("dans save logo " + param);
			paramR.updateInfo(param.getTexteAccueil(), param.getLogoTitre(), param.getLogoPosition(), param.isShowCategories(), param.isShowPromo(), param.getStatutPromoToShow(), param.getPromoTitre());				
		
			if (fileLogo.getSize()>0) {				
				paramR.updateLogo(Utilitys.getImgDataTh(fileLogo, 200,200));				
			}
			return "redirect:/mainPage";			
		}
		return "redirect:/accueil";
	}
	
	public String saveBG(@RequestParam(name="choixBG",required = false) MultipartFile fileBg, 
			@ModelAttribute("paramP") @Valid ParamMainPage param) {
		
		if (param != null) {
			
			paramR.updateInfo(param.getTexteAccueil(), param.getLogoTitre(), param.getLogoPosition(), param.isShowCategories(), param.isShowPromo(), param.getStatutPromoToShow(), param.getPromoTitre());				
		
			if (fileBg.getSize()>0) {				
				paramR.updateBG(Utilitys.getImageData(fileBg));				
			}
			return "redirect:/mainPage";			
		}
		return "redirect:/accueil";			
	}
	
	public String saveImg(@RequestParam(name="choixImg",required = false) MultipartFile fileImg, 
			@ModelAttribute("paramP") @Valid ParamMainPage param) {
				
		if (param != null) {
			
			paramR.updateInfo(param.getTexteAccueil(), param.getLogoTitre(), 
					param.getLogoPosition(), param.isShowCategories(), param.isShowPromo(), param.getStatutPromoToShow(), param.getPromoTitre());	
			
			Integer prevOrdre=phR.maxOrdre(param.getId());
			if (prevOrdre==null) 
				prevOrdre=0;
			
			Photo_param newPhoto=new Photo_param();
			newPhoto.setImgData(Utilitys.getImageData(fileImg)) ;
			newPhoto.setParam(param);
			newPhoto.setOrdre(prevOrdre + 1);
			phR.save(newPhoto);	
			return "redirect:/mainPage";
		}
		return "redirect:/accueil";
	}
	
	
	@GetMapping("/supprimerLogo")
	public String supprimerLogo() {
		paramR.deleteLogo();	
		return "redirect:/mainPage";
	}
	
	@GetMapping("/supprimerBG")
	public String supprimerBG() {
		paramR.deleteBG();	
		return "redirect:/mainPage";
	}
	
	@GetMapping("/supprimerImgParam")
	public String supprimerImgParam(@RequestParam(name="id") Long id) {		
		phR.deleteById(id);
		return "redirect:/mainPage";
	}
}
