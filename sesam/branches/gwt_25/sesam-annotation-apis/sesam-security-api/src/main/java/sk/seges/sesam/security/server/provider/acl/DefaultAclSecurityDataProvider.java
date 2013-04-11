package sk.seges.sesam.security.server.provider.acl;

import sk.seges.sesam.domain.IDomainObject;
import sk.seges.sesam.security.server.model.acl.AclSecurityData;

public class DefaultAclSecurityDataProvider implements AclSecurityDataProvider<IDomainObject<?>> {

	@Override
	public AclSecurityData getSecurityData(IDomainObject<?> t) {
		if (t == null) {
			return null;
		}

		if (t.getId() instanceof Long) {
			return new AclSecurityData((Long) t.getId(), t.getClass().getCanonicalName());
		}
		
		return null;
	}
}