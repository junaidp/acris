package sk.seges.corpis.server.domain.invoice.jpa;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import sk.seges.corpis.shared.domain.invoice.api.AccountableItemData;
import sk.seges.corpis.shared.domain.invoice.api.HasOrderItems;
import sk.seges.corpis.shared.domain.invoice.api.OrderItemData;

@Entity
@SequenceGenerator(name = JpaOrder.SEQ_ORDERS, sequenceName = "seq_orders", initialValue = 1)
@Table(name = "orders", uniqueConstraints = {@UniqueConstraint(columnNames = JpaOrderBase.ORDER_ID)})
public class JpaOrder extends JpaOrderBase implements HasOrderItems<JpaOrder> {
	private static final long serialVersionUID = -3117593133828636987L;

	protected static final String SEQ_ORDERS = "seqOrders";
	
	private Long id;

	private List<OrderItemData<JpaOrder>> orderItems;

	@Override
	@Id
	@GeneratedValue(generator = SEQ_ORDERS)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order", targetEntity = JpaOrderItem.class)
	public List<OrderItemData<JpaOrder>> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItemData<JpaOrder>> orderItems) {
		this.orderItems = orderItems;
	}
	
	@Override
	public List<? extends AccountableItemData> getAccountableItems() {
		return orderItems;
	}
}