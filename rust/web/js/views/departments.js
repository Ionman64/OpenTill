var DepartmentsView = {
	view: function(vnode) {
		return m("section.container-fluid", [
			m("section.row", [
				m("section.col-lg-2.col-md-2.col-sm-4.d-xs-none", [
					m("section.card.fill", {style:"height:80vh;margin-top:1em;"}, [
						m("section.card-body", [
							m("label", "Filter")
						])
					])
				]),
				m("section.col-lg-10.col-md-10.col-sm-8.col-xs-12", [
					m("section.container-fluid", [
						m("section.row", [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0].map(function(x,y) {
							return m("section.col-lg-3.col-md-4.col-sm-6.col-xs-12", [
								m("section.card.card-default.rounded-0", {style:"margin-top:1em;"}, [
									m("h5", "Department " + y),
									m("p", "Last updated 3 mins ago"),
								])
							])
						}))
						])
					])
				])
			]);
	}
}