window.departmentNames = [];
function Takings(){
	this.init = function() {
		$.ajax({
			url:"api/kvs.php?function=DEPARTMENTS",
			dataType: "JSON",
			success: function(data) {
				$.each(data, function(key, item) {
					window.departmentNames.push(item);
					var row = el("section", {class:"row table-box"});
					var col = el("section", {class:"col-md-2", style:"padding-left:0;padding-right:0;"});
					var p = el("b", {html:item.name});
					col.appendChild(p);
					row.appendChild(col);
					for (var i=0;i<10;i++) {
						var col = el("section", {class:"col-md-1", style:"padding-left:0;padding-right:0;"});
						var p = el("b", {html:"hello"});
						col.appendChild(p);
						row.appendChild(col);
					}
					$("#takings-columns").append(row);
				});
				window.takings.get_takings();
			}
		});
		var date = moment().subtract(9, "days");
		var currentTimestamp = parseInt(moment().format("x"));
		while (currentTimestamp > parseInt(moment(date.format("YYYY-MM-DD")).format("x"))) {
			var col = el("section", {class:"col-md-1 table-box"});
			var label = el("label", {html:date.format("YYYY-MM-DD"), style:"padding-left:0;padding-right:0;"});
			col.appendChild(label);
			$("#dates-header").append(col);
			date.add(1, "days");
		}
		
	}
	this.get_transactions = function(start, end) {
		$.ajax({
			url:"api/kvs.php?function=GETTRANSACTIONS",
			data:{"start":start, "end":end},
			dataType: "JSON",
			method:"POST",
			success: function(data) {
				if (!data.success) {
					alert("Error collecting totals");
				}
				var refunds = 0.00;
				var cardGiven = 0.00;
				var totalTransactions = 0.00;
				var totalPayouts = 0.00;
				var payoutsGiven = 0.00;
				var total = 0.00;
				var payouts = 0.00;
				var cashback = 0.00;
				$.each(data.transactions, function(key, item) {
					switch (item.type) {
						case "PURCHASE":
							totalTransactions++;
							cardGiven += parseFloat(item.card);
							total += parseFloat(item.total);
							cashback += parseFloat(item.cashback);
							break;
						case "REFUND":
							refunds += parseFloat(item.total);
							total -= parseFloat(item.total); //Take away refunds from takings
							break;
						case "PAYOUT":
							payouts++;
							payoutsGiven += parseFloat(item.total);
							break;
					}
				});
				$("#payouts").html(formatMoney(payoutsGiven));
				$("#refunds").html(formatMoney(refunds));
				$("#card-payments").html(formatMoney(cardGiven));
				$("#takings").html(formatMoney(total));
				$("#cashback").html(formatMoney(cashback));
				var cashid = total;
				cashid -= payoutsGiven;
				cashid -= (cardGiven-cashback);
				cashid -= (cashback*2); //this is taken away twice because it is added as a product
				$("#cash-id").html(formatMoney(cashid));
				$("#takings-modal .modal-title").html(moment(start*1000).format("LLLL"));
				$("#takings-modal").modal("show");
			}
		});
	}
	this.get_takings = function() {
		window.totals = [];
		window.semaphore = 0;
		var date = moment().subtract(1, "months");
		var currentTimestamp = parseInt(moment().format("x"));
		while (currentTimestamp > parseInt(moment(date.format("YYYY-MM-DD")).format("x"))) {
			var start = moment(date.format("YYYY-MM-DD")).format("x")/1000;
			var end = moment(date.add(1, "days").format("YYYY-MM-DD")).format("x")/1000;
			$.ajax({
				url:"api/kvs.php?function=TOTALS",
				data:{"start":start, "end":end, "type":"PURCHASE"},
				dataType: "JSON",
				method:"POST",
				beforeSend: function() {
					window.semaphore++;
				},
				success: function(data) {
					if (!data.success) {
						alert("Error collecting totals");
					}
					window.totals.push(data);
					window.takings.populate_table(window.totals);
				},
				complete:function() {
					window.semaphore--;
				}
			});
		}
	}
	this.populate_table = function(data) {
		if (window.semaphore > 1) {
			return;
		}
		var holder = document.getElementById("takings-viewport");
		$(holder).empty();
		var section = el("section");
		var table = el("table", {id:"takings-table", class:"table", style:"width:100%"});
		var thead = el("thead");
		var tr = el("tr");
		var th = el("th", {html:"Date"});
		tr.appendChild(th);
		var departments = window.departmentNames;
		for (var i=0;i<departments.length;i++) {
			var th = el("th");
			th.className = "head";
			th.innerHTML = departments[i].shorthand;
			tr.appendChild(th);
		}
		var th = el("th", {class:"head", html:"Total"});
		tr.appendChild(th);
		thead.appendChild(tr);
		table.appendChild(thead);
		var tbody = el("tbody");
		$.each(data, function(key, item) {
			var total = 0.00;
			var tr = el("tr", {"data-start":item.start, "data-end":item.end});
			var td = el("td", {html:moment(item.start*1000).format("YYYY-MM-DD")});
			tr.appendChild(td);
			$.each(window.departmentNames, function(id, department) {
				var td = el("td");
				var amount = parseFloat(item.totals[department.id] ? item.totals[department.id] : "0.00");
				total = total + amount;
				td.innerHTML = formatMoney(amount);
				tr.appendChild(td);
			});
			var td = el("td", {html:formatMoney(total)});
			tr.appendChild(td);
			tbody.appendChild(tr);
		});
		table.appendChild(tbody);
		section.appendChild(table);
		//holder.appendChild(section);
	}
	$("#takings-table").on("click", "tr", function() {
		alert("hello");
		if (!$(this).attr("data-start") || !$(this).attr("data-end")) {
			return;
		}
		alert("again");
		window.takings.get_transactions($(this).attr("data-start"), $(this).attr("data-end"));
	});
}
function formatMoney(amount) {
	return accounting.formatMoney(amount, "");
}

