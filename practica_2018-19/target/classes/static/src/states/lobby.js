Spacewar.lobbyState = function(game) {

}

Spacewar.lobbyState.prototype = {

	init : function() {
		if (game.global.DEBUG_MODE) {
			console.log("[DEBUG] Entering **LOBBY** state");
			
		}
		if (game.global.DEBUG_MODE_CARMEN){
			console.log("name is " +  game.global.myPlayer.name)
			console.log("health is " + game.global.myPlayer.health)
		}
	},

	preload : function() {

	},

	create : function() {
		game.state.start('matchmakingState')
	},

	update : function() {

	}
}