var ProductsView = {
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
								m("section.card.bg-dark.text-white.rounded-0", {style:"margin-top:1em;"}, [
									m("img.card-img", {style:"filter: brightness(50%);", src:"https://proxy.duckduckgo.com/iu/?u=https%3A%2F%2Ftse1.mm.bing.net%2Fth%3Fid%3DOIP.B2NJxbmg3p9Jno9Pl_7MNwHaDt%26pid%3D15.1&f=1"}),
									m("section.card-img-overlay", [
										m("h5.card-title", "Product " + y),
										m("p.card-text", "Last updated 3 mins ago"),
									])
								])
							])
						}))
						])
					])
				])
			]);
	}
}