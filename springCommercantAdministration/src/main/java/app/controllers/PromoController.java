package app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import data.entitys.Promotion;

@Controller
public class PromoController {

	@GetMapping("/addPromo")
	public String editPromoFrm(Model model) {		
		model.addAttribute("promo", new Promotion());
		return "viewEditPromotion";
	}
}
