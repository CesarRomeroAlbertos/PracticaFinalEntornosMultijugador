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
			
			game.state.start('gameState')
		}
		
		var readybutton = game.add.button(game.world.centerX ,game.world.centerY, "readybutton" , imReady, this);
		
	},

	update : function() {
	}
}