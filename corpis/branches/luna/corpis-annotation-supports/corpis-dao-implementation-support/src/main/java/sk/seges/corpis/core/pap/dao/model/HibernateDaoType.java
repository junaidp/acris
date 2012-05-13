package sk.seges.corpis.core.pap.dao.model;

import sk.seges.corpis.appscaffold.model.pap.model.DomainDataInterfaceType;
import sk.seges.corpis.core.pap.dao.accessor.DataAccessObjectAccessor;
import sk.seges.sesam.core.pap.model.mutable.api.MutableDeclaredType;
import sk.seges.sesam.core.pap.model.mutable.utils.MutableProcessingEnvironment;

public class HibernateDaoType extends AbstractHibernateDaoType {

	public HibernateDaoType(MutableDeclaredType mutableDomainType, MutableProcessingEnvironment processingEnv) {
		super(mutableDomainType, processingEnv);
	}
	
	public AbstractDaoApiType getDaoInterface() {
		if (getDataInterface() == null) {
			return null;
		}
		DaoApiType daoBase = new DataAccessObjectAccessor(mutableDomainType, processingEnv).getDaoBase();
		if (daoBase != null) {
			return daoBase;
		}
		
		MutableDeclaredType dataInterface = getDataInterface();

		if (dataInterface instanceof DomainDataInterfaceType) {
			return new DaoApiType(((DomainDataInterfaceType)dataInterface), processingEnv);
		}
		
		return new DaoApiType(dataInterface.asElement(), processingEnv);
	}
}