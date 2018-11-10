var TransactionsView = {
	view: function(vnode) {
		return m("section.container-fluid", [
			m("section.row", [
				m("section.col-lg-12.col-md-12.col-sm-12.col-xs-12", [
					m("section.card.fill", [
						m("section.card-body", [
							m("label", "Transactions Graph")
						])
					])
				]),
			]),
			m("section.row", [
				m("section.col-lg-12.col-md-12.col-sm-12.col-xs-12", [
					m("section.card.fill", [
						m("section.card-body", [
							m("label", "Transactions Table")
						])
					])
				]),
			])
		]);
	}
}