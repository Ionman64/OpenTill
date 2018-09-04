window.departmentNames = {};
function Takings(){
	this.init = function() {
		window.takings.populate_table();
		
		
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
			url:"api/kvs.jsp?function=GETDAYTOTALS",
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
	this.populate_table = function() {
		m.mount(document.getElementById("takings-viewport"), mithril_takings);
		return;
		$(holder).empty();no-padding-right
		var section = el("section");
		var table = el("table");
		table.id = "table";
		table.className = "table";
		var thead = el("thead");
		var tr = el("tr");
		var th = el("th");
		th.innerHTML = "Date";
		tr.appendChild(th);
		$.each(window.dashboard_data.departments, function(id, department) {
			var th = el("th");
			th.className = "head";
			th.innerHTML = department;
			tr.appendChild(th);
		});
		var th = el("th");
		th.className = "head";
		th.innerHTML = "Total";
		tr.appendChild(th);
		thead.appendChild(tr);
		table.appendChild(thead);
		var tbody = el("tbody");
		$.each(window.dashboard_data.takings, function(date, totals) {
			var total = 0.00;
			var tr = el("tr", {"data-start":moment(date).unix(), "data-end":moment(date).add(1, "days").subtract(1, "seconds").unix()});
			var td = el("td");
			td.innerHTML = date;
			tr.appendChild(td);
			$.each(window.dashboard_data.departments, function(id, department) {
				var td = el("td");
				var amount = parseFloat(totals[id] ? totals[id] : "0.00");
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
	}
}
function formatMoney(amount) {
	if (typeof amount == undefined) {
		accounting.formatMoney(0, "");
	}
	if (typeof amount != "String") {
		amount = amount.toString();
	}
	return accounting.formatMoney(amount, "");
}
var mithril_takings = {
	dates:[],
	ctrl: function() {
		$.each(window.dashboard_data.takings, function(date, totals) {
			totals["date"] = key;
			this.dates.push(totals);
		});
		//m.redraw();
	},
    view: function() {
    	return m("section", {class:"col-lg-12 col-md-12 col-sm-12 col-xs-12"}, [
        	m("canvas#takings-canvas", {onready:function() {console.log("Hello");}})
        ]);
    }
};

/*this.dates.map(function(item) {
	return m("section", {class:"col-lg-2 col-md-3 col-sm-6 col-xs-12"}, [
		m("section", {class:"panel panel-default"}, [
			m("section", {class:"panel-body"}, [
				m("section", {class:"row"}, [ 
					m("section", {class:"col-lg-12 col-md-12 col-sm-12 col-xs-12"}, [
						 m("h4", item.date)
					]),
					m("section", {class:"col-lg-6 col-md-6 col-sm-6 col-xs-6"}, 
						(function(totals) {
							var elems = [];
							$.each(window.dashboard_data.departments, function(id, department) {
								elems.push(m("h4", department));
								elems.push(m("h4", {class:"pull-right"}, formatMoney(item.price)));
							});
						})(item)
					)
				])
			])
		])
	]);
})*/
