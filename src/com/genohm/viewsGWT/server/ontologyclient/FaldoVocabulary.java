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
package com.genohm.viewsGWT.server.ontologyclient;

import com.hp.hpl.jena.graph.Node;

public class FaldoVocabulary {
	public static String baseURI = "http://biohackathon.org/resource/faldo#";
	public static Node position = Node.createURI(baseURI+"position");
	public static Node begin = Node.createURI(baseURI+"begin");
	public static Node end = Node.createURI(baseURI+"end");
	public static Node reference = Node.createURI(baseURI+"reference");
	public static Node Position = Node.createURI(baseURI+"Position");
	public static Node StrandPosition = Node.createURI(baseURI+"StrandedPosition");
	public static Node ForwardStrandPosition = Node.createURI(baseURI+"ForwardStrandPosition");
	public static Node ReverseStrandPosition = Node.createURI(baseURI+"ReverseStrandPosition");
}
