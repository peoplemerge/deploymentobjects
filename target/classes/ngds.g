grammar ngds;

options{
  backtrack=true;
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

ID: ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;
COMMENT: '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
	|    '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;};
  
NEWLINE: ('\r'? '\n') {$channel=HIDDEN;};
WS: ( ' '| '\t' | '\r' | '\n' ) {$channel=HIDDEN;};
INT_CONST: '0'..'9'+;
STRING: '"' (~('\\'|'"') )* '"';

COMMA : ',';
AND : 'and';
PERIOD : '.';

COMMA_AND : COMMA | AND | COMMA AND;

ENVIRONMENT_DEFINITION : 'The' ID 'environment consists of' NODE_APP_MAPPING (COMMA_AND NODE_APP_MAPPING)* PERIOD;

