Spacewar.lobbyState = function(game) {

}

Spacewar.lobbyState.prototype = {

	init : function() {
		if (game.global.DEBUG_MODE) {
			console.log("[DEBUG] Entering **LOBBY** state");
			console.log("name is " +  game.global.myPlayer.name)
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