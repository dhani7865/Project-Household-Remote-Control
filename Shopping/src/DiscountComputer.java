

public class DiscountComputer {

	public double computeDiscount(Basket basket, boolean isStaff) {

		if (isStaff) {
			return basket.totalCost * 0.35;
		}
		else {

			// Check to see if they are eligable

			if (isNotEligible(basket)) {
				return 0;
			}

			// Work out discount amount

			int discountPercentage = 0;
			
			/**
			 * if the total cost is less than 10 and 5, return discountPercentage.
			 * Otherwise, if the total cost is greater than 10, return 1.
			 */
			if (basket.totalCost < 10 && basket.totalCost < 5) {
				return discountPercentage;
			}
			else if (basket.totalCost > 10) {
				discountPercentage = 1;
			}
			
			/**
			 * If the total cost is less than 20, equal to 2.
			 * Otherwise, if the total cost is less than 30, return 3.
			 * Otherwise, return 5
			 */
			if (basket.totalCost < 20) {
				discountPercentage = 2;
			}
			else if  (basket.totalCost < 30) {
				discountPercentage = 3;
			}
			else {
				discountPercentage = 5;
			}
			// Return discount percentage
			return basket.totalCost * discountPercentage / 100.0;
		}
	}		
	
	/**
	 *  Creating public boolean method for isNotEligible.
	 * if the productNames are less than 2, return true.
	 * If the total cost is less than 5.00, return true. 
	 * Otheriwse, return false
	 * @param basket
	 * @return
	 */
	public boolean isNotEligible(Basket basket) {
		// Check to see if they are eligable
		if (basket.productNames.size() < 2) {
			return true;
		}
		else if (basket.totalCost < 5.00) {
			return true;
		}
		return false;


	}
}
