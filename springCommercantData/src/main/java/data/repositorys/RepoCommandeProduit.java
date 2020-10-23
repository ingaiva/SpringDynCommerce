package data.repositorys;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import data.entitys.*;

@CrossOrigin("*")
@RepositoryRestResource
public interface RepoCommandeProduit  extends JpaRepository<CommandeProduit, Long>   {
	@Transactional
	@Modifying
	@Query("delete from CommandeProduit cp where cp.commande.id=:x")
	public void deleteByCommande(@Param("x") Long id);
	
	@Transactional
	@Modifying
	@Query("delete from CommandeProduit cp where cp.commande.id in (select DISTINCT c.id from Commande c  where c.user.id=:x)")
	public void deleteByUser(@Param("x") Long idUser);
	
	@Transactional
	@Modifying
	@Query("delete from CommandeProduit cp where cp.commande.id=:x and cp.id NOT IN (:y)")
	public void deleteNotIncluded(@Param("x") Long id,@Param("y") ArrayList<Long> lstToExclude);
}
