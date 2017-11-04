function Transactions() {
	this.init = function() {
		this.get_transactions();
		$("#transactions-viewport").on("click", ".row", function() {
			if (!$(this).attr("data-id")) {
				return;
			}
			window.transactions.showTransaction($(this).attr("data-id"));
		});
	}
	this.get_transactions = function() {
		var date = moment();
		var start = moment(date.format("YYYY-MM-DD")).format("x")/1000;
		var end = (moment(date.add(1, "days").format("YYYY-MM-DD")).format("x")/1000)-1;
		$.ajax({
			url:"api/kvs.php?function=GETTRANSACTIONS",
			data:{"start":start, "end":end},
			dataType: "JSON",
			method:"POST",
			success: function(data) {
				if (!data.success) {
					alert("Error collecting transactions");
				}
				$("#dateFrom").html(moment(data.start*1000).format("YYYY-MM-DD"));
				$("#timeFrom").html(moment(data.start*1000).format("HH:mm:ss"));
				$("#dateTo").html(moment(data.end*1000).format("YYYY-MM-DD"));
				$("#timeTo").html(moment(data.end*1000).format("HH:mm:ss"));
				window.transactions.populate_table(data.transactions);
			}
		});
	}
	this.populate_table = function(data) {
		var holder = document.getElementById("transactions-viewport");
		$(holder).empty();
		if (data.length == 0) {
			var h3 = el("h3");
			h3.innerHTML = "No Transactions Yet";
			holder.appendChild(h3);
			return;
		}
		$.each(data, function(key, item) {
			var className = "";
			if (item.type) {
				if (item.type == "PAYOUT") {
					className = "row payout";
				}
				else {
					className = "row payin";
				}
			}
			var row = el("section", {class:className});
			if (item.id) {
				row.setAttribute("data-id", item.id);
			}
			//Date
			var col = el("section", {class:"col-lg-2 col-md-2 col-sm-2 col-xs-2"});
			var label = el("label", {html:moment(item.ended*1000).format("YYYY-MM-DD hh:mm:ss a")});
			col.appendChild(label);
			row.appendChild(col);
			//Cashier
			var col = el("section", {class:"col-lg-1 col-md-1 col-sm-1 col-xs-1"});
			var label = el("label", {html:item.cashier});
			col.appendChild(label);
			row.appendChild(col);
			//Products
			var col = el("section", {class:"col-lg-1 col-md-1 col-sm-1 col-xs-1"});
			var label = el("label", {html:item["#Products"]});
			col.appendChild(label);
			row.appendChild(col);
			//Card
			var col = el("section", {class:"col-lg-1 col-md-1 col-sm-1 col-xs-1"});
			var label = el("label", {html:item.card});
			col.appendChild(label);
			row.appendChild(col);
			//Cashback
			var col = el("section", {class:"col-lg-1 col-md-1 col-sm-1 col-xs-1"});
			var label = el("label", {html:item.cashback});
			col.appendChild(label);
			row.appendChild(col);
			//money_given
			var col = el("section", {class:"col-lg-1 col-md-1 col-sm-1 col-xs-1"});
			var label = el("label", {html:item.money_given});
			col.appendChild(label);
			row.appendChild(col);
			//type 
			var col = el("section", {class:"col-lg-1 col-md-1 col-sm-1 col-xs-1"});
			var label = el("label", {html:item.type});
			col.appendChild(label);
			row.appendChild(col);
			//total
			var col = el("section", {class:"col-lg-1 col-md-1 col-sm-1 col-xs-1"});
			var label = el("label", {html:item.total});
			col.appendChild(label);
			row.appendChild(col);
			holder.appendChild(row);
		});		
	}
	this.showTransaction = function(id) {
		$.ajax({
			url:"api/kvs.php?function=GETTRANSACTION",
			data:{"id":id},
			dataType: "JSON",
			method:"POST",
			success: function(data) {
				if (!data.success) {
					alert("Error retreiving details");
				}
				var holder = $("#product-list")[0];
				$(holder).empty();
				var transTotal = 0;
				var transQuantity = 0;
				$.each(data.products, function(key, item) {
					var total
					var row = el("section", {class:"row"});
					//quantity
					var col = el("section", {class:"col-md-2 col-xs-2 col-sm-2"});
					var label = el("label");
					label.innerHTML = 1;
					//transQuantity += parseInt(item.quantity);
					col.appendChild(label);
					row.appendChild(col);
					//name
					var col = el("section", {class:"col-md-8 col-xs-8 col-sm-8"});
					var label = el("label");
					if (item.id) {
						var a = el("a");
						a.href = "https://www.goldstandardresearch.co.uk/kvs/product.php?id=" + item.id;
						a.target = "_blank";
						a.innerHTML = item.name;
						label.appendChild(a);
					}
					else {
						label.innerHTML = item.name;
					}
					col.appendChild(label);
					row.appendChild(col);
					//price
					var col = el("section", {class:"col-md-2 col-xs-2 col-sm-2"});
					var label = el("label");
					label.innerHTML = formatMoney(item.price);
					transTotal += parseFloat(item.price)
					col.appendChild(label);
					row.appendChild(col);
					holder.appendChild(row);
				});
				$("#transTotal").html(formatMoney(transTotal));
				$("#transactionProducts").modal("show");
			}
		});
	}
}
