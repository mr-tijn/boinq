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
package com.genohm.viewsGWT.client.eventbus;

/**
 * Default callback that is notified when an event occurs
 * 
 * @author farrukh@wellfleetsoftware.com , mihai.ile@gmail.com
 */
public interface TopicSubscriber<T> {

	/**
	 * Listener that gets notified of an event on a topic.
	 * 
	 * @param subscription
	 *            the subscription that generated the event
	 * @param event
	 *            the event object
	 */
	public void onEvent(Subscription subscription, T event);
	
	public int getId();
}