package data.specifications;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import data.entitys.Commande;

public class CommandeSpecification implements Specification<Commande> {	
	private static final long serialVersionUID = 1072457987893493011L;
	
	private List<SearchCriteria> list;
	
//	Join<Commande, PointVente> joinPtV = null;
//	Join<Commande,User> joinUser=null;
	 
	public CommandeSpecification() {
		this.list = new ArrayList<>();
	}

	public void add(SearchCriteria criteria) {
		list.add(criteria);
	}

	@Override
	public Predicate toPredicate(Root<Commande> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

		List<Predicate> predicates = new ArrayList<>();

		for (SearchCriteria criteria : list) {
			
			String key=criteria.getKey();
			boolean useJoinObj=false;			
			String joinFK="";	
			if(criteria.getKey().startsWith("pointVente.")) {
				joinFK="pointVente";
				key=criteria.getKey().replace("pointVente.", "");
				useJoinObj=true;				
			}
			else if(criteria.getKey().startsWith("user.")) {					
				joinFK="user";
				key=criteria.getKey().replace("user.", "");
				useJoinObj=true;				
			}
			
			
			if (criteria.getOperation().equals(SearchOperation.GREATER_THAN)) {
				if(!useJoinObj)
					predicates.add(builder.greaterThan(root.get(key), criteria.getValue().toString()));
				else
					predicates.add(builder.greaterThan(root.join(joinFK).get(key), criteria.getValue().toString()));
								
				
			} else if (criteria.getOperation().equals(SearchOperation.LESS_THAN)) {
				if(!useJoinObj)
					predicates.add(builder.lessThan(root.get(key), criteria.getValue().toString()));
				else
					predicates.add(builder.lessThan(root.join(joinFK).get(key), criteria.getValue().toString()));
					
			} else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
				if(!useJoinObj)
					predicates.add(builder.greaterThanOrEqualTo(root.get(key), criteria.getValue().toString()));
				else
					predicates.add(builder.greaterThanOrEqualTo(root.join(joinFK).get(key), criteria.getValue().toString()));
				
			} else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
				if(!useJoinObj)
					predicates.add(builder.lessThanOrEqualTo(root.get(key), criteria.getValue().toString()));
				else
					predicates.add(builder.lessThanOrEqualTo(root.join(joinFK).get(key), criteria.getValue().toString()));

			} else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL)) {
				if(!useJoinObj)
					predicates.add(builder.notEqual(root.get(key), criteria.getValue()));
				else
					predicates.add(builder.notEqual(root.join(joinFK).get(key), criteria.getValue()));
				
			} else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
				if(!useJoinObj)
					predicates.add(builder.equal(root.get(key), criteria.getValue()));
				else
					predicates.add(builder.equal(root.join(joinFK).get(key), criteria.getValue()));

				
			} else if (criteria.getOperation().equals(SearchOperation.MATCH)) {
				if(!useJoinObj)
					predicates.add(builder.like(builder.lower(root.get(key)),"%" + criteria.getValue().toString().toLowerCase() + "%"));
				else
					predicates.add(builder.like(builder.lower(root.join(joinFK).get(key)),"%" + criteria.getValue().toString().toLowerCase() + "%"));
			
			} else if (criteria.getOperation().equals(SearchOperation.MATCH_END)) {
				if(!useJoinObj)
					predicates.add(builder.like(builder.lower(root.get(key)),criteria.getValue().toString().toLowerCase() + "%"));
				else
					predicates.add(builder.like(builder.lower(root.join(joinFK).get(key)),criteria.getValue().toString().toLowerCase() + "%"));

			} else if (criteria.getOperation().equals(SearchOperation.MATCH_START)) {
				if(!useJoinObj)
					predicates.add(builder.like(builder.lower(root.get(key)),	"%" + criteria.getValue().toString().toLowerCase()));
				else
					predicates.add(builder.like(builder.lower(root.join(joinFK).get(key)),	"%" + criteria.getValue().toString().toLowerCase()));

			} else if (criteria.getOperation().equals(SearchOperation.IN)) {
				if(!useJoinObj)
					predicates.add(builder.in(root.get(key)).value(criteria.getValue()));
				else
					predicates.add(builder.in(root.join(joinFK).get(key)).value(criteria.getValue()));
				
			} else if (criteria.getOperation().equals(SearchOperation.NOT_IN)) {
				if(!useJoinObj)
					predicates.add(builder.not(root.get(key)).in(criteria.getValue()));
				else
					predicates.add(builder.not(root.join(joinFK).get(key)).in(criteria.getValue()));
			}else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL_DATE)) {
				if(!useJoinObj)
					predicates.add(builder.greaterThanOrEqualTo(root.get(key), (Date)criteria.getValue()));
				else
					predicates.add(builder.greaterThanOrEqualTo(root.join(joinFK).get(key), (Date)criteria.getValue()));
				
			} else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL_DATE)) {
				if(!useJoinObj)
					predicates.add(builder.lessThanOrEqualTo(root.get(key), (Date)criteria.getValue()));
				else
					predicates.add(builder.lessThanOrEqualTo(root.join(joinFK).get(key), (Date)criteria.getValue()));
				
			}
			else if (criteria.getOperation().equals(SearchOperation.IS_NULL)) {				
				if(!useJoinObj)
					predicates.add(builder.isNull(root.get(key)));
				else
					predicates.add(builder.isNull(root.join(joinFK).get(key)));
				
			}
		}
	
		return builder.and(predicates.toArray(new Predicate[0]));

	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	@Override
