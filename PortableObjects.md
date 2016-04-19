# Introduction #

Portable domain object consists of:

  * domain object declaration
  * transfer object declaration
  * declaration and definition of specific persistent engine implementation

## Domain object declaration ##

  * write interface
    * for domain entity extend from `IMutableDomainObject`
    * for basic type (e.g. embedded entity) you don't need to extend, probably only from Serializable
      * don't forget to extend from Serializable if you send your objects using any serialization mechanism (like GWT-RPC)
  * use "Data" suffix
  * declare only business related methods

```
public interface ParticipantData<K> extends IMutableDomainObject<K> {
...
    static final String CUSTOMER = "customer";
    static final String CREATED = "created";
    static final String ORIENTATION = "orientation";
...
    CustomerBaseData getCustomer();
    void setCustomer(CustomerBaseData customer);

    Date getCreated();
    void setCreated(Date created);

    String getOrientation();
    void setOrientation(String orientation);
...
}

```

## Transfer object declaration ##

  * write class extending from domain object declaration
  * use "Dto" suffix
  * implement fields, getters, setters and overall logic available/runnable on all persistent engines

```
public class ParticipantDto implements ParticipantData<Long> {
    ...
    private Long id;
    private CustomerBaseData customer;
    private Date created;
    private String orientation;
    ...
    // and getters and setters
}

```

## JPA domain object ##

  * don't forget to inherit all getters you want to be present as columns
  * annotate ID getter with @Id
  * association mapping:
    * specify **targetEntity**
    * (optional) narrow the return type of the getter to the target entity class
  * embedded mapping:
    * annotate with @Embedded
    * **narrow** the return type of the getter to the target entity class, don't use domain object declaration type (interface)

```
@Entity
@SequenceGenerator(name = "seqParticipant", sequenceName = "SEQ_PARTICIPANT", initialValue = 1)
@Table(name = "ou_participant")
public class JpaParticipant extends ParticipantDto {
	private static final long serialVersionUID = 3621992042068349596L;

	@Id
	@GeneratedValue(generator = "seqParticipant")
	@Override
	public Long getId() {
		return super.getId();
	}

	@OneToOne(targetEntity = JpaCustomerBase.class)
	@Override
	public CustomerBaseData getCustomer() {
		return super.getCustomer();
	}
...
}

```

```
@Entity
public class JpaCustomerBase extends CustomerBaseDto<Integer> {
    ...
	@AttributeOverride(name = JpaCompanyName.COMPANY_NAME, column = @Column(unique = true, nullable = true))
	@Embedded
	public CompanyNameData getCompany() {
		return super.getCompany();
	}
    ...
}

```

## Hibernate specific domain object ##

  * don't forget to adjust **targetEntity** on all associations to point to Hibernate specific domain objects
  * don't forget to narrow embedded getter return types to Hibernate specific domain objects

```
@Entity
public class HibernateCustomerBase extends CustomerBaseDto {
    ...
	@AttributeOverride(name = JpaCompanyName.COMPANY_NAME, column = @Column(unique = true, nullable = true))
	@Embedded
	@Override
	public CompanyNameData getCompany() {
		return super.getCompany();
	}
    ...
}

```

```
@Embeddable
public class Address extends AddressDto {
    ...
	@ManyToOne(targetEntity = HibernateCountry.class)
	public CountryData getCountry() {
		return super.getCountry();
	}
    ...
}

```

# Identified problems #

Current **Content** relation:
  * has different return type for //menu item// in Content (String) and in ContentDTO (MenuItem)