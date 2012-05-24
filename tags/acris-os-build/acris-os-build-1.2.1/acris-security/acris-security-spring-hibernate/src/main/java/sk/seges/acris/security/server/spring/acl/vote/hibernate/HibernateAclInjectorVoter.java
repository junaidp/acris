package sk.seges.acris.security.server.spring.acl.vote.hibernate;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.DetachedCriteriaUtils;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.security.acls.Permission;
import org.springframework.security.acls.sid.PrincipalSid;
import org.springframework.security.acls.sid.Sid;

import sk.seges.acris.security.core.server.acl.domain.jpa.JpaAclEntry;
import sk.seges.acris.security.server.core.acl.domain.api.AclEntryMetaModel;
import sk.seges.acris.security.server.core.acl.domain.api.AclSecuredClassDescriptionMetaModel;
import sk.seges.acris.security.server.core.acl.domain.api.AclSecuredObjectIdentityMetaModel;
import sk.seges.acris.security.server.core.acl.domain.api.AclSidMetaModel;
import sk.seges.acris.security.server.spring.acl.vote.AbstractAclInjectionVoter;


public class HibernateAclInjectorVoter extends AbstractAclInjectionVoter {

    public HibernateAclInjectorVoter(String configAttribute, Permission[] requirePermission) {
		super(configAttribute, requirePermission);
	}

	protected DetachedCriteria createCriteria(DetachedCriteria clazzCriteria, Sid[] sids, Class<?> clazz) {

        // Create detached criteria with alias - name of the alias is not
        // importat, only purpose
        // is to not use this_ default alias
        DetachedCriteria criteria = DetachedCriteria.forClass(JpaAclEntry.class, "aclEntry");

        // We just want to select objectIdentities
        criteria.setProjection(Projections.alias(Projections.property(AclEntryMetaModel.OBJECT_IDENTITY.THIS), "object_identity"));

        criteria.createCriteria(AclEntryMetaModel.OBJECT_IDENTITY.THIS).
        // select secured object id
                add(Restrictions.sqlRestriction("{alias}." + AclSecuredObjectIdentityMetaModel.DB_OBJECT_ID_IDENTITY + "=this_.id")).createCriteria(
                        AclSecuredObjectIdentityMetaModel.OBJECT_ID_CLASS.THIS).
                // select secured object class
                add(Restrictions.eq(AclSecuredClassDescriptionMetaModel.CLASS_NAME, clazz.getName()));

        // create disjunction of the principals
        Junction junction = Restrictions.disjunction();

        for (Sid sid : sids) {
            if (sid instanceof PrincipalSid) {
                junction.add(Restrictions.eq(AclSidMetaModel.SID, ((PrincipalSid) sid).getPrincipal()));
            }
        }

        criteria.createCriteria(AclEntryMetaModel.SID.THIS).add(junction);

        // combine sub-queries
        clazzCriteria.add(Subqueries.exists(criteria));

        return clazzCriteria;
    }
    
    protected void injectIntoCriteria(Sid[] sids, Class<?>[] params, Object[] args) {
        int index = 0;
        for (Class<?> clazz : params) {
            if (clazz.isAssignableFrom(DetachedCriteria.class)) {
                Class<?> entityClazz = new DetachedCriteriaUtils().getDetachedCriteriaDomainObjectClass(((DetachedCriteria) args[index]));
                createCriteria(((DetachedCriteria) args[index]), sids, entityClazz);
            }

            index++;
        }
    }
}
