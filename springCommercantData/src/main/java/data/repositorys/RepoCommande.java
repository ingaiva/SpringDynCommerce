package data.repositorys;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
public interface RepoCommande   extends JpaRepository<Commande, Long>  {

	@Query("select cmd from Commande cmd where cmd.user.id=:x  ORDER BY cmd.date DESC")
	public List<Commande> getCommandesUser(@Param("x") Long idUser);
	
	@Query("select SUM(cmd.totalFinal) from Commande cmd where cmd.user.id=:x and cmd.statut in (:y)")
	public Float getTotalByUserFiltered(@Param("x") Long idUser, @Param("y") List<String> statutValues);
	
	@Query("select count(*) from Commande cmd where cmd.user.id=:x and cmd.statut in (:y)")
	public Integer getCountByUserFiltered(@Param("x") Long idUser, @Param("y") List<String> statutValues);
	
	@Query("select cmd from Commande cmd where cmd.user.id=:x and cmd.statut in (:y) ORDER BY cmd.date DESC")
	public List<Commande> getCommandesUserFiltered(@Param("x") Long idUser, @Param("y") List<String> statutValues);
	
	@Query("select cmd from Commande cmd where cmd.statut in (:y) ORDER BY cmd.date DESC")
	public List<Commande> getCommandesFilteredByStatut(@Param("y") List<String> statutValues);
	
	@Query("select cmd from Commande cmd where cmd.pointVente.id in (:ptv) and cmd.statut in (:y) ORDER BY cmd.date DESC")
	public List<Commande> getCommandesFiltered(@Param("y") List<String> statutValues,@Param("ptv") List<Long> ptvValues);
	
	@Query("select cmd from Commande cmd where cmd.pointVente.id in (:ptv) ORDER BY cmd.date DESC")
	public List<Commande> getCommandesFilteredByPtV(@Param("ptv") List<Long> ptvValues);
	
	
	@Query("select cmd from Commande cmd ORDER BY cmd.date DESC")
	public List<Commande> getCommandesOrdered();
	
	@Transactional
	@Modifying
	@Query("delete from Commande cmd where cmd.user.id=:x")
	public void deleteCommandesByUser(@Param("x") Long idUser);

	@Transactional
	@Modifying
	@Query("update Commande c "
			+ "set c.infoComp = :infoComp, c.msgCommercant=:msgCommercant, c.statut=:statut"
			+ ",c.totalSansPromo=:totalSansPromo, c.totalReductionStandard=:totalReductionStandard, c.reductionSpeciale=:reductionSpeciale, c.totalFinal=:totalFinal where c.id =:id")
	int updateInfo( @Param("id") Long idCommande, 
			@Param("infoComp") String infoComp, 
			@Param("msgCommercant") String msgCommercant, @Param("statut") String statut
			, @Param("totalSansPromo") Float totalSansPromo , @Param("totalReductionStandard") Float totalReductionStandard,
			@Param("reductionSpeciale") Float reductionSpeciale, @Param("totalFinal") Float totalFinal);
	
	
	@Transactional
	@Modifying
	@Query("update Commande c set c.statut=:statut where c.id =:id")
	int updateStatut( @Param("id") Long idCommande, @Param("statut") String statut);
	
	@Transactional
	@Modifying
	@Query("update Commande c set c.dateLivraison=:d where c.id =:id")
	int updateDateLivraison( @Param("id") Long idCommande, @Param("d") Date dateLivraison);
	
	@Transactional
	@Modifying
	@Query("update Commande c set c.pointVente=:pt where c.id =:id")
	int updatePointVente( @Param("id") Long idCommande, @Param("pt") PointVente pt);
	
	@Transactional
	@Modifying
	@Query("update Commande c set c.pointVente=null where c.pointVente.id =:id")
	int deletePointVenteFromCommande( @Param("id") Long idPtV);
}
