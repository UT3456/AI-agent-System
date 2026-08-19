package com.substring.agent.backend.tools;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InventoryToolsTest {

	private final InventoryTools inventoryTools = new InventoryTools();

	@Test
	void reportsInStockProductWithQuantity() {
		assertThat(inventoryTools.checkStock("Wireless Mouse"))
				.contains("In Stock", "42");
	}

	@Test
	void reportsOutOfStockProduct() {
		assertThat(inventoryTools.checkStock("USB-C Cable"))
				.contains("Out of Stock");
	}

	@Test
	void reportsUnknownProduct() {
		assertThat(inventoryTools.checkStock("Hoverboard"))
				.contains("Product not found");
	}

	@Test
	void listsOnlyProductsThatHaveStock() {
		assertThat(inventoryTools.getAllProductsInStock())
				.contains("Wireless Mouse", "Laptop Stand")
				.doesNotContain("USB-C Cable", "Smartphone Case", "Noise-Cancelling Earbuds");
	}

	@Test
	void countsAllProductsInCatalogue() {
		assertThat(inventoryTools.getTotalProductsInStock()).isEqualTo(10);
	}

}
