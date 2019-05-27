Spacewar.gameState = function(game) {
	this.bulletTime
	this.fireBullet
	this.numStars = 100 // Should be canvas size dependant
	this.maxProjectiles = 800 // 8 per player
	
}
var quitbutton

function reloadAmmo(){
	setTimeout(() => {
		  game.global.myPlayer.ammo = 20 // hardcoded
		  myAmmo.text = game.global.myPlayer.ammo;
		}, 5000);
}


Spacewar.gameState.prototype = {

	init : function() {
		if (game.global.DEBUG_MODE) {
			console.log("[DEBUG] Entering **GAME** state");
		}
	},

	preload : function() {
		game.global.myPlayer.ammo = 20 // hardcoded

		// We create a procedural starfield background
		for (var i = 0; i < this.numStars; i++) {
			let sprite = game.add.sprite(game.world.randomX,
					game.world.randomY, 'spacewar', 'staralpha.png');
			let random = game.rnd.realInRange(0, 0.6);
			sprite.scale.setTo(random, random)
		}

		// We preload the bullets pool
		game.global.proyectiles = new Array(this.maxProjectiles)
		for (var i = 0; i < this.maxProjectiles; i++) {
			game.global.projectiles[i] = {
				image : game.add.sprite(0, 0, 'spacewar', 'projectile.png')
			}
			game.global.projectiles[i].image.anchor.setTo(0.5, 0.5)
			game.global.projectiles[i].image.visible = false
		}

		// we load a random ship
		let random = [ 'blue', 'darkgrey', 'green', 'metalic', 'orange',
				'purple', 'red' ]
		let randomImage = random[Math.floor(Math.random() * random.length)]
				+ '_0' + (Math.floor(Math.random() * 6) + 1) + '.png'
		game.global.myPlayer.image = game.add.sprite(0, 0, 'spacewar',
				game.global.myPlayer.shipType)
		game.global.myPlayer.image.anchor.setTo(0.5, 0.5)
		var style = { font: "bold 32px Arial", fill: "#ffe500", boundsAlignH: "center", boundsAlignV: "middle" };
		game.global.myPlayer.name = game.add.text(game.global.myPlayer.image.x, game.global.myPlayer.image.y + 30, game.global.myPlayer.name ,style);
		game.global.myPlayer.name.anchor.setTo(0.5);
		game.global.myPlayer.name.fontSize = 20;
	    game.global.myPlayer.myHCounter = game.add.text(250, 16, '', { fill: '#ffffff' });
	    game.global.myPlayer.myHCounter.text =  game.global.myPlayer.health;
	   game.global.myPlayer.myAmmoCounter = game.add.text(265, 16, '', { fill: '#37ff0a' });
	    game.global.myPlayer.myAmmoCounter.text = game.global.myPlayer.ammo;
	    
	},

	create : function() {
		
	
		// game.state.start('scoresState')

		
		async function backtoMenu(){
			console.log("Backtomenu")
			let msg = {
				event : "PLAYER LEFT",
				id:  game.global.myPlayer.id
			}
			await(game.global.socket.send(JSON.stringify(msg)))
			game.state.start('menuState')


		}
		quitbutton = game.add.button(game.world.centerX-175 ,game.world.centerY, "quitbutton" , backtoMenu, this);
		quitbutton.alpha= 0;
		quitbutton.inputEnabled = false
		this.bulletTime = 0
		this.fireBullet = function() {
			if (game.time.now > this.bulletTime) {
				this.bulletTime = game.time.now + 250;
				// this.weapon.fire()
				return true
			} else {
				return false
			}
		}

		this.wKey = game.input.keyboard.addKey(Phaser.Keyboard.W);
		this.sKey = game.input.keyboard.addKey(Phaser.Keyboard.S);
		this.aKey = game.input.keyboard.addKey(Phaser.Keyboard.A);
		this.dKey = game.input.keyboard.addKey(Phaser.Keyboard.D);
		this.spaceKey = game.input.keyboard.addKey(Phaser.Keyboard.SPACEBAR);
		this.eKey = game.input.keyboard.addKey(Phaser.Keyboard.E);

		// Stop the following keys from propagating up to the browser
		game.input.keyboard.addKeyCapture([ Phaser.Keyboard.W,
				Phaser.Keyboard.S, Phaser.Keyboard.A, Phaser.Keyboard.D,
				Phaser.Keyboard.SPACEBAR , Phaser.Keyboard.E]);

		game.camera.follow(game.global.myPlayer.image);
	},

	update : function() {
		
		game.global.myPlayer.name.x = game.global.myPlayer.image.x;
		game.global.myPlayer.name.y = game.global.myPlayer.image.y-40;
		
		if (game.global.myPlayer.playerIsGhost){
			console.log("que la pasa")
			// this.spaceKey = null;
			game.global.myPlayer.image.alpha = 0.25;
			quitbutton.alpha = 1;
			quitbutton.inputEnabled = true;
		}
		let msg = new Object()
		msg.event = 'UPDATE MOVEMENT'

		msg.movement = {
			thrust : false,
			brake : false,
			rotLeft : false,
			rotRight : false
		}
		
	

		msg.bullet = false

		if (this.wKey.isDown)
			msg.movement.thrust = true;
		if (this.sKey.isDown)
			msg.movement.brake = true;
		if (this.aKey.isDown)
			msg.movement.rotLeft = true;
		if (this.dKey.isDown)
			msg.movement.rotRight = true;
		if (this.spaceKey.isDown ) {
			msg.bullet = this.fireBullet()
		}
		if(this.eKey.isDown){
			msg.reload = true;
		}
			
		

		if (game.global.DEBUG_MODE) {
			console.log("[DEBUG] Sending UPDATE MOVEMENT message to server")
		}
		game.global.socket.send(JSON.stringify(msg))
	}
}