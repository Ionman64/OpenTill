var HomeView = {
	view: function(vnode) {
		return m("section.container-fluid", [
			m("section.row", [
				m("section.col-lg-8.col-md-10.col-sm-12.col-xs-12.mx-auto", [0,0,0,0,0,0,0,0,0,0,0,0].map(function(x, y) {
					return m("section.row", [
						m("section.col-lg-12.col-md-12.col-sm-12.col-xs-12", [
							m("section.card.card-default.rounded-0", {style:"margin-top:1em;"}, [
								m("section.card-body", [
									m("label", "System Event")
								]),
								m("section.card-footer", [
									m("label.card-text", "Peter Pickerill"),
									m("label.card-text.float-right", "6 minutes ago")
								])
							])
						]),
					]);
				}))
			])
		]);
	}
}