Spacewar.roomState = function(game) {

}

Spacewar.roomState.prototype = {

	init : function() {
		if (game.global.DEBUG_MODE) {
			console.log("[DEBUG] Entering **ROOM** state");
		}
	},

	preload : function() {

	},

	create : function() {
		var thediv = document.getElementById("deletethis")
		  thediv.parentNode.removeChild(thediv);
		
		
		function imReady () {
			console.log("IM READY")
			
			let readymsg = {
				event : "PLAYER IS READY",
				roomid : game.global.room.id
			}
			
			game.global.socket.send(JSON.stringify(readymsg))

			//game.state.start('gameState')
		}
		
		var readybutton = game.add.button(game.world.centerX ,game.world.centerY, "readybutton" , imReady, this);
		
	},

	update : function() {
	}
}