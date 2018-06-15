//Product Class for Cash Register
function Product() {
	this.id = null;
	this.name = "Unknown Product";
	this.barcode = "";
	this.cost = 0.00;
	this.quantity = 1;
	this.inDatabase = false;
	this.department = null;
	this.priceOverride = false;
	this.priceOverrideNewCost = 0.00;
	this.updated = 0;
	this.maxStock = 0;
	this.currentStock = 0;
	this.comment = ""; //Is the product on sale, or has missing data?
	this.overridePrice = function(newCost) {
		this.priceOverride = true;
		this.priceOverrideNewCost = newCost;
		getTransaction().refreshTable();
	}
	this.setCost = function(cost) {
		this.cost = cost;
	}
	this.totalCost = function() {
		if (!this.priceOverride) {
			return (this.cost * this.quantity);
		}
		return (this.priceOverrideNewCost * this.quantity);
	}
	this.productCost = function() {
		if (!this.priceOverride) {
			return parseFloat(this.cost);
		}
		return this.priceOverrideNewCost;
	}
	this.update = function(barcode, cost, price) {
		$.ajax({
			url: "api/product.jsp",
			data : {"number" : barcode, "Name" : this.name, "Cost" : cost, "price" : price},
			success: function(data) {
				if (data.success) {
					this.barcode = Barcode;
					this.Cost = price;
					refreshTable();
				}
				else {
					bootbox.alert("Product Not Updated");
				}
			}
		});
	}
	this.updated = 0; //Product Update Timestamp
}
