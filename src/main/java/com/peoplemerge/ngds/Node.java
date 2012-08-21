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

import java.util.ArrayList;
import java.util.List;

public class Node implements AcceptsCommands {

	private String hostname;

	public Node(String hostname) {
		this.hostname = hostname;
	}

	public String getHostname() {
		return hostname;
	}

	public String toString() {
		return hostname;
	}

	@Override
	public Job accept(Executable command) {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO This is here simply because Environment.getNodes makes sense which
	// shares the interface AcceptsCommands. Think about ISP, break up AcceptsCommands?
	public List<Node> getNodes() {
		List<Node> asList = new ArrayList<Node>();
		asList.add(this);
		return asList;
	}

}
