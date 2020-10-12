package data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import data.entitys.Produit;
import data.repositorys.RepoProduit;

@SpringBootApplication
public class SpringCommercantDataApplication implements CommandLineRunner  {
	@Autowired
	private RepoProduit products;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringCommercantDataApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	
		int cpt=products.findAll().size();
		if (cpt==0) {
			Produit p1 = new Produit("saumon", "poisson d'elevage", "filet de 200g", 22f, 5f);
			products.save(p1);
			Produit p2 = new Produit("saumon2", "poisson d'elevage2", "filet de 400g", 28f, 4f);
			products.save(p2);			
		}
		
	}

}
