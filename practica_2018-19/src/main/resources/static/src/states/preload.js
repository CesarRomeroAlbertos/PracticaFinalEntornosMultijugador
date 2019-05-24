Spacewar.preloadState = function(game) {

}

Spacewar.preloadState.prototype = {

	init : function() {
		if (game.global.DEBUG_MODE) {
			console.log("[DEBUG] Entering **PRELOAD** state");
		}
	},

	preload : function() {
		game.load.atlas('spacewar', 'assets/atlas/spacewar.png',
				'assets/atlas/spacewar.json',
				Phaser.Loader.TEXTURE_ATLAS_JSON_HASH)
		game.load.atlas('explosion', 'assets/atlas/explosion.png',
				'assets/atlas/explosion.json',
				Phaser.Loader.TEXTURE_ATLAS_JSON_HASH)
		game.load.image("menubackground", "assets/images/mmBackground.png");  
		//by  Luke.RUSTLTD : https://opengameart.org/content/4-large-planets
		
		game.load.image("namebutton", "assets/images/namebutton.png");  
		game.load.image("quitbutton", "assets/images/quitbutton.png"); 
		game.load.image("readybutton", "assets/images/readybutton.png"); 
		game.load.image("makeroombutton", "assets/images/makeroombutton.png"); 


	},

	create : function() {
		game.state.start('menuState')
	},

	update : function() {

	}
}