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
		game.add.tileSprite(0, 0,1024, 600, "readybackground");

		
		game.global.myPlayer.playersWithMe = game.add.text(250, 16, '', { fill: '#ffffff' });
		game.global.myPlayer.playersWithMeReady = game.add.text(250, 24, '', { fill: '#42f4c5' });
		
		
		let requestroomstatus = {
				event : "REQUEST ROOM STATUS",
				roomid : game.global.room.id
				
				
		}
		
		game.global.socket.send(JSON.stringify(requestroomstatus))

		
		var thediv = document.getElementById("deletethis")
		  thediv.parentNode.removeChild(thediv);
		
		
		function imReady () {
			console.log("IM READY")
			
			let readymsg = {
				event : "PLAYER IS READY",
				roomid : game.global.room.id
			}
			
			game.global.socket.send(JSON.stringify(readymsg))

		}
		
		var readybutton = game.add.button(game.world.centerX ,game.world.centerY, "readybutton" , imReady, this);
		readybutton.anchor.setTo(0.5)

	},

	update : function() {
	}
}