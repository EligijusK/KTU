/* 
  Track Multimedia views on GA. Including FLVPlayer. (For UrchinTracker)
  Coder: Victoria Chan
	Date: 09-09-2008
 */

jQuery(document).ready(function() {
	jQuery('a[href $= .mp3]').click(function(event){ 
		logMultimediaFileToGA(this.href);
	 });
});

function logMultimediaFileToGA(fullpath){
	var thisfile_url = fullpath;
	var thisfile_name = thisfile_url.substring(thisfile_url.lastIndexOf('/')+1);
	urchinTracker ('/multimedia/' + thisfile_name);
}

//FOR FLVPlayer
var vplayer = new Array();;

function playerReady( obj )
{
	vplayer[obj.id] = document.getElementById( obj.id );
	vplayer[obj.id].addModelListener( "STATE", "stateMonitor" );
};

function stateMonitor( obj )
{
	if( obj.newstate == "PLAYING" ) //COMPLETED | PLAYING
	{
		logMultimediaFileToGA(vplayer[obj.id].getConfig().file);
	}
};
