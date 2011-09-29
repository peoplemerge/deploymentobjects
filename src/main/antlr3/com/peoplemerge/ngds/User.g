grammar User;

import Types, Common;

@header{
	package com.peoplemerge.ngds;
	import com.peoplemerge.ngds.Program;
	import com.peoplemerge.ngds.Command;
}

letters : letter+ ;

stuff : ( letters spaces )+ ;


/*
On host localhost run: <<__EOF__
ls
__EOF__
*/

here_doc_statement returns [Command command, Node node] : ON + 
	HOST ID {}
	RUN REDIRECT HEREDELIM
 	here=.* {
 		$command = new ScriptedCommand($here.text);
 		$node = new Node($ID.text);
 	} 
 	HEREDELIM;
/*
create_statement returns [Command command, Node node]: 
	'Create a new environment called' ID 
	'using' node_param (COMMA_AND node_param)* 
	'.'
	{$command = new CreateEnvironmentCommand();}
	;
*/