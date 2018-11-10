var SelectedUserView = {
	user:{},
	oninit:function() {
		this.fetch();
	},
	fetch: function() {
		m.request({
			url:"/api/users/:id",
			data:{id:m.route.param("id")}
		}).then(function(data) {
			StaffView.staff = data;
		}).catch(function() {
			alert("there has been an error");
		});
	},
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
						m("section.row", this.staff.map(function(x) {
							return m("section.col-lg-3.col-md-4.col-sm-6.col-xs-12", [
								m("a[href=/users/" + x.id + "]", {onupdate:m.route.link, oncreate:m.route.link}, [
									m("section.card.bg-dark.rounded-0.text-white", {style:"margin-top:1em;"}, [
										m("section.card-body", [
											m("h5.card-title", x.name),
											m("p.card-text", moment(x.updated*1000).fromNow()),
										])
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