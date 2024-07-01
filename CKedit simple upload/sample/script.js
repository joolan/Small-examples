const watchdog = new CKSource.EditorWatchdog();
var myEditor;
window.watchdog = watchdog;
watchdog.setCreator( ( element, config ) => {
	
	return CKSource.Editor
		.create( element, config )
		.then( editor => {
			
			 myEditor = editor;
			 		
			return editor;
		} );
} );

watchdog.setDestructor( editor => {
	return editor.destroy();
} );

watchdog.on( 'error', handleSampleError );

var token = '456s456asd456sd45as6d4s';
watchdog
	//.create( document.querySelector( '.editor' ), {
	.create( document.querySelector( '.editor' ), {
		// Editor configuration.

		simpleUpload: {           
            uploadUrl: 'http://xxxxxxx/upload.php',
            withCredentials: false,//跨域请求需要false
			headers: {
                'X9999-CSRF-TOKEN': 'CSRF-Token',
                Authorization: token
            },
        }
	} )
	.catch( handleSampleError );

function handleSampleError( error ) {
	const issueUrl = 'https://github.com/ckeditor/ckeditor5/issues';

	const message = [
		'Oops, something went wrong!',
		`Please, report the following error on ${ issueUrl } with the build id "uu7x03n5lvo7-311i5035qk3b" and the error stack trace:`
	].join( '\n' );

	console.error( message );
	console.error( error );
}

function getvalue(){
	
	//var s =  CKSource.editor.getData();
	var s = myEditor.getData();
	console.log(s);
}

function setvalue(){

myEditor.setData("<p>内容</p>");

}