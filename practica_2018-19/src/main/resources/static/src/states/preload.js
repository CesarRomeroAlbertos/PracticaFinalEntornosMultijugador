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
		//large planet backgrounds by  Luke.RUSTLTD : https://opengameart.org/content/4-large-planets
		game.load.image("menubackground", "assets/images/mmBackground.png");  
		game.load.image("roomsbackground", "assets/images/salasBackground.png");  
		game.load.image("readybackground", "assets/images/readyBackground.png");  


		
		
		game.load.image("namebutton", "assets/images/namebutton.png");  
		game.load.image("quitbutton", "assets/images/quitbutton.png"); 
		game.load.image("readybutton", "assets/images/readybutton.png"); 
		game.load.image("makeroombutton", "assets/images/makeroombutton.png"); 
		game.load.image("cancelbutton", "assets/images/cancelbutton.png"); 
		game.load.image("actualquitbutton", "assets/images/actualquitbutton.png"); 
		game.load.image("backbutton", "assets/images/backbutton.png"); 
		game.load.image("scoresbanner","assets/images/scoresbanner.png")



	},

	create : function() {
		game.state.start('menuState')
	},

	update : function() {

	}
}