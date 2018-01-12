function Transactions() {
	this.init = function() {
		this.get_transactions();
		$("#transactions-viewport").on("click", ".row", function() {
			if (!$(this).attr("data-id")) {
				return;
			}
			window.transactions.showTransaction($(this).attr("data-id"));
		});
		$("#product-list").on("click", ".selectable", function() {
			if ($(this).attr("data-id") == "") {
				return;
			}
			window.open("https://www.goldstandardresearch.co.uk/kvs/product.php?id=" + $(this).attr("data-id"), '_blank');
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
					className = "row selectable payout";
				}
				else {
					className = "row selectable payin";
				}
			}
			var row = el("section", {class:className});
			if (item.id) {
				row.setAttribute("data-id", item.id);
			}
			//Date
			var col = el("section", {class:"col-lg-2 col-md-3 col-sm-3 col-xs-6"});
			var label = el("h4", {html:moment(item.ended*1000).format("YYYY-MM-DD hh:mm:ss a")});
			col.appendChild(label);
			row.appendChild(col);
			//Cashier
			var col = el("section", {class:"col-lg-2 col-md-3 col-sm-1 col-xs-3"});
			var label = el("h4", {html:item.cashier});
			col.appendChild(label);
			row.appendChild(col);
			//Products
			var col = el("section", {class:"col-lg-2 col-md-1 col-sm-1 hidden-xs"});
			var label = el("h4", {html:item.numProducts});
			col.appendChild(label);
			row.appendChild(col);
			//Card
			var col = el("section", {class:"col-lg-2 col-md-1 col-sm-1 hidden-xs"});
			var label = el("h4", {html:formatMoney(item.card)});
			col.appendChild(label);
			row.appendChild(col);
			//Cashback
			var col = el("section", {class:"col-lg-1 col-md-1 col-sm-1 hidden-xs"});
			var label = el("h4", {html:formatMoney(item.cashback)});
			col.appendChild(label);
			row.appendChild(col);
			//money_given
			var col = el("section", {class:"col-lg-1 col-md-1 col-sm-3 hidden-xs"});
			var label = el("h4", {html:formatMoney(item.money_given)});
			col.appendChild(label);
			row.appendChild(col);
			//type 
			var col = el("section", {class:"col-lg-1 col-md-1 col-sm-1 hidden-xs"});
			var label = el("h4", {html:item.type});
			col.appendChild(label);
			row.appendChild(col);
			//total
			var col = el("section", {class:"col-lg-1 col-md-1 col-sm-1 col-xs-3"});
			var label = el("h4", {html:formatMoney(item.total)});
			col.appendChild(label);
			row.appendChild(col);
			holder.appendChild(row);
		});		
		var row=el("section", {class:"row"});
		var col = el("section", {class:"col-lg-12 col-md-12 col-sm-12 col-xs-12"});
		var label = el("label", {class:"text-center", style:"width:100%;"});
		var i = el("i", {class:"fa fa-spinner fa-spin fa-3x"});
		label.appendChild(i);
		label.appendChild(document.createTextNode("Loading"));
		col.appendChild(label);
		row.appendChild(col);
		//holder.appendChild(row);
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
					var row = el("section", {class:"row selectable", "data-id":item.id ? item.id : ""});
					//quantity
					var col = el("section", {class:"col-md-2 col-xs-2 col-sm-2"});
					var label = el("label");
					label.innerHTML = item.quantity;
					//transQuantity += parseInt(item.quantity);
					col.appendChild(label);
					row.appendChild(col);
					//name
					var col = el("section", {class:"col-md-8 col-xs-8 col-sm-8"});
					var label = el("label", {class:item.id ? "text-info" : ""});
					label.innerHTML = item.name;
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
