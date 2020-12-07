package data.entitys;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import data.Utilitys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter 
@Entity
@Table(name = "pointVente")
public class PointVente  implements Serializable  {
	private static final long serialVersionUID = -9003025264926360924L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_pointVente")
	private Long id;
	
	@Column(length = 1024)
	private String libelle;
	
	@Column(length = 2048)
	private String description;
	
	@Column(length = 2048)
	private String emplacementText;
	
	@Column(length = 2048)
	private String horairesText;
	
	@Column(columnDefinition = "tinyint(1) NOT NULL DEFAULT '0'")
	private boolean lundi;	
	@Column(length = 500)
	private String horairesLundi;
	
	@Column(columnDefinition = "tinyint(1) NOT NULL DEFAULT '0'")
	private boolean mardi;
	@Column(length = 500)
	private String horairesMardi;
	
	@Column(columnDefinition = "tinyint(1) NOT NULL DEFAULT '0'")
	private boolean mercredi;
	@Column(length = 500)
	private String horairesMercredi;
	
	@Column(columnDefinition = "tinyint(1) NOT NULL DEFAULT '0'")
	private boolean jeudi;
	@Column(length = 500)
	private String horairesJeudi;
	
	@Column(columnDefinition = "tinyint(1) NOT NULL DEFAULT '0'")
	private boolean vendredi;
	@Column(length = 500)
	private String horairesVendredi;
	
	@Column(columnDefinition = "tinyint(1) NOT NULL DEFAULT '0'")
	private boolean samedi;
	@Column(length = 500)
	private String horairesSamedi;
	
	@Column(columnDefinition = "tinyint(1) NOT NULL DEFAULT '0'")
	private boolean dimanche;
	@Column(length = 500)
	private String horairesDimanche;
	
	@Column(name = "infoComp")
	private String infoComp;
	
	private String photoTitre;
	
	@Lob @Column(columnDefinition="MEDIUMBLOB",nullable = true)
	private byte[] photoData;
	
	@Column(columnDefinition = "tinyint(1) NOT NULL DEFAULT '1'")
	private  boolean isActif;
	
	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable( name = "pointVente_User",
    			joinColumns = @JoinColumn (name = "\"id_pointVente\""),
                inverseJoinColumns = @JoinColumn( name = "id_user" ) )
	private List<User> users = new ArrayList<User>();
	
	@OneToMany (mappedBy = "pointVente",fetch = FetchType.LAZY)
	List<Commande> commandes = new ArrayList<Commande>();	

	
	public boolean hasPhoto() {
		return this.getPhotoData()!=null;
	}
	public String getPhotoStrLg() {
		String retVal ="";
		if (this.getPhotoData()!=null) {		
			retVal="data:image/png;base64,"+Utilitys.getImgDataThAs64String(this.getPhotoData(),450,450);			
		}				
		return retVal;
	}
	public String getPhotoStrMd() {
		String retVal ="";
		if (this.getPhotoData()!=null) {		
			retVal="data:image/png;base64,"+Utilitys.getImgDataThAs64String(this.getPhotoData(),250,250);			
		}				
		return retVal;
	}
	public String getPhotoStrSm() {
		String retVal ="";
		if (this.getPhotoData()!=null) {		
			retVal="data:image/png;base64,"+Utilitys.getImgDataThAs64String(this.getPhotoData(),100,100);			
		}				
		return retVal;
	}
	
