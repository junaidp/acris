# Introduction #

The idea of emulation java classes is well known from GWT core. Google web toolkit has emulated basic set of [JRE classes](http://code.google.com/intl/sk/webtoolkit/doc/latest/RefJreEmulation.html) which can be used in client side code (in the result in javascript code). Many of the examples exists:
  * JRE emulation in GWT (in com.google.gwt.emul package)
  * GWTx framework - an extended GWT JRE Emulation Library mostly for emulating java.beans package
  * Acris security has an emulated subset of classes from spring-security
  * Generally If you want to use the same class on client and on the server side but on client you want to use lightweight version of server side implementation

# Spring security emulation #

User in security implementation implements `UserDetails` interface as specified in spring security tutorial (user is obtained from database via `UserDetailsService` so it has to implement `UserDetails` interace). `UserDetails` provides core user information such as:
  * username, password, isEnabled, ...
  * array of granted authorities `GrantedAuthority[] getAuthorities();`

Now if you want to use `User` class in client implementation you should also have emulated at least 3 classes:
  * `UserDetails` interface
  * `GrantedAuthority` interface
  * `GrantedAuthorityImpl` as one implementation of `GrantedAuthority` interface - GWT compiler has to have accessible at least one implementation of the generic interface because it computes all possible implementations of the interface due to the static compilation result. If no implementor is found GWT will exclude this class and mark it as no accessible.

Using `UserDetails` class in client code is pretty easy, beacause it is by default serializable and has no special dependencies except one:
```
import org.springframework.security.Authentication;
```

This import is only used in comment so it is not necessary to emulate also `Authentication` class in client code and it should be removed. See original `UserDetails` implementation below - it is taken from spring-security-core project version 2.0.4.

```
/* Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.userdetails;

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;

import java.io.Serializable;


/**
 * Provides core user information.
 *
 * <p>
 * Implementations are not used directly by Spring Security for security
 * purposes. They simply store user information which is later encapsulated
 * into {@link Authentication} objects. This allows non-security related user
 * information (such as email addresses, telephone numbers etc) to be stored
 * in a convenient location.
 * </p>
 *
 * <p>
 * Concrete implementations must take particular care to ensure the non-null
 * contract detailed for each method is enforced. See
 * {@link org.springframework.security.userdetails.User} for a
 * reference implementation (which you might like to extend).
 * </p>
 *
 * <p>
 * Concrete implementations should be immutable (value object semantics,
 * like a String). This is because the <code>UserDetails</code> will be
 * stored in caches and as such multiple threads may use the same instance.
 * </p>
 *
 * @author Ben Alex
 * @version $Id: UserDetails.java 2735 2008-03-16 04:02:55Z benalex $
 */
public interface UserDetails extends Serializable {
    //~ Methods ========================================================================================================

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    GrantedAuthority[] getAuthorities();

    /**
     * Returns the password used to authenticate the user. Cannot return <code>null</code>.
     *
     * @return the password (never <code>null</code>)
     */
    String getPassword();

    /**
     * Returns the username used to authenticate the user. Cannot return <code>null</code>.
     *
     * @return the username (never <code>null</code>)
     */
    String getUsername();

    /**
     * Indicates whether the user's account has expired. An expired account cannot be authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie non-expired), <code>false</code> if no longer valid
     *         (ie expired)
     */
    boolean isAccountNonExpired();

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
     */
    boolean isAccountNonLocked();

    /**
     * Indicates whether the user's credentials (password) has expired. Expired credentials prevent
     * authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie non-expired), <code>false</code> if no longer
     *         valid (ie expired)
     */
    boolean isCredentialsNonExpired();

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
     */
    boolean isEnabled();
}
```

Now the process of emulation will cover folowing steps:
  * create spring.emul.org.springframework.security package in your project (for our case it is located in sk.seges.acris.security.rpc.spring.emul.org.springframework.security package) and put there class you want to emulate in GWT application - userdetails/UserDetails.java, GrantedAuthority.java and GrantedAuthorityImpl.java
  * just copy & paste code from Spring and remove dependencies which cannot be used in client code, like `org.springframework.util.Assert` and `org.springframework.security.Authentication` and you will get follwoing result for `UserDetails` class

```
package org.springframework.security.userdetails;

import org.springframework.security.GrantedAuthority;

import java.io.Serializable;

public interface UserDetails extends Serializable {

    GrantedAuthority[] getAuthorities();

    String getPassword();

    String getUsername();

    boolean isAccountNonExpired();

    boolean isAccountNonLocked();

    boolean isCredentialsNonExpired();

    boolean isEnabled();
}
```

Now ... you have the problem, that your class have package org.springframework.security.userdetails and it is located in sk.seges.acris.security.rpc.spring.emul.org.springframework.security.userdetails. Its because there are still three more steps missings.

## Setting up the maven eclipse and compiler plugin ##

You have to tell maven-compiler and maven-eclipse plugin that they should ignore this files because they are used only as GWT emulated classes (while translating java code into javascript code) and not for real code development phase (in this phase you should use original spring-security-core dependency).

```
<plugins>
	<plugin>
		<groupId>org.apache.maven.plugins</groupId>
		<artifactId>maven-compiler-plugin</artifactId>
		<configuration>
			<excludes>
				<value>**/emul/**/*.java</value>
			</excludes>
		</configuration>
	</plugin>
	<plugin>
	        <groupId>org.apache.maven.plugins</groupId>
	        <artifactId>maven-eclipse-plugin</artifactId>
	        <version>2.7</version>
	        <configuration>
	                <sourceExcludes>
		                <sourceExclude>**/emul/**/*.java</sourceExclude>
                        </sourceExcludes>
		</configuration>
	</plugin>
</plugins>
```

## Maven dependency management ##

You can now use spring-security-core dependency also to your client side project/code. Don't worry, it won't be used in production javascript code, bude GWT will replace real implementation from spring-security-core with emulated classes.

```
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-security-core</artifactId>
        <version>2.0.4</version>
</dependency>
```

## How maven know emulated classes? ##

The trick is in `<super-source value="..."/>` XML tag. Instead of standard `<source value="..."/>` tag you can use super-source tag and GWT will know that classes in this directory are emulated.
  1. Create Spring.gwt.xml located in `spring` (in our case sk.seges.acris.security.rpc.spring) resource package
  1. add `<super-source value="emul"/>`
  1. add `<inherits name="sk.seges.acris.security.rpc.spring.Spring" />` in your GWT module

Now you can use `UserDetails`, `GrantedAuthority` and also `GrantedAuthorityImpl` from spring-security package in your client side (GWT) code and also you can use it in serialization process:
  * In the client is used emulated class from package spring.emul.org.springframework.security
  * In serialization process it is serialized as payload string
  * In deserialization process is deserialized in server side into Spring classes

# Command pattern example #

Lets assume we are using standard command pattern in our application:
  1. Client (GWT) constructs specific command
  1. Command is sent to the server via RPC (as serialized string)
  1. Server executes command and sent result back to the client

[![http://acris.googlecode.com/svn/wiki/images/emulation_command_pattern.png](http://acris.googlecode.com/svn/wiki/images/emulation_command_pattern.png)

We have the same situation as in previous example. Let's assume the common interface located in rpc package - Command - available also for client side code and server side code.

```
public interface Command extends Serializable {
    void execute();
}
```

Server side implementation canlooks like:

```
public class SaveContentCommand implements Command {

    private String content;

    public SaveContentCommand() {
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void execute() {
        //get DAO for storing the content into the DB
        IAbstractDao dao = getDao();
        dao.save(content);
    }
}
```


Client side implementation can looks like this:

```
public class SaveContentCommand implements Command {

    private String content;

    public SaveContentCommand() {
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void execute() {
        throw new RuntimeException("Unable to execute command on client side!");
    }
}
```

Now when you:
  1. Use client side commands as emulated GWT classes
  1. Add server side commands as dependency to your client side code (it's used only for java compiler in order to write java code)

You can transparently use the lightweight classes in client side code, automatically deserialize them on server side as server side commands and executed them with specific data.