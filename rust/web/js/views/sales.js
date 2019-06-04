var SalesView = {
	createCells: function() {
		var year = valueOrDefault(m.route.param('year'), moment().year());
	    var month = valueOrDefault(m.route.param('month'), moment().month());
	    let tempDate = moment().set("year", year).set("month", month).set("date", 1);
		
	    while (tempDate.day() != 0) {
	    	tempDate.subtract(1, "days");
	    }
	    
	    var cells = [];
		var tr = [];
		var i = 0;
		
		while (i < (5*7)) {
			if (i % 7 == 0) {
				cells.push(m("tr", tr));
				tr = [];
			}
			tr.push(m("td.no-padding", [
				m("section.calendar-date", tempDate.date())
			]));
			tempDate.add(1, "days");
			i++;
		}

		if (tr.length > 0) {
			cells.push(m("tr", tr));
		}
		
		return cells;
	},
	view: function(ctrl) {
		var year = valueOrDefault(m.route.param('year'), moment().year());
	    var month = valueOrDefault(m.route.param('month'), moment().month());
	    var tempDate = moment().set("year", year).set("month", month).set("date", 1);
	    console.log(tempDate);
		return m("section.container-fluid", [
			m("section.row", [
				m("section.col-lg-4.col-md-4.col-sm-4.col-xs-4", [
					m("button[href='" + tempDate.clone().subtract(2, 'months').format("/[sales]/YYYY/MM") + "'].btn.btn-default.btn-lg", { onupdate:m.route.link, oncreate:m.route.link}, tempDate.clone().subtract(1, 'months').format("MMMM"))
				]),
				m("section.col-lg-4.col-md-4.col-sm-4.col-xs-4.text-center", [
					m("h3.text-default", tempDate.format("MMMM YYYY"))
				]),
				m("section.col-lg-4.col-md-4.col-sm-4.col-xs-4", [
					m("button[href='" + tempDate.format("/[sales]/YYYY/MM") + "'].btn.btn-default.btn-lg.float-right", { onupdate:m.route.link, oncreate:m.route.link}, tempDate.clone().add(1, 'months').format("MMMM"))
				]),
			]),
			m("section.row", [
				m("section.col-lg-12.col-md-12.col-sm-12.col-xs-12", [
					m("table.table", [
						m("thead", moment.weekdays().map(function(day) {
							return m("th", day);
						})),
						m("tbody", this.createCells())
					])
				])
			])
		])
	}
}