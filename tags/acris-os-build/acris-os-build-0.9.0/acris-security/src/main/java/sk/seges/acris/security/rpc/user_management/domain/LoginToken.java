/**
 * 
 */
package sk.seges.acris.security.rpc.user_management.domain;

import sk.seges.acris.security.rpc.domain.ITransferableObject;

/**
 * A token transferring login information to a user service (or user service
 * broadcaster). The token might be specific for a service but when used in
 * conjunction with service broadcaster it must hold login information common to
 * all user services.
 * 
 * There is a transformation executed in the service layer where the login token
 * is transformed to authentication token used on the server side.
 * 
 * @author ladislav.gazo
 */
public interface LoginToken extends ITransferableObject {}