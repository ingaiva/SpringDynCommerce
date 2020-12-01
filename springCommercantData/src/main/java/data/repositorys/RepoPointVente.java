package data.repositorys;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import data.entitys.PointVente;
import data.entitys.User;

public interface RepoPointVente  extends JpaRepository<PointVente, Long>{
	@Transactional
	@Modifying
	@Query("update PointVente p set p.photoData=:logo where p.id = :x")
	int updatePhoto(@Param("x") Long idPointVente, @Param("logo") byte[]  logo);
	
	@Transactional
	@Modifying
	@Query("update PointVente p set p.photoData=null where p.id = :x")
	int deletePhoto(@Param("x") Long idPointVente);
	
	@Transactional
	@Modifying
	@Query("update PointVente p set p.libelle=:libelle, p.description=:description, p.emplacementText=:emplacementText"
			+ ", p.horairesText=:horairesText, p.infoComp=:infoComp, p.photoTitre=:photoTitre, p.isActif=:isActif where p.id = :x")
	int updateInfo(@Param("x") Long idPointVente, @Param("libelle") String  libelle, @Param("description") String  description
			, @Param("emplacementText") String  emplacementText, @Param("horairesText") String  horairesText
			, @Param("infoComp") String  infoComp
			, @Param("photoTitre") String  photoTitre, @Param("isActif") Boolean  isActif);
	
	@Transactional
	@Modifying
	@Query("update PointVente p set "
			+ " p.lundi=:isLundi, p.horairesLundi=:horairesLundi,"
			+ " p.mardi=:isMardi,  p.horairesMardi=:horairesMardi,"
			+ " p.mercredi=:isMercredi, p.horairesMercredi=:horairesMercredi,"
			+ " p.jeudi=:isJeudi, p.horairesJeudi=:horairesJeudi,"
			+ " p.vendredi=:isVendredi, p.horairesVendredi=:horairesVendredi,"
			+ " p.samedi=:isSamedi, p.horairesSamedi=:horairesSamedi,"
			+ " p.dimanche=:isDimanche, p.horairesDimanche=:horairesDimanche"
			+ "  where p.id = :x")
	int updateHoraires(@Param("x") Long idPointVente, 
			@Param("isLundi") boolean  isLundi, @Param("horairesLundi") String  horairesLundi, 
			@Param("isMardi") boolean  isMardi, @Param("horairesMardi") String  horairesMardi, 
			@Param("isMercredi") boolean  isMercredi, @Param("horairesMercredi") String  horairesMercredi, 
			@Param("isJeudi") boolean  isJeudi, @Param("horairesJeudi") String  horairesJeudi, 
			@Param("isVendredi") boolean  isVendredi, @Param("horairesVendredi") String  horairesVendredi, 
			@Param("isSamedi") boolean  isSamedi, @Param("horairesSamedi") String  horairesSamedi, 
			@Param("isDimanche") boolean  isDimanche, @Param("horairesDimanche") String  horairesDimanche
			);
	
	List<PointVente> findAllByUsers(User user);
	

}
