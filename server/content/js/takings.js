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
		/*var date = moment().subtract(9, "days");
		var currentTimestamp = parseInt(moment().format("x"));
		while (currentTimestamp > parseInt(moment(date.format("YYYY-MM-DD")).format("x"))) {
			var col = el("section", {class:"col-md-1 table-box"});
			var label = el("label", {html:date.format("YYYY-MM-DD"), style:"padding-left:0;padding-right:0;"});
			col.appendChild(label);
			$("#dates-header").append(col);
			date.add(1, "days");
		}*/
	}
	this.get_transactions = function(start, end) {
		$.ajax({
			url:"api/kvs.php?function=GETDAYTOTALS",
			data:{"start":start, "end":end},
			dataType: "JSON",
			method:"POST",
			success: function(data) {
				if (!data.success) {
					alert("Error collecting totals");
				}
				var refunds = data["refunds"];
				var cardGiven = data["card"];
				var total = data["takings"];
				var payouts = data["payouts"];
				var cashback = data["cashback"];
				$("#payouts").html(formatMoney(payouts));
				$("#refunds").html(formatMoney(refunds));
				$("#card-payments").html(formatMoney(cardGiven));
				$("#takings").html(formatMoney(total));
				$("#cashback").html(formatMoney(cashback));
				var cashid = total;
				cashid -= payouts;
				cashid -= (cardGiven-cashback);
				cashid -= (cashback*2); //this is taken away twice because it is added as a product
				$("#cash-id").html(formatMoney(cashid));
				$("#takings-modal .modal-title").html(moment(start*1000).format("LLLL"));
				$("#takings-modal").modal("show");
			}
		});
	}
	this.get_takings = function() {
		var start = Math.floor(moment().subtract(5, "months").format("x")/1000);
		var end = Math.floor(moment().format("x")/1000);
		$.ajax({
			url:"api/kvs.php?function=TOTALS",
			data:{"start":start, "end":end, "type":"PURCHASE"},
			dataType: "JSON",
			method:"POST",
			success: function(data) {
				if (!data.success) {
					alert("Error collecting totals");
				}
				window.takings.populate_table(data.totals);
			}
		});
	}
	this.populate_table = function(data) {
		var holder = document.getElementById("takings-viewport");
		$(holder).empty();
		var section = el("section");
		var table = el("table");
		table.id = "table";
		table.className = "table";
		var thead = el("thead");
		var tr = el("tr");
		var th = el("th");
		th.innerHTML = "Date";
		tr.appendChild(th);
		var departments = window.departmentNames;
		for (var i=0;i<departments.length;i++) {
			var th = el("th");
			th.className = "head";
			th.innerHTML = departments[i].shorthand;
			tr.appendChild(th);
		}
		var th = el("th");
		th.className = "head";
		th.innerHTML = "Total";
		tr.appendChild(th);
		thead.appendChild(tr);
		table.appendChild(thead);
		var tbody = el("tbody");
		$.each(data, function(date, totals) {
			var total = 0.00;
			var tr = el("tr", {"data-start":moment(date).unix(), "data-end":moment(date).add(1, "days").subtract(1, "seconds").unix()});
			var td = el("td");
			td.innerHTML = date;
			tr.appendChild(td);
			$.each(window.departmentNames, function(id, department) {
				var td = el("td");
				var amount = parseFloat(totals[department.id] ? totals[department.id] : "0.00");
				total = total + amount;
				td.innerHTML = formatMoney(amount);
				tr.appendChild(td);
			});
			var td = el("td");
			td.innerHTML = formatMoney(total);
			tr.appendChild(td);
			tbody.appendChild(tr);
		});
		table.appendChild(tbody);
		section.appendChild(table);
		holder.appendChild(section);
		$(table).on("click", "tr", function() {
			if (!$(this).attr("data-start") || !$(this).attr("data-end")) {
				return;
			}
			window.takings.get_transactions($(this).attr("data-start"), $(this).attr("data-end"));
		});
		/*$(table).DataTable({
			responsive: true,
			dom: 'Bfrtip',
			buttons: [
				'copy', 'csv', 'excel', 
				{
					extend: 'pdfHtml5',
					orientation: 'landscape',
					pageSize: 'LEGAL'
				},
				'print'
			],
			"order": [[ 0, "desc" ]]
		});*/
	}
}
function formatMoney(amount) {
	if (typeof amount != "String") {
		amount = amount.toString();
	}
	return accounting.formatMoney(amount, "");
}