Spacewar.menuState = function(game) {

}

Spacewar.menuState.prototype = {

	init : function() {
		if (game.global.DEBUG_MODE) {
			console.log("[DEBUG] Entering **MENU** state");
		}
	},

	preload : function() {
		// In case JOIN message from server failed, we force it
		if (typeof game.global.myPlayer.id == 'undefined') {
			if (game.global.DEBUG_MODE) {
				console.log("[DEBUG] Forcing joining server...");
			}
			let message = {
				event : 'JOIN',
			}
			game.global.socket.send(JSON.stringify(message))
		}
	},

	create : function() {
		 let person = prompt("Please enter your name");
		 if (person != null) {
			 let message = {
					 event: "NAME",
					 name : person
			 }
			 game.global.socket.send(JSON.stringify(message))
			  }
	},

	update : function() {
		if (typeof game.global.myPlayer.id !== 'undefined' && typeof game.global.myPlayer.name !== "undefined") {
			game.state.start('lobbyState')
		}
	}
}