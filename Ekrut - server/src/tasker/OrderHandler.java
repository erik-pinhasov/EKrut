package tasker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

import DBHandler.DBController;
import Entities.OrderDetails;
import Entities.OrderProduct;
import Util.Msg;

public class OrderHandler {
	private static final DecimalFormat decimal = new DecimalFormat("0.00");
	private static int lastOrder = 1;
	private static String pickUpCode;

	/**
	 * Inserts a new item to the order_items table in the database
	 * @param order  the order details object containing the items to be inserted
	 */
	private static void insertItemsQuery(OrderDetails order) {
		StringBuilder query = new StringBuilder("INSERT INTO order_items VALUES ");
		for (OrderProduct p : order.getItems()) {
			query.append("(" + lastOrder + ",");
			query.append(p.getProductID() + ",");
			query.append(p.getCartQuant() + ",");
			query.append(decimal.format(order.getProductPrice(p) * p.getCartQuant()) + "),");
		}
		query.deleteCharAt(query.length() - 1);
		DBController.update(query.toString());
	}

	/**
	 * Updates the quantity of products in a store after an order is placed
	 * @param order  the order details object containing the items to be updated
	 */
	private static void updateSotreQuant(OrderDetails order) {
		for (OrderProduct p : order.getItems()) {
			DBController.update("UPDATE store_product set quantity = quantity - " + p.getCartQuant() + " WHERE pid = "
					+ p.getProductID() + " AND sid = " + order.getStore_ID());
		}
	}

	/**
	* Inserts a new order into the orders table in the database
	* @param order  the order details object containing the information to be inserted
	*/
	private static void insertOrderQuery(OrderDetails order) {
		String values = String.format("%d,%d,%.2f,'%s','%s','%s'", order.getUserId(), order.getStore_ID(),
				order.getAfterDiscount(), java.sql.Date.valueOf(LocalDate.now()),
				java.sql.Time.valueOf(LocalTime.now()), order.getMethod());
		if (order.getMethod().equals("Local")) {
			DBController.update("INSERT INTO orders (cid,sid,total_price,ord_date,ord_time,method,ord_status,delayed_payment) VALUES ("
					+ values + ",'Completed'" + (order.isDelayed_payment() ? ",1)" : ",0)"));
		} else {
			
			DBController.update("INSERT INTO orders (cid,sid,total_price,ord_date,ord_time,method,delayed_payment) VALUES ("
			+ values + (order.isDelayed_payment() ? ",1)" : ",0)"));
		}
		updateLastOrder(); //fetch the orderId that we insert now.
	}

	/**
	* Generates a random 6 digit code
	* @return a random 6 digit code as a string
	*/
	private static String createRandomCode() {
		String rndnumber = "";
		Random rnd = new Random();
		for (int i = 0; i < 6; i++)
			rndnumber = rndnumber + rnd.nextInt(9);
		return rndnumber;
	}

	/**
	* Check if a store has enough quantity of a product
	* @param product  the product to be checked
	* @param storeID  the id of the store
	* @return true if the store has enough quantity of the product, false otherwise
	*/
	private static boolean productQuant(OrderProduct product, int storeID) {
		ResultSet rs = DBController.select(
				"SELECT quantity FROM store_product WHERE pid = " + product.getProductID() + " AND sid = " + storeID);
		try {
			rs.first();
			return rs.getInt(1) < product.getCartQuant() ? false : true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * get last (this) order id from the database 
	 */
	private static void updateLastOrder() {
		ResultSet rs = DBController.select("SELECT oid FROM orders ORDER BY oid DESC LIMIT 1");//last order
		try {
			rs.first();
			lastOrder = rs.getInt(1);
		} catch (SQLException e) {
			//No orders so id to insert is 1;
		}
	}

	/**
	* Check if an order is valid, quantity in order quant less then quantity in database.
	* @param order  the order to be checked
	* @return true if the order is valid, false otherwise
	*/
	private static boolean checkOrder(OrderDetails order) {
		if (order.getItems().isEmpty())
			return false;
		for (OrderProduct p : order.getItems())
			if (!productQuant(p, order.getStore_ID()))
				return false;
		return true;
	}

	/**
	* Inserts order to Delivery/Pickup table in database if order method Delivery/Pickup.  
	* @param order  the order to insert.
	* @see OrderDetails
	*/
	private static void extraUpdate(OrderDetails order) {
		if (order.getMethod().equals("Delivery"))
			DBController.update("INSERT INTO deliveries (oid,shipping_address) VALUES (" + lastOrder + ",'"
					+ order.getAddress() + "')");
		if (order.getMethod().equals("Pickup")) {
			pickUpCode = createRandomCode();
			DBController.update("INSERT INTO pickup (oid,sid,orderCode) VALUES (" + lastOrder + ","
					+ order.getStore_ID() + ",'" + pickUpCode + "')");
		}
	}

	/**
	* Creates a new order in the database
	* @param msg  the message object containing the order details
	*/
	public static synchronized void createOrder(Msg msg) {
		if (!checkOrder(msg.getOrder())) {
			msg.setBool(false);
		} else {
			pickUpCode = null; //init pickupcode to null.
			insertOrderQuery(msg.getOrder());
			updateSotreQuant(msg.getOrder()); //update store quantity.
			insertItemsQuery(msg.getOrder());
			extraUpdate(msg.getOrder()); //if order is delivery or pickup then update extra tables.
			msg.setBool(true);
			msg.setResponse(pickUpCode);
		}

	}

}
