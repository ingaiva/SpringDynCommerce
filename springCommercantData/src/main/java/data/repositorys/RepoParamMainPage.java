package data.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import data.entitys.ParamMainPage;

@CrossOrigin("*")
@RepositoryRestResource
public interface RepoParamMainPage extends JpaRepository<ParamMainPage, Long>  {

	
	@Transactional
	@Modifying
	@Query("update ParamMainPage p "
			+ "set p.texteAccueil = :texteAccueil, p.logoTitre=:logoTitre, p.logoPosition=:logoPosition"
			+ ",p.showCategories=:showCategories, p.showPromo=:showPromo, p.statutPromoToShow=:statutPromoToShow, p.promoTitre=:promoTitre where p.id = 1")
	int updateInfo( 
			@Param("texteAccueil") String texteAccueil, 
			@Param("logoTitre") String logoTitre, @Param("logoPosition") String	 logoPosition
			, @Param("showCategories") Boolean	showCategories , @Param("showPromo") Boolean showPromo, @Param("statutPromoToShow") String statutPromoToShow, @Param("promoTitre") String promoTitre);
	
	@Transactional
	@Modifying
	@Query("update ParamMainPage p set p.contactTitre = :contactTitre, p.contactTel1=:contactTel1, p.contactTel2=:contactTel2, p.contactMail=:contactMail where p.id = 1")
	int updateContact( 
			@Param("contactTitre") String contactTitre, 
			@Param("contactTel1") String contactTel1, @Param("contactTel2") String contactTel2
			, @Param("contactMail") String contactMail);
	
	@Transactional
	@Modifying
	@Query("update ParamMainPage p set p.horairesTitre = :horairesTitre, p.horairesText=:horairesText where p.id = 1")
	int updateHoraires(	@Param("horairesTitre") String horairesTitre, @Param("horairesText") String horairesText);
	
	
	@Transactional
	@Modifying
	@Query("update ParamMainPage p set p.logoData=:logo where p.id = 1")
	int updateLogo(@Param("logo") byte[]  logo);
	
	
	@Transactional
	@Modifying
	@Query("update ParamMainPage p set p.logoData=null where p.id = 1")
	int deleteLogo();
	
	@Transactional
	@Modifying
	@Query("update ParamMainPage p set p.bgData=:bgData where p.id = 1")
	int updateBG(@Param("bgData") byte[]  bgData);
	
	@Transactional
	@Modifying
	@Query("update ParamMainPage p set p.logoData=null where p.id = 1")
	int deleteBG();
}
