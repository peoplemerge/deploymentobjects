/************************************************************************
** 
** Copyright (C) 2011 Dave Thomas, PeopleMerge.
** All rights reserved.
** Contact: opensource@peoplemerge.com.
**
** This file is part of the NGDS language.
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**    http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
**  
** Other Uses
** Alternatively, this file may be used in accordance with the terms and
** conditions contained in a signed written agreement between you and the 
** copyright owner.
************************************************************************/

parser grammar Common;

@header{
	import com.peoplemerge.ngds.Program;
	import com.peoplemerge.ngds.Command;
	import com.peoplemerge.ngds.Node;
	import com.peoplemerge.ngds.ScriptedCommand;
}

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
 	
 	

node_param : NODE_PARAM ;



letter : LETTER ;
spaces : WS+ ;
