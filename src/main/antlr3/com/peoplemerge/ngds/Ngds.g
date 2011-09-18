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

grammar Ngds;

options{
	language = Java;
//    backtrack=true;
    output = AST;              // build trees
    ASTLabelType = CommonTree; // use standard tree nodes
}
@header{
	package com.peoplemerge.ngds;
	import com.peoplemerge.ngds.Program;
	import com.peoplemerge.ngds.Command;
}

@members{
//  SymbolTable symbolTable = new SymbolTable();
//  Scope currentScope = symbolTable.globals;
}

@lexer::header{
	package com.peoplemerge.ngds;
}

COMMA : ',';
AND : 'and';
PERIOD : '.';

COMMA_AND : COMMA | AND | COMMA AND;

COMMENT: '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
	|    '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;};
  
NEWLINE: ('\r'? '\n') {$channel=HIDDEN;};
WS: ( ' '| '\t' | '\r' | '\n' ) {$channel=HIDDEN;};
INT_CONST: '0'..'9'+;
STRING: '"' (~('\\'|'"') )* '"';


//ENVIRONMENT_DEFINITION : 'The' ID 'environment consists of' NODE_APP_MAPPING (COMMA_AND NODE_APP_MAPPING)* PERIOD;

//NODE_APP_MAPPING :  

NODE_CLASSIFIER : 'ldap' | 'ec2' | 'dom0' | 'zookeeper';

CAPABILITY : 'small' | 'large' | 'database';

FABRIC : 'fabric';
MCOLLECTIVE : 'mcollective';
PUPPET : 'puppet';

ID: ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;

node_param : INT_CONST CAPABILITY 'nodes from' NODE_CLASSIFIER;

create_statement returns [Command command]: 
	'Create a new environment called' ID 
	'using' node_param (COMMA_AND node_param)* 
	'.'
	{$command = new CreateEnvironmentCommand();}
	;

PATH : ('a'..'z'|'A'..'Z'|'_'|'/'|'\\'|'.');


scripted_statement returns [Command command] : 'On' + 
	 	'host' ID {}
//	|   'role' ID 
	'run:' '<<__EOF__'
 	body=.* {$command = new ScriptedCommand($body.text);} 
 	'__EOF__';

version_param : 'latest' | ID  | INT_CONST;

module_type : 'application' | 'infrastructure';

filesystem_location : ID ':' PATH;

code_repository : 'version control' | 'the maven repository' | 'the filesystem' filesystem_location;

orchestration_method : (FABRIC | MCOLLECTIVE) 'orchestration';
configuration_management_method : PUPPET 'configuration management';

use_statement : 'Use' (orchestration_method | configuration_management_method);

deploy_statement returns [Command command]: 
	'Deploy' version_param module_type 
	'code from' code_repository 
	'to the' ID 'environment.'
	{$command = new DeployCommand();}
;

// Do I need to know about packages, dependencies, distributions?

// Need to specify default meta parameters like where the source control is located, ec2 credentials or a way to lookup
// free VMs.


program returns [Program program]: 
	(
		scripted_statement {$program = Program.factory($scripted_statement.command);}
		| create_statement {$program = Program.factory($create_statement.command);}
		| deploy_statement {$program = Program.factory($deploy_statement.command);}
	)
	
	;
	
emit_command: 'Emit';

execute_command: 'Execute';


 	