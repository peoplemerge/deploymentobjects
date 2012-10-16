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
package com.peoplemerge.ngds;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Step implements Executable {

	private Executable command;
	private AcceptsCommands node;
	private String output;

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public Step(Executable command, AcceptsCommands node) {
		this.command = command;
		this.node = node;
	}

	public Executable getCommand() {
		return command;
	}

	public List<Node> getNodes() {
		return node.getNodes();
	}

	@Override
	public ExitCode execute() {
		return command.execute();
	}

	public String toString() {
		if (output == null) {
			return "Running: " + command + " on " + node;
		} else {
			return "Ran: " + command + " on " + node + " result: " + output;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		Step rhs = (Step) obj;
		// TODO invesigate why it fails when .appendSuper(super.equals(obj))
		EqualsBuilder builder = new EqualsBuilder()
				.append(command, rhs.command).append(node, rhs.node);
		if (!(output == null && rhs.output == null)) {
			builder.append(output, rhs.output);
		}		
		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(1245737, 534515).append(command)
				.append(node).append(output).toHashCode();
	}

}
