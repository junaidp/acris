package sk.seges.crm.shared.domain.api;

import java.util.List;

public interface LeadModel<T> {
	T id();
	
	String state();
	SalesmanModel responsible();
	List<LeadActivityModel> activities();
	CustomerModel customer();
}
