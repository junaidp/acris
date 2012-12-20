package 	sk.seges.acris.security.server.acl.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sk.seges.acris.security.server.acl.service.api.AclManager;
import sk.seges.acris.security.server.utils.LoggedUserRole;
import sk.seges.acris.security.server.utils.SecuredClassHelper;
import sk.seges.acris.security.shared.domain.ISecuredObject;
import sk.seges.acris.security.shared.user_management.domain.Permission;
import sk.seges.acris.security.user_management.server.model.data.UserData;
import sk.seges.sesam.pap.service.annotation.LocalService;
import sk.seges.sesam.server.domain.converter.utils.ClassConverter;
import sk.seges.sesam.shared.model.converter.api.ConverterProvider;

@LocalService
public class AclMaintenanceService implements IAclMaintenanceServiceLocal {

    private AclManager aclManager;
    
    private final ConverterProvider converterProvider;
    
    public AclManager getAclManager() {
        return aclManager;
    }

    public void setAclManager(AclManager aclManager) {
        this.aclManager = aclManager;
    }

    public AclMaintenanceService(ConverterProvider converterProvider, AclManager aclManager) {
    	this.aclManager = aclManager;
    	this.converterProvider = converterProvider;
    }
    
    public void removeACLEntries(UserData user, String[] securedClassNames) {
        for(String securedClassName : securedClassNames) {
            Class<? extends ISecuredObject<?>> securedClass = SecuredClassHelper.getSecuredClass(securedClassName);
            aclManager.removeAclRecords(securedClass, user);
        }
    }
    
    public void removeACLEntries(List<Long> aclIds, String className, UserData user) {
    	className=  ClassConverter.getDomainClassName(converterProvider, className);
    	for (Long id : aclIds) {
    		aclManager.removeAclRecords(id, className, user);
    	}
    }

    public void resetACLEntries(String className, Long aclId, UserData user, Permission[] authorities) {
    	className=  ClassConverter.getDomainClassName(converterProvider, className);
   		aclManager.resetAclRecords(SecuredClassHelper.getSecuredClass(className), aclId, user, authorities);
    }

    @Override
    public void resetACLEntriesLoggedRole(String className, Long aclId, Permission[] authorities) {
    	className=  ClassConverter.getDomainClassName(converterProvider, className);
   		aclManager.resetAclRecords(SecuredClassHelper.getSecuredClass(className), aclId, new LoggedUserRole(), authorities);
    }

	@Override
	public void setAclEntries(String className, Long aclId, UserData user,	Permission[] authorities) {
    	className=  ClassConverter.getDomainClassName(converterProvider, className);
   		aclManager.setAclRecords(SecuredClassHelper.getSecuredClass(className), aclId, user, authorities);
	}

	@Override
	public void setAclEntries(Map<String, List<Long>> acls,
			UserData user, Permission[] authorities) {
		for(Entry<String, List<Long>> entry : acls.entrySet()) {
	    	String className = ClassConverter.getDomainClassName(converterProvider, entry.getKey());
			Class<? extends ISecuredObject<?>> securedClass = SecuredClassHelper.getSecuredClass(className);
	    	for (Long aclId : entry.getValue()) {
				aclManager.setAclRecords(securedClass, aclId, user, authorities);
	    	}
		}
	}
}