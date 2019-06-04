var SettingsView = {
	view: function(vnode) {
		return m("section.container-fluid", [
			m("section.row", [
				m("section.col-lg-6.col-md-8.col-sm-10.col-xs-12.mx-auto", [
					m("section.card.card-default.rounded-0", [
						m(SettingsNavigation),
						m("section.card-body", [
							m("label", "Settings Page")
						])
					])
				])
			])
		]);
	}
}