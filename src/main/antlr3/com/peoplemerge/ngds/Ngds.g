grammar Ngds;

options{
	language = Java;
//    backtrack=true;
    output = AST;              // build trees
    ASTLabelType = CommonTree; // use standard tree nodes
}
@header{
	package com.peoplemerge.ngds;
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

ID: ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;

node_param : INT_CONST CAPABILITY 'nodes from' NODE_CLASSIFIER;

create_statement : 'Create a new environment called' ID 'using' node_param (COMMA_AND node_param)* '.';
