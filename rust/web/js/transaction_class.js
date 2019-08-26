//Transaction class for cash register
function Transaction() {
	$("#notify").addClass("hidden");
	this.id = null;
	this.cashier = getOperator();
	this.products = {};
	this.appliedOffers = {};
	this.moneyGiven = 0.00;
	this.cardGiven = 0.00;
	this.total = 0.00; //only used for payouts
	this.type = "PURCHASE";
	this.lastAddedProduct = null;
	this.payee = null;
	this.in_progress = false;
	this.refreshTable = function() {
		if (this.numItems() > 0) {
			refreshTable();
			return;
		}
		clearTransactionTable();
	}
	this.refundItems = function() {
		this.type = "REFUND";
		this.completeTransaction();
	}
	this.getProductsJSON = function() {
		json = {}
		tempArr = []
		$.each(this.products, function(key, product) {
			tempArr.push({"id":product.id, "inDatabase":product.inDatabase, "price":product.productCost(), "department":product.department, "quantity":product.quantity});
		});
		json["products"] = tempArr;
		return JSON.stringify(json);
	}
	this.numItems = function() {
		return Object.keys(this.products).length;
	}
	this.totalQuantity = function() {
		var total = 0;
		$.each(this.products, function(key, item) {
			total += item.quantity;
		});
		return (total);
	}
	this.totalCost = function() {
		return formatMoney(this.totalCostValue());
	}
	this.totalCostValue = function() {
		var total = this.total; //total only used for payouts
		$.each(this.products, function(key, item) {
			total += (item.cost * item.quantity);
		});
		return total;
	}
	this.startTransaction = function(product) {
		var product = product || false;
		if (this.in_progress) {
			return false;
		}
		$.ajax({
			url: CONTEXT + "/transaction",
			method:"POST",
			contentType: "application/json",
			data : JSON.stringify({"cashier" : this.cashier}),
			success : function(data) {
				getTransaction().id = data.id;
				getTransaction().in_progress = true;
				if (product) {
					getTransaction().addProduct(product);
					return;
				}
			},
			error:function(e, status, error) {
				console.log(error);
				bootbox.alert("Error starting transaction");
			}
		});
	}
	this.changeQuantity = function(id, quantity) {
		if (!this.products[id]) {
			return;
		}
		this.products[id].quantity = quantity;	
	}
	this.addProduct = function(product) {
		if (!this.in_progress) {
			this.startTransaction(product);
			return;
		}
		if (!product.inDatabase || !this.products[product.id])
			this.products[product.id] = product;
		else
			this.products[product.id].quantity++;
		this.lastAddedProduct = product.id;
		this.refreshTable();
	}
	this.getCashback = function() {
		var cashback = 0.00;
		$.each(this.products, function(key, item) {
			if (item.department == CASHBACK_DEPARTMENT) {
				cashback += item.cost;
			}
		});
		return cashback;
	}
	this.removeProduct = function(id) {
		delete this.products[id];
		if (Object.keys(this.products).length == 0) {
			this.clearTransaction();
			return;
		}
		this.refreshTable();
	}
	this.completeTransaction = function() {
		var transactionJSON = {"id":this.id, "cashier":this.cashier, "json":this.getProductsJSON(), "money_given":this.moneyGiven, "cashback":this.getCashback(), "card_given":this.cardGiven, "total":this.totalCostValue(), "type":this.type, "payee":this.payee};
		window.offlineStorage.putTransaction(transactionJSON);
		$.ajax({
			url: CONTEXT + "kvs.jsp?function=COMPLETETRANSACTION",
			data : transactionJSON,
			success : function(data) {
				if (!data.success) {
					bootbox.alert("Failed to complete transaction: " + getTransaction().id + "-please try again");
					return;
				}
				delete window.cashiersTransactions[getTransaction().cashier];
				window.cache = {};
				clearTransactionTable();
			}
		});
		return true;
	}
	this.clearTransaction = function() {
		$.ajax({
			url: CONTEXT + "kvs.jsp?function=CLEARTRANSACTION",
			data : {"transaction_id":this.id},
			success : function(data) {
				if (!data.success) {
					bootbox.alert("Failed to clear transaction");
				}
				delete window.cashiersTransactions[getTransaction().cashier];
				clearTransactionTable();
			}
		});
	}
}
