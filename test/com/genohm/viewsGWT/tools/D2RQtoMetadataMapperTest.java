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
package com.genohm.viewsGWT.tools;

import static org.junit.Assert.*;

import org.junit.Test;

public class D2RQtoMetadataMapperTest {

	@Test
	public void testConvert() {
		D2RQtoMetadataMapper mapper = new D2RQtoMetadataMapper();
		System.out.println(mapper.convert("file:/opt/jena-fuseki/homo_sapiens_core_71_37_simple.ttl","homo_sapiens_core_71_31_simple"));
	}

}
