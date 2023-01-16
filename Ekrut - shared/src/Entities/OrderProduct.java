package Entities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

import javafx.beans.property.SimpleIntegerProperty;

public class OrderProduct implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7126677968806442791L;
	// image
	private int productID;
	private String name;
	private int price, quant;
	private transient SimpleIntegerProperty cartQuant = new SimpleIntegerProperty(0);
	// quantity

	public OrderProduct(int productID, String name, int price, int quant) {
		this.productID = productID;
		this.name = name;
		this.price = price;
		this.quant = quant;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cartQuant, name, price, productID, quant);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderProduct other = (OrderProduct) obj;
		return Objects.equals(cartQuant, other.cartQuant) && Objects.equals(name, other.name) && price == other.price
				&& productID == other.productID && quant == other.quant;
	}

	public int getProductID() {
		return productID;
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}

	public int getQuant() {
		return quant;
	}

	public int getCartQuant() {
		return cartQuant.get();
	}

	public SimpleIntegerProperty getCartQuantProperty() {
		return cartQuant;
	}

	public void setQuant(int quant) {
		this.quant = quant;
	}

	public void setCartQuant(int quant) {
		this.cartQuant.set(quant);
	}

	public boolean addToCart() {
		if (cartQuant.get() < quant) {
			cartQuant.set(cartQuant.get() + 1);
			return false;
		}
		return true;
	}

	public int removeFromCart() {
		if (cartQuant.get() > 0) {
			cartQuant.set(cartQuant.get() - 1);
			return cartQuant.get();
		}
		return 0;
	}

	public void setProductID(int productID) {
		this.productID = productID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeInt(productID);
		out.writeUTF(name);
		out.writeInt(price);
		out.writeInt(quant);
		out.writeInt(cartQuant.get());
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		productID = in.readInt();
		name = in.readUTF();
		price = in.readInt();
		quant = in.readInt();
		cartQuant = new SimpleIntegerProperty(in.readInt());
	}

}
