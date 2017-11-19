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
	this.updated = 0;
	this.maxStock = 0;
	this.currentStock = 0;
	this.comment = ""; //Is the product on sale, or has missing data?
	//this.override(overridden) {
		
	//}
	this.setCost = function(cost) {
		this.cost = cost;
	}
	this.totalCost = function() {
		return (this.cost * this.quantity);
	}
	this.productCost = function() {
		return this.cost;
	}
	this.update = function(barcode, cost, price) {
		$.ajax({
			url: "api/product.php",
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
