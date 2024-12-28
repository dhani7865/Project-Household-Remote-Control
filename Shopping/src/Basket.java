import java.util.ArrayList;

public class Basket {

	public ArrayList<String> productNames = new ArrayList<String>();
	public ArrayList<Category> productCategories = new ArrayList<Category>();
	public ArrayList<Double> productPrices = new ArrayList<Double>();
	public double totalCost = 0.0;
	public double totalTax = 0.0;

	/**
	 * Add a product to the basket
	 */
	public void addProduct(String name, Category category, double price) {

		// Work out tax percentage
		int taxPercentage = taxPercentage(category);


		double taxAmount =  price * (1 - 100.0 / (100 + taxPercentage));
		totalTax += taxAmount;
		totalCost += price;
		productNames.add(name);
		productCategories.add(category);
		productPrices.add(price);

		System.out.printf("Added %s \t Total: £%.2f \t (VAT: £%.2f)\n", name, totalCost, totalTax);


	}

	/**
	 * Print the products out to the console
	 */
	public void listProducts() {
		System.out.println("Products:");

		for (int i = 0; i < productNames.size(); i++) {
			Category category = productCategories.get(i);

			int taxPercentage = taxPercentage(category);
			
			// Work out tax percentage
//			int taxPercentage = taxPercentage(name, category, price);

			System.out.printf("  %s \t £%.2f \t (VAT @ %d%%)\n", productNames.get(i), productPrices.get(i), taxPercentage);
		}

		System.out.printf("Total: £%.2f\n", totalCost);
	}

	private String productString(String name, double price) {
		return String.format("%s\t%.2f", name, price);
	}

	/**
	 * Tax percentage a product to the basket
	 * @return 
	 */
	public int taxPercentage(Category category) {

		// Work out tax percentage

		int taxPercentage = 0;
		if (category == Category.Food) taxPercentage = 0;
		else if (category == Category.SafetyEquipment) taxPercentage = 5;
		//else if (product.category == Category.Alcohol) taxPercentage = 40;
		else if (category == Category.Other) taxPercentage = 20;
		return taxPercentage;

	}
}
