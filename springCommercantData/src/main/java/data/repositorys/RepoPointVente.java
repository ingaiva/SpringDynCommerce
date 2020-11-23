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
	
	List<PointVente> findAllByUsers(User user);
	

}
