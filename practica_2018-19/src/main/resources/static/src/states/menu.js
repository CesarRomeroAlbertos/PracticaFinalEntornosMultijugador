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
		game.add.tileSprite(0, 0,1024, 600, "menubackground");
		function showPrompt(){
		 let person = prompt("Please enter your name");
		 if (person != null) {
			 let message = {
					 event: "NAME",
					 name : person
			 }
			 game.global.socket.send(JSON.stringify(message))
		 }
		 //game.global.myPlayer.name = person
		}
		var  menubutton = game.add.button(game.world.centerX-200 ,game.world.centerY, "namebutton" , showPrompt, this);
	},

	update : function() {
		console.log(game.global.myPlayer.id)
		console.log(game.global.myPlayer.name)
		if (typeof game.global.myPlayer.id !== 'undefined' && typeof game.global.myPlayer.name !== "undefined") {
			game.state.start('lobbyState')
		}
	}
}