	public String getHorairesConcatString() {
		if(!this.lundi && !this.mardi && !this.mercredi && !this.jeudi && !this.vendredi && !this.samedi && !this.dimanche ) {
			return this.getHorairesText();			
		}
		else {
			String newLine = System.getProperty("line.separator");
			String retVal=this.getHorairesText();
			if(!retVal.isBlank())
				retVal+=newLine ;
			
			if(this.lundi) {				
				retVal+="lundi";
				if(this.horairesLundi!=null && !this.horairesLundi.isBlank())
					retVal+=" " + this.horairesLundi;
			}
			if(this.mardi) {
				if(!retVal.isBlank() && this.isLundi())
					retVal+=", " ;				
				retVal+="mardi";
				if(this.horairesMardi!=null && !this.horairesMardi.isBlank())
					retVal+=" " + this.horairesMardi;
			}
			
			if(this.mercredi) {
				if(!retVal.isBlank()  && (this.isLundi()|| this.isMardi()))
					retVal+=", " ;				
				retVal+="mercredi";
				if(this.horairesMercredi!=null && !this.horairesMercredi.isBlank())
					retVal+=" " + this.horairesMercredi;
			}
			if(this.jeudi) {
				if(!retVal.isBlank()  && (this.isLundi()|| this.isMardi() || this.isMercredi()))
					retVal+=", " ;				
				retVal+="jeudi";
				if(this.horairesJeudi!=null && !this.horairesJeudi.isBlank())
					retVal+=" " + this.horairesJeudi;
			}
			if(this.vendredi) {
				if(!retVal.isBlank() && (this.isLundi()|| this.isMardi() || this.isMercredi() || this.isJeudi()))
					retVal+=", " ;				
				retVal+="vendredi";
				if(this.horairesVendredi!=null && !this.horairesVendredi.isBlank())
					retVal+=" " + this.horairesVendredi;
			}
			if(this.samedi) {
				if(!retVal.isBlank() && (this.isLundi()|| this.isMardi() || this.isMercredi() || this.isJeudi() || this.isVendredi()))
					retVal+=", " ;				
				retVal+="samedi";
				if(this.horairesSamedi!=null && !this.horairesSamedi.isBlank())
					retVal+=" " + this.horairesSamedi;
			}
			
			if(this.dimanche) {
				if(!retVal.isBlank() && (this.isLundi()|| this.isMardi() || this.isMercredi() || this.isJeudi() || this.isVendredi() || this.isSamedi()))
					retVal+=", " ;				
				retVal+="dimanche";
				if(this.horairesDimanche!=null && !this.horairesDimanche.isBlank())
					retVal+=" " + this.horairesDimanche;
			}			
			return retVal;
		}
			
	}
	private Integer countOpeningDaysOfWeek() {
		if(this.lundi || this.mardi || this.mercredi || this.jeudi || this.vendredi || this.samedi || this.dimanche ) {
			Integer ret=0;
			if(this.lundi)
				ret++;
			if(this.mardi)
				ret++;
			if(this.mercredi)
				ret++;
			if(this.jeudi)
				ret++;
			if(this.vendredi)
				ret++;
			if(this.samedi)
				ret++;
			if(this.dimanche)
				ret++;
			return ret;
		}
		else
			return 0;
	}
	
	private ArrayList<Integer> convertOpeningDays() {
		if(this.lundi || this.mardi || this.mercredi || this.jeudi || this.vendredi || this.samedi || this.dimanche ) {
			ArrayList<Integer> ret=new ArrayList<Integer>();
			if(this.lundi)
				ret.add(Calendar.MONDAY);
			if(this.mardi)
				ret.add(Calendar.TUESDAY);
			if(this.mercredi)
				ret.add(Calendar.WEDNESDAY);
			if(this.jeudi)
				ret.add(Calendar.THURSDAY);
			if(this.vendredi)
				ret.add(Calendar.FRIDAY);
			if(this.samedi)
				ret.add(Calendar.SATURDAY);
			if(this.dimanche)
				ret.add(Calendar.SUNDAY);
			return ret;
		}
		return null;		
	}
	
	private Integer getNextOpeningDayOfWeek() {

		Integer nbOpeningDays = countOpeningDaysOfWeek();
		if (nbOpeningDays == 1) {
			if (this.lundi)
				return Calendar.MONDAY;
			if (this.mardi)
				return Calendar.TUESDAY;
			if (this.mercredi)
				return Calendar.WEDNESDAY;
			if (this.jeudi)
				return Calendar.THURSDAY;
			if (this.vendredi)
				return Calendar.FRIDAY;
			if (this.samedi)
				return Calendar.SATURDAY;
			if (this.dimanche)
				return Calendar.SUNDAY;
		} else if (nbOpeningDays > 1) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			int curDW = cal.get(Calendar.DAY_OF_WEEK);
			ArrayList<Integer> dw = convertOpeningDays();
			for (Integer day : dw) {
				if (day > curDW || (day==Calendar.SUNDAY && curDW != Calendar.SATURDAY))
					return day;
			}
			return dw.get(0);
		} else
			return null;

