var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var players = [];
var shortid = require('gen-id')('nnnnnnnnc');
var map = {};
var pam = {};

server.listen(3000, function(){
	console.log("Server is running. . .");
});

io.on('connection', function(socket){
	console.log("Player Connected!");
	map[socket.id]=shortid.generate();
	pam[map[socket.id]]=socket.id;
	socket.emit('socketID', map[socket.id]);
	socket.on('playerMoved',function(move, opp){
		if (io.sockets.connected[pam[opp]]) {
    			io.sockets.connected[pam[opp]].emit('playerMoved', move);
		};
	console.log(move);
	});
	socket.on('disconnect',function(){
		console.log("Player Disconnected!");
	});
	socket.on('opponentConnected',function(iden,opp){
		if (io.sockets.connected[pam[opp]]) {
    			io.sockets.connected[pam[opp]].emit('opponentConnected', iden);
		};
	});

});

function player(id,move){
   	 this.id = id;
   	 this.move = move;
}