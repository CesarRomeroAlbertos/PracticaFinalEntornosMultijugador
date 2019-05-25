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
		
		
		
		
		var gamediv = document.getElementById("gameDiv")
		var nameform = document.createElement("form");
		nameform.setAttribute("action","/action_page.php")
		nameform.setAttribute("id","deletemenuform")
		nameform.style.position = "absolute";
		nameform.style.zIndex = "1000";
		nameform.style.left =  "200px";
		nameform.style.top =  "150px";
		
		var br = document.createElement("br")
		br.setAttribute("input type","text")
		br.setAttribute("name" , "fnName")
		br.setAttribute("id","nameField")
		
		nameform.appendChild(br)



		
		
		
		
		
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
		if (typeof game.global.myPlayer.id !== 'undefined' && typeof game.global.myPlayer.name !== "undefined") {
			game.state.start('lobbyState')
		}
	}
}