		return null;
	}
	private Integer getNextOpeningDayOfWeek(Date curDate) {

		Integer nbOpeningDays = countOpeningDaysOfWeek();
		if (nbOpeningDays == 1) {
			if (this.lundi)
				return Calendar.MONDAY;
			if (this.mardi)
				return Calendar.TUESDAY;
			if (this.mercredi)
				return Calendar.WEDNESDAY;
			if (this.jeudi)
				return Calendar.THURSDAY;
			if (this.vendredi)
				return Calendar.FRIDAY;
			if (this.samedi)
				return Calendar.SATURDAY;
			if (this.dimanche)
				return Calendar.SUNDAY;
		} else if (nbOpeningDays > 1) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(curDate);
			int curDW =getDayOfWeekFr( cal.get(Calendar.DAY_OF_WEEK));


			ArrayList<Integer> dw = convertOpeningDays();
			for (Integer day : dw) {

				if (getDayOfWeekFr(day) > curDW ){						
					return day;
				}
			}
			return dw.get(0);
		} else
			return null;

		return null;
	}
	
	private static Integer getDayOfWeekFr(Integer calendarDayOfWeek) {
		if(calendarDayOfWeek==1)
			return 7;
		if(calendarDayOfWeek>1)
			return calendarDayOfWeek-1;
		else
			return 0;
	}
	
//	public Date getNextDateValidation(){
//		if(countOpeningDaysOfWeek()>0) {
//		
//			Integer nextDW=this.getNextOpeningDayOfWeek();
//			if(nextDW!=null) {
//				Calendar cal = Calendar.getInstance();			
//				
//				cal.set(Calendar.DAY_OF_WEEK,nextDW);
//				
//				Date retDate=cal.getTime();
//				
//				cal.setTime(new Date());
//				int curDW=cal.get(Calendar.DAY_OF_WEEK);
//				if(curDW==nextDW) {
//					cal.add(Calendar.DATE,7);
//					retDate=cal.getTime();
//				}
//				return retDate;
//			}
//			
//			return null;
//			
//		}
//		else
//			return null;
//	}
	public String getNextDateValidationStr(Commande cmd){
		Date retDate=getNextDateValidation(cmd);
		Calendar cal = Calendar.getInstance();	
		cal.setTime(retDate);
		Integer dw=cal.get(Calendar.DAY_OF_WEEK);
		String dwStr="";
		if (dw.equals(Calendar.MONDAY))
			dwStr="lundi";
		else if (dw.equals(Calendar.TUESDAY))
			dwStr="mardi";
		else if (dw.equals(Calendar.WEDNESDAY))
			dwStr="mercredi";
		else if (dw.equals(Calendar.THURSDAY))
			dwStr="jeudi";
		else if (dw.equals(Calendar.FRIDAY))
			dwStr="vendredi";
		else if (dw.equals(Calendar.SATURDAY))
			dwStr="samedi";
		else if (dw.equals(Calendar.SUNDAY))
			dwStr="dimanche";
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
	    
	    return dwStr + " " + formater.format(retDate);
	}
	public Date getNextDateValidation(Commande cmd){
		Date dtCmd=cmd.getDate();
		if(dtCmd==null)
			dtCmd=new Date();
		Integer delais=0;
		for (CommandeProduit cp : cmd.getLignesCommandeProduit()) {
			if(cp.getProduit().calculeDelaisJours()>delais)
				delais=cp.getProduit().calculeDelaisJours();
		}
		
		if(countOpeningDaysOfWeek()>0 || delais > 0) {
			Calendar cal = Calendar.getInstance();	
			cal.setTime(dtCmd);
			if(delais>0) {
				cal.add(Calendar.DATE,delais);
				dtCmd=cal.getTime();
			}
			cal.setTime(dtCmd);
			
			Integer nextDW=this.getNextOpeningDayOfWeek(dtCmd);
			if(nextDW!=null) {				
				
				cal.set(Calendar.DAY_OF_WEEK,nextDW);				
				Date retDate=cal.getTime();				

				if(retDate.compareTo(dtCmd)<=0){					
					cal.setTime(retDate);
					cal.add(Calendar.DATE,7);
					retDate=cal.getTime();
				}
				return retDate;
			}
			
			return dtCmd;
			
		}
		else
			return dtCmd;
	}
}
