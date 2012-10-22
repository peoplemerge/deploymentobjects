/************************************************************************
** 
** Copyright (C) 2011 Dave Thomas, PeopleMerge.
** All rights reserved.
** Contact: opensource@peoplemerge.com.
**
** This file is part of the DeploymentObjects language.
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

grammar DeploymentObjects;

options{
	language = Java;
//    backtrack=true;
    output = AST;              // build trees
    ASTLabelType = CommonTree; // use standard tree nodes
}
@header{
	package org.deploymentobjects.core;
	import java.io.IOException;
	import org.deploymentobjects.core.application.*;
	import org.deploymentobjects.core.domain.model.execution.*;
	import org.deploymentobjects.core.domain.model.environment.*;
	import org.deploymentobjects.core.domain.model.configuration.*;
	import org.deploymentobjects.core.infrastructure.persistence.*;
	import org.deploymentobjects.core.infrastructure.persistence.zookeeper.*;
	import org.deploymentobjects.core.infrastructure.configuration.*;
	import org.deploymentobjects.core.infrastructure.execution.*;
}

@members{
//  SymbolTable symbolTable = new SymbolTable();
//  Scope currentScope = symbolTable.globals;
	private ConfigurationManagement configurationManagement;
	private EnvironmentRepository environmentRepository;
	private Dispatchable dispatchable;
	// TODO is controlsHosts used?  May need to add to builder
	private ControlsHosts controlsHosts;
	private Persistence persistence;
	private Template template;

	public ConfigurationManagement getConfigurationManagement() {
		if (configurationManagement == null) {
			configurationManagement = new NoConfigurationManagement();
		}
		return configurationManagement;
	}

	public EnvironmentRepository getEnvironmentRepository() {
		if (persistence != null && environmentRepository == null) {
			if (persistence instanceof ZookeeperPersistence) {
				// Yes, this violates OCP, but this is an assembler and probably
				// appropriate in this case
				environmentRepository = new ZookeeperEnvironmentRepository(
						(ZookeeperPersistence) persistence);
			}
		}
		if (environmentRepository == null) {
			environmentRepository = new NoEnvironmentRepository();
		}
		return environmentRepository;
	}
	private Program program;
}

@lexer::header{
	package org.deploymentobjects.core;
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


//Host_APP_MAPPING :  

node_classifier returns [HostPool pool] : 'ldap' | 'ec2' | 'dom0' ID {$pool = new Hypervisor($ID.text, new NfsMount());} | 'zookeeper' ;

capability returns [Host.Type type] : 'small' {$type = Host.Type.getSmall();} | 'large' | 'database' ;

FABRIC : 'fabric';
MCOLLECTIVE : 'mcollective';
PUPPET : 'puppet';
ZOOKEEPER : 'zookeeper';
YAML : 'yaml';
JSCH : 'jsch';

ID: ('a'..'z'|'A'..'Z'|'0'..'9') ('a'..'z'|'A'..'Z'|'0'..'9'|'-'|'.')*;

PATH : ('a'..'z'|'A'..'Z'|'_'|'/'|'\\'|'.');
 // TODO: Don't let the user shoot himself in the foot with this very unrestrictive host string.
//HOST : ('a'..'z'|'A'..'Z'|'0'..'9')('a'..'z'|'A'..'Z'|'0'..'9' | '.'|'-')*;
ZOOKEEPER_CONNECTION_STRING : ( ID ':' INT_CONST )(COMMA ID ':' INT_CONST )*;


//ENVIRONMENT_DEFINITION : 'The' ID 'environment consists of' Host_APP_MAPPING (COMMA_AND Host_APP_MAPPING)* PERIOD;



// TODO allow better here docs like so http://www.antlr.org/pipermail/antlr-interest/2005-September/013673.html
scripted_statement returns [Executable command, Host node] : 'On' + 
	 	'host' ID {}
//	|   'role' ID 
//	|   'environment' ID 
	'run:' '<<EOF'
 	body=.* {
 		$command = new ScriptedCommand($body.text);
 		$node = new Host($ID.text);
 	} 
 	'EOF';


node_param returns [Integer qty, Host.Type type, HostPool pool, List<Role> roles] : 
	INT_CONST {$qty = new Integer($INT_CONST.text);}
	capability {$type = $capability.type;}
	'nodes from' node_classifier {$pool = $node_classifier.pool;}
	{$roles = new ArrayList<Role>();}
	
	(
		'having roles'
		(ID {$roles.add(new Role($ID.text));})+
		
	)?
	;


create_statement returns [Executable command, Host node]:
'Create a new environment called' ID 
	{
		CreateEnvironmentCommand.Builder builder = new CreateEnvironmentCommand.Builder($ID.text, getEnvironmentRepository());
		builder.withDispatch(dispatchable);
		builder.withConfigurationManagement(getConfigurationManagement());
	}
	'using' first=node_param { builder.withNodes($first.qty,$first.type, $first.pool, $first.roles.toArray(new Role[]{}) ); }
	(COMMA_AND other=node_param { builder.withNodes($other.qty,$other.type, $other.pool, $other.roles.toArray(new Role[]{}) ); })*
	
	{
		$command = builder.build();
	}
	;


version_param : 'latest' | ID  | INT_CONST;

module_type : 'application' | 'infrastructure';

filesystem_location : ID ':' PATH;

code_repository : 'version control' | 'the maven repository' | 'the filesystem' filesystem_location;

//dispatch_method : (FABRIC | MCOLLECTIVE | PURE_JAVA) 'dispatch';
dispatch_method : JSCH 'dispatch as user' ID 
{
	dispatchable = new JschDispatch($ID.text);
}
;

//naming_service_method : HOSTS_FILE | BIND |NSD_UNBOUND

//configuration_management_method : (CHEF | PUPPET 'server' HOST | CFENGINE) ;

//TODO consider storing global config information in zookeeper or whatever persistence
configuration_management_method : PUPPET 'with puppetmaster on' hostname=ID domainname=ID ipaddress=ID
{
	configurationManagement = new Puppet(new Host($hostname.text, $domainname.text, $ipaddress.text));
};

persistence_statement : 'Persist with' 
	(ZOOKEEPER 'with connection string' ZOOKEEPER_CONNECTION_STRING 
	{
	try{
		persistence = new ZookeeperPersistence($ZOOKEEPER_CONNECTION_STRING.text);
	}catch(IOException e){
		System.err.println(e.toString());
	}
	}
	| YAML 'file' PATH);

use_statement : 'Use' (dispatch_method | configuration_management_method);

deploy_statement returns [Executable command, Host node]:
	'Deploy' version_param module_type
	'code from' code_repository
	'to the' ID 'environment'
	{$command = new DeployApplicationCommand.Builder("text", $ID.text, new NoEnvironmentRepository()).build();}
;

// Do I need to know about packages, dependencies, distributions?

// Need to specify default meta parameters like where the source control is located, ec2 credentials or a way to lookup
// free VMs.

emit_statement: 'Emit'
{
	System.out.println(program.toString());
};
execute_statement: 'Execute'
{
	program.execute();
};


program returns [Program program]: 
	{
	$program = new Program(); 
	// set the global @member too
	program = $program;
	}
	use_statement*
	persistence_statement
	(
		//scripted_statement {$program.addStep($scripted_statement.command, $scripted_statement.node);} | 
		create_statement {$program.addStep($create_statement.command, new Host(""));} | 
		deploy_statement {$program.addStep($deploy_statement.command, new Host(""));}
	)*
	(emit_statement | execute_statement)*
	;
	
 	