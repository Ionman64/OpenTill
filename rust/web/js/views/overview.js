var OverviewView = {
	view: function(vnode) {
		return m("section.container-fluid", [
			m("section.row", [
				m("section.col-lg-2.col-md-3.col-sm-6.col-xs-12", [
					m("section.card.fill", [
						m("section.card-body", [
							m("label", "Totals List")
						])
					])
				]),
				m("section.col-lg-10.col-md-9.col-sm-6.col-xs-12", [
					m("section.card.fill", [
						m("section.card-body", [
							m("label", "Graph of Takings")
						])
					])
				]),
			]),
			m("section.row", [
				m("section.col-lg-4.col-md-4.col-sm-6.col-xs-12", [
					m("section.card.fill", [
						m("section.card-body", [
							m("label", "Top Items List")
						])
					])
				]),
				m("section.col-lg-8.col-md-8.col-sm-6.col-xs-12", [
					m("section.card.fill", [
						m("section.card-body", [
							m("label", "Takings Per Cashier")
						])
					])
				]),
			])
		]);
	}
}