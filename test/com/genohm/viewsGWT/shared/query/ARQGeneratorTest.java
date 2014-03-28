/*  
 *   Copyright 2012-2014 Martijn Devisscher
 *
 *   This file is part of boinq.
 *
 *   boinq is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   boinq is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with boinq.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genohm.viewsGWT.shared.query;

import static org.junit.Assert.*;

import org.junit.Test;

import com.genohm.viewsGWT.server.query.ARQGenerator;
import com.genohm.viewsGWT.shared.fieldconfig.FieldType;

public class ARQGeneratorTest {

	protected ARQGenerator gen = new ARQGenerator();
	
	@Test
	public void test() {
		MatchAll full = new MatchAll();
		MatchField chain = new MatchField();
		chain.setFieldIRI("http://pipo.org#hasAssociatedObject");
		MatchField subMatch = new MatchField();
		chain.setSubMatch(subMatch);
		subMatch.setFieldIRI("http://pipo.org#hasInteger");
		subMatch.setType(FieldType.INTEGER_TYPE);
		subMatch.setValueExpression("3*?val <= 20000");
		full.addMatch(chain);
		MatchField text = new MatchField();
		text.setType(FieldType.STRING_TYPE);
		text.setFieldIRI("http://pipo.org#hasDescription");
		text.setValueExpression("Hallojongens");
		full.addMatch(text);
		MatchLocation loc = new MatchLocation();
		loc.setContig("chr1");
		loc.setStrand(true);
		loc.setMatchStrand(true);
		loc.setStart(1000000L);
		loc.setEnd(2000000000L);
		full.addMatch(loc);
		MatchAny types = new MatchAny();
		types.addMatch(new MatchType("http://thetype"));
		types.addMatch(new MatchType("http://theothertype"));
		full.addMatch(types);
		System.out.println(gen.generateQuery(full));
	}

}