//	public Predicate toPredicate(Root<Commande> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
//
//		List<Predicate> predicates = new ArrayList<>();
//
//		for (SearchCriteria criteria : list) {
//			
//			String key=criteria.getKey();
//			boolean useJoinObj=false;			
//			String joinFK="";	
//			if(criteria.getKey().startsWith("pointVente.")) {
////				if(joinPtV==null)
////					joinPtV=root.join("pointVente");//	
//				joinFK="pointVente";
//				key=criteria.getKey().replace("pointVente.", "");
//				useJoinObj=true;				
//			}
//			else if(criteria.getKey().startsWith("user.")) {					
//				joinFK="user";
//				key=criteria.getKey().replace("user.", "");
//				useJoinObj=true;				
//			}
//			
//			
//			if (criteria.getOperation().equals(SearchOperation.GREATER_THAN)) {
//				if(!useJoinObj)
//					predicates.add(builder.greaterThan(root.get(key), criteria.getValue().toString()));
//				else
//					predicates.add(builder.greaterThan(root.join(joinFK).get(key), criteria.getValue().toString()));
//					//predicates.add(builder.greaterThan(joinPtV.get(key), criteria.getValue().toString()));
//				
//				
//				
//			} else if (criteria.getOperation().equals(SearchOperation.LESS_THAN)) {
//				if(!useJoinObj)
//					predicates.add(builder.lessThan(root.get(key), criteria.getValue().toString()));
//				else
//					predicates.add(builder.lessThan(root.join(joinFK).get(key), criteria.getValue().toString()));
//					//predicates.add(builder.lessThan(joinPtV.get(key), criteria.getValue().toString()));
//			} else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
//				if(!useJoinObj)
//					predicates.add(builder.greaterThanOrEqualTo(root.get(key), criteria.getValue().toString()));
//				else
//					predicates.add(builder.greaterThanOrEqualTo(joinPtV.get(key), criteria.getValue().toString()));
//				//				predicates.add(builder.greaterThanOrEqualTo(joinPtV.get(key), criteria.getValue().toString()));
//			} else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
//				if(!useJoinObj)
//					predicates.add(builder.lessThanOrEqualTo(root.get(key), criteria.getValue().toString()));
//				else
//					predicates.add(builder.lessThanOrEqualTo(joinPtV.get(key), criteria.getValue().toString()));
//
//			} else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL)) {
//				if(!useJoinObj)
//					predicates.add(builder.notEqual(root.get(key), criteria.getValue()));
//				else
//					predicates.add(builder.notEqual(joinPtV.get(key), criteria.getValue()));
//				
//			} else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
//				if(!useJoinObj)
//					predicates.add(builder.equal(root.get(key), criteria.getValue()));
//				else
//					predicates.add(builder.equal(joinPtV.get(key), criteria.getValue()));
//
//				
//			} else if (criteria.getOperation().equals(SearchOperation.MATCH)) {
//				if(!useJoinObj)
//					predicates.add(builder.like(builder.lower(root.get(key)),"%" + criteria.getValue().toString().toLowerCase() + "%"));
//				else
//					predicates.add(builder.like(builder.lower(joinPtV.get(key)),"%" + criteria.getValue().toString().toLowerCase() + "%"));
//			
//			} else if (criteria.getOperation().equals(SearchOperation.MATCH_END)) {
//				if(!useJoinObj)
//					predicates.add(builder.like(builder.lower(root.get(key)),criteria.getValue().toString().toLowerCase() + "%"));
//				else
//					predicates.add(builder.like(builder.lower(joinPtV.get(key)),criteria.getValue().toString().toLowerCase() + "%"));
//
//			} else if (criteria.getOperation().equals(SearchOperation.MATCH_START)) {
//				if(!useJoinObj)
//					predicates.add(builder.like(builder.lower(root.get(key)),	"%" + criteria.getValue().toString().toLowerCase()));
//				else
//					predicates.add(builder.like(builder.lower(joinPtV.get(key)),	"%" + criteria.getValue().toString().toLowerCase()));
//
//			} else if (criteria.getOperation().equals(SearchOperation.IN)) {
//				if(!useJoinObj)
//					predicates.add(builder.in(root.get(key)).value(criteria.getValue()));
//				else
//					predicates.add(builder.in(joinPtV.get(key)).value(criteria.getValue()));
//				
//			} else if (criteria.getOperation().equals(SearchOperation.NOT_IN)) {
//				if(!useJoinObj)
//					predicates.add(builder.not(root.get(key)).in(criteria.getValue()));
//				else
//					predicates.add(builder.not(joinPtV.get(key)).in(criteria.getValue()));
//			}else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL_DATE)) {
//				if(!useJoinObj)
//					predicates.add(builder.greaterThanOrEqualTo(root.get(key), (Date)criteria.getValue()));
//				else
//					predicates.add(builder.greaterThanOrEqualTo(joinPtV.get(key), (Date)criteria.getValue()));
//				
//			} else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL_DATE)) {
//				if(!useJoinObj)
//					predicates.add(builder.lessThanOrEqualTo(root.get(key), (Date)criteria.getValue()));
//				else
//					predicates.add(builder.lessThanOrEqualTo(joinPtV.get(key), (Date)criteria.getValue()));
//				
//			}
//			else if (criteria.getOperation().equals(SearchOperation.IS_NULL)) {
//				System.out.println("-----------------------filter sur null " + key);
//				if(!useJoinObj)
//					predicates.add(builder.isNull(root.get(key)));
//				else
//					predicates.add(builder.isNull(joinPtV.get(key)));
//				
//			}
//		}
//	
//		return builder.and(predicates.toArray(new Predicate[0]));
//
//	}
}
