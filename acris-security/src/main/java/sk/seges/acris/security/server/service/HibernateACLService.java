package sk.seges.acris.security.server.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.AccessControlEntry;
import org.springframework.security.acls.Acl;
import org.springframework.security.acls.AclService;
import org.springframework.security.acls.NotFoundException;
import org.springframework.security.acls.Permission;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.jdbc.AclCache;
import org.springframework.security.acls.objectidentity.ObjectIdentity;
import org.springframework.security.acls.sid.GrantedAuthoritySid;
import org.springframework.security.acls.sid.PrincipalSid;
import org.springframework.security.acls.sid.Sid;
import org.springframework.security.util.FieldUtils;

import sk.seges.acris.security.server.dao.acl.IACLEntryDAO;
import sk.seges.acris.security.server.dao.acl.IACLObjectIdentityDAO;
import sk.seges.acris.security.server.dao.acl.IACLSecuredClassDAO;
import sk.seges.acris.security.server.dao.acl.IACLSecurityIDDAO;
import sk.seges.acris.security.server.domain.acl.ACLEntry;
import sk.seges.acris.security.server.domain.acl.ACLObjectIdentity;
import sk.seges.acris.security.server.domain.acl.ACLSecuredClass;
import sk.seges.acris.security.server.domain.acl.ACLSecurityID;


public class HibernateACLService implements AclService {

    @Autowired
    protected AclAuthorizationStrategy aclAuthorizationStrategy;
    
    protected AclCache aclCache;
    
    @Autowired
    protected AuditLogger auditLogger;

	@Autowired
	protected IACLEntryDAO aclEntryDao;

	@Autowired
	protected IACLSecurityIDDAO aclSecurityIDDao;

	@Autowired
	protected IACLSecuredClassDAO aclSecuredClassDao;

	@Autowired
	protected IACLObjectIdentityDAO aclObjectIdentityDao;

    public ObjectIdentity[] findChildren(ObjectIdentity parentIdentity) {
        // TODO Auto-generated method stub
        return null;
    }

    public Acl readAclById(ObjectIdentity object) throws NotFoundException {
        return readAclById(object, null);
    }

    @SuppressWarnings("unchecked")
    public Acl readAclById(ObjectIdentity object, Sid[] sids)
            throws NotFoundException {
        Map map = readAclsById(new ObjectIdentity[] {object}, sids);
        return (Acl) map.get(object);
    }

    @SuppressWarnings("unchecked")
    public Map readAclsById(ObjectIdentity[] objects) throws NotFoundException {
        return readAclsById(objects, null);
    }

    @SuppressWarnings("unchecked")
    public Map<ObjectIdentity, Acl> readAclsById(ObjectIdentity[] objects, Sid[] sids)
            throws NotFoundException {
        final Map acls = new HashMap<ObjectIdentity, Acl>();
        
        for (ObjectIdentity object :objects){

        	Acl aclFromCache = aclCache.getFromCache(object);

        	if (aclFromCache != null) {
        		acls.put(object, aclFromCache);
        		continue;
        	}

        	ACLSecuredClass aclClass = aclSecuredClassDao.load(object.getJavaType());
            
            if (aclClass == null) {
            	//There is for sure no ACL entries for this object identity
            	throw new NotFoundException("Could not found specified aclObjectIdentity.");
            }
            // No need to check for nulls, as guaranteed non-null by ObjectIdentity.getIdentifier() interface contract
            String identifier = object.getIdentifier().toString();
            long id = (Long.valueOf(identifier)).longValue();
            ACLObjectIdentity aclObjectIdentity = aclObjectIdentityDao.findByObjectId(aclClass.getId(), id);

            if(aclObjectIdentity==null){
                throw new NotFoundException("Could not found specified aclObjectIdentity.");
//                AclImpl acl = new AclImpl(object, 0, 
//                        aclAuthorizationStrategy, auditLogger, 
//                        null, null, false, new GrantedAuthoritySid("ROLE_ADMIN"));
//                acls.put(object, acl); 
//                continue;
            }
            ACLSecurityID aclOwnerSid = aclObjectIdentity.getSid();
            Sid owner;

            if (aclOwnerSid.isPrincipal()) {
                owner = new PrincipalSid(aclOwnerSid.getSid());
            } else {
                owner = new GrantedAuthoritySid(aclOwnerSid.getSid());
            }
            
            AclImpl acl = new AclImpl(object, aclObjectIdentity.getId(), 
                                        aclAuthorizationStrategy, auditLogger, 
                                        null, null, false, owner);
            acls.put(object, acl); 
            
            aclCache.putInCache(acl);

            Field acesField = FieldUtils.getField(AclImpl.class, "aces");
            List<AccessControlEntry> aces;

            try {
                acesField.setAccessible(true);
                aces = (List) acesField.get(acl);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Could not obtain AclImpl.ace field: cause[" + ex.getMessage() + "]");
            }
            
            List<ACLEntry> aclEntrys = aclEntryDao.findByIdentityId(aclObjectIdentity.getId());
            
            for(ACLEntry aclEntry:aclEntrys){
                ACLSecurityID aclSid = aclEntry.getSid();
                Sid recipient;
                if (aclSid.isPrincipal()) {
                    recipient = new PrincipalSid(aclSid.getSid());
                } else {
                    recipient = new GrantedAuthoritySid(aclSid.getSid());
                }  
                
                int mask = aclEntry.getMask();
                Permission permission = convertMaskIntoPermission(mask);
                boolean granting = aclEntry.isGranting();
                boolean auditSuccess = aclEntry.isAuditSuccess();
                boolean auditFailure = aclEntry.isAuditFailure();       
                
                AccessControlEntryImpl ace = new AccessControlEntryImpl(aclEntry.getId(), acl, recipient, permission, granting,
                        auditSuccess, auditFailure);       
                
                // Add the ACE if it doesn't already exist in the ACL.aces field
                 if (!aces.contains(ace)) {
                     aces.add(ace);
                 }                   
            }
       
        }
        return acls;
    }
    
    protected Permission convertMaskIntoPermission(int mask) {
    	//TODO, Add extended permission
        return BasePermission.buildFromMask(mask);
    }

	public void setAclCache(AclCache aclCache) {
		this.aclCache = aclCache;
	}
}