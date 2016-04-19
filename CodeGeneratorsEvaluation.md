## Pros & Cons & Ideas ##

Comparison to PAP + usecases

  * generate code on top of objects that are in the 3rd-party library
  * configure different generators for different objects
    * example: typically you generate Hibernate DAOs and services for domain objects but you can also generate JSON/JAXB objects and parsers. You need to be able to define it somehow
      * sculptor - a need to change the DSL = not practical
      * PAP - create annotation, write processor
  * what if you cannot annotate a class/interface?
    * PAP is useless? how to achieve this goal in PAP? [easily achievable - use delegate annotation, for example @DataAccessObject(provider = Provider.INTERFACE, delegate = Person.class) - this will generate DAO for Person class instead of the file where the annotaton is defined)
    * how to achieve it in MWE? -> custom DSL, sculptor DSL modification -> relatively easy? compare it against creating a mechanism of loading additional project-specific info into PAP
  * generate code in more projects based on one model - model is in a common project and other projects run different workflow against the model
    * in PAP I think it is not possible to do
  * generate only specific code - in MWE run specific workflow, in PAP run specific processor
    * is it possible to run only specific PAP processor?

## PAP disadvantages ##
  * hard to write an annotation processor (need senior skill and a lot of experiences)
  * longer time for learning java compiler API and processor API
  * not able to process local variable annotations (http://www.cs.rice.edu/~mgricken/research/laptjavac/)

## PAP advantages ##
  * refactoring aware
  * better integration with 3rd party libraries
  * processors are indenpent and can be started separately (developer should have own implementation)

#### PAP Model ####

```
@DistributionChannelModel.Package("sk.seges.acris.distribution")
public interface DistributionChannelModel {

	/** to be moved to framework */
	public @interface Package {
		String value() default "";
	}

	public @interface Module {}
	public @interface Hint {
		String value() default "";
	}

	public @interface ValueObject {}
	public @interface DomainInterface {}
	public @interface DomainInterfaceSpec {
		PersistenceType[] generateDao();
	}

	public @interface DataTransferObject {}
	public @interface Enumeration {}
	public @interface PersistentObject {
		public enum PersistenceType {
			HIBERNATE, JPA, TWIG
		}

		PersistenceType[] value();
	}

	/** real definition. */
	@Module
	interface shared {

		@Enumeration
		interface DistributionState {
			void CREATED();
			void READY();
			void DISTRIBUTED();
		}

		@DomainInterface
		@DomainInterfaceSpec(generateDao = PersistenceType.HIBERNATE)
		@DataTransferObject
		@PersistentObject(PersistenceType.JPA)
		interface DistributionItem {
			DistributionState state();
		}

		@DomainInterface
		@DomainInterfaceSpec(generateDao = PersistenceType.HIBERNATE)
		@DataTransferObject
		@PersistentObject(PersistenceType.JPA)
		interface DistributionChannel {
			String webId();
			String name();
			String parameters();

			Set<DistributionItem> items();
			DistributionChannelType channelType();
			DistributionSchedule schedule();

			interface Repository {
				List<DistributionChannel> findByName(String name);
			}
		}

		@Enumeration
		interface DistributionChannelType {
			void EMAIL();
			void TWITTER();
		}

		@DomainInterface
		@DomainInterfaceSpec(generateDao = PersistenceType.HIBERNATE)
		@DataTransferObject
		@PersistentObject(PersistenceType.JPA)
		interface DistributionSchedule {}

		@ValueObject
		interface DistributionParameters {}

		@ValueObject
		interface EmailDistributionParameters extends DistributionParameters {
			@Hint("mail.smtp.host")
			String serverHost();
		}
	}
}
```

#### MWE Sculptor model ####

```
Application AcrisDistribution {
    basePackage=sk.seges.acris.distribution

	Module shared {
        enum DistributionChannelType {
        	EMAIL,
        	TWITTER
        }
        
        enum DistributionState {
        	CREATED,
        	READY,
        	DISTRIBUTED
        }
	
        Entity DistributionChannel {
        	not auditable
        	String webId;
        	String name;
        	String parameters;
        	-Set<@DistributionItem> items;
        	- @DistributionChannelType channelType nullable;
        	-DistributionSchedule schedule;
        }

        Entity DistributionRecipient {
        	not auditable
        	String parameters;
        	-@DistributionChannel channel;
        }

        Entity DistributionItem {
        	not auditable
        	- @DistributionState state nullable;
        	- @DistributionChannel channel;
        	-DistributionSchedule schedule;
        }
        
        Entity ContentDistributionItem extends @DistributionItem {
        	not auditable
        	String contentWebId;
        	String contentLanguage;
        	Long contentId;
        }
        
		BasicType DistributionSchedule {
			String schedule;
		}
	}
}
```Application AcrisDistribution {
    basePackage=sk.seges.acris.distribution

	Module shared {
        enum DistributionChannelType {
        	EMAIL,
        	TWITTER
        }
        
        enum DistributionState {
        	CREATED,
        	READY,
        	DISTRIBUTED
        }
	
        Entity DistributionChannel {
        	not auditable
        	String webId;
        	String name;
        	String parameters;
        	-Set<@DistributionItem> items;
        	- @DistributionChannelType channelType nullable;
        	-DistributionSchedule schedule;
        }

        Entity DistributionRecipient {
        	not auditable
        	String parameters;
        	-@DistributionChannel channel;
        }

        Entity DistributionItem {
        	not auditable
        	- @DistributionState state nullable;
        	- @DistributionChannel channel;
        	-DistributionSchedule schedule;
        }
        
        Entity ContentDistributionItem extends @DistributionItem {
        	not auditable
        	String contentWebId;
        	String contentLanguage;
        	Long contentId;
        }
        
		BasicType DistributionSchedule {
			String schedule;
		}
	}
}
}}}```