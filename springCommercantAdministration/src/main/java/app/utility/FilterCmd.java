package app.utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import data.entitys.Commande;
import data.entitys.Commande.StatutCommande;
import data.specifications.CommandeSpecification;
import data.specifications.SearchCriteria;
import data.specifications.SearchOperation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class FilterCmd {
	private Long userId;
	
	List<String> statutSelectedValues=new ArrayList<String>();
	List<Long> ptSelectedValues=new ArrayList<Long>();
	
	String choixDate=enumChoixDate.none.toString();
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	Date dateDebut;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	Date dateFin;
	
	public FilterCmd(Long userId) {		
		this.userId = userId;
	}
	
	public void effacer() {
		statutSelectedValues=new ArrayList<String>() ;
		ptSelectedValues=new ArrayList<Long>();
		choixDate=enumChoixDate.none.toString();
		dateDebut=null;
		dateFin=null;		
	}
	public void checkDates() {
		if(this.dateDebut!=null && this.dateFin!=null) {
			if(this.dateFin.compareTo(this.dateDebut)<0)
				this.dateFin=this.dateDebut;
		}
	}
	public boolean isEmpty() {
		if(!this.hasSelectedStatut() && ! this.hasSelectedPt() && !this.hasSelectedDate())
			return true;
		else
			return false;
	}
	public boolean hasSelectedStatut() {
		if(statutSelectedValues == null || statutSelectedValues.size()==0) 
			return false;
		else
			return true;
	}
	
	public boolean hasSelectedPt() {
		if(ptSelectedValues == null || ptSelectedValues.size()==0) 
			return false;
		else
			return true;
	}
	public boolean hasSelectedDate() {
		if(choixDate.equals(enumChoixDate.none.toString())) 
			return false;
		else if(choixDate.equals(enumChoixDate.retrait_null.toString())) 
			return true;
		else if (dateDebut==null)
			return false;
		else
			return true;
	}
	
	public boolean allowChooseDate() {
		if(choixDate.equals(enumChoixDate.none.toString()) || choixDate.equals(enumChoixDate.retrait_null.toString())) {
			return false;
		}
		else {
			return true;
		}
	}
	public static boolean allowChooseDate(enumChoixDate choixDateValue) {
		if(choixDateValue.equals(enumChoixDate.none) || choixDateValue.equals(enumChoixDate.retrait_null)) {
			return false;
		}
		else
			return true;
	}
	public CommandeSpecification getCriteria() {
		
		 CommandeSpecification spec = new CommandeSpecification();
		 if(this.userId!=null) {
			 spec.add(new SearchCriteria("user.id",this.userId, SearchOperation.EQUAL));//user
		 }
		 if(hasSelectedStatut())			 
			 spec.add(new SearchCriteria("statut", this.getStatutSelectedValues(), SearchOperation.IN));
		 if(hasSelectedPt())			 
			 spec.add(new SearchCriteria("pointVente.id", this.getPtSelectedValues(), SearchOperation.IN));
         if(hasSelectedDate()) {  
        	 Date dateFinAdjusted=this.getDateFin();
        	 if(dateFinAdjusted!=null) {
        		Calendar cal = Calendar.getInstance();	
     			cal.setTime(dateFinAdjusted);
     			cal.add(Calendar.DATE,1);
     			dateFinAdjusted=cal.getTime();     			
        	 }
        	 
        	 if(this.choixDate.equals(enumChoixDate.cmd.toString())) {
        		 spec.add(new SearchCriteria("date", this.getDateDebut(), SearchOperation.GREATER_THAN_EQUAL_DATE));
        		 if(dateFinAdjusted!=null)
        			 spec.add(new SearchCriteria("date", dateFinAdjusted, SearchOperation.LESS_THAN_EQUAL_DATE));
        	 }
        	 else if(this.choixDate.equals(enumChoixDate.retrait.toString())) {
        		 spec.add(new SearchCriteria("dateLivraison", this.getDateDebut(), SearchOperation.GREATER_THAN_EQUAL_DATE));
        		 if(dateFinAdjusted!=null)
        			 spec.add(new SearchCriteria("dateLivraison", dateFinAdjusted, SearchOperation.LESS_THAN_EQUAL_DATE));
        	 }
        	 else if(this.choixDate.equals(enumChoixDate.retrait_null.toString())) {
        		 spec.add(new SearchCriteria("dateLivraison", null, SearchOperation.IS_NULL));        		
        	 }
         }
         System.out.println("---------------");
         return spec;
	}
	
	public enum enumChoixDate {
		none, cmd, retrait, retrait_null;
	}
	
	public static String getChoixDateLibelle(enumChoixDate choixDateValue) {
		
		if (choixDateValue!=null) {
			if (choixDateValue.equals(enumChoixDate.none)) 
				return "ne pas filtrer par date";
			else if (choixDateValue.equals(enumChoixDate.cmd)) 
				return "de la commande";
			else if (choixDateValue.equals(enumChoixDate.retrait)) 
				return "de retrait";
			else if (choixDateValue.equals(enumChoixDate.retrait_null)) 
				return "sans date de retrait";
			else
				return choixDateValue.toString();			
		}
		else
			return "";	
	}

	@Override
	public String toString() {
		return "FilterCmd [" + (userId != null ? "userId=" + userId + ", " : "")
				+ (statutSelectedValues != null ? "statutSelectedValues=" + statutSelectedValues + ", " : "")
				+ (ptSelectedValues != null ? "ptSelectedValues=" + ptSelectedValues + ", " : "")
				+ (choixDate != null ? "choixDate=" + choixDate + ", " : "")
				+ (dateDebut != null ? "dateDebut=" + dateDebut + ", " : "")
				+ (dateFin != null ? "dateFin=" + dateFin : "") + "]";
	}
	
}
