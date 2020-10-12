package data.repositorys;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import data.entitys.*;

@CrossOrigin("*")
@RepositoryRestResource
public interface RepoCommande   extends JpaRepository<Commande, Long>  {

	@Query("select cmd from Commande cmd JOIN FETCH cmd.lignesCommandeProduit cp JOIN FETCH cp.produit where cmd.user.id=:x")
	public Set<Commande> getCommandesUser(@Param("x") Long idUser);
}
