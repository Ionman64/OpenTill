var UserNotification = {
	_message:"",
	_timeout:-1,
	_alertType:"alert-default",
	_shown:false,
	_timeoutFunc:undefined,
	show:function(message,timeout,alertType) {
		UserNotification._message = message;
		UserNotification._timeout = timeout;
		UserNotification._alertType = alertType;
		UserNotification._shown = true;
	},
	hide: function() {
		this._shown = false;
		m.redraw();
	},
	_hide:function() {
		if (UserNotification._timeoutFunc) {
			clearTimeout(UserNotification.timeoutFunc);
		}
		if (isSameValue(UserNotification._timeout, 2)) {
			UserNotification._shown = false;
		}
		if (isSameValue(UserNotification._timeout, -1)) {
			return;
		}
		UserNotification._timeoutFunc = setTimeout(function() {
			UserNotification._shown = false;
			m.redraw();
		}, UserNotification._timeout);
	},
	view() {
		if (this._shown) {
			return m("section#userNotification.alert.no-padding.user-notification." + this._alertType, {oncreate:this._hide, onupdate:this._hide, onclick:this.hide});
		}
		return m("section#userNotification.alert.no-padding.user-notification.d-none." + this._alertType, {oncreate:this._hide, onupdate:this._hide, onclick:this.hide});
	}
}