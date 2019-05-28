Spacewar.menuState = function(game) {

}

//Tenemos una funcion para ir a lobby , esta se le llamara desde indexjs cuando el server nos de permiso
function goToLobby(){
	if (typeof game.global.myPlayer.id !== 'undefined' && typeof game.global.myPlayer.name !== "undefined") {
		game.state.start('lobbyState')
}
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
			
		//Ponemos una funcion para enviarle un mensaje al servidor de que nos ponga nuestro nombre 
		 let person = prompt("Please enter your name");
		 if (person != null) {
			 let message = {
					 event: "NAME",
					 name : person
			 }
			 game.global.socket.send(JSON.stringify(message))
		 }
		}
		var  menubutton = game.add.button(game.world.centerX ,game.world.centerY, "namebutton" , showPrompt, this);
		menubutton.anchor.setTo(0.5)
	},

	update : function() {
		
		}
	
}