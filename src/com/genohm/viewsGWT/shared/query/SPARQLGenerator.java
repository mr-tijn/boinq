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

public interface SPARQLGenerator {
	public void visitMatch(MatchField match, String subjectIdentifier);
	public void visitMatch(MatchAll match, String subjectIdentifier);
	public void visitMatch(MatchAny match, String subjectIdentifier);
	public void visitMatch(MatchLocation match, String subjectIdentifier);
	public void visitMatch(MatchType match, String subjectIdentifier);
}
