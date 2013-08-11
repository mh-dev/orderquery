package mh.dev.common.orderquery;

import java.util.List;

public interface OrderState {

	/**
	 * Changes the ordering of the given column to the specified {@link Order}
	 * 
	 * @param column
	 * @param order
	 */
	public void order(String column, Order order);

	/**
	 * Set the given column to {@link Order} none
	 * 
	 * @param column
	 * @param order
	 */
	public void remove(String column, Order order);

	/**
	 * Returns the {@link Order} of a specific column
	 * 
	 * @param column
	 * @return
	 */
	public Order state(String column);

	/**
	 * Returns the current sort order
	 * 
	 * @return
	 */
	public List<String> orderedColumns();
}
