lexer grammar Types;

@header{
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


//NODE_APP_MAPPING :  

NODE_CLASSIFIER : 'ldap' | 'ec2' | 'dom0' | 'zookeeper';

CAPABILITY : 'small' | 'large' | 'database';

FABRIC : 'fabric';
MCOLLECTIVE : 'mcollective';
PUPPET : 'puppet';
ON: 'On';
HOST: 'host';
RUN: 'run:';
REDIRECT: '<<';

ID: ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;

HEREDELIM: '__' + ID + '__';

PATH : ('a'..'z'|'A'..'Z'|'_'|'/'|'\\'|'.');

NODE_PARAM : INT_CONST CAPABILITY 'nodes from' NODE_CLASSIFIER;

