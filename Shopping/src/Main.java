
public class Main {

	public static void main(String[] args) {
		
		Basket basket = new Basket();		
		basket.addProduct("Brown bread", Category.Food, 1.20);
		basket.addProduct("Frying pan", Category.Other, 9.99);
		basket.addProduct("Dust mask", Category.SafetyEquipment, 4.99);
		basket.listProducts();
		
		
		// Compute discount
		
		DiscountComputer discountComputer = new DiscountComputer();
		
		double discount = discountComputer.computeDiscount(basket, false);
		System.out.printf("Discount for customers: £%.2f\n", discount);
	
		
		discount = discountComputer.computeDiscount(basket, true);
		System.out.printf("Discount for staff: £%.2f\n", discount);
		
	}

